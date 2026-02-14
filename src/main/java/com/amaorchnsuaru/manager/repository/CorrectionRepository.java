package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.Correction;

public interface CorrectionRepository extends JpaRepository<Correction, Long> {
	List<Correction> findAllByOrderBySortOrderAsc();
}
