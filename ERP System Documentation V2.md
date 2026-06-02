# LedgerWorks ERP

## Complete Functional Specification Document (FSD)

### Version 2.0 – Developer Handover Edition

---

# 1. PROJECT OVERVIEW

## Introduction

LedgerWorks ERP is a web-based Enterprise Resource Planning (ERP) application developed for small and medium manufacturing, trading and distribution businesses.

The objective of the application is to centralize all business operations into a single system and eliminate dependency on multiple spreadsheets, manual registers and disconnected software applications.

The ERP enables businesses to manage:

* Customers
* Vendors
* Inventory
* Purchase
* Sales
* Production
* Delivery Challan
* Accounting
* GST Reporting
* Backup & Recovery

through a single integrated platform.

---

## Technology Stack

### Frontend

LedgerWorks ERP uses React JS for creating a modern and responsive user interface.

Technologies:

* React JS
* Axios
* React Router

Benefits:

* Fast user experience
* Modular screen development
* Easy maintenance

---

### Backend

LedgerWorks ERP uses Spring Boot as the backend framework.

Technologies:

* Spring Boot
* Spring Data JPA
* REST APIs

Benefits:

* Secure architecture
* Scalable development
* Easy API integration

---

### Database

Database Technology:

MySQL 8

Purpose:

Store all business transactions and master records.

Benefits:

* Reliable
* High performance
* Easy backup and recovery

---

## Major Functional Areas

LedgerWorks ERP currently covers:

### Master Management

Customer, Vendor and Item Management.

### Inventory Management

Stock tracking and movement management.

### Purchase Management

Procurement and vendor transactions.

### Sales Management

Customer invoicing and receivable tracking.

### Production Management

Manufacturing process tracking.

### Delivery Challan Management

Dispatch management before invoicing.

### Accounting

Financial reporting and ledger management.

### GST Management

Tax reporting and compliance.

### Backup & Recovery

Business continuity and disaster recovery.

### Security

Authentication and authorization framework.

---

# 2. SYSTEM ARCHITECTURE

## Architecture Overview

The application follows a layered architecture.

Frontend (React)

↓

REST API Layer

↓

Spring Boot Controllers

↓

Service Layer

↓

Repository Layer

↓

MySQL Database

---

## Why This Architecture Is Used

This architecture separates business logic from presentation logic.

Benefits:

* Easier maintenance
* Easier testing
* Better scalability
* Faster future development

---

## Frontend Layer

Responsibilities:

* User Interface
* Data Entry
* Validation
* Report Display

Technology:

React JS

---

## Backend Layer

Responsibilities:

* Business Logic
* Security
* Validation
* Transaction Processing

Technology:

Spring Boot

---

## Database Layer

Responsibilities:

* Master Data Storage
* Transaction Storage
* Reporting Data
* Audit Data

Technology:

MySQL

---

## Future Architecture Roadmap

Future versions may include:

* Mobile Application
* Cloud Deployment
* Microservices Architecture
* Third Party Integrations

---

# 3. DEPLOYMENT ARCHITECTURE

## Current Deployment Model

LedgerWorks ERP is deployed using:

* Spring Boot Fat JAR
* React Production Build
* Embedded MySQL
* Windows Startup Scripts

Purpose:

Provide a simple deployment process for clients.

---

## Application Startup Flow

User Double Clicks:

LedgerWorks ERP

↓

MySQL Starts

↓

Spring Boot Starts

↓

Browser Opens

↓

Dashboard Loads

---

## Why This Deployment Model Is Used

Many small businesses do not have dedicated IT teams.

Benefits:

* Simple installation
* Easy support
* Reduced technical dependency

---

## Future Deployment Enhancements

### Windows EXE Launcher

Purpose:

Provide single-click startup.

Benefits:

* Better user experience
* Reduced support calls

---

### Auto Database Initialization

Purpose:

Automatically create database during first installation.

Benefits:

* Faster deployment
* Easier onboarding

---

### Desktop Shortcut

Purpose:

Allow users to launch ERP directly from desktop.

Benefits:

* Faster access
* Improved usability

---

# 4. COMPANY SETTINGS MODULE

## Status

Planned (Branch-6)

---

## Purpose

Company Settings acts as the central configuration area of the ERP.

All company-specific information required across invoices, reports and GST documents is maintained here.

---

## Why This Module Is Required

Every organization has its own:

* Company Name
* GST Number
* PAN Number
* Address
* Banking Information

Instead of storing these values repeatedly, they are maintained centrally.

---

## Business Usage

Used in:

* Sales Invoice
* Delivery Challan
* GST Reports
* Accounting Reports
* Printed Documents

---

## Planned Features

### Company Name

Stores organization name.

---

### GST Number

Stores GST registration number.

---

### PAN Number

Stores PAN information.

---

### Company Address

Stores registered address.

---

### Bank Details

Stores:

* Bank Name
* Account Number
* IFSC Code
* Branch Name

---

### Company Logo

Used in:

* Invoice
* Delivery Challan
* Reports

---

### Invoice Terms & Conditions

Default terms automatically printed on documents.

---

### Invoice Footer Configuration

Custom footer message for documents.

---

## Business Flow

Administrator

↓

Open Company Settings

↓

Enter Company Information

↓

Save Configuration

↓

Used Across ERP

---

## User Roles

### ADMIN

Full Access

Can Create

Can Modify

Can Update

---

### ACCOUNTANT

Read Only

---

### USER

No Access

---

## Benefits

* Centralized company information
* Consistent document generation
* Easier maintenance
* Better GST compliance

---

## Future Enhancements

### Multiple Company Profiles

Support multiple organizations.

### Digital Signature

Automatic document signing.

### Company Branding Templates

Custom invoice themes.


# 5. CUSTOMER MASTER MODULE

## Status

Completed

---

## Purpose

Customer Master stores all customer information used throughout the ERP.

Every sales transaction, invoice, delivery challan and outstanding report depends on Customer Master data.

The objective of this module is to maintain a single source of customer information.

---

## Why This Module Is Required

