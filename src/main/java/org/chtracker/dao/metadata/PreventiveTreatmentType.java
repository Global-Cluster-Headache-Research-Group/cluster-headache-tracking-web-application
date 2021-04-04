package org.chtracker.dao.metadata;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.chtracker.dao.DataConfiguration;
import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(schema = DataConfiguration.METADA_SCHEMA_NAME)
public class PreventiveTreatmentType extends AbstractTreatmentType{

	private static final long serialVersionUID = 1L;

}
