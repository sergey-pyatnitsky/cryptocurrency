package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.FavoriteCoin;
import com.cryptocurrency.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteCoinRepository extends JpaRepository<FavoriteCoin, Long> {

    Optional<FavoriteCoin> findByUser(User user);
}