Without a centralized customer database:

* Duplicate customer records may occur.
* Incorrect billing may occur.
* Outstanding tracking becomes difficult.
* GST reporting becomes inaccurate.

---

## Business Usage

Used in:

* Sales Invoice
* Delivery Challan
* Outstanding Reports
* GST Reports
* Customer Ledger Reports
* Receipt Voucher

---

## Fields

### Basic Information

* Customer Name
* Contact Person
* Mobile Number
* Email Address

### Address Information

* Address
* City
* State
* Pincode

### Tax Information

* GST Number
* PAN Number

### Financial Information

* Opening Balance
* Credit Limit

---

## Implemented Features

### Add Customer

Create new customer records.

### Update Customer

Modify existing customer information.

### Delete Customer

Remove unused customer records.

### Search Customer

Quick customer lookup.

### Customer Listing

View all customer records.

---

## Business Flow

Customer Creation

↓

Customer Saved

↓

Available In Invoice

↓

Available In Delivery Challan

↓

Available In Reports

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Create / Update / View

### USER

View & Select During Transactions

### VIEWER

Read Only

---

## Benefits

* Centralized customer management
* Better billing accuracy
* Faster transaction processing
* Accurate outstanding tracking

---

## Migration Support

### Customer Opening Balance

Purpose:

Support migration from existing software.

Example:

Old Software Outstanding:

₹50,000

Customer Opening Balance:

₹50,000

---

## Future Enhancements

### Dynamic State Dropdown

Purpose:

Prevent manual state entry errors.

Benefits:

* Standardized data
* Better GST reporting

---

### GST Based State Auto Detection

Purpose:

Automatically identify state from GST Number.

Example:

27XXXXXXXXXXXX

↓

Maharashtra

---

### Customer Credit Limit

Purpose:

Control customer credit exposure.

---

### Customer Category

Examples:

* Retail Customer
* Wholesale Customer
* Distributor

---

### Customer Document Upload

Store:

* GST Certificate
* PAN Copy
* Agreements

---

# 6. VENDOR MASTER MODULE

## Status

Completed

---

## Purpose

Vendor Master stores supplier information used for purchases and vendor accounting.

---

## Why This Module Is Required

Vendor information is required for:

* Purchase Entry
* Vendor Payment
* Vendor Ledger
* GST Reporting

Without Vendor Master, purchase management becomes difficult.

---

## Business Usage

Used In:

* Purchase Module
* Payment Voucher
* Vendor Ledger
* GST Reports

---

## Fields

### Basic Information

* Vendor Name
* Contact Person
* Mobile Number
* Email Address

### Address Information

* Address
* City
* State
* Pincode

### Tax Information

* GST Number
* PAN Number

### Financial Information

* Opening Balance
* Payment Terms

---

## Implemented Features

### Add Vendor

### Edit Vendor

### Delete Vendor

### Search Vendor

### Vendor Listing

---

## Business Flow

Vendor Creation

↓

Vendor Saved

↓

Available In Purchase

↓

Available In Payment Voucher

↓

Available In Reports

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Create / Update / View

### USER

View & Select

### VIEWER

Read Only

---

## Benefits

* Centralized vendor management
* Better purchase control
* Vendor payment tracking
* Accurate GST reporting

---

## Migration Support

### Vendor Opening Balance

Purpose:

Carry forward payable balances from previous software.

---

## Future Enhancements

### Dynamic State Dropdown

Purpose:

Prevent incorrect state entry.

---

### GST Based State Auto Detection

Purpose:

Automatically identify vendor state from GST Number.

---

### Vendor Rating

Evaluate supplier performance.

---

### Vendor Performance Analysis

Track:

* Delivery Performance
* Quality Performance
* Payment History

---

### Vendor Payment Terms

Examples:

* Immediate
* 30 Days
* 60 Days

---

# 7. ITEM MASTER MODULE

## Status

Completed

---

## Purpose

Item Master is the central inventory repository.

Every purchase, production, delivery challan and sales transaction depends on Item Master.

---

## Why This Module Is Required

Without Item Master:

* Inventory tracking is not possible.
* Stock reports become inaccurate.
* Production planning becomes difficult.

---

## Business Usage

Used In:

* Purchase Module
* Inventory Module
* Production Module
* Delivery Challan Module
* Sales Module

---

## Fields

### Item Information

* Item Name
* Item Code

### Tax Information

* HSN Code
* GST Percentage

### Inventory Information

* Unit
* Opening Stock
* Opening Value

### Pricing Information

* Purchase Rate
* Sales Rate

---

## Implemented Features

### Add Item

### Edit Item

### Delete Item

### Search Item

### Item Listing

---

## Business Flow

Item Creation

↓

Item Saved

↓

Available In Purchase

↓

Available In Production

↓

Available In Sales

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Create / Update / View

### USER

View & Select

### VIEWER

Read Only

---

## Benefits

* Inventory standardization
* Better stock control
* Accurate reporting
* GST compliance

---

## Migration Support

### Opening Stock Import

Purpose:

Import stock from previous software.

### Opening Stock Valuation

Purpose:

Maintain inventory value during migration.

---

## Future Enhancements

### Barcode Support

### QR Code Support

### Item Images

### Batch Tracking

### Serial Number Tracking

---

# 8. STATE MASTER MODULE

## Status

Planned (Branch-6)

---

## Purpose

State Master provides centralized management of Indian states.

---

## Why This Module Is Required

Currently state information is manually entered.

Manual entry can cause:

* Spelling errors
* GST mismatches
* Reporting inconsistencies

---

## Business Usage

Used In:

* Customer Master
* Vendor Master
* GST Module

---

## Planned Features

### State List

Maintain all Indian states.

---

### State Code Mapping

Map GST state codes.

---

### Dynamic State Dropdown

Provide dropdown selection instead of free text.

---

### GST State Validation

Validate GST state codes.

---

### GST Auto Detection Support

Automatically identify state using GST Number.

---

## Business Flow

GST Entered

↓

State Code Identified

↓

State Auto Selected

↓

User Verification

↓

