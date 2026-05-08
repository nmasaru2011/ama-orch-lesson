package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orch_data")
public class OrchData {

    @Id
    @Column(name = "orch_id", length = 6)
    private String orchId;

    @Column(name = "orch_name", nullable = false, length = 64)
    private String orchName;

    @Column(name = "orch_name_en", length = 64)
    private String orchNameEn;

    @Column(name = "orch_type", length = 10)
    private String orchType;

    @Column(name = "homepage_url", length = 256)
    private String homepageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "activity")
    private Boolean activity;

    @Column(name = "pro_state")
    private Integer proState;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "creator")
    private Integer creator;

    @Column(name = "since")
    private Integer since;

    public String getOrchId() { return orchId; }
    public void setOrchId(String orchId) { this.orchId = orchId; }

    public String getOrchName() { return orchName; }
    public void setOrchName(String orchName) { this.orchName = orchName; }

    public String getOrchNameEn() { return orchNameEn; }
    public void setOrchNameEn(String orchNameEn) { this.orchNameEn = orchNameEn; }

    public String getOrchType() { return orchType; }
    public void setOrchType(String orchType) { this.orchType = orchType; }

    public String getHomepageUrl() { return homepageUrl; }
    public void setHomepageUrl(String homepageUrl) { this.homepageUrl = homepageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActivity() { return activity; }
    public void setActivity(Boolean activity) { this.activity = activity; }

    public Integer getProState() { return proState; }
    public void setProState(Integer proState) { this.proState = proState; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Integer getCreator() { return creator; }
    public void setCreator(Integer creator) { this.creator = creator; }

    public Integer getSince() { return since; }
    public void setSince(Integer since) { this.since = since; }
}
