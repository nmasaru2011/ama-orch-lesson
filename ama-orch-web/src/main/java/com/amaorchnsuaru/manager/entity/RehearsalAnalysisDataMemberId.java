package com.amaorchnsuaru.manager.entity;

import java.io.Serializable;
import java.util.Objects;

public class RehearsalAnalysisDataMemberId implements Serializable {
	private String analysisId;
	private Long lessonAt;

	public RehearsalAnalysisDataMemberId() {}

	public RehearsalAnalysisDataMemberId(String analysisId, Long lessonAt) {
		this.analysisId = analysisId;
		this.lessonAt = lessonAt;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RehearsalAnalysisDataMemberId other))
			return false;
		return Objects.equals(analysisId, other.analysisId)
				&& Objects.equals(lessonAt, other.lessonAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(analysisId, lessonAt);
	}
}
