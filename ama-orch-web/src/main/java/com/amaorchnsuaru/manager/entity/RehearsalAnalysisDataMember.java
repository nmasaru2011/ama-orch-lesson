package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "rehea_analysis_data_member")
@IdClass(RehearsalAnalysisDataMemberId.class)
public class RehearsalAnalysisDataMember {

	@Id
	@Column(name = "analysis_id", length = 64)
	private String analysisId;

	@Id
	@Column(name = "lesson_at")
	private Long lessonAt;

	@Column(name = "measure", length = 256)
	private String measure;

	@Column(name = "instrument", length = 256)
	private String instrument;

	@Column(name = "instruction", columnDefinition = "TEXT")
	private String instruction;

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public Long getLessonAt() {
		return lessonAt;
	}

	public void setLessonAt(Long lessonAt) {
		this.lessonAt = lessonAt;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
}
