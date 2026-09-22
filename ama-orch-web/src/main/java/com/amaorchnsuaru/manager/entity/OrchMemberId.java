package com.amaorchnsuaru.manager.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrchMemberId implements Serializable {

	private Long personId;
	private String orchId;

	public OrchMemberId() {}

	public OrchMemberId(Long personId, String orchId) {
		this.personId = personId;
		this.orchId = orchId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrchMemberId that))
			return false;
		return Objects.equals(personId, that.personId) && Objects.equals(orchId, that.orchId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personId, orchId);
	}
}
