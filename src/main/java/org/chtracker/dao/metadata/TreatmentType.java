package org.chtracker.dao.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("metadata.treatment_type")
public class TreatmentType {

	@Id
	private final int id;
	private final String name;
	private final String units;
	private final String tradeName;

	public TreatmentType(int id, String name, String units, String tradeName) {
		this.id = id;
		this.name = name;
		this.units = units;
		this.tradeName = tradeName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUnits() {
		return units;
	}

	public String getTradeName() {
		return tradeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreatmentType other = (TreatmentType) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Treatment [id=" + id + ", name=" + name + ", units=" + units + "]";
	}

}
