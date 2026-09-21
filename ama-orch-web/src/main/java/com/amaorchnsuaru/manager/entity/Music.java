package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "music_mst")
public class Music extends AuditableEntity {

    @Id
    @Column(name = "music_id", length = 12)
    private String musicId;

    @Column(name = "composer_id", nullable = false, length = 5)
    private String composerId;

    @Column(name = "composer_name", nullable = false, length = 64)
    private String composerName;

    @Column(name = "music_title_formal_jp", nullable = false, length = 128)
    private String musicTitleFormalJp;

    @Column(name = "music_title_formal", length = 128)
    private String musicTitleFormal;

    @Column(name = "music_type_id", length = 2)
    private String musicTypeId;

    @Column(name = "music_no")
    private Integer musicNo;

    @Column(name = "opus_no")
    private Integer opusNo;

    public String getMusicId() { return musicId; }
    public void setMusicId(String musicId) { this.musicId = musicId; }

    public String getComposerId() { return composerId; }
    public void setComposerId(String composerId) { this.composerId = composerId; }

    public String getComposerName() { return composerName; }
    public void setComposerName(String composerName) { this.composerName = composerName; }

    public String getMusicTitleFormalJp() { return musicTitleFormalJp; }
    public void setMusicTitleFormalJp(String musicTitleFormalJp) { this.musicTitleFormalJp = musicTitleFormalJp; }

    public String getMusicTitleFormal() { return musicTitleFormal; }
    public void setMusicTitleFormal(String musicTitleFormal) { this.musicTitleFormal = musicTitleFormal; }

    public String getMusicTypeId() { return musicTypeId; }
    public void setMusicTypeId(String musicTypeId) { this.musicTypeId = musicTypeId; }

    public Integer getMusicNo() { return musicNo; }
    public void setMusicNo(Integer musicNo) { this.musicNo = musicNo; }

    public Integer getOpusNo() { return opusNo; }
    public void setOpusNo(Integer opusNo) { this.opusNo = opusNo; }

    public String getDisplayLabel() {
        return composerName + " / " + musicTitleFormalJp;
    }
}
