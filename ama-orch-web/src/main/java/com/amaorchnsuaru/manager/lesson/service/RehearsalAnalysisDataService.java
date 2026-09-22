package com.amaorchnsuaru.manager.lesson.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaorchnsuaru.manager.entity.RehearsalAnalysisData;
import com.amaorchnsuaru.manager.entity.RehearsalAnalysisDataMember;
import com.amaorchnsuaru.manager.repository.RehearsalAnalysisDataMemberRepository;
import com.amaorchnsuaru.manager.repository.RehearsalAnalysisDataRepository;
import com.amaorchnsuaru.manager.lesson.resource.RehearsalInstruction;

@Service
public class RehearsalAnalysisDataService {

	private final RehearsalAnalysisDataRepository dataRepository;
	private final RehearsalAnalysisDataMemberRepository memberRepository;

	public RehearsalAnalysisDataService(RehearsalAnalysisDataRepository dataRepository,
			RehearsalAnalysisDataMemberRepository memberRepository) {
		this.dataRepository = dataRepository;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void save(String analysisId, List<RehearsalInstruction> instructions, Long lessonId,
			String concertId, Integer branchNo, LocalDate lessonDate) {
		RehearsalAnalysisData data = new RehearsalAnalysisData();
		data.setAnalysisId(analysisId);
		data.setLessonId(lessonId);
		data.setConcertId(concertId);
		data.setBranchNo(branchNo);
		data.setLessonDate(lessonDate);
		data.setDownloadDatetime(LocalDateTime.now());
		dataRepository.save(data);

		memberRepository.deleteByAnalysisId(analysisId);
		for (Map.Entry<Long, List<RehearsalInstruction>> entry : groupByLessonAt(instructions)
				.entrySet()) {
			List<RehearsalInstruction> sameTime = entry.getValue();
			RehearsalAnalysisDataMember member = new RehearsalAnalysisDataMember();
			member.setAnalysisId(analysisId);
			member.setLessonAt(entry.getKey());
			member.setMeasure(joinValues(
					sameTime.stream().flatMap(instruction -> instruction.getMeasures().stream())
							.distinct().toList()));
			member.setInstrument(joinValues(
					sameTime.stream().flatMap(instruction -> instruction.getInstruments().stream())
							.distinct().toList()));
			member.setInstruction(joinValues(
					sameTime.stream().map(RehearsalInstruction::getText).distinct().toList()));
			memberRepository.save(member);
		}
	}

	public String hash(String source) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(source.getBytes(StandardCharsets.UTF_8));
			StringBuilder result = new StringBuilder(digest.length * 2);
			for (byte value : digest) {
				result.append(String.format("%02x", value));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256が利用できません", e);
		}
	}

	private Map<Long, List<RehearsalInstruction>> groupByLessonAt(
			List<RehearsalInstruction> instructions) {
		return instructions.stream().collect(Collectors.groupingBy(
				RehearsalInstruction::getTotalSeconds, LinkedHashMap::new, Collectors.toList()));
	}

	private String joinValues(List<String> values) {
		return values.stream().filter(value -> value != null && !value.isBlank())
				.collect(Collectors.joining(", "));
	}
}
