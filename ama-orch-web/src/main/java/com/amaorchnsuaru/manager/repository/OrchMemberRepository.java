package com.amaorchnsuaru.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.OrchMember;
import com.amaorchnsuaru.manager.entity.OrchMemberId;

public interface OrchMemberRepository extends JpaRepository<OrchMember, OrchMemberId> {
}
