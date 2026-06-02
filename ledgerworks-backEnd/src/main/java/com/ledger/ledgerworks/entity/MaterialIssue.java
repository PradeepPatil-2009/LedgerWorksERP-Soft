package com.ledger.ledgerworks.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "material_issue")
public class MaterialIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // ISSUE NUMBER
    // =====================================================

    @Column(unique = true)
    private String issueNumber;

    // =====================================================
    // ISSUE DATE
    // =====================================================

    private LocalDate issueDate;

    // =====================================================
    // DEPARTMENT
    // =====================================================

    private String department;

    // =====================================================
    // REMARKS
    // =====================================================

    private String remarks;

    // =====================================================
    // ITEMS
    // =====================================================

    @OneToMany(
            mappedBy = "materialIssue",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<MaterialIssueItem> items;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(
            String issueNumber
    ) {
        this.issueNumber = issueNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(
            LocalDate issueDate
    ) {
        this.issueDate = issueDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(
            String department
    ) {
        this.department = department;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks
    ) {
        this.remarks = remarks;
    }

    public List<MaterialIssueItem> getItems() {
        return items;
    }

    public void setItems(
            List<MaterialIssueItem> items
    ) {
        this.items = items;
    }
}