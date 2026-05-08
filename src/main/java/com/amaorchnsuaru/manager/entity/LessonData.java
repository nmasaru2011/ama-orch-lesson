package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_data")
public class LessonData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_main_id", nullable = false, length = 12)
    private String concertMainId;

    @Column(name = "branch_no", nullable = false)
    private Integer branchNo;

    @Column(name = "lesson_date", nullable = false)
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

    public String getConcertMainId() { return concertMainId; }
    public void setConcertMainId(String concertMainId) { this.concertMainId = concertMainId; }

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
