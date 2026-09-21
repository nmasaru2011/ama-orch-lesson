package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amaorchnsuaru.manager.entity.ConcertProgram;
import com.amaorchnsuaru.manager.entity.ConcertProgramId;

public interface ConcertProgramRepository extends JpaRepository<ConcertProgram, ConcertProgramId> {

    List<ConcertProgram> findByConcertIdOrderByProgramNoAsc(String concertId);

    @Query("SELECT COALESCE(MAX(p.programNo), 0) FROM ConcertProgram p WHERE p.concertId = :concertId")
    Integer findMaxProgramNo(@Param("concertId") String concertId);

    void deleteByConcertIdAndProgramNo(String concertId, Integer programNo);

    List<ConcertProgram> findByLayoutId(Long layoutId);

    /** 配置ID -> その配置を使っている曲数 */
    @Query("SELECT p.layoutId, COUNT(p) FROM ConcertProgram p WHERE p.layoutId IS NOT NULL GROUP BY p.layoutId")
    List<Object[]> countProgramsByLayout();
}
