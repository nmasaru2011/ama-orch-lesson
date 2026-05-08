package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amaorchnsuaru.manager.entity.LessonData;

public interface LessonDataRepository extends JpaRepository<LessonData, Long> {

    List<LessonData> findByConcertMainIdOrderByBranchNoAsc(String concertMainId);

    @Query(value = """
        SELECT * FROM lesson_data l
        WHERE (:concertMainId IS NULL OR l.concert_main_id = :concertMainId)
        AND (:orchId IS NULL OR EXISTS (
            SELECT 1 FROM concert_data c
            WHERE c.concert_main_id = l.concert_main_id AND c.orch_id = :orchId
        ))
        AND (:year IS NULL OR YEAR(l.lesson_date) = :year)
        AND (:future IS NULL
            OR (:future = 'future' AND l.lesson_date >= CURRENT_DATE)
            OR (:future = 'past'   AND l.lesson_date <  CURRENT_DATE))
        ORDER BY l.lesson_date DESC, l.concert_main_id, l.branch_no
        """,
        countQuery = """
        SELECT COUNT(*) FROM lesson_data l
        WHERE (:concertMainId IS NULL OR l.concert_main_id = :concertMainId)
        AND (:orchId IS NULL OR EXISTS (
            SELECT 1 FROM concert_data c
            WHERE c.concert_main_id = l.concert_main_id AND c.orch_id = :orchId
        ))
        AND (:year IS NULL OR YEAR(l.lesson_date) = :year)
        AND (:future IS NULL
            OR (:future = 'future' AND l.lesson_date >= CURRENT_DATE)
            OR (:future = 'past'   AND l.lesson_date <  CURRENT_DATE))
        """,
        nativeQuery = true)
    Page<LessonData> findByFilters(
            @Param("concertMainId") String concertMainId,
            @Param("orchId")        String orchId,
            @Param("year")          Integer year,
            @Param("future")        String future,
            Pageable pageable);

    @Query("SELECT MAX(l.branchNo) FROM LessonData l WHERE l.concertMainId = :concertMainId")
    Integer findMaxBranchNo(@Param("concertMainId") String concertMainId);
}
