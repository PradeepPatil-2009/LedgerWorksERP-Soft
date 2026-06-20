package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ContraVoucher;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContraVoucherRepository extends JpaRepository<ContraVoucher, Long> {

    Optional<ContraVoucher> findTopByOrderByIdDesc();

    default List<ContraVoucher> findAllLatestFirst() {
        return findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
