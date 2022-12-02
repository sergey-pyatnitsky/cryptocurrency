package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, String> {
}