package com.amaorchnsuaru.manager.entity;

import java.io.Serializable;
import java.util.Objects;

public class ConcertMemberId implements Serializable {

	private Long personId;
	private String oconcertId;

	public ConcertMemberId() {}

	public ConcertMemberId(Long personId, String oconcertId) {
		this.personId = personId;
		this.oconcertId = oconcertId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ConcertMemberId that))
			return false;
		return Objects.equals(personId, that.personId)
				&& Objects.equals(oconcertId, that.oconcertId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personId, oconcertId);
	}
}
