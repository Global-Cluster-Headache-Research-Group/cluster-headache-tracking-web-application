package org.chtracker.dao.loader;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.parse;
import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.ABORTION_TREATMENT;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.COMMENTS;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.DATE;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.DESCRIPTION;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.LASTED;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.LEVEL;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.TIME;
import static org.chtracker.dao.loader.YilativsCvsDataLoader.CsvColumn.TREATMNT_STATUS;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.csv.CSVRecord;
import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.metadata.AbortiveTreatmentTypeRepository;
import org.chtracker.dao.metadata.AbstractTreatmentType;
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
import org.springframework.util.StringUtils;

@Service
@ConditionalOnNotWebApplication
public class YilativsCvsDataLoader extends DataLoader {

    private static final String TREATMENT_SPLIT_PATTERN = "[\\+,]";

    private static final String OXYGEN_VIA_NONREBREATHING_MASK = "100% oxygen via nonrebreathing mask";

    private static final String EMPTY_STRING = "";

    private static final String LOWCASE_SYMBOL_PATTERN = "[a-z]";

    @Value("${attacks.yilativs.path:data/yilativs-attacks.tsv}")
    String attacksDataPath;

    static final DateTimeFormatter dateTimeFormatterWithSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm:ss");
    static final DateTimeFormatter dateTimeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm");

    enum CsvColumn {
        DATE, TIME, LASTED, LEVEL, DESCRIPTION, ABORTION_TREATMENT, TREATMNT_STATUS, LEFT_SHADOWS, PREVENTIVE_TREATMENT, COMMENTS
    }

    final AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository;
    final PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository;

    final PatientRepository patientRepository;

    final Patient patient;

    final Logger logger;

    final AttackRepository attackRepository;

    private PreventiveTreatmentRepository preventiveTreatmentRepository;

    private AbortiveTreatmentRepository abortiveTreatmentRepository;

    public YilativsCvsDataLoader(
            AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository,
            PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository,
            PatientRepository patientRepository,
            AttackRepository attackRepository,
            AbortiveTreatmentRepository abortiveTreatmentRepository,
            PreventiveTreatmentRepository preventiveTreatmentRepository,
            Logger logger) {
        this.abortiveTreatmentTypeRepository = abortiveTreatmentTypeRepository;
        this.preventiveTreatmentTypeRepository = preventiveTreatmentTypeRepository;
        this.patientRepository = patientRepository;
        this.attackRepository = attackRepository;
        this.abortiveTreatmentRepository = abortiveTreatmentRepository;
        this.preventiveTreatmentRepository = preventiveTreatmentRepository;
        this.logger = logger;
        patient = this.patientRepository.findByLogin("yilativs");
    }

    @Transactional
    public void load() throws IOException {
        if (attacksDataPath == null) {
            throw new IllegalStateException("Attacks data can not be loaded: attacks.yilativs.path is not specified");
        }
        if (attackRepository.countByPatient(patient) > 0) {
            logger.warn("Data from Yilativs were already loaded");
            return;
        }
        try (Reader reader = new FileReader(attacksDataPath)) {
            for (CSVRecord record : DEFAULT.withIgnoreEmptyLines().withDelimiter('\t').parse(reader)) {
                try {
                    String date = record.get(DATE.ordinal());
                    if (date == null || date.length() < 5 || !date.matches("\\d.*")) {
                        continue;
                    }
                    String dateTimeString = record.get(DATE.ordinal()) + " " + record.get(TIME.ordinal());

                    LocalDateTime startDateTime = dateTimeString.split(":").length > 2 ? parse(dateTimeString, dateTimeFormatterWithSeconds) : parse(dateTimeString, dateTimeFormatterWithoutSeconds);
                    int lasted = parseInt(record.get(LASTED.ordinal()));
                    byte level = (byte) (2 * parseInt(record.get(LEVEL.ordinal())));

                    String comments = getComments(record);
                    Attack attack = new Attack(startDateTime, startDateTime.plusMinutes(lasted), patient, level, null, comments);
                    attackRepository.save(attack);
                    saveAbortiveTreatments(attack, record.get(ABORTION_TREATMENT.ordinal()), startDateTime, startDateTime.plusMinutes(lasted), record.get(TREATMNT_STATUS.ordinal()));
                    savePreventiveTreatments(record.get(CsvColumn.PREVENTIVE_TREATMENT.ordinal()), attack);
                } catch (RuntimeException e) {
                    throw new IllegalStateException("failed to handle record:" + record + " failed because of " + e.getMessage(), e);
                }
            }
        }
        logger.info("Data was loaded");
    }

