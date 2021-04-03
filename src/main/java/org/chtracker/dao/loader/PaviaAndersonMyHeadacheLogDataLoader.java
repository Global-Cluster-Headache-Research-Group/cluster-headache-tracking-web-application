package org.chtracker.dao.loader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.metadata.AbortiveTreatmentTypeRepository;
import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.metadata.PreventiveTreatmentTypeRepository;
import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.profile.PatientRepository;
import org.chtracker.dao.report.AbortiveTreatment;
import org.chtracker.dao.report.AbortiveTreatmentRepository;
import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttackRepository;
import org.chtracker.dao.report.PreventiveTreatment;
import org.chtracker.dao.report.PreventiveTreatmentRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnNotWebApplication
public class PaviaAndersonMyHeadacheLogDataLoader extends DataLoader {

	private final Map<Integer, LocalDateTime> idToStart = new HashedMap<>();
	private final Map<Integer, LocalDateTime> idToEnd = new HashedMap<>();
	private final Map<Integer, Attack> idToAttack = new HashedMap<>();
	@Value("${attacks.pavia.path:data/Pavias-anfald-og-behandlinger-5000-anfald.xlsx}")
	private String attacksDataPath;

	static final DateTimeFormatter dateTimeFormatterWithSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm:ss");
	static final DateTimeFormatter dateTimeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm");

	enum AttacSheetColumn {
		ID, TYPE, START, END, LEVEL, LOCATION, NOTES, SYMPTOMS, TRIGGERS
	}

	enum TreatmentSheetColumn {
		ID, START, TREATMENT, DOSE_COUNT, TREATMENT_HELPED
	}

	final PatientRepository patientRepository;

	final Patient patient;

	final AbortiveTreatmentRepository abortiveTreatmentRepository;
	final AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository;

	private Logger logger;
	private AttackRepository attackRepository;
	private PreventiveTreatmentRepository preventiveTreatmentRepository;
	private PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository;

	public PaviaAndersonMyHeadacheLogDataLoader(
			PatientRepository patientRepository, 
			AttackRepository attackRepository, 
			AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository, 
			AbortiveTreatmentRepository abortiveTreatmentRepository,
			PreventiveTreatmentRepository preventiveTreatmentRepository,
			PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository,
			Logger logger) {
		this.attackRepository = attackRepository;
		this.abortiveTreatmentRepository = abortiveTreatmentRepository;
		this.abortiveTreatmentTypeRepository = abortiveTreatmentTypeRepository;
		
		this.preventiveTreatmentRepository = preventiveTreatmentRepository;
		this.preventiveTreatmentTypeRepository = preventiveTreatmentTypeRepository;
		this.patientRepository = patientRepository;
		patient = this.patientRepository.findByLogin("pavias");
		this.logger = logger;
	}

	@Transactional
	public synchronized void load() throws  IOException, InvalidFormatException {
		if (attacksDataPath == null) {
			throw new IllegalStateException("Attacks data can not be loaded: attacks.pavia.path is not specified");
		}
		if (attackRepository.countByPatient(patient) > 0) {
			logger.warn("Data from Pavia Anderson were already loaded");
			return;
		}

		var workbook = new XSSFWorkbook(new File(attacksDataPath));
		storeAttacks(workbook);
		saveTreatments(workbook);
	}

	private void saveTreatments(XSSFWorkbook workbook) {
		var treatmentSheet = workbook.getSheetAt(1);

		for (int r = 2; r < treatmentSheet.getPhysicalNumberOfRows(); r++) {
			var row = treatmentSheet.getRow(r);
			if (row == null) {
				break;
			}
			int id = (int) row.getCell(TreatmentSheetColumn.ID.ordinal()).getNumericCellValue();
			LocalDateTime start = row.getCell(TreatmentSheetColumn.START.ordinal()).getLocalDateTimeCellValue();
			String treatment = row.getCell(TreatmentSheetColumn.TREATMENT.ordinal()).getStringCellValue();
			Boolean successful = "yes".equalsIgnoreCase(row.getCell(TreatmentSheetColumn.TREATMENT_HELPED.ordinal()).getStringCellValue().trim());
			saveTreatment(id, start, treatment, successful);
		}
	}

