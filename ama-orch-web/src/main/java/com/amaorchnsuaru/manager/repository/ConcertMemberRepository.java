package com.amaorchnsuaru.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.ConcertMember;
import com.amaorchnsuaru.manager.entity.ConcertMemberId;

public interface ConcertMemberRepository extends JpaRepository<ConcertMember, ConcertMemberId> {
}
