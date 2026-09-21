package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 舞台配置の1座席（{@link StageLayout} の子テーブル）。
 *
 * <p>座標系は舞台を真上から見た図で、原点は左上。
 * x は下手→上手、y は舞台奥→客席側（y が大きいほど客席に近い）。
 * {@code rotation} は 0 = 画面上（舞台奥）向き、時計回りに 0-359 度で、
 * 客席を向く場合が 180 度。既定値は指揮者を向く角度。</p>
 */
@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "stage_layout_seat")
public class StageLayoutSeat extends AuditableEntity {

    /** 座席ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    /** 親の配置ID */
    @Column(name = "layout_id", nullable = false)
    private Long layoutId;

    /** 配置内の表示順 */
    @Column(name = "seat_no", nullable = false)
    private Integer seatNo;

    /** 役割: CONDUCTOR / PLAYER / SOLOIST / OTHER */
    @Column(name = "role_type", nullable = false, length = 16)
    private String roleType = ROLE_PLAYER;

    /** パートコード（Vn1 / Va / Fl ...） */
    @Column(name = "part_code", length = 16)
    private String partCode;

    /** パート表示名（1stヴァイオリン など） */
    @Column(name = "part_name", length = 32)
    private String partName;

    /** プルト番号（弦楽器のみ。1 始まり） */
    @Column(name = "pult_no")
    private Integer pultNo;

    /** プルト内の位置: OUT = 表（客席側）/ IN = 裏（指揮者側） */
    @Column(name = "seat_side", length = 8)
    private String seatSide;

    /** 人物マスタの person_id（マスタ外の人は null） */
    @Column(name = "person_id")
    private Long personId;

    /** 表示名。人物マスタを選んだ場合もここに氏名を持つ */
    @Column(name = "person_name", length = 32)
    private String personName;

    @Column(name = "pos_x", nullable = false)
    private Integer posX;

    @Column(name = "pos_y", nullable = false)
    private Integer posY;

    /** 向き（度）。0 = 舞台奥向き、時計回り */
    @Column(name = "rotation", nullable = false)
    private Integer rotation = 0;

    @Column(name = "memo", length = 128)
    private String memo;

    public static final String ROLE_CONDUCTOR = "CONDUCTOR";
    public static final String ROLE_PLAYER    = "PLAYER";
    public static final String ROLE_SOLOIST   = "SOLOIST";
    public static final String ROLE_OTHER     = "OTHER";

    public static final String SIDE_OUT = "OUT";
    public static final String SIDE_IN  = "IN";

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public Long getLayoutId() { return layoutId; }
    public void setLayoutId(Long layoutId) { this.layoutId = layoutId; }

    public Integer getSeatNo() { return seatNo; }
    public void setSeatNo(Integer seatNo) { this.seatNo = seatNo; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public Integer getPultNo() { return pultNo; }
    public void setPultNo(Integer pultNo) { this.pultNo = pultNo; }

    public String getSeatSide() { return seatSide; }
    public void setSeatSide(String seatSide) { this.seatSide = seatSide; }

    public Long getPersonId() { return personId; }
    public void setPersonId(Long personId) { this.personId = personId; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public Integer getPosX() { return posX; }
    public void setPosX(Integer posX) { this.posX = posX; }

    public Integer getPosY() { return posY; }
    public void setPosY(Integer posY) { this.posY = posY; }

    public Integer getRotation() { return rotation; }
    public void setRotation(Integer rotation) { this.rotation = rotation; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
