package com.ledger.ledgerworks.repository;

import com.ledger.ledgerworks.dto.GstSummaryDto;

import com.ledger.ledgerworks.dto.OutstandingRow;
import com.ledger.ledgerworks.entity.Invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ledger.ledgerworks.dto.GstDetailDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    // =========================================
    // TOTAL SALES
    // =========================================

    @Query("""
            SELECT COALESCE(
                SUM(i.grandTotal),
                0
            )
            FROM Invoice i
            """)
    BigDecimal getTotalSales();

    // =========================================
    // TOTAL OUTSTANDING
    // =========================================

    @Query("""
            SELECT COALESCE(
                SUM(
                    i.grandTotal
                    -
                    COALESCE(i.paidAmount, 0)
                ),
                0
            )
            FROM Invoice i
            """)
    BigDecimal getTotalOutstanding();

    // =========================================
    // MONTHLY SALES
    // =========================================
 // =========================================
 // MONTHLY SALES
 // =========================================

 @Query(value = """

         SELECT
             DATE_FORMAT(invoice_date, '%Y-%m') AS month,
             SUM(grand_total) AS amount

         FROM invoice

         GROUP BY DATE_FORMAT(invoice_date, '%Y-%m')

         ORDER BY DATE_FORMAT(invoice_date, '%Y-%m')

         """, nativeQuery = true)
 List<Object[]> getMonthlySales();
   
    
    // =========================================
    // AGING SUPPORT
    // =========================================

    List<Invoice> findByPaymentStatusNot(
            String paymentStatus
    );

    // =========================================
    // FIND BY INVOICE NUMBER
    // =========================================

    Optional<Invoice> findByInvoiceNumber(
            String invoiceNumber
    );

    // =========================================
    // OUTSTANDING REPORT
    // CUSTOMER-WISE
    // =========================================

    @Query("""

            SELECT new com.ledger.ledgerworks.dto.OutstandingRow(

                i.customer.id,

                i.customerName,

                CAST(
                    SUM(

                        COALESCE(
                            i.grandTotal,
                            0
                        )

                        -

                        COALESCE(
                            i.paidAmount,
                            0
                        )

                    ) as double
                )

            )

            FROM Invoice i

            WHERE i.customer IS NOT NULL

            GROUP BY

                i.customer.id,

                i.customerName

            HAVING SUM(

                COALESCE(
                    i.grandTotal,
                    0
                )

                -

                COALESCE(
                    i.paidAmount,
                    0
                )

            ) > 0

            ORDER BY SUM(

                COALESCE(
                    i.grandTotal,
                    0
                )

                -

                COALESCE(
                    i.paidAmount,
                    0
                )

            ) DESC

            """)
    List<OutstandingRow> findOutstandingCustomers();

    // =========================================
    // LAST INVOICE
    // =========================================

    Invoice findTopByOrderByIdDesc();
    
    @Query("""

    		SELECT new com.ledger.ledgerworks.dto.GstSummaryDto(

    		    COALESCE(SUM(i.totalTaxable),0),

    		    COALESCE(SUM(i.totalCGST),0),

    		    COALESCE(SUM(i.totalSGST),0),

    		    COALESCE(SUM(i.totalIGST),0),

    		    COALESCE(SUM(i.grandTotal),0)

    		)

    		FROM Invoice i

    		WHERE i.invoiceDate BETWEEN :fromDate AND :toDate

    		""")
    		GstSummaryDto getGstSummary(
    		        java.time.LocalDate fromDate,
    		        java.time.LocalDate toDate
    		);
    
    
    @Query("""

    		SELECT i

    		FROM Invoice i

    		WHERE i.invoiceDate
    		BETWEEN :fromDate AND :toDate

    		ORDER BY i.invoiceDate ASC

    		""")
    		List<Invoice> findInvoicesForGstReport(
    		        @Param("fromDate") LocalDate fromDate,
    		        @Param("toDate") LocalDate toDate
    		);
    
    List<Invoice> findByInvoiceDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );

    // =========================================
    // PAGINATED FREE-TEXT SEARCH
    // =========================================
    // Case-insensitive substring match across the obvious text columns.
    // Ordering comes from the Pageable.

    @Query("""
            SELECT i FROM Invoice i
            WHERE LOWER(COALESCE(i.invoiceNumber, ''))  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(i.customerName, ''))   LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(i.customerEmail, ''))  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(i.customerPhone, ''))  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(i.paymentStatus, ''))  LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<Invoice> searchPage(
            @Param("q") String q,
            Pageable pageable
    );

}