package com.amaorchnsuaru.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

	Optional<AppUser> findByUserAccount(String userAccount);

	boolean existsByGoogleSubject(String googleSubject);

	Optional<AppUser> findByGoogleSubject(String googleSubject);
}
