package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amaorchnsuaru.manager.entity.ConcertData;

public interface ConcertDataRepository extends JpaRepository<ConcertData, String> {

    Page<ConcertData> findAllByOrderByConcertDateDescConcertIdAsc(Pageable pageable);

    @Query("SELECT c FROM ConcertData c WHERE (:orchId IS NULL OR c.orchId = :orchId) " +
           "AND (:concertId IS NULL OR c.concertId LIKE CONCAT('%', cast(:concertId as String), '%')) " +
           "AND (:year IS NULL OR c.concertDate LIKE CONCAT(cast(:year as String), '%')) " +
           "AND (:musicId IS NULL OR c.concertId IN (SELECT p.concertId FROM ConcertProgram p WHERE p.musicId = :musicId)) " +
           "ORDER BY c.concertDate DESC, c.concertId ASC")
    Page<ConcertData> findByFilters(@Param("orchId") String orchId,
                                    @Param("concertId") String concertId,
                                    @Param("year") String year,
                                    @Param("musicId") String musicId,
                                    Pageable pageable);

    List<ConcertData> findByOrchIdOrderByConcertDateDesc(String orchId);

    @Query("SELECT c FROM ConcertData c WHERE c.concertSubId = '0' ORDER BY c.concertDate DESC")
    List<ConcertData> findMainConcerts();

    @Query("SELECT c FROM ConcertData c WHERE c.concertSubId = '0' AND (c.concertDate IS NULL OR c.concertDate >= :fromDate) ORDER BY c.concertDate DESC")
    List<ConcertData> findMainConcertsFrom(@Param("fromDate") String fromDate);
}