Record Saved

---

## User Roles

### ADMIN

Create / Update States

### ACCOUNTANT

View States

### USER

Select States

### VIEWER

Read Only

---

## Benefits

* Improved GST compliance
* Better reporting accuracy
* Standardized master data
* Reduced manual errors

---

## Future Enhancements

### Country Master

### District Master

### City Master

### PIN Code Validation

# 9. INVENTORY MANAGEMENT MODULE

## Status

Completed

---

## Purpose

Inventory Management controls stock movement across the entire ERP system.

Every purchase, production, sales invoice and delivery challan directly impacts inventory.

The objective of this module is to maintain accurate stock quantities and provide real-time inventory visibility.

---

## Why This Module Is Required

Without inventory management:

* Stock shortages cannot be identified.
* Excess inventory cannot be controlled.
* Production planning becomes difficult.
* Sales may occur without stock availability.

---

## Business Usage

Used In:

* Purchase Module
* Production Module
* Delivery Challan Module
* Sales Module
* Inventory Reports

---

# Stock Increase

## Status

Completed

### Purpose

Increase stock quantity after:

* Purchase Entry
* Production Completion
* Opening Stock Entry

### Business Flow

Purchase Entry

↓

Stock Ledger Update

↓

Inventory Increase

↓

Available For Sale

### Benefits

* Real-time stock visibility
* Accurate inventory valuation

---

# Stock Decrease

## Status

Completed

### Purpose

Reduce inventory after:

* Sales Invoice
* Production Consumption
* Stock Adjustment

### Business Flow

Sales Invoice

↓

Stock Ledger Update

↓

Inventory Reduction

### Benefits

* Accurate stock control
* Prevents inventory mismatch

---

# Low Stock Report

## Status

Completed

### Purpose

Identify items below minimum stock levels.

### Benefits

* Better purchase planning
* Avoid production delays
* Prevent stock shortages

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

View Reports

### STORE USER

Inventory Operations

### VIEWER

Read Only

---

## Future Enhancements

### Automatic Purchase Suggestion

Automatically recommend purchase quantity.

---

### Reorder Level Alerts

Generate stock alerts.

---

### Warehouse Management

Support multiple warehouses.

---

### Godown Management

Location-based inventory tracking.

---

### Stock Reservation

Reserve inventory against sales orders.

---

### Negative Stock Control

Prevent stock from becoming negative.

---

### Stock Aging Report

Identify slow-moving inventory.

---

### Physical Stock Verification

Compare system stock with physical stock.

---

# 10. OPENING STOCK MODULE

## Status

Required

---

## Purpose

Allow businesses migrating from existing software to enter current inventory before ERP go-live.

---

## Why This Module Is Required

Without opening stock:

* Inventory reports become inaccurate.
* Production cannot start correctly.
* Financial valuation becomes incorrect.

---

## Business Usage

Used During:

* Initial Implementation
* Financial Year Opening
* Client Migration

---

## Features Planned

### Opening Quantity Entry

### Opening Value Entry

### Bulk Import

### Excel Upload

### Opening Stock Verification

---

## Business Flow

Opening Stock Import

↓

Validation

↓

Inventory Update

↓

Stock Available

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Import & Verify

---

## Benefits

* Smooth migration
* Accurate inventory valuation
* Faster implementation

---

# 11. PURCHASE MODULE

## Status

Completed

---

## Purpose

Manage procurement of raw materials and finished goods.

---

## Why This Module Is Required

Purchases increase inventory and create vendor liabilities.

Proper purchase tracking is essential for inventory and accounting accuracy.

---

## Business Usage

Used In:

* Inventory Management
* Vendor Accounting
* GST Reporting
* Cost Calculation

---

## Implemented Features

### Purchase Entry

Record purchase transactions.

### Vendor Selection

Select supplier.

### Item Selection

Select purchased items.

### Automatic Stock Update

Inventory increases automatically.

---

## Business Flow

Vendor

↓

Purchase Entry

↓

Stock Increase

↓

Accounting Entry

↓

GST Calculation

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Purchase Operations

### USER

Create Purchase Entry

### VIEWER

Read Only

---

## Benefits

* Inventory tracking
* Vendor management
* Accurate accounting
* GST compliance

---

## Future Enhancements

### Purchase Return

Return defective materials to vendor.

Business Flow:

Purchase

↓

Return

↓

Debit Note

↓

Stock Reduction

---

### Purchase Approval Workflow

Approval before final posting.

---

### Vendor Outstanding Tracking

Monitor payable balances.

---

### Purchase Aging Report

Track overdue vendor payments.

---

# 12. SALES MODULE

## Status

Completed

---

## Purpose

Manage customer invoicing and revenue generation.

---

## Why This Module Is Required

Sales transactions:

* Generate revenue
* Reduce inventory
* Create customer outstanding

---

## Business Usage

Used In:

* Accounting
* Inventory
* GST
* Customer Reporting

---

## Implemented Features

### Create Invoice

### Invoice Listing

### Invoice Search

### Outstanding Calculation

---

## Business Flow

Customer

↓

Invoice

↓

Stock Reduction

↓

Accounting Entry

↓

GST Calculation

↓

Outstanding Update

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

Invoice Operations

### USER

Create Invoice

### VIEWER

Read Only

---

## Benefits

* Faster billing
* Accurate stock update
* Outstanding tracking

---

## Future Enhancements

### PDF Invoice

Generate PDF copy.

---

### Email Invoice

Send invoice through email.

---

### WhatsApp Invoice

Share invoice through WhatsApp.

---

### Customer Outstanding Aging

Analyze overdue receivables.

---

### Invoice From Sales Order

Automatic invoice generation.

---

# 13. QUOTATION MODULE

## Status

Future Enhancement

---

## Purpose

Generate quotations before receiving confirmed customer orders.

---

## Why This Module Is Required

Many businesses provide quotations before order confirmation.

---

## Business Usage

Sales Team

↓

Quotation

↓

Customer Approval

↓

Sales Order

---

## Features Planned

### Create Quotation

### Edit Quotation

