package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.entity.ReceiptVoucher;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiptVoucherRepository extends JpaRepository<ReceiptVoucher, Long> {

    Optional<ReceiptVoucher> findTopByOrderByIdDesc();

    default List<ReceiptVoucher> findAllLatestFirst() {
        return findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
