package com.amaorchnsuaru.manager.entity;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "concert_member")
@IdClass(ConcertMemberId.class)
public class ConcertMember extends AuditableEntity {

	@Id
	@Column(name = "person_id", nullable = false)
	private Long personId;

	/** concert.concert_id を参照する演奏会ID（要求仕様の列名を維持） */
	@Id
	@Column(name = "oconcert_id", nullable = false, length = 12)
	private String oconcertId;

	@Column(name = "main_active_instrument", length = 32)
	private String mainActiveInstrument;

	@Column(name = "is_member")
	private Boolean isMember = true;

	@Column(name = "is_part_leader")
	private Boolean isPartLeader = false;

	@Column(name = "is_concert_master")
	private Boolean isConcertMaster = false;

	@Column(name = "coordinator", length = 64)
	private String coordinator;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getOconcertId() {
		return oconcertId;
	}

	public void setOconcertId(String oconcertId) {
		this.oconcertId = oconcertId;
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

	public Boolean getIsPartLeader() {
		return isPartLeader;
	}

	public void setIsPartLeader(Boolean isPartLeader) {
		this.isPartLeader = isPartLeader;
	}

	public Boolean getIsConcertMaster() {
		return isConcertMaster;
	}

	public void setIsConcertMaster(Boolean isConcertMaster) {
		this.isConcertMaster = isConcertMaster;
	}

	public String getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
	}
}
