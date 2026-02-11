package com.amaorcnsuaru.lesson.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorcnsuaru.lesson.entity.Correction;

public interface CorrectionRepository extends JpaRepository<Correction, Long> {
	List<Correction> findAllByOrderBySortOrderAsc();
}
