package org.matt.budget.models;

import java.io.Serializable;

public interface BaseEntity {

	public <T extends Serializable> T getId();

}
