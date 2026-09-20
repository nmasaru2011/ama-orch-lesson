package com.amaorchnsuaru.manager.entity;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "category")
@IdClass(CategoryId.class)
public class Category extends AuditableEntity {

    @Id
    @Column(name = "categ_type", length = 32)
    private String categType;

    @Id
    @Column(name = "categ_id", length = 12)
    private String categId;

    @Column(name = "categ_alter_id", length = 32)
    private String categAlterId;

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "name_jp", length = 128)
    private String nameJp;

    @Column(name = "prop_str_1", columnDefinition = "TEXT")
    private String propStr1;

    @Column(name = "prop_str_2", length = 128)
    private String propStr2;

    @Column(name = "prop_num_1")
    private Integer propNum1;

    @Column(name = "prop_num_2")
    private Integer propNum2;

    public String getCategType() { return categType; }
    public void setCategType(String categType) { this.categType = categType; }
    public String getCategId() { return categId; }
    public void setCategId(String categId) { this.categId = categId; }
    public String getCategAlterId() { return categAlterId; }
    public void setCategAlterId(String categAlterId) { this.categAlterId = categAlterId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameJp() { return nameJp; }
    public void setNameJp(String nameJp) { this.nameJp = nameJp; }
    public String getPropStr1() { return propStr1; }
    public void setPropStr1(String propStr1) { this.propStr1 = propStr1; }
    public String getPropStr2() { return propStr2; }
    public void setPropStr2(String propStr2) { this.propStr2 = propStr2; }
    public Integer getPropNum1() { return propNum1; }
    public void setPropNum1(Integer propNum1) { this.propNum1 = propNum1; }
    public Integer getPropNum2() { return propNum2; }
    public void setPropNum2(Integer propNum2) { this.propNum2 = propNum2; }
}
