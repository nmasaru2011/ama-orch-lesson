package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amaorchnsuaru.manager.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Page<Person> findAllByOrderByLastNameKanaEstimateAscFirstNameKanaEstimateAsc(Pageable pageable);

    List<Person> findByMainActiveInstrumentOrderByLastNameKanaEstimateAscFirstNameKanaEstimateAsc(String mainActiveInstrument);

    @Query("SELECT p FROM Person p WHERE " +
           "CONCAT(p.lastName, p.firstName) LIKE %:kw% OR " +
           "CONCAT(p.lastNameKanaEstimate, p.firstNameKanaEstimate) LIKE %:kw% OR " +
           "p.account LIKE %:kw%")
    Page<Person> search(@Param("kw") String keyword, Pageable pageable);
}
