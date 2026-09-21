package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "concert_lesson")
public class LessonData extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id", nullable = false, length = 12)
    private String concertId;

    @Column(name = "branch_no", nullable = false)
    private Integer branchNo;

    @Column(name = "lesson_date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lessonDate;

    @Column(name = "lesson_start_time_str", length = 32)
    private String lessonStartTimeStr;

    @Column(name = "lesson_end_time_str", length = 32)
    private String lessonEndTimeStr;

    @Column(name = "lesson_start_time")
    private LocalDateTime lessonStartTime;

    @Column(name = "lesson_end_time")
    private LocalDateTime lessonEndTime;

    @Column(name = "place_name", length = 64)
    private String placeName;

    @Column(name = "contain", length = 256)
    private String contain;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConcertId() { return concertId; }
    public void setConcertId(String concertId) { this.concertId = concertId; }

    public Integer getBranchNo() { return branchNo; }
    public void setBranchNo(Integer branchNo) { this.branchNo = branchNo; }

    public LocalDate getLessonDate() { return lessonDate; }
    public void setLessonDate(LocalDate lessonDate) { this.lessonDate = lessonDate; }

    public String getLessonStartTimeStr() { return lessonStartTimeStr; }
    public void setLessonStartTimeStr(String lessonStartTimeStr) { this.lessonStartTimeStr = lessonStartTimeStr; }

    public String getLessonEndTimeStr() { return lessonEndTimeStr; }
    public void setLessonEndTimeStr(String lessonEndTimeStr) { this.lessonEndTimeStr = lessonEndTimeStr; }

    public LocalDateTime getLessonStartTime() { return lessonStartTime; }
    public void setLessonStartTime(LocalDateTime lessonStartTime) { this.lessonStartTime = lessonStartTime; }

    public LocalDateTime getLessonEndTime() { return lessonEndTime; }
    public void setLessonEndTime(LocalDateTime lessonEndTime) { this.lessonEndTime = lessonEndTime; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getContain() { return contain; }
    public void setContain(String contain) { this.contain = contain; }
}
