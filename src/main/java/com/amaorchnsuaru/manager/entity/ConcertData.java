package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "concert_data")
public class ConcertData {

    @Id
    @Column(name = "concert_id", length = 12)
    private String concertId;

    @Column(name = "orch_id", nullable = false, length = 6)
    private String orchId;

    @Column(name = "concert_main_id", nullable = false, length = 12)
    private String concertMainId;

    @Column(name = "concert_sub_id", nullable = false, length = 2)
    private String concertSubId;

    @Column(name = "concert_name", length = 128)
    private String concertName;

    @Column(name = "music_descript", columnDefinition = "TEXT")
    private String musicDescript;

    @Column(name = "conductor_id")
    private Long conductorId;

    @Column(name = "conductor_name", length = 20)
    private String conductorName;

    @Column(name = "concert_num")
    private Long concertNum;

    @Column(name = "hall_id", length = 6)
    private String hallId;

    @Column(name = "hall_branch_id")
    private Long hallBranchId;

    @Column(name = "place_name", length = 64)
    private String placeName;

    @Column(name = "concert_date", length = 12)
    private String concertDate;

    @Column(name = "open_time", length = 6)
    private String openTime;

    @Column(name = "open_space", length = 6)
    private String openSpace;

    @Column(name = "price")
    private Long price;

    @Column(name = "price_under_cond", length = 16)
    private String priceUnderCond;

    @Column(name = "price_under")
    private Long priceUnder;

    @Column(name = "teket_url", length = 128)
    private String teket_url;

    public String getConcertId() { return concertId; }
    public void setConcertId(String concertId) { this.concertId = concertId; }

    public String getOrchId() { return orchId; }
    public void setOrchId(String orchId) { this.orchId = orchId; }

    public String getConcertMainId() { return concertMainId; }
    public void setConcertMainId(String concertMainId) { this.concertMainId = concertMainId; }

    public String getConcertSubId() { return concertSubId; }
    public void setConcertSubId(String concertSubId) { this.concertSubId = concertSubId; }

    public String getConcertName() { return concertName; }
    public void setConcertName(String concertName) { this.concertName = concertName; }

    public String getMusicDescript() { return musicDescript; }
    public void setMusicDescript(String musicDescript) { this.musicDescript = musicDescript; }

    public Long getConductorId() { return conductorId; }
    public void setConductorId(Long conductorId) { this.conductorId = conductorId; }

    public String getConductorName() { return conductorName; }
    public void setConductorName(String conductorName) { this.conductorName = conductorName; }

    public Long getConcertNum() { return concertNum; }
    public void setConcertNum(Long concertNum) { this.concertNum = concertNum; }

    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }

    public Long getHallBranchId() { return hallBranchId; }
    public void setHallBranchId(Long hallBranchId) { this.hallBranchId = hallBranchId; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getConcertDate() { return concertDate; }
    public void setConcertDate(String concertDate) { this.concertDate = concertDate; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getOpenSpace() { return openSpace; }
    public void setOpenSpace(String openSpace) { this.openSpace = openSpace; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getPriceUnderCond() { return priceUnderCond; }
    public void setPriceUnderCond(String priceUnderCond) { this.priceUnderCond = priceUnderCond; }

    public Long getPriceUnder() { return priceUnder; }
    public void setPriceUnder(Long priceUnder) { this.priceUnder = priceUnder; }

    public String getTeket_url() { return teket_url; }
    public void setTeket_url(String teket_url) { this.teket_url = teket_url; }
}
