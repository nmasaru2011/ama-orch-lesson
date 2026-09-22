package com.amaorchnsuaru.manager.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "orch_member")
@IdClass(OrchMemberId.class)
public class OrchMember extends AuditableEntity {

	@Id
	@Column(name = "person_id", nullable = false)
	private Long personId;

	@Id
	@Column(name = "orch_id", nullable = false, length = 6)
	private String orchId;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = LocalDate.of(1900, 1, 1);

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "main_active_instrument", length = 32)
	private String mainActiveInstrument;

	@Column(name = "is_member")
	private Boolean isMember = true;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getOrchId() {
		return orchId;
	}

	public void setOrchId(String orchId) {
		this.orchId = orchId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getMainActiveInstrument() {
		return mainActiveInstrument;
	}

	public void setMainActiveInstrument(String mainActiveInstrument) {
		this.mainActiveInstrument = mainActiveInstrument;
	}

	public Boolean getIsMember() {
		return isMember;
	}

	public void setIsMember(Boolean isMember) {
		this.isMember = isMember;
	}
}
