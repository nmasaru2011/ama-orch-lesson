package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.StageLayoutSeat;

public interface StageLayoutSeatRepository extends JpaRepository<StageLayoutSeat, Long> {

    List<StageLayoutSeat> findByLayoutIdOrderBySeatNoAsc(Long layoutId);

    void deleteByLayoutId(Long layoutId);
}
