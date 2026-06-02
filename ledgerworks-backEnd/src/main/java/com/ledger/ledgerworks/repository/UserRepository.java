package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}