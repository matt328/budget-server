package org.matt.budget.persistence;

import java.io.Serializable;

public interface BaseEntity {

	public <T extends Serializable> T getId();

}
