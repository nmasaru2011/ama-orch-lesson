package com.amaorchnsuaru.manager.entity;


import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 舞台配置マスタ。
 * concert_program.layout_id から参照され、実際の人の座席は
 * 子テーブル stage_layout_seat（{@link StageLayoutSeat}）が持つ。
 */
@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "stage_layout")
public class StageLayout extends AuditableEntity {

    /** 配置ID（concert_program.layout_id に入るキー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id")
    private Long layoutId;

    @Column(name = "layout_name", nullable = false, length = 64)
    private String layoutName;

    /** 舞台の幅（下手－上手方向の論理サイズ） */
    @Column(name = "stage_width", nullable = false)
    private Integer stageWidth = 1200;

    /** 舞台の奥行（舞台奥－客席方向の論理サイズ） */
    @Column(name = "stage_depth", nullable = false)
    private Integer stageDepth = 800;

    /** 団体名。未入力の場合は紐づく演奏会の団体名を登録する */
    @Column(name = "orch_name", length = 64)
    private String orchName;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    public String getOrchName() { return orchName; }
    public void setOrchName(String orchName) { this.orchName = orchName; }

    public Long getLayoutId() { return layoutId; }
    public void setLayoutId(Long layoutId) { this.layoutId = layoutId; }

    public String getLayoutName() { return layoutName; }
    public void setLayoutName(String layoutName) { this.layoutName = layoutName; }

    public Integer getStageWidth() { return stageWidth; }
    public void setStageWidth(Integer stageWidth) { this.stageWidth = stageWidth; }

    public Integer getStageDepth() { return stageDepth; }
    public void setStageDepth(Integer stageDepth) { this.stageDepth = stageDepth; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

}
