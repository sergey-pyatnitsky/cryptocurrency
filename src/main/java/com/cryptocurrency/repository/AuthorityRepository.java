package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    int removeByName(String name);

    Optional<Authority> findByName(String name);
}