	private void storeAttacks(XSSFWorkbook workbook) {
		var attacksSheet = workbook.getSheetAt(0);
		for (int r = 1; r < attacksSheet.getPhysicalNumberOfRows(); r++) {
			var row = attacksSheet.getRow(r);
			if (row == null) {
				break;
			}
			int id = (int) row.getCell(AttacSheetColumn.ID.ordinal()).getNumericCellValue();
			LocalDateTime start = row.getCell(AttacSheetColumn.START.ordinal()).getLocalDateTimeCellValue();
			LocalDateTime end = row.getCell(AttacSheetColumn.END.ordinal()).getLocalDateTimeCellValue();
			if (start.equals(end)) {
				end = end.plusMinutes(1);
			}
			int level = (int) row.getCell(AttacSheetColumn.LEVEL.ordinal()).getNumericCellValue();
			idToStart.put(id, start);
			idToEnd.put(id, end);
			Attack attack = attackRepository.save(new Attack(start, end, patient, level, null, null));
			idToAttack.put(id, attack);
		}
	}

	public void saveTreatment(int attackId, LocalDateTime treatmentStart, String treatment, Boolean successful) {
		LocalDateTime attackEnd = idToEnd.get(attackId);
		Attack attack = idToAttack.get(attackId);

		if (treatmentStart.compareTo(attackEnd) >= 0) {
			logger.debug("treatment started affter attack stopped for attack id={} and treatment={}", attackId, treatment);
			treatmentStart = attackEnd.minusMinutes(5);
		}
		try {
			if (treatment.startsWith("Energidrik")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Caffeine in a drink");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient, attack, treatmentStart, null, type, DEFAULT_COFFIEINE_DRINK_MG, successful, null));
			} else if (treatment.startsWith("Ilt O2 - 10-25l/min - Inhalant")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("100% oxygen via nonrebreathing mask");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient, attack, treatmentStart, attackEnd, type, DEFUALT_O2_LPM, successful, null));
			} else if (treatment.startsWith("ODV - 130L/min - Inhalant/Spray")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("100% oxygen via demand valve");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, attackEnd, type, DEFUALT_O2_DEMAND_VALVE_LPM, successful, null));
			} else if (treatment.startsWith("Cannabis - 1 joint - Inhalant/Spray")) {
				AbortiveTreatmentType tetrahydrocannabinolType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Tetrahydrocannabinol");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, null, tetrahydrocannabinolType, 6, successful, null));
				
				PreventiveTreatmentType preventiveTetrahydrocannabinolType = preventiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Tetrahydrocannabinol");
				preventiveTreatmentRepository.save(new PreventiveTreatment(patient,treatmentStart,null, preventiveTetrahydrocannabinolType, 6, treatment));
				
				AbortiveTreatmentType cannabidiolType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Cannabidiol");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, null, cannabidiolType, 8, successful, null));
				
				PreventiveTreatmentType preventiveCannabidiolType = preventiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Cannabidiol");
				preventiveTreatmentRepository.save(new PreventiveTreatment(patient,treatmentStart, null, preventiveCannabidiolType, 8, treatment));
			} else if (treatment.startsWith("Imigran - 6 mg - Injectable") || treatment.startsWith("Sumavel dose pro - 6mg - Injectable")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Sumatriptan injection");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack,treatmentStart, null, type, DEFAULT_SUMATRIPTAN_INJECTION_MG, successful, null));
			} else if (treatment.startsWith("Sumatriptan") && treatment.contains("Capsule")) {
				int indexOfDozeSeparator = treatment.indexOf("-");
				int dozeMg = Integer.parseInt(treatment.subSequence(indexOfDozeSeparator + 2, indexOfDozeSeparator + 4).toString());
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Sumatriptan pills");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, null,  type, dozeMg, successful, null));
			} else if (treatment.startsWith("Treo")) {
				AbortiveTreatmentType asperinType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Acetylsalicylic");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, null,  asperinType, 500, successful, null));
				AbortiveTreatmentType caffeinePilltype = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Caffeine pills");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, null,  caffeinePilltype, 50, successful, null));
			} else if (treatment.startsWith("Neurostimulans")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("SPG Neurostimulator");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, treatmentStart.plusMinutes(15),  type, 15 * 60, successful, null));
			} else if (treatment.startsWith("Gon blokade - 2 ml + 0,5")) {
				AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Betamethasone");
				abortiveTreatmentRepository.save(new AbortiveTreatment(patient,attack, treatmentStart, treatmentStart.plusMinutes(15),  type, 500, successful, null));
			} else {
				throw new IllegalArgumentException("unnown teatment for attack=" + attackId + " " + treatmentStart + " " + treatment);
			}

		} catch (RuntimeException e) {
			logger.error("eror while handling treatment for attackId={} treatmentStart={} successful={}", attackId, treatmentStart ,successful );
			throw e;
		}

	}

}
