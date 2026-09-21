package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.Category;
import com.amaorchnsuaru.manager.entity.CategoryId;

public interface CategoryRepository extends JpaRepository<Category, CategoryId> {

    List<Category> findByCategTypeOrderByCategId(String categType);
}