### Print Quotation

### Email Quotation

### Quotation Register

---

## Benefits

* Faster sales process
* Better customer communication

---

## User Roles

### ADMIN

### SALES USER

### ACCOUNTANT

---

# 14. SALES ORDER MODULE

## Status

Future Enhancement

---

## Purpose

Manage confirmed customer orders before invoicing.

---

## Business Flow

Quotation

↓

Customer Approval

↓

Sales Order

↓

Dispatch

↓

Invoice

---

## Features Planned

### Sales Order Creation

### Sales Order Register

### Pending Order Tracking

### Partial Delivery Support

### Order Status Tracking

---

## Benefits

* Better order control
* Improved dispatch planning

---

## User Roles

### ADMIN

### SALES USER

### ACCOUNTANT

---

# 15. PROFORMA INVOICE MODULE

## Status

Future Enhancement

---

## Purpose

Generate invoice-like document before actual accounting transaction.

---

## Why This Module Is Required

Customers often require estimated invoice before purchase confirmation.

---

## Business Usage

Customer Inquiry

↓

Proforma Invoice

↓

Customer Approval

↓

Sales Invoice

---

## Features Planned

### Proforma Creation

### Proforma Print

### Proforma PDF

### Convert To Invoice

---

## Benefits

* Better customer communication
* Faster order processing

---

## User Roles

### ADMIN

### ACCOUNTANT

### SALES USER

---

## Future Enhancements

### Online Approval Workflow

### Customer Portal Integration

# 16. PRODUCTION MODULE

## Status

Completed

---

## Purpose

Production Module manages the manufacturing process by converting raw materials into finished goods.

This module helps organizations track production activities, material consumption and finished goods generation.

---

## Why This Module Is Required

Manufacturing businesses require proper tracking of:

* Raw Material Consumption
* Finished Goods Production
* Production Cost
* Inventory Movement

Without production tracking:

* Stock becomes inaccurate
* Material wastage cannot be identified
* Production planning becomes difficult

---

## Business Usage

Used In:

* Inventory Management
* Cost Calculation
* Manufacturing Tracking
* Production Reporting

---

## Implemented Features

### Production Entry

Record finished goods production.

### Production Listing

View all production transactions.

### Material Issue

Issue raw materials for manufacturing.

---

## Business Flow

Raw Material

↓

Material Issue

↓

Production Entry

↓

Finished Goods Creation

↓

Inventory Update

---

## User Roles

### ADMIN

Full Access

### PRODUCTION USER

Production Operations

### ACCOUNTANT

View Reports

### VIEWER

Read Only

---

## Benefits

* Production tracking
* Inventory accuracy
* Manufacturing visibility
* Better planning

---

## Future Enhancements

### Bill Of Material (BOM)

Purpose:

Define raw materials required for manufacturing.

Example:

Product A

↓

Raw Material 1

Raw Material 2

Raw Material 3

---

### Production Costing

Purpose:

Calculate actual manufacturing cost.

Includes:

* Material Cost
* Labour Cost
* Machine Cost
* Overhead Cost

---

### Work Orders

Purpose:

Plan and execute production jobs.

Business Flow:

Work Order

↓

Material Issue

↓

Production

↓

Finished Goods

---

### Finished Goods Cost Calculation

Purpose:

Determine product profitability.

---

### Batch Tracking

Purpose:

Track production batches.

---

### Production Loss Tracking

Purpose:

Identify material wastage.

---

# 17. DELIVERY CHALLAN MODULE

## Status

Completed

---

## Purpose

Delivery Challan is used to dispatch goods before invoice generation.

This module supports businesses where material is delivered first and invoicing happens later.

---

## Why This Module Is Required

Many businesses follow:

Dispatch First

↓

Invoice Later

Without Delivery Challan:

* Dispatch tracking becomes difficult
* Material movement cannot be monitored
* Customer delivery history is unavailable

---

## Business Usage

Used In:

* Dispatch Operations
* Logistics
* Customer Deliveries
* Invoice Generation

---

## Implemented Features

### Challan Creation

Create Delivery Challan.

### Challan Listing

View all challans.

### Customer Mapping

Link challan with customer.

---

## Business Flow

Customer

↓

Delivery Challan

↓

Dispatch

↓

Delivery Confirmation

↓

Invoice Generation

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

View & Print

### DISPATCH USER

Create Challan

### VIEWER

Read Only

---

## Benefits

* Dispatch control
* Customer tracking
* Material movement visibility

---

## Current Client Requirement

### Delivery Challan Print Format

Display Columns:

* Sr No
* Item Name
* HSN Code
* Quantity

Hide Columns:

* Rate
* CGST
* SGST
* IGST
* Amount

Purpose:

Delivery Challan should function as dispatch document only.

Pricing information should remain hidden.

---

## Future Enhancements

### E-Way Bill Integration

Generate transportation compliance documents.

---

### Multiple Dispatch Copies

Generate:

* Original Copy
* Duplicate Copy
* Transport Copy
* Office Copy

---

### Print Without Rates

Dispatch Copy.

---

### Print With Rates

Internal Copy.

---

# 18. INVOICE FROM DELIVERY CHALLAN

## Status

Required (Branch-6)

---

## Purpose

Generate invoice directly from approved Delivery Challan.

---

## Why This Feature Is Required

Currently users may need to re-enter invoice data manually.

This creates:

* Duplicate work
* Human errors
* Time consumption

---

## Business Flow

Delivery Challan

↓

Dispatch Completed

↓

Generate Invoice

↓

Invoice Created

↓

Accounting Entry

↓

Outstanding Update

---

## Features Planned

### Convert Challan To Invoice

Single click invoice creation.

---

### Partial Invoice Support

Invoice selected challan items.

---

### Multiple Challan To Single Invoice

Combine dispatches.

---

### Invoice Tracking

Track challan to invoice conversion.

---

## Benefits

* Faster billing
* Reduced manual work
* Improved accuracy

---

## User Roles

### ADMIN

### ACCOUNTANT

---

# 19. GST MODULE