    private String getComments(CSVRecord record) {
        String painDescription = record.get(DESCRIPTION.ordinal());
        String comments = record.get(COMMENTS.ordinal());
        if (StringUtils.hasText(comments)) {
            if (StringUtils.hasText(painDescription)) {
                comments = painDescription + "; " + comments;
            }
        } else {
            comments = painDescription;
        }
        return comments;
    }

    private void savePreventiveTreatments(String preventiveTreatmentString, Attack attack) {
        if (StringUtils.hasText(preventiveTreatmentString)) {
            String[] treatmentStrings = preventiveTreatmentString.split(TREATMENT_SPLIT_PATTERN);
            for (String treatmentString : treatmentStrings) {
                try {
                    savePreventiveTreatment(preventiveTreatmentString, attack, treatmentString);
                } catch (RuntimeException e) {
                    logger.error(preventiveTreatmentString + " " + treatmentString, e);
                }
            }
        }
    }

    private void savePreventiveTreatment(String preventiveTreatmentString, Attack attack, String treatmentString) {
        String treatmentTypeSearchString = treatmentString.split(" \\d")[0].trim();
        treatmentTypeSearchString = getPeventimTreatmentTypeSynonymOrSelf(treatmentTypeSearchString);

        Optional<PreventiveTreatmentType> preventiveTreatmentTypeOptional = preventiveTreatmentTypeRepository.findByNameContainingIgnoreCase(treatmentTypeSearchString);
        if (preventiveTreatmentTypeOptional.isEmpty()) {
            logger.warn("no preventive treatment type found for : {}", treatmentTypeSearchString);
            return;
        }
        PreventiveTreatmentType preventiveTreatmentType = preventiveTreatmentTypeOptional.get();
        String comments = excludeMeaninglessComments(preventiveTreatmentString);

        int doze = getDoze(preventiveTreatmentType, treatmentString);
        LocalDate startDate = attack.getStarted().toLocalDate();
        Optional<PreventiveTreatment> previouspreventiveTreatmentOptional = preventiveTreatmentRepository.findFirstByPatientAndPreventiveTreatmentTypeAndStartedLessThanOrderByStartedDesc(patient,
                preventiveTreatmentType, attack.getStarted());
        if (previouspreventiveTreatmentOptional.isEmpty()) {
            preventiveTreatmentRepository.save(new PreventiveTreatment(patient, attack.getStarted(), null, preventiveTreatmentType, doze, comments));
        } else {
            LocalDateTime previousUsageDate = previouspreventiveTreatmentOptional.get().getStarted();
            if (!previousUsageDate.toLocalDate().equals(startDate)) {
                preventiveTreatmentRepository.save(new PreventiveTreatment(patient, attack.getStarted(), null, preventiveTreatmentType, doze, comments));
                long days = ChronoUnit.DAYS.between(previousUsageDate.toLocalDate(), startDate);
                if (days < 30) {
                    int i = 1;
                    while (previousUsageDate.plusDays(i).toLocalDate().compareTo(startDate) < 0) {
                        preventiveTreatmentRepository.save(new PreventiveTreatment(patient, previousUsageDate.plusDays(i++), null, preventiveTreatmentType, doze, comments));
                    }
                }

            }
        }
    }

    private String excludeMeaninglessComments(String preventiveTreatmentString) {
        if (preventiveTreatmentString.toUpperCase().contains("LSA"))
            return "shrooms";

        return preventiveTreatmentString.toUpperCase().trim().contains("VERAPAMIL")
                || preventiveTreatmentString.toUpperCase().trim().contains("INDOMETHACIN")
                || preventiveTreatmentString.toUpperCase().trim().contains("TRIAMCINOLONE")
                        ? null
                        : preventiveTreatmentString.trim().toUpperCase();
    }

