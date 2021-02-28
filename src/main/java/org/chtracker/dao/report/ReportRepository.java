package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.profile.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ReportRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	static final String ATTACK_INSERT_SQL = "INSERT INTO report.attack (started,stopped,patient_id,max_pain_level,comments) VALUES (?,?,?,?,?) ON CONFLICT DO NOTHING";

	public void save(Attack attack) {
		jdbcTemplate.update(ATTACK_INSERT_SQL, attack.getStarted(), attack.getStopped(), attack.getPatient().getId(), attack.getMaxPainLevel(), attack.getComments());
	}

	static final String ABORTIVE_INSERT_SQL = "INSERT INTO report.abortive_treatment (attack_started,started,stopped,patient_id,treatment_type_id, doze,successful,comments) VALUES(?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";

	public void save(AbortiveTreatment treatment) {
		jdbcTemplate.update(ABORTIVE_INSERT_SQL, treatment.getAttackStarted(), treatment.getStarted(), treatment.getStopped(), treatment.getPatient().getId(), treatment.getTreatmentType().getId(),
				treatment.getDoze(), treatment.getSuccessful(), treatment.getComments());
	}

	static final String PREVIOUS_PREVENTIVE_TREATMENT_SELECT_SQL = "SELECT started FROM report.preventive_treatment WHERE treatment_type_id=? AND patient_id=? ORDER BY started DESC LIMIT 1";

	public Optional<LocalDateTime> getPreviousPreventiveTreatmentUsageStart(TreatmentType treatmentType, LocalDateTime currentTreatmentDateTime, Patient patient) {
		List<LocalDateTime> results = jdbcTemplate.queryForList(PREVIOUS_PREVENTIVE_TREATMENT_SELECT_SQL, LocalDateTime.class, treatmentType.getId(), patient.getId());
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	static final String PREVENTIVE_TREATMENT_INSERT_SQL = "INSERT INTO report.preventive_treatment (started,patient_id,treatment_type_id, doze) VALUES(?,?,?,?) ON CONFLICT DO NOTHING";

	public void save(PreventiveTreatment treatment) {
		jdbcTemplate.update(PREVENTIVE_TREATMENT_INSERT_SQL, treatment.getStarted(), treatment.getPatient().getId(), treatment.getTreatmentType().getId(), treatment.getDoze());
	}

	static final String ATTACK_COUNT_SQL = "SELECT count(*) from report.attack where patient_id=?";

	public int getAttackCount(Patient patient) {
		return jdbcTemplate.queryForObject(ATTACK_COUNT_SQL, Integer.class, patient.getId());
	}

}
