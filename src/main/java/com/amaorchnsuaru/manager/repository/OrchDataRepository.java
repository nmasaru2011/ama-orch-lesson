package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.OrchData;

public interface OrchDataRepository extends JpaRepository<OrchData, String> {
    List<OrchData> findAllByOrderByOrchIdAsc();
}
