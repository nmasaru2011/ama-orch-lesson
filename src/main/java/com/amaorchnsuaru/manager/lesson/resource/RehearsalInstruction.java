package com.amaorchnsuaru.manager.lesson.resource;

import java.util.List;

public class RehearsalInstruction {

	private String timeStr;
	private long totalSeconds;
	private List<String> measures;
	private List<String> instruments;
	private String text;
	private String youtubeLink;

	public RehearsalInstruction() {}

	public RehearsalInstruction(String timeStr, long totalSeconds, List<String> measures,
			List<String> instruments, String text, String youtubeLink) {
		this.timeStr = timeStr;
		this.totalSeconds = totalSeconds;
		this.measures = measures;
		this.instruments = instruments;
		this.text = text;
		this.youtubeLink = youtubeLink;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public long getTotalSeconds() {
		return totalSeconds;
	}

	public void setTotalSeconds(long totalSeconds) {
		this.totalSeconds = totalSeconds;
	}

	public List<String> getMeasures() {
		return measures;
	}

	public void setMeasures(List<String> measures) {
		this.measures = measures;
	}

	public List<String> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<String> instruments) {
		this.instruments = instruments;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getYoutubeLink() {
		return youtubeLink;
	}

	public void setYoutubeLink(String youtubeLink) {
		this.youtubeLink = youtubeLink;
	}

	public String getMeasuresJoined() {
		return measures != null && !measures.isEmpty() ? String.join(", ", measures) : "-";
	}

	public String getInstrumentsJoined() {
		return instruments != null && !instruments.isEmpty() ? String.join(", ", instruments) : "-";
	}
}