    private String getPeventimTreatmentTypeSynonymOrSelf(String treatmentTypeSearchString) {
        String upperCasedSearchString = treatmentTypeSearchString.toUpperCase();
        if (upperCasedSearchString.contains("COD LIVER")
                || upperCasedSearchString.contains("UVB")
                || upperCasedSearchString.contains("D3 PILLS")) {
            return "Cholecalciferol";
        }
        return treatmentTypeSearchString;
    }

    static int getDoze(AbstractTreatmentType abstractTreatmentType, String doze) {
        switch (abstractTreatmentType.getName()) {
        case "Indomethacin":
            return Integer.parseInt(doze.toLowerCase().replaceAll(LOWCASE_SYMBOL_PATTERN, EMPTY_STRING).trim());
        case "Triamcinolone injection":
            return 20;
        case OXYGEN_VIA_NONREBREATHING_MASK:
            return DEFUALT_O2_LPM;
        case "Cholecalciferol":
            return DEFAULT_VITAMIN_D3_DOZE_UI;
        case "Sumatriptan pills":
            return Integer.parseInt(doze.toLowerCase().replaceAll(LOWCASE_SYMBOL_PATTERN, EMPTY_STRING).trim());
        case "Sumatriptan nasal spray":
            return Integer.parseInt(doze.toLowerCase().replaceAll(LOWCASE_SYMBOL_PATTERN, EMPTY_STRING).trim());
        case "Verapamil":
            return Integer.parseInt(doze.toLowerCase().replaceAll(LOWCASE_SYMBOL_PATTERN, EMPTY_STRING).trim());
        case "Caffeine in a drink e.g. coffee, redbull or pepsi":
            return DEFAULT_COFFIEINE_DRINK_MG;
        case "LSA":
            return Integer.parseInt(doze.toLowerCase().replaceAll(LOWCASE_SYMBOL_PATTERN, EMPTY_STRING).trim()) * 10 / 2;
        case "Lidocaine drops 4%":
            return 4;
        case "Cardio Workout":
        case "Hyperventilation":
            return DEFAULT_BPM;
        default:
            throw new IllegalArgumentException(doze + " doze is incorrect for type " + abstractTreatmentType);
        }
    }

    void saveAbortiveTreatments(Attack attack, String treatmentsString, LocalDateTime started, LocalDateTime stopped, String statusesString) {
        if (StringUtils.hasText(treatmentsString)) {
            String[] treatmentStrings = treatmentsString.split(TREATMENT_SPLIT_PATTERN);
            String[] statusStrings = statusesString.split("/");
            Boolean[] statuses = new Boolean[statusStrings.length];
            for (int i = 0; i < statusStrings.length; i++) {
                statuses[i] = StringUtils.hasText(statusStrings[i]) ? parseBoolean(statusStrings[i]) : null;
            }
            Boolean status = null;
            int i = 0;
            for (String treatmentString : treatmentStrings) {
                status = i < statuses.length ? statuses[i] : status;
                i++;
                saveAbortiveTreatment(attack, treatmentsString, started, stopped, status, treatmentString);
            }
        }

    }

    private void saveAbortiveTreatment(Attack attack, String treatmentsString, LocalDateTime started, LocalDateTime stopped, Boolean status, String treatmentString) {
        try {
            if (treatmentsString.trim().length() == 0) {
                return;
            }
            String treatmentName = treatmentString.split("\\d")[0].trim();

            AbortiveTreatmentType abstractTreatmentType = treatmentName.toLowerCase().contains("oxygen")
                    ? abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase(OXYGEN_VIA_NONREBREATHING_MASK)
                    : abortiveTreatmentTypeRepository.findByNameContainingIgnoreCase(treatmentName);
            if (abstractTreatmentType == null)
                return;
            int doze = getDoze(abstractTreatmentType, treatmentString);
            if (abstractTreatmentType.getName().equals(OXYGEN_VIA_NONREBREATHING_MASK) || abstractTreatmentType.getName().equals("Cardio Workout")
                    || abstractTreatmentType.getName().equals("Hyperventilation")) {
                abortiveTreatmentRepository.save(new AbortiveTreatment(attack, started, stopped, abstractTreatmentType, doze, status));
            } else {
                abortiveTreatmentRepository.save(new AbortiveTreatment(attack, started, null, abstractTreatmentType, doze, status));
            }
        } catch (RuntimeException e) {
            logger.error("failed to save treatment string" + treatmentsString, e);
        }
    }

}
