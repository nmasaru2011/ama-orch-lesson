package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "last_name", nullable = false, length = 8)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 8)
    private String firstName;

    @Column(name = "old_name", length = 8)
    private String oldName;

    @Column(name = "last_name_kana", length = 12)
    private String lastNameKana;

    @Column(name = "first_name_kana", length = 12)
    private String firstNameKana;

    @Column(name = "last_name_kana_estimate", nullable = false, length = 12)
    private String lastNameKanaEstimate;

    @Column(name = "first_name_kana_estimate", nullable = false, length = 12)
    private String firstNameKanaEstimate;

    @Column(name = "musician_status", length = 12)
    private String musicianStatus;

    @Column(name = "main_active_instrument", length = 32)
    private String mainActiveInstrument;

    @Column(name = "main_active_orch", length = 32)
    private String mainActiveOrch;

    @Column(name = "orch_since")
    private Integer orchSince;

    @Column(name = "account", length = 32)
    private String account;

    public Long getPersonId() { return personId; }
    public void setPersonId(Long personId) { this.personId = personId; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getOldName() { return oldName; }
    public void setOldName(String oldName) { this.oldName = oldName; }

    public String getLastNameKana() { return lastNameKana; }
    public void setLastNameKana(String lastNameKana) { this.lastNameKana = lastNameKana; }

    public String getFirstNameKana() { return firstNameKana; }
    public void setFirstNameKana(String firstNameKana) { this.firstNameKana = firstNameKana; }

    public String getLastNameKanaEstimate() { return lastNameKanaEstimate; }
    public void setLastNameKanaEstimate(String lastNameKanaEstimate) { this.lastNameKanaEstimate = lastNameKanaEstimate; }

    public String getFirstNameKanaEstimate() { return firstNameKanaEstimate; }
    public void setFirstNameKanaEstimate(String firstNameKanaEstimate) { this.firstNameKanaEstimate = firstNameKanaEstimate; }

    public String getMusicianStatus() { return musicianStatus; }
    public void setMusicianStatus(String musicianStatus) { this.musicianStatus = musicianStatus; }

    public String getMainActiveInstrument() { return mainActiveInstrument; }
    public void setMainActiveInstrument(String mainActiveInstrument) { this.mainActiveInstrument = mainActiveInstrument; }

    public String getMainActiveOrch() { return mainActiveOrch; }
    public void setMainActiveOrch(String mainActiveOrch) { this.mainActiveOrch = mainActiveOrch; }

    public Integer getOrchSince() { return orchSince; }
    public void setOrchSince(Integer orchSince) { this.orchSince = orchSince; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getFullName() {
        return (lastName != null ? lastName : "") + " " + (firstName != null ? firstName : "");
    }
}
