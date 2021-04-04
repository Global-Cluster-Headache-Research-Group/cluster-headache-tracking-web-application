package org.chtracker.dao.metadata;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractTreatmentType implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	private String name;
	private String units;
	private String tradeName;

	AbstractTreatmentType(){
		//needed for Hibernate (we can use private, but it will trigger Unused constructor warning
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
		AbstractTreatmentType other = (AbstractTreatmentType) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "Treatment [id=" + id + ", name=" + name + ", units=" + units + "]";
	}

}
