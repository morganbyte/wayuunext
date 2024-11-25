package com.example.wayuunext_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wayuunext_app.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
}
