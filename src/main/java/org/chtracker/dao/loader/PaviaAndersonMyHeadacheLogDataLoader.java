package org.chtracker.dao.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.metadata.TreatmentTypeRepository;
import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.profile.PatientRepository;
import org.chtracker.dao.report.AbortiveTreatment;
import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.PreventiveTreatment;
import org.chtracker.dao.report.ReportRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnNotWebApplication
public class PaviaAndersonMyHeadacheLogDataLoader extends AbstractLoader {

	private Map<Integer, LocalDateTime> idToStart = new HashedMap<>();
	private Map<Integer, LocalDateTime> idToEnd = new HashedMap<>();
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

	final ReportRepository reportRepository;
	final TreatmentTypeRepository treatmentRepository;

	private Logger logger;

	public PaviaAndersonMyHeadacheLogDataLoader(TreatmentTypeRepository treatmentRepository, PatientRepository patientRepository, ReportRepository reportRepository, Logger Logger) {
		this.treatmentRepository = treatmentRepository;
		this.patientRepository = patientRepository;
		logger = Logger;
		patient = this.patientRepository.findByLogin("pavias");
		this.reportRepository = reportRepository;
	}

	@Transactional
	public synchronized void load() throws FileNotFoundException, IOException, InvalidFormatException {
		if (attacksDataPath == null) {
			throw new IllegalStateException("Attacks data can not be loaded: attacks.pavia.path is not specified");
		}
		if (reportRepository.getAttackCount(patient)>0) {
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
			Boolean successful = "yes".equals(row.getCell(TreatmentSheetColumn.TREATMENT_HELPED.ordinal()).getStringCellValue().trim().toLowerCase());
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
			reportRepository.save(new Attack(start, end, patient, level, null, null));
		}
	}

	public void saveTreatment(int attackId, LocalDateTime treatmentStart, String treatment, Boolean successful) {
		LocalDateTime attackStart = idToStart.get(attackId);
		LocalDateTime attackEnd = idToEnd.get(attackId);

		if (treatmentStart.compareTo(attackEnd) >= 0) {
			logger.debug("treatment started affter attack stopped for attack id=" + attackId + " and treatment=" + treatment);
			treatmentStart = attackEnd.minusMinutes(5);
		}
		try {
			if (treatment.startsWith("Energidrik")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("Caffeine in a drink");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, type, DEFAULT_COFFIEINE_DRINK_MG, successful, null));
			} else if (treatment.startsWith("Ilt O2 - 10-25l/min - Inhalant")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("100% oxygen via nonrebreathing mask");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, attackEnd, patient, type, DEFUALT_O2_LPM, successful, null));
			} else if (treatment.startsWith("ODV - 130L/min - Inhalant/Spray")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("100% oxygen via demand valve");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, attackEnd, patient, type, DEFUALT_O2_DEMAND_VALVE_LPM, successful, null));
			} else if (treatment.startsWith("Cannabis - 1 joint - Inhalant/Spray")) {
				TreatmentType tetrahydrocannabinolType = treatmentRepository.findByNameContainingIgnoreCase("Tetrahydrocannabinol");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, tetrahydrocannabinolType, 6, successful, null));
				reportRepository.save(new PreventiveTreatment(treatmentStart, patient, tetrahydrocannabinolType, 6, treatment));
				TreatmentType cannabidiolType = treatmentRepository.findByNameContainingIgnoreCase("Cannabidiol");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, cannabidiolType, 8, successful, null));
				reportRepository.save(new PreventiveTreatment(treatmentStart, patient, cannabidiolType, 8, treatment));
			} else if (treatment.startsWith("Imigran - 6 mg - Injectable") || treatment.startsWith("Sumavel dose pro - 6mg - Injectable")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("Sumatriptan injection");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, type, DEFAULT_SUMATRIPTAN_INJECTION_MG, successful, null));
			} else if (treatment.startsWith("Sumatriptan") && treatment.contains("Capsule")) {
				int indexOfDozeSeparator = treatment.indexOf("-");
				int dozeMg = Integer.parseInt(treatment.subSequence(indexOfDozeSeparator + 2, indexOfDozeSeparator + 4).toString());
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("Sumatriptan pills");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, type, dozeMg, successful, null));
			} else if (treatment.startsWith("Treo")) {
				TreatmentType asperinType = treatmentRepository.findByNameContainingIgnoreCase("Acetylsalicylic");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, asperinType, 500, successful, null));
				TreatmentType caffeinePilltype = treatmentRepository.findByNameContainingIgnoreCase("Caffeine pills");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, null, patient, caffeinePilltype, 50, successful, null));
			} else if (treatment.startsWith("Neurostimulans")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("SPG Neurostimulator");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, treatmentStart.plusMinutes(15), patient, type, 15 * 60, successful, null));
				reportRepository.save(new PreventiveTreatment(treatmentStart, patient, type, 15 * 60, treatment));
			} else if (treatment.startsWith("Gon blokade - 2 ml + 0,5")) {
				TreatmentType type = treatmentRepository.findByNameContainingIgnoreCase("Betamethasone");
				reportRepository.save(new AbortiveTreatment(attackStart, treatmentStart, treatmentStart.plusMinutes(15), patient, type, 500, successful, null));
			} else {
				throw new IllegalArgumentException("unnown teatment for attack=" + attackId + " " + treatmentStart + " " + treatment);
			}

		} catch (RuntimeException e) {
			logger.error("eror while handling treatment for attackId=" + attackId + " " + treatmentStart + " " + treatment + " " + successful);
			throw e;
		}

	}

}
