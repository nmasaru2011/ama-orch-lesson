package com.amaorchnsuaru.manager.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rehea_analysis_data")
public class RehearsalAnalysisData {

	@Id
	@Column(name = "analysis_id", length = 64)
	private String analysisId;

	@Column(name = "lesson_id")
	private Long lessonId;

	@Column(name = "concert_id", length = 12)
	private String concertId;

	@Column(name = "branch_no")
	private Integer branchNo;

	@Column(name = "lesson_date")
	private LocalDate lessonDate;

	@Column(name = "download_datetime")
	private LocalDateTime downloadDatetime;

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public Long getLessonId() {
		return lessonId;
	}

	public void setLessonId(Long lessonId) {
		this.lessonId = lessonId;
	}

	public String getConcertId() {
		return concertId;
	}

	public void setConcertId(String concertId) {
		this.concertId = concertId;
	}

	public Integer getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(Integer branchNo) {
		this.branchNo = branchNo;
	}

	public LocalDate getLessonDate() {
		return lessonDate;
	}

	public void setLessonDate(LocalDate lessonDate) {
		this.lessonDate = lessonDate;
	}

	public LocalDateTime getDownloadDatetime() {
		return downloadDatetime;
	}

	public void setDownloadDatetime(LocalDateTime downloadDatetime) {
		this.downloadDatetime = downloadDatetime;
	}
}
