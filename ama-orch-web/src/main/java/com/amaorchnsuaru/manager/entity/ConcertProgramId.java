package com.amaorchnsuaru.manager.entity;

import java.io.Serializable;
import java.util.Objects;

public class ConcertProgramId implements Serializable {

    private String concertId;
    private Integer programNo;

    public ConcertProgramId() {}

    public ConcertProgramId(String concertId, Integer programNo) {
        this.concertId = concertId;
        this.programNo = programNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConcertProgramId)) return false;
        ConcertProgramId that = (ConcertProgramId) o;
        return Objects.equals(concertId, that.concertId) && Objects.equals(programNo, that.programNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concertId, programNo);
    }
}
