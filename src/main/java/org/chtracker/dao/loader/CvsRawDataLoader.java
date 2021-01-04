package org.chtracker.dao.loader;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isBlank;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.parse;
import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.ABORTION_TREATMENT;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.COMMENTS;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.DATE;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.DESCRIPTION;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.LASTED;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.LEVEL;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.PREVENTING;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.TIME;
import static org.chtracker.dao.loader.CvsRawDataLoader.CsvColumn.TREATMNT_STATUS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.metadata.TreatmentTypeRepository;
import org.chtracker.dao.report.AbortiveTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CvsRawDataLoader {

	@Autowired
	TreatmentTypeRepository treatmentRepository;

	private static final int PATIENT_ID = 1;
	static final String ABORTIVE_INSERT_SQL = "INSERT INTO report.aborting_treatment_usage (started,stopped,attack_started,patient_id,treatment_id, doze,successful) (?,?,?,?,?,?)";
	static final String PREVENTIVE_TREATMENT_INSERT_SQL = "INSERT INTO report.preventive_treatment_usage (started,patient_id,treatment_id, doze) (?,?,?,?)";
	static final String ATTACK_INSERT_SQL = "INSERT INTO report.attack (started,stopped,patient_id,max_pain_level,comments) VALUES (?,?,?,?,?)";

	enum CsvColumn {
		DATE, TIME, LASTED, LEVEL, DESCRIPTION, ABORTION_TREATMENT, TREATMNT_STATUS, LEFT_SHADOWS, PREVENTING, COMMENTS
	}

//	static class Treatment {
//		final TreatmentType type;
//		final int doze;
//
//		public Treatment(TreatmentType type, int doze) {
//			this.type = type;
//			this.doze = doze;
//		}
//
//		enum TreatmentType {
//			O2(1), SUMATRIPTAN(5), SUMATRIPTAN_(5), CAFFEINE(6, of("COFFEE")), VERAPAMIL(12), VITAMIN_D3(13, of("UVB")), SHROOMS(14);
//
//			private TreatmentType(int id, Optional<String> alternativeName) {
//				this.id = id;
//				this.alternativeName = alternativeName;
//			}
//
//			private TreatmentType(int id) {
//				this(id, empty());
//			}
//
//			int id;
//			Optional<String> alternativeName;
//
//			static TreatmentType byName(String name) {
//				for (TreatmentType treatmentType : TreatmentType.values()) {
//					if (treatmentType.name().toLowerCase().startsWith(name.toLowerCase())) {
//						return treatmentType;
//					} else if (treatmentType.alternativeName.isPresent() && treatmentType.alternativeName.get().toLowerCase().startsWith(name.toLowerCase().substring(0, 2))) {
//						return treatmentType;
//					}
//				}
//				throw new IllegalArgumentException("no sutable treatment found for " + name);
//			}
//		}
//	}

	static int getDoze(TreatmentType treatmentType, String doze) {
		switch (treatmentType.getName()) {
		case "100% O2 via nonrebreathing mask":
			return 15;
		case "Vitamin D3 pills":
		case "Vitamin D3 sun exposure":
		case "Vitamin D3 UVB lamp":
			return 1000;
		case "Sumatriptan pills":
			return Integer.parseInt(doze.split(".*(\\d)+")[1]);
		case "Verapamil":
			return Integer.parseInt(doze.split(".*(\\d)+")[1]);
		case "Caffeine drink, e.g. coffee or redbull":
			return 400;
		case "Psilocybin mushroom":
			return Integer.parseInt(doze.split(".*(\\d)+")[1]);
		case "Lidocaine drops":
			return 4;
		case "Cardio Workout":
		case "Hyperventilation":
			return 150;
		default:
			throw new IllegalArgumentException(doze + " doze is incorrect for type " + treatmentType);
		}
	}

	List<AbortiveTreatment> getAbortiveTreatments(String s) {
		if (isBlank(s))
			return Collections.emptyList();
		String[] treatmentStrings = s.split("\\+|,");
		List<AbortiveTreatment> treatments = new ArrayList<>(treatmentStrings.length);
		for (String treatmentString : treatmentStrings) {
			try {
//				TreatmentType treatmentType = TreatmentType.byName(treatmentString);
//				int doze = getDoze(treatmentType, treatmentString);
//				treatments.add(new AbortiveTreatment( treatmentType, doze));
			} catch (RuntimeException e) {
				System.err.println(e);
			}
		}
		return treatments;
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void load() throws FileNotFoundException, IOException, ParseException {

		DateTimeFormatter dateTimeFormatterWithSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm:ss");
		DateTimeFormatter dateTimeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("M/d/uuuu H:mm");
		try (Reader reader = new FileReader("/home/yilativs/Documents/health/cluster_headaches/attacks.tsv")) {
			for (CSVRecord record : DEFAULT.withDelimiter('\t').parse(reader)) {
				try {
					String date = record.get(DATE.ordinal());
					if (date == null || date.length() < 5 || !date.matches("\\d.*")) {
						continue;
					}
					String dateTimeString = record.get(DATE.ordinal()) + " " + record.get(TIME.ordinal());

					LocalDateTime startDateTime = dateTimeString.split(":").length > 2 ? parse(dateTimeString, dateTimeFormatterWithSeconds) : parse(dateTimeString, dateTimeFormatterWithoutSeconds);
					int lasted = parseInt(record.get(LASTED.ordinal()));
					int level = 2 * parseInt(record.get(LEVEL.ordinal()));
					String painDescription = record.get(DESCRIPTION.ordinal());
//					List<AbortiveTreatment> abortiveTreatments = getTreatments(record.get(ABORTION_TREATMENT.ordinal()));
					String[] treatmentStatusStrings = record.get(TREATMNT_STATUS.ordinal()).split("/");
					Boolean[] treatmentStatuses = new Boolean[treatmentStatusStrings.length];
					for (int i = 0; i < treatmentStatusStrings.length; i++) {
						treatmentStatuses[i] = isBlank(treatmentStatusStrings[i]) ? null : parseBoolean(treatmentStatusStrings[i]);
					}
					String preventingTreatmentsString = record.get(PREVENTING.ordinal());
//					List<AbortiveTreatment> preventingTreatments = getTreatments(record.get(ABORTION_TREATMENT.ordinal()));
					String comments = record.get(COMMENTS.ordinal());
					if (isBlank(comments)) {
						comments = painDescription;
					} else {
						if (!isBlank(painDescription)) {
							comments = painDescription + "; " + comments;
						}
					}
					jdbcTemplate.update(ATTACK_INSERT_SQL, startDateTime, startDateTime.plusMinutes(lasted), PATIENT_ID, level, comments);

					// started,stopped,attack_started,patient_id,treatment_id, doze
//					for (int i = 0; i < abortiveTreatments.size(); i++) {
//						if (abortiveTreatment.type == TreatmentType.O2) {
//							jdbcTemplate.update(ABORTIVE_INSERT_SQL, startDateTime, startDateTime.plusMinutes(lasted), startDateTime, abortiveTreatments.get(i).type.id, abortiveTreatment.doze,
//									treatmentStatus);
//						} else {
//							jdbcTemplate.update(ABORTIVE_INSERT_SQL, startDateTime, startDateTime.plusMinutes(lasted), startDateTime, abortiveTreatments.get(i), abortiveTreatment.doze,
//									treatmentStatus);
//						}
//					}

				} catch (Exception e) {
					System.err.println(record + " " + e.getClass().getName() + " " + e.getMessage());
				}
			}
		}
	}

}