## Status

Completed

---

## Purpose

Manage GST reporting and compliance requirements.

---

## Why This Module Is Required

GST compliance is mandatory for registered businesses.

ERP should provide tax calculations and reporting.

---

## Implemented Features

### GST Report

Tax summary report.

### GST Analytics

GST analysis dashboard.

---

## Business Usage

Used In:

* Purchase
* Sales
* Accounting
* Reporting

---

## Benefits

* Simplified compliance
* Better tax visibility
* Faster reporting

---

## Future Enhancements

### GSTR-1 Export

Generate GSTR-1 data.

---

### GSTR-3B Export

Generate GSTR-3B data.

---

### GST Reconciliation

Compare sales and purchase GST.

---

### GST Auto State Detection

Purpose:

Automatically identify state from GST Number.

Example:

27

↓

Maharashtra

---

### GST Validation

Validate GST format during data entry.

---

# 20. DASHBOARD MODULE

## Status

Completed

---

## Purpose

Provide business summary on a single screen.

---

## Why This Module Is Required

Management requires quick visibility of business performance.

---

## Displayed Metrics

### Total Sales

### Outstanding Amount

### Overdue Amount

### Overdue Count

---

## Benefits

* Faster decisions
* Better monitoring
* Business visibility

---

## User Roles

### ADMIN

### ACCOUNTANT

### MANAGEMENT

### VIEWER

---

## Future Enhancements

### Graphs

Sales trend charts.

---

### Monthly Trends

Monthly business analysis.

---

### KPI Dashboard

Management KPIs.

---

### Inventory Dashboard

Stock monitoring.

---

### Production Dashboard

Production summary.

---

### GST Dashboard

Tax monitoring.

---

# 21. REPORTS MODULE

## Status

Partially Implemented

---

## Purpose

Provide operational, financial and management reports.

---

## Why This Module Is Required

Reports help businesses:

* Analyze performance
* Monitor operations
* Make decisions

---

## Implemented Reports

### Customer Ledger

### Vendor Ledger

### Ledger Statement

### Trial Balance

### Profit & Loss

### Balance Sheet

### Cash Flow

### GST Reports

---

## Planned Reports

### Customer Ledger Summary

### Vendor Ledger Summary

### Sales Register

### Purchase Register

### Stock Report

### Low Stock Report

### Production Report

### Delivery Challan Register

### Customer Outstanding Report

### Vendor Outstanding Report

### GST Summary Report

### Monthly Sales Report

### Monthly Purchase Report

### Item Wise Sales Report

### Item Wise Purchase Report

### Management MIS Report

---

## Export Options

### PDF Export

Supported For:

* Invoice
* Delivery Challan
* Ledger
* Reports

---

### Excel Export

Supported For:

* Customer Reports
* Vendor Reports
* Stock Reports
* Sales Reports
* Purchase Reports

---

## Benefits

* Better decision making
* Faster reporting
* Improved business visibility

---

## User Roles

### ADMIN

### ACCOUNTANT

### MANAGEMENT

### VIEWER

---

## Future Enhancements

### Scheduled Reports

Automatic report generation.

### Email Reports

Automatic report delivery.

### Dashboard Report Widgets

Real-time report integration.


# 22. ACCOUNTING MODULE

## Status

Completed (Core Accounting)

Enhanced Accounting Features Planned for Branch-6 and Future Releases.

---

## Purpose

Accounting Module provides complete financial visibility of business transactions.

All purchase, sales, receipts, payments and financial adjustments ultimately impact accounting records.

---

## Why This Module Is Required

Without proper accounting:

* Business profitability cannot be measured.
* Outstanding balances cannot be tracked.
* Financial statements cannot be generated.
* Compliance becomes difficult.

---

## Business Usage

Used In:

* Sales Module
* Purchase Module
* Inventory Valuation
* GST Reporting
* Financial Reporting

---

## Implemented Reports

### Ledger Accounts

### Ledger Statement

### Trial Balance

### Profit & Loss Account

### Balance Sheet

### Cash Flow Statement

---

## Benefits

* Financial visibility
* Accurate reporting
* Better decision making
* Compliance support

---

# 22.1 RECEIPT VOUCHER MODULE

## Status

Future Enhancement

---

## Purpose

Receipt Voucher records money received from customers.

---

## Why This Module Is Required

Customer payments must be recorded separately to maintain accurate outstanding balances.

---

## Business Usage

Used For:

* Customer Payments
* Advance Receipts
* Partial Payments

---

## Business Flow

Sales Invoice

↓

Customer Payment

↓

Receipt Voucher

↓

Outstanding Reduced

↓

Ledger Updated

---

## Features Planned

### Customer Selection

### Invoice Reference Selection

### Partial Payment Support

### Advance Receipt Support

### Cash Receipt

### Bank Receipt

### UPI Receipt

### Receipt Register

### Printable Receipt Voucher

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Better receivable tracking
* Accurate customer balances
* Faster reconciliation

---

# 22.2 PAYMENT VOUCHER MODULE

## Status

Future Enhancement

---

## Purpose

Payment Voucher records payments made to vendors and expenses.

---

## Why This Module Is Required

Vendor liabilities and business expenses must be tracked separately.

---

## Business Usage

Used For:

* Vendor Payment
* Salary Payment
* Expense Payment
* Advance Vendor Payment

---

## Business Flow

Purchase Invoice

↓

Vendor Payment

↓

Payment Voucher

↓

Outstanding Reduced

↓

Ledger Updated

---

## Features Planned

### Vendor Selection

### Invoice Adjustment

### Partial Payment Support

### Cash Payment

### Bank Payment

### UPI Payment

### Payment Register

### Printable Voucher

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Accurate payable tracking
* Expense monitoring
* Better cash management

---

# 22.3 CONTRA VOUCHER MODULE

## Status

Future Enhancement

---

## Purpose

Contra Voucher records transfer of funds between Cash and Bank Accounts.

---

## Why This Module Is Required

Cash movement between bank accounts and cash accounts should not affect profit and loss.

---

## Business Usage

