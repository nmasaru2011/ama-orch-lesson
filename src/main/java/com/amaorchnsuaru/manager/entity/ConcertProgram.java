package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "concert_program")
@IdClass(ConcertProgramId.class)
public class ConcertProgram extends AuditableEntity {

    @Id
    @Column(name = "concert_id", length = 12)
    private String concertId;

    @Id
    @Column(name = "program_no")
    private Integer programNo;

    @Column(name = "music_id", length = 12)
    private String musicId;

    @Column(name = "music_title_formal_jp", nullable = false, length = 128)
    private String musicTitleFormalJp;

    @Column(name = "from_part", length = 64)
    private String fromPart;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "mov_service", length = 64)
    private String movService;

    @Column(name = "mov_id", length = 64)
    private String movId;

    @Column(name = "mov_owner", length = 64)
    private String movOwner;

    @Column(name = "mov_url", length = 256)
    private String movUrl;

    @Column(name = "mov_is_public")
    private Boolean movIsPublic;

    /** 舞台配置ID（stage_layout.layout_id）。未設定なら null */
    @Column(name = "layout_id")
    private Long layoutId;

    public String getConcertId() { return concertId; }
    public void setConcertId(String concertId) { this.concertId = concertId; }

    public Integer getProgramNo() { return programNo; }
    public void setProgramNo(Integer programNo) { this.programNo = programNo; }

    public String getMusicId() { return musicId; }
    public void setMusicId(String musicId) { this.musicId = musicId; }

    public String getMusicTitleFormalJp() { return musicTitleFormalJp; }
    public void setMusicTitleFormalJp(String musicTitleFormalJp) { this.musicTitleFormalJp = musicTitleFormalJp; }

    public String getFromPart() { return fromPart; }
    public void setFromPart(String fromPart) { this.fromPart = fromPart; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getMovService() { return movService; }
    public void setMovService(String movService) { this.movService = movService; }

    public String getMovId() { return movId; }
    public void setMovId(String movId) { this.movId = movId; }

    public String getMovOwner() { return movOwner; }
    public void setMovOwner(String movOwner) { this.movOwner = movOwner; }

    public String getMovUrl() { return movUrl; }
    public void setMovUrl(String movUrl) { this.movUrl = movUrl; }

    public Boolean getMovIsPublic() { return movIsPublic; }
    public void setMovIsPublic(Boolean movIsPublic) { this.movIsPublic = movIsPublic; }

    public Long getLayoutId() { return layoutId; }
    public void setLayoutId(Long layoutId) { this.layoutId = layoutId; }
}
