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
           "AND (:year IS NULL OR c.concertDate LIKE CONCAT(:year, '%')) " +
           "ORDER BY c.concertDate DESC, c.concertId ASC")
    Page<ConcertData> findByFilters(@Param("orchId") String orchId,
                                    @Param("year") String year,
                                    Pageable pageable);

    List<ConcertData> findByOrchIdOrderByConcertDateDesc(String orchId);

    java.util.Optional<ConcertData> findFirstByConcertMainIdOrderByConcertDateDesc(String concertMainId);

    @Query("SELECT c FROM ConcertData c WHERE c.concertSubId = '0' ORDER BY c.concertDate DESC")
    List<ConcertData> findMainConcerts();
}
