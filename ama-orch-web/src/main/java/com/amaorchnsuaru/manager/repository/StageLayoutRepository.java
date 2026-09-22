package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.StageLayout;

public interface StageLayoutRepository extends JpaRepository<StageLayout, Long> {

    List<StageLayout> findAllByOrderByLayoutNameAsc();
}
