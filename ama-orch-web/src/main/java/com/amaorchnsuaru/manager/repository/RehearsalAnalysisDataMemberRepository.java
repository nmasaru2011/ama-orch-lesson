package com.amaorchnsuaru.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.RehearsalAnalysisDataMember;
import com.amaorchnsuaru.manager.entity.RehearsalAnalysisDataMemberId;

public interface RehearsalAnalysisDataMemberRepository
		extends JpaRepository<RehearsalAnalysisDataMember, RehearsalAnalysisDataMemberId> {
	void deleteByAnalysisId(String analysisId);
}
