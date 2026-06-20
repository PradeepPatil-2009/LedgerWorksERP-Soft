package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.PaymentVoucher;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long> {

    Optional<PaymentVoucher> findTopByOrderByIdDesc();

    default List<PaymentVoucher> findAllLatestFirst() {
        return findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
