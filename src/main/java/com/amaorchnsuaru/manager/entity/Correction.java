package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "correction")
public class Correction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "wrong_text", nullable = false)
	private String wrongText;

	@Column(name = "correct_text", nullable = false)
	private String correctText;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getWrongText() {
		return wrongText;
	}

	public void setWrongText(String wrongText) {
		this.wrongText = wrongText;
	}

	public String getCorrectText() {
		return correctText;
	}

	public void setCorrectText(String correctText) {
		this.correctText = correctText;
	}
}
