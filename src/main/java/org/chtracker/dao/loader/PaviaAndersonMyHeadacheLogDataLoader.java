package org.chtracker.dao.loader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

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

    private static final int NEUROSTIMULATOR_DOZE = 15 * 60;
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
    public synchronized void load() throws IOException, InvalidFormatException {
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
        logger.info("Data was loaded");
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
            saveTreatmentUpdatetingAttackEndIfBeforeStart(id, start, treatment, successful);
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
            byte level = (byte) row.getCell(AttacSheetColumn.LEVEL.ordinal()).getNumericCellValue();
            idToStart.put(id, start);
            idToEnd.put(id, end);
            Attack attack = attackRepository.save(new Attack(start, end, patient, level, null, null));
            idToAttack.put(id, attack);
        }
    }

    public void saveTreatmentUpdatetingAttackEndIfBeforeStart(int attackId, LocalDateTime treatmentStart, String treatment, Boolean successful) {
        LocalDateTime attackEnd = idToEnd.get(attackId);
        Attack attack = idToAttack.get(attackId);

        if (treatmentStart.compareTo(attackEnd) >= 0) {
            logger.warn("Treatment {} started after attack id={} has stopped. ", treatment, attackId);
            treatmentStart = attackEnd.minusMinutes(5);
        }
        try {
            saveTreatment(attackId, treatmentStart, treatment, successful, attackEnd, attack);
        } catch (RuntimeException e) {
            logger.error("eror while handling treatment for attackId={} treatmentStart={} successful={}", attackId, treatmentStart, successful);
            throw e;
        }

    }

    private void saveTreatment(int attackId, LocalDateTime treatmentStart, String treatment, Boolean successful, LocalDateTime attackEnd, Attack attack) {
        if (treatment.startsWith("Energidrik")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Caffeine in a drink");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, type, DEFAULT_COFFIEINE_DRINK_MG, successful));
        } else if (treatment.startsWith("Ilt O2 - 10-25l/min - Inhalant")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("100% oxygen via nonrebreathing mask");
            Optional<AbortiveTreatment> abortiveTreatment = abortiveTreatmentRepository.findByAttackAndStartedAndAbortiveTreatmentType(attack, treatmentStart, type);
            saveAbortiveTreatmentIfNotDuplicate(treatmentStart, successful, attackEnd, attack, type, abortiveTreatment, DEFUALT_O2_LPM);
        } else if (treatment.startsWith("ODV - 130L/min - Inhalant/Spray")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("100% oxygen via demand valve");
            Optional<AbortiveTreatment> abortiveTreatment = abortiveTreatmentRepository.findByAttackAndStartedAndAbortiveTreatmentType(attack, treatmentStart, type);
            saveAbortiveTreatmentIfNotDuplicate(treatmentStart, successful, attackEnd, attack, type, abortiveTreatment, DEFUALT_O2_DEMAND_VALVE_LPM);
        } else if (treatment.startsWith("Cannabis - 1 joint - Inhalant/Spray")) {
            AbortiveTreatmentType tetrahydrocannabinolType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Tetrahydrocannabinol");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, tetrahydrocannabinolType, 6, successful));

            PreventiveTreatmentType preventiveTetrahydrocannabinolType = preventiveTreatmentTypeRepository.findByName("Tetrahydrocannabinol inhalant");
            preventiveTreatmentRepository.save(new PreventiveTreatment(patient, treatmentStart, null, preventiveTetrahydrocannabinolType, 6, treatment));

            AbortiveTreatmentType cannabidiolType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Cannabidiol");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, cannabidiolType, 8, successful));

            PreventiveTreatmentType preventiveCannabidiolType = preventiveTreatmentTypeRepository.findByName("Cannabidiol inhalant");
            preventiveTreatmentRepository.save(new PreventiveTreatment(patient, treatmentStart, null, preventiveCannabidiolType, 8, treatment));
        } else if (treatment.startsWith("Imigran - 6 mg - Injectable") || treatment.startsWith("Sumavel dose pro - 6mg - Injectable")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Sumatriptan injection");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, type, DEFAULT_SUMATRIPTAN_INJECTION_MG, successful));
        } else if (treatment.startsWith("Sumatriptan") && treatment.contains("Capsule")) {
            int indexOfDozeSeparator = treatment.indexOf("-");
            int dozeMg = Integer.parseInt(treatment.subSequence(indexOfDozeSeparator + 2, indexOfDozeSeparator + 4).toString());
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Sumatriptan pills");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, type, dozeMg, successful));
        } else if (treatment.startsWith("Treo")) {
            AbortiveTreatmentType asperinType = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Acetylsalicylic");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, asperinType, 500, successful));
            AbortiveTreatmentType caffeinePilltype = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Caffeine pills");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, null, caffeinePilltype, 50, successful));
        } else if (treatment.startsWith("Neurostimulans")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("SPG Neurostimulator");
            Optional<AbortiveTreatment> abortiveTreatment = abortiveTreatmentRepository.findByAttackAndStartedAndAbortiveTreatmentType(attack, treatmentStart, type);
            saveAbortiveTreatmentIfNotDuplicate(treatmentStart, successful, attackEnd, attack, type, abortiveTreatment, NEUROSTIMULATOR_DOZE);
        } else if (treatment.startsWith("Gon blokade - 2 ml + 0,5")) {
            AbortiveTreatmentType type = abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase("Betamethasone");
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, treatmentStart.plusMinutes(15), type, 500, successful));
        } else {
            throw new IllegalArgumentException("unnown teatment for attack=" + attackId + " " + treatmentStart + " " + treatment);
        }
    }

    private void saveAbortiveTreatmentIfNotDuplicate(
            LocalDateTime treatmentStart,
            Boolean successful,
            LocalDateTime attackEnd,
            Attack attack,
            AbortiveTreatmentType type,
            Optional<AbortiveTreatment> abortiveTreatment, int doze) {
        if (abortiveTreatment.isEmpty()) {
            abortiveTreatmentRepository.save(new AbortiveTreatment(attack, treatmentStart, attackEnd, type, doze, successful));
        } else {
            logger.warn("{}  is already stored", abortiveTreatment.get());
        }
    }

}
