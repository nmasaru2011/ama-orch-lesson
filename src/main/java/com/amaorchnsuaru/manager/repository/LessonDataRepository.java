package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.amaorchnsuaru.manager.entity.LessonData;

public interface LessonDataRepository extends JpaRepository<LessonData, Long> {

    List<LessonData> findByConcertIdOrderByBranchNoAsc(String concertId);

    @Query(value = """
        SELECT * FROM concert_lesson l
        WHERE (:concertId IS NULL OR l.concert_id = :concertId)
        AND (:orchId IS NULL OR EXISTS (
            SELECT 1 FROM concert c
            WHERE c.concert_id = l.concert_id AND c.orch_id = :orchId
        ))
        AND (:year IS NULL OR EXTRACT(YEAR FROM l.lesson_date) = :year)
        AND (:future IS NULL
            OR (:future = 'future' AND l.lesson_date >= CURRENT_DATE)
            OR (:future = 'past'   AND l.lesson_date <  CURRENT_DATE))
        AND l.delete_datetime IS NULL
        ORDER BY l.lesson_date DESC, l.concert_id, l.branch_no
        """,
        countQuery = """
        SELECT COUNT(*) FROM concert_lesson l
        WHERE (:concertId IS NULL OR l.concert_id = :concertId)
        AND (:orchId IS NULL OR EXISTS (
            SELECT 1 FROM concert c
            WHERE c.concert_id = l.concert_id AND c.orch_id = :orchId
        ))
        AND (:year IS NULL OR EXTRACT(YEAR FROM l.lesson_date) = :year)
        AND (:future IS NULL
            OR (:future = 'future' AND l.lesson_date >= CURRENT_DATE)
            OR (:future = 'past'   AND l.lesson_date <  CURRENT_DATE))
        AND l.delete_datetime IS NULL
        """,
        nativeQuery = true)
    Page<LessonData> findByFilters(
            @Param("concertId") String concertId,
            @Param("orchId")        String orchId,
            @Param("year")          Integer year,
            @Param("future")        String future,
            Pageable pageable);

    @Query(value = """
        SELECT * FROM concert_lesson l
        WHERE (:concertId IS NULL OR l.concert_id = :concertId)
        AND (:orchId IS NULL OR EXISTS (
            SELECT 1 FROM concert c
            WHERE c.concert_id = l.concert_id AND c.orch_id = :orchId
        ))
        AND (:year IS NULL OR EXTRACT(YEAR FROM l.lesson_date) = :year)
        AND (:future IS NULL
            OR (:future = 'future' AND l.lesson_date >= CURRENT_DATE)
            OR (:future = 'past'   AND l.lesson_date <  CURRENT_DATE))
        AND l.delete_datetime IS NULL
        ORDER BY l.lesson_date ASC, l.concert_id, l.branch_no
        """,
        nativeQuery = true)
    List<LessonData> findAllByFilters(
            @Param("concertId") String concertId,
            @Param("orchId")        String orchId,
            @Param("year")          Integer year,
            @Param("future")        String future);

    @Query("SELECT MAX(l.branchNo) FROM LessonData l WHERE l.concertId = :concertId")
    Integer findMaxBranchNo(@Param("concertId") String concertId);
}
