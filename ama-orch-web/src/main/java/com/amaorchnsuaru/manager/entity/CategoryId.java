package com.amaorchnsuaru.manager.entity;

import java.io.Serializable;
import java.util.Objects;

public class CategoryId implements Serializable {

    private String categType;
    private String categId;

    public CategoryId() {}

    public CategoryId(String categType, String categId) {
        this.categType = categType;
        this.categId = categId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryId that)) return false;
        return Objects.equals(categType, that.categType) && Objects.equals(categId, that.categId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categType, categId);
    }
}