Used For:

* Cash Deposit
* Cash Withdrawal
* Bank Transfer

---

## Business Flow

Cash Account

↓

Bank Account

↓

Contra Voucher

↓

Ledger Update

---

## Features Planned

### Cash To Bank Transfer

### Bank To Cash Transfer

### Bank To Bank Transfer

### Multiple Bank Support

### Contra Register

### Printable Voucher

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Better bank control
* Easier reconciliation
* Accurate cash balances

---

# 22.4 JOURNAL VOUCHER MODULE

## Status

Future Enhancement

---

## Purpose

Journal Voucher records non-cash accounting adjustments.

---

## Why This Module Is Required

Some accounting transactions cannot be recorded through sales or purchase modules.

---

## Business Usage

Used For:

* Adjustment Entries
* Depreciation
* Provision Entries
* Correction Entries
* Opening Balance Adjustments

---

## Business Flow

Adjustment Entry

↓

Journal Voucher

↓

Ledger Posting

↓

Financial Reports Updated

---

## Features Planned

### Multi Ledger Selection

### Debit Entry

### Credit Entry

### Narration Support

### Journal Register

### Printable Voucher

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Flexible accounting
* Better audit trail
* Accurate reporting

---

# 22.5 CREDIT NOTE MODULE

## Status

Future Enhancement

---

## Purpose

Credit Note is used when invoice value must be reduced.

---

## Why This Module Is Required

Customers may return goods or receive discounts after invoice generation.

---

## Business Usage

Used For:

* Sales Return
* Damaged Goods Return
* Rate Difference
* Discount Adjustment

---

## Business Flow

Sales Invoice

↓

Customer Return

↓

Credit Note

↓

Outstanding Reduced

↓

Ledger Updated

---

## Features Planned

### Customer Selection

### Invoice Linking

### Partial Credit Note

### GST Adjustment

### Credit Note Register

### Printable Credit Note

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Accurate sales return accounting
* Correct GST adjustment
* Customer balance accuracy

---

# 22.6 DEBIT NOTE MODULE

## Status

Future Enhancement

---

## Purpose

Debit Note records additional recoverable amount or purchase return transactions.

---

## Why This Module Is Required

Businesses frequently return goods to vendors or adjust purchase values.

---

## Business Usage

Used For:

* Purchase Return
* Quantity Difference
* Rate Difference
* Additional Charges

---

## Business Flow

Purchase Entry

↓

Purchase Return

↓

Debit Note

↓

Vendor Outstanding Updated

↓

Ledger Updated

---

## Features Planned

### Vendor Selection

### Purchase Linking

### GST Adjustment

### Debit Note Register

### Printable Debit Note

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Accurate vendor accounting
* Better purchase control
* GST compliance

---

# 22.7 BANK RECONCILIATION MODULE

## Status

Enterprise Enhancement

---

## Purpose

Compare ERP Bank Ledger with actual Bank Statement.

---

## Why This Module Is Required

Bank balances may differ due to pending or unmatched transactions.

---

## Business Usage

Used During:

* Monthly Closing
* Financial Audits
* Account Verification

---

## Features Planned

### Bank Statement Import

### Auto Matching

### Manual Matching

### Reconciliation Report

### Pending Transaction Report

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Accurate bank balances
* Faster audit process
* Better financial control

---

# 22.8 COST CENTER ACCOUNTING

## Status

Enterprise Enhancement

---

## Purpose

Track profitability department-wise, project-wise or branch-wise.

---

## Why This Module Is Required

Management requires detailed profit analysis.

---

## Examples

### Production Department

### Sales Department

### Administration Department

### Individual Projects

---

## Business Flow

Transaction

↓

Cost Center Allocation

↓

Accounting Entry

↓

Department Wise Reporting

---

## Features Planned

### Cost Center Creation

### Transaction Allocation

### Cost Center Reports

### Department Wise P&L

---

## User Roles

### ADMIN

### ACCOUNTANT

### MANAGEMENT

---

## Benefits

* Profitability analysis
* Better cost control
* Department performance measurement

---

# 22.9 TDS MODULE

## Status

Enterprise Enhancement

---

## Purpose

Manage Tax Deducted At Source (TDS) compliance.

---

## Why This Module Is Required

Many vendor payments require statutory TDS deductions.

---

## Business Usage

Used During:

* Vendor Payment
* Professional Services
* Contract Payments

---

## Features Planned

### TDS Deduction

### TDS Ledger

### TDS Reports

### Vendor TDS Tracking

### TDS Certificate Support

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Tax compliance
* Accurate deductions
* Simplified reporting

---

# 22.10 FINANCIAL YEAR MANAGEMENT

## Status

Required (Branch-6)

---

## Purpose

Maintain accounting records separately for each financial year.

---

## Why This Module Is Required

Businesses operate across multiple financial years.

Reports, GST filings and accounting balances must be maintained year-wise.

---

## Business Usage

Used In:

* Accounting
* GST
* Reporting
* Auditing

---

## Business Flow

Financial Year Created

↓

Transactions Recorded

↓

Year End Closing

↓

Opening Balances Carried Forward

↓

New Financial Year Activated

---

## Features Planned

### Financial Year Creation

### Financial Year Selection

### Financial Year Locking

### Year End Closing

### Balance Carry Forward

### Outstanding Carry Forward

### Stock Carry Forward

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Better accounting control
* Regulatory compliance
* Easier audits
* Accurate year-wise reporting

---

## Future Enhancements

### Automated Financial Year Closing

### Financial Year Comparison Reports

### Multi Company Financial Year Management


# 23. BACKUP MANAGEMENT MODULE

## Status

Completed

---

## Purpose

Backup Management protects business data from accidental deletion, hardware failure, database corruption and operational errors.

The module ensures business continuity and disaster recovery.

---

## Why This Module Is Required

Business data is the most critical asset of the organization.

Loss of data may result in:

* Financial loss
* Operational disruption
* Compliance issues
* Customer dissatisfaction

---

## Business Usage

Used For:

* Data Protection
* Disaster Recovery
* Migration Support
* Audit Support

---

## Implemented Features

### Manual Backup

User can generate backup manually.

### Startup Auto Backup

Backup generated when ERP starts.

### Daily Scheduled Backup

Automatic backup generation.

### Backup History

View backup records.

### Download Backup

Download backup files.

### Delete Backup

Remove old backups.

### Restore Backup

Restore selected backup.

### Backup Retention

Keep latest 30 backups.

---

## Business Flow

ERP Running

↓

Backup Created

↓

Backup Stored

↓

Restore Available When Required

---

## User Roles

### ADMIN

Full Access

### ACCOUNTANT

View & Download

### USER

No Access

---

## Benefits

* Data safety
* Faster recovery
* Business continuity

---

## Future Enhancements

### Cloud Backup

### Backup Encryption

### Backup Verification

### Portable Backup Support

### Embedded MySQL Support

### Backup Path Configuration

### Backup Health Check

---

# 24. DATA IMPORT UTILITY

## Status

Required (Branch-6)

---

## Purpose

Allow bulk migration of data from existing software.

---

## Why This Module Is Required

Manual entry of thousands of records is time consuming.

Migration utilities reduce implementation effort.

---

## Features Planned

### Customer Import

### Vendor Import

### Item Import

### Opening Stock Import

### Opening Balance Import

### Excel Import

### CSV Import

---

## Business Flow

Import File

↓

Validation

↓

Preview

↓

Import

↓

Data Available In ERP

---

## User Roles

### ADMIN

### ACCOUNTANT

---

## Benefits

* Faster implementation
* Reduced manual work
* Easy migration

---

# 25. NUMBER SERIES MANAGEMENT

## Status

Required (Branch-6)

---

## Purpose

Manage numbering sequence of business documents.

---

## Why This Module Is Required

Clients migrating from existing software require numbering continuity.

---

## Features Planned

### Invoice Series

### Delivery Challan Series

### Receipt Series

### Payment Series

### Contra Series

### Journal Series

### Credit Note Series

### Debit Note Series

---

## Business Flow

Admin Configuration

↓

Series Setup

↓

Auto Number Generation

↓

Document Creation

---

## User Roles

### ADMIN

Only

---

## Benefits

* Flexible numbering
* Easy migration
* Better document control

---

# 26. USER MANAGEMENT MODULE

## Status

Planned (Branch-6)

---

## Purpose

Manage ERP users and access permissions.

---

## Features Planned

### User Creation

### Password Management

### User Status

### Role Assignment

### Login History

### Password Expiry

### User Locking

---

## Business Flow

Admin Creates User

↓

Role Assigned

↓

User Login

↓

System Access

---

## User Roles

### ADMIN

Full Access

---

## Benefits

* Security
* Accountability
* Controlled access

---

# 27. ROLE BASED ACCESS CONTROL

## Status

Planned (Branch-6)

---

## Purpose

Control user permissions according to responsibilities.

---

## Roles

### ADMIN

Full System Access

### ACCOUNTANT

Accounting Operations

Cannot Delete Backup

Cannot Restore Backup

### USER

Operational Activities

### VIEWER

Read Only

---

## Benefits

* Better security
* Controlled operations
* Reduced risk

---

# 28. SECURITY MODULE

## Status

Planned (Branch-6)

---

## Purpose

Protect ERP from unauthorized access.

---

## Features Planned

### Login Screen

### Spring Security

### JWT Authentication

### Password Encryption

### Session Management

### API Protection

### Password Policies

---

## Business Flow

Login

↓

Authentication

↓

JWT Token

↓

Authorized Access

---

## User Roles

All Users

---

## Benefits

* Secure application
* Data protection
* Controlled access

---

# 29. AUDIT LOG MODULE

## Status

Planned

---

## Purpose

Track important business activities.

---

## Why This Module Is Required

Organizations require complete traceability.

---

## Activities Tracked

### Customer Created

### Vendor Created

### Invoice Deleted

### Backup Restored

### User Login

### Configuration Changes

---

## Benefits

* Compliance
* Accountability
* Audit readiness

---

# 30. USER ACTIVITY LOG MODULE

## Status

Future Enhancement

---

## Purpose

Track user login and system usage activity.

---

## Difference From Audit Log

Audit Log:

Tracks business transactions.

User Activity Log:

Tracks user behavior.

---

## Features Planned

### Login Time

### Logout Time

### IP Address

### Failed Login Attempts

### Session Duration

---

## Benefits

* Better monitoring
* Security analysis
* User tracking

---

# 31. NOTIFICATION MODULE

## Status

Planned

---

## Purpose

Provide alerts and reminders.

---

## Features Planned

### Payment Reminder

### Low Stock Alert

### Backup Status Alert

### GST Due Reminder

### Outstanding Reminder

---

## Benefits

* Better follow-up
* Improved compliance
* Reduced delays

---

# 32. CLOUD SERVICES

## Status

Future Enhancement

---

## Purpose

Provide cloud integration and remote data protection.

---

## Features Planned

### Google Drive Backup

### AWS S3 Backup

### Azure Storage Backup

### Cloud Restore

---

## Benefits

* Offsite backup
* Better disaster recovery
* Increased reliability

---

# 33. MOBILE APPLICATION

## Status

Future Phase

---

## Purpose

Provide ERP access through mobile devices.

---

## Supported Platforms

### Android

### iOS

---

## Features Planned

### Dashboard

### Sales Entry

### Stock Lookup

### Reports

### Outstanding Tracking

---

## Benefits

* Anywhere access
* Faster decisions
* Improved productivity

---

# 34. MULTI COMPANY SUPPORT

## Status

Future Enhancement

---

## Purpose

Manage multiple companies within one ERP installation.

---

## Why This Module Is Required

Business groups often operate multiple companies.

---

## Features Planned

### Company Creation

### Company Switching

### Separate GST Numbers

### Separate Financial Data

### Separate Reports

### Separate Number Series

---

## Benefits

* Scalability
* Better administration
* Centralized management

---

# 35. CLIENT MIGRATION STRATEGY

## Status

Required

---

## Purpose

Provide smooth migration from existing software.

---

## Migration Scope

### Customer Data

### Vendor Data

### Item Data

### Opening Stock

### Opening Balances

### Invoice Series

### Delivery Challan Series

---

## Benefits

* Faster implementation
* Easier onboarding
* Reduced manual work

---

# 36. DEPLOYMENT GUIDE

## Current Deployment Architecture

React Production Build

↓

Spring Boot Fat JAR

↓

Embedded MySQL

↓

Windows Startup Script

---

## Startup Flow

Desktop Shortcut

↓

Start MySQL

↓

Start Spring Boot

↓

Open Browser

↓

Dashboard Loaded

---

## Future Enhancements

### Windows EXE Installer

### Auto Database Creation

### Auto Service Startup

---

# 37. BRANCH WISE DEVELOPMENT HISTORY

## Branch-1

Project Initialization

* React Setup
* Spring Boot Setup
* MySQL Setup

---

## Branch-2

Master Modules

* Customer
* Vendor
* Item

---

## Branch-3

Commercial Operations

* Purchase
* Sales
* Inventory

---

## Branch-4

Production & Accounting

* Production
* GST
* Financial Reports

---

## Branch-5

Backup Management

* Backup
* Restore
* Scheduler
* Retention Policy

Deployment Enhancements

* Embedded MySQL
* Production Build
* Client Deployment

---

## Branch-6 (Planned)

* Security
* User Management
* Dynamic State Dropdown
* GST Auto Detection
* Number Series Management
* Data Import Utility
* Invoice From DC

---

# 38. FUTURE ROADMAP

## Phase 1

Core ERP

(Completed)

---

## Phase 2

Business Enhancements

* Dynamic State Dropdown
* Number Series
* Data Import
* Invoice From DC

---

## Phase 3

Accounting Expansion

* Receipt Voucher
* Payment Voucher
* Contra Voucher
* Journal Voucher
* Credit Note
* Debit Note

---

## Phase 4

Enterprise Features

* Multi Company
* Financial Year Management
* Cost Centers
* TDS
* Bank Reconciliation

---

## Phase 5

Advanced Platform

* Cloud Backup
* Mobile App
* Email Integration
* WhatsApp Integration

---

# 39. CURRENT PROJECT STATUS

## Backend Completion

95%

---

## Frontend Completion

90%

---

## Database Completion

95%

---

## Overall Completion

95%

---

## Production Readiness

Ready For Internal Usage

Ready For Client Pilot Deployment

---

# 40. DEVELOPER HANDOVER NOTES

## Current Status

LedgerWorks ERP is functionally stable and suitable for continued development.

---

## Recommended Priority For Next Developer

1. Dynamic State Dropdown
2. State Master
3. Number Series Management
4. Data Import Utility
5. Invoice From Delivery Challan
6. User Management
7. Security Module

---

## Long Term Vision

LedgerWorks ERP aims to become a complete business management platform covering:

* Manufacturing
* Trading
* Inventory
* Accounting
* GST Compliance
* Production
* Dispatch Management
* Financial Reporting
* Multi Company Operations
 
===========================================================
# 41. LICENSE MANAGEMENT MODULE

## Status

Future Enhancement

---

## Purpose

Control software usage based on subscription validity and licensing terms.

This module ensures that only authorized customers can use LedgerWorks ERP during the subscribed period.

---

## Why This Module Is Required

LedgerWorks ERP is a commercial software product.

License management helps:

* Control software distribution
* Manage annual subscriptions
* Prevent unauthorized usage
* Generate recurring revenue

---

## License Model

### Initial License

Customer receives:

* 1 Year Usage License
* Product Support
* Bug Fixes
* Minor Updates

License Validity:

12 Months from Activation Date

---

## Renewal Model

After completion of first year:

Customer must purchase Annual Renewal Subscription.

Renewal Benefits:

* Continued ERP Access
* Technical Support
* Product Updates
* Security Updates
* New Features

---

## Business Flow

ERP Installation

↓

License Activation

↓

License Expiry Date Stored

↓

License Monitoring

↓

Expiry Notification

↓

Renewal Subscription

↓

License Extended

---

## Features Planned

### License Key Generation

Unique key generated for each customer.

---

### License Activation

Activate ERP installation.

---

### License Expiry Tracking

Track subscription end date.

---

### License Renewal

Extend license validity.

---

### Grace Period

Optional grace period after expiry.

Example:

License Expiry:

31-Dec-2026

Grace Period:

15 Days

System Access Allowed Until:

15-Jan-2027

---

### License Validation

Validate active license during application startup.

---

### Customer License History

Track:

* Activation Date
* Renewal Date
* Expiry Date
* Support Validity

---

## User Roles

### SUPER ADMIN

Create License

Renew License

Deactivate License

---

### CLIENT ADMIN

View License Information

View Expiry Date

---

## Benefits

* Subscription management
* Revenue protection
* Customer lifecycle tracking
* Better support management

---

## Future Enhancements

### Online License Validation

Validate license through cloud server.

---

### Hardware Based License Locking

Bind license to specific machine.

---

### Auto Renewal Reminder

Send reminders:

* 30 Days Before Expiry
* 15 Days Before Expiry
* 7 Days Before Expiry

---

### Email Renewal Notification

Automatic renewal communication.

---

### Subscription Dashboard

Display:

* Active Licenses
* Expired Licenses
* Renewal Due Licenses

---

## Recommended Commercial Policy

### Year 1

Includes:

* ERP License
* Installation
* Training
* Support
* Updates

---

### Year 2 Onwards

Annual Subscription Required

Includes:

* Continued ERP Usage
* Support
* Product Updates
* Security Updates

Without Renewal:

* ERP Access May Be Restricted
* Support Will Not Be Available
* New Updates Will Not Be Provided



through a scalable Spring Boot, React and MySQL architecture.

---

# END OF DOCUMENT

### LedgerWorks ERP FSD v2.0

### Developer Handover Edition

### Branch-6 Planning Included



