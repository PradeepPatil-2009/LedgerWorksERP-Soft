LedgerWorks ERP
Complete Functional Specification Document (FSD)
Technical Reference & Development Roadmap
Version: Branch-5 Project Status: 95% Complete
1. PROJECT OVERVIEW
LedgerWorks ERP is a web-based ERP application developed using:
Frontend:
• 
• 
• 
React JS
Axios
React Router
Backend:
• 
• 
• 
Spring Boot
Spring Data JPA
REST APIs
Database:
• 
MySQL 8
Purpose: Provide a complete ERP solution for small and medium manufacturing and trading businesses.
Major Areas:
• 
• 
• 
• 
• 
• 
• 
• 
• 
Masters
Inventory
Sales
Purchase
Production
Accounting
Reporting
Backup & Recovery
Security
1
2. SYSTEM ARCHITECTURE
Frontend (React) ↓ REST APIs ↓ Spring Boot ↓ Service Layer ↓ Repository Layer ↓ MySQL Database
Benefits:
• 
• 
• 
• 
Separation of concerns
Easy maintenance
Easy deployment
Scalable architecture
3. MASTER MODULES
Customer Master
Status: Completed
Purpose: Stores customer information used across the ERP.
Fields:
• 
• 
• 
• 
• 
Customer Name
GST Number
Address
Mobile
Email
Business Usage: Used in:
• 
• 
• 
• 
Invoice
Delivery Challan
Outstanding Reports
GST Reports
Implemented Features:
• 
• 
• 
• 
• 
Add Customer
Update Customer
Delete Customer
Search Customer
Customer Listing
2
Future Improvements:
• 
• 
• 
Customer Credit Limit
Customer Category
Customer Document Upload
Vendor Master
Status: Completed
Purpose: Stores supplier information.
Business Usage: Used in Purchase module.
Implemented Features:
• 
• 
• 
• 
Add Vendor
Edit Vendor
Delete Vendor
Search Vendor
Future Improvements:
• 
• 
Vendor Rating
Vendor Performance Analysis
Item Master
Status: Completed
Purpose: Central inventory database.
Business Usage: Used in:
• 
• 
• 
• 
Purchase
Production
Sales
Stock Reports
Implemented Features:
• 
• 
Add Item
Edit Item
Delete Item
3
• 
Search Item
• 
Future Improvements:
• 
• 
• 
• 
Barcode
QR Code
Item Images
Batch Tracking
4. INVENTORY MANAGEMENT
Stock Increase
Status: Completed
Purpose: Adds inventory after purchase or production.
How It Works: Purchase Entry → Stock Ledger Update → Inventory Increase
Benefits: Accurate stock availability.
Stock Decrease
Status: Completed
Purpose: Reduces inventory after sales or consumption.
How It Works: Invoice/Production → Stock Ledger Update → Inventory Decrease
Benefits: Real-time stock tracking.
Low Stock Report
Status: Completed
Purpose: Identifies stock below minimum level.
Benefits: Prevents stock shortages.
4
Future Improvements:
• 
• 
Automatic Purchase Suggestion
Reorder Level Alerts
5. PURCHASE MODULE
Status: Completed
Purpose: Manages procurement process.
Implemented Features:
• 
• 
• 
• 
Purchase Entry
Vendor Selection
Item Selection
Stock Update
Business Flow: Vendor → Purchase Entry → Stock Increase
Future Improvements:
• 
• 
Purchase Return
Purchase Approval Workflow
6. SALES MODULE
Sales Invoice
Status: Completed
Purpose: Generates customer invoices.
Implemented Features:
• 
• 
• 
• 
Create Invoice
Invoice Listing
Invoice Search
Outstanding Calculation
Business Flow: Customer → Invoice → Stock Reduction → Accounting Entry
5
Future Improvements:
• 
• 
• 
PDF Invoice
Email Invoice
WhatsApp Invoice
Invoice Outstanding
Status: Completed
Purpose: Tracks pending customer payments.
Benefits: Improves collection management.
Future Improvements:
• 
Automated Reminder System
Invoice Payment
Status: Completed
Purpose: Records customer payments.
Benefits: Updates outstanding balance automatically.
Future Improvements:
• 
Online Payment Gateway
7. PRODUCTION MODULE
Status: Completed
Purpose: Supports manufacturing operations.
Implemented Features:
• 
• 
• 
Production Entry
Production List
Material Issue
6
Business Flow: Raw Material → Production → Finished Goods
Benefits: Tracks manufacturing activities.
Future Improvements:
• 
• 
• 
BOM (Bill of Material)
Production Costing
Work Orders
8. DELIVERY CHALLAN MODULE
Status: Completed
Purpose: Dispatch goods before invoicing.
Implemented Features:
• 
• 
• 
Challan Creation
Challan Listing
Customer Mapping
Benefits: Supports dispatch-first business model.
Future Improvements:
• 
E-Way Bill Integration
9. ACCOUNTING MODULE
Status: Completed
Implemented Reports:
• 
• 
• 
• 
• 
• 
Ledger Accounts
Ledger Statement
Trial Balance
Profit & Loss
Balance Sheet
Cash Flow
Benefits: Provides complete financial visibility.
7
Future Improvements:
• 
• 
• 
Auto Journal Posting
Year End Closing
Cost Centers
10. GST MODULE
Status: Completed
Implemented Features:
• 
• 
GST Report
GST Analytics
Benefits: Simplifies GST compliance.
Future Improvements:
• 
• 
• 
GSTR-1 Export
GSTR-3B Export
GST Reconciliation
11. DASHBOARD MODULE
Status: Completed
Displayed Metrics:
• 
• 
• 
• 
Total Sales
Outstanding Amount
Overdue Amount
Overdue Count
Benefits: Single-screen business summary.
Future Improvements:
• 
• 
• 
Graphs
Monthly Trends
KPI Dashboard
8
12. BACKUP MANAGEMENT MODULE
Status: Completed
Implemented Features:
• 
• 
• 
• 
• 
• 
• 
• 
Manual Backup
Startup Auto Backup
Daily Scheduled Backup
Backup History
Download Backup
Delete Backup
Restore Backup
Keep Last 30 Backups
Business Benefits:
• 
• 
• 
• 
Disaster Recovery
Data Protection
Audit Support
Migration Support
Future Improvements:
• 
• 
• 
Cloud Backup
Backup Encryption
Backup Verification
13. USER MANAGEMENT MODULE
Status: Planned (Branch-6)
Features Planned:
• 
• 
• 
• 
User Creation
Password Management
User Status
Role Assignment
Benefits: Improves security and accountability.
9
14. ROLE BASED ACCESS CONTROL
Roles:
• 
• 
• 
• 
ADMIN
ACCOUNTANT
USER
VIEWER
Updated Rules:
ADMIN
• 
Full Access
ACCOUNTANT
• 
• 
• 
USER
• 
Full Access
Cannot Delete Backup
Cannot Restore Backup
Operational Activities Only
VIEWER
• 
Read Only Access
15. SECURITY MODULE
Status: Planned
Branch-6 Features:
• 
• 
• 
• 
• 
• 
Spring Security
JWT Authentication
Login Screen
Password Encryption
Session Management
API Protection
10
16. AUDIT LOG MODULE
Status: Planned
Purpose: Track every business activity.
Examples:
• 
• 
• 
• 
Customer Created
Invoice Deleted
Backup Restored
User Login
Benefits: Complete traceability.
17. NOTIFICATION MODULE
Status: Planned
Features:
• 
• 
• 
• 
Payment Reminder
Low Stock Alert
Backup Status Alert
GST Due Reminder
18. CLOUD SERVICES
Status: Planned
Features:
• 
• 
• 
Google Drive Backup
AWS S3 Backup
Azure Storage Backup
Benefits: Protection against hardware failure.
11
19. MOBILE APP
Status: Future Phase
Features:
• 
• 
• 
• 
Dashboard
Sales Entry
Stock Lookup
Reports
Platform:
• 
• 
Android
iOS
20. BRANCH-WISE COMPLETION
Branch-1
• 
Initial Setup
Branch-2
• 
Customer & Vendor
Branch-3
• 
Sales & Purchase
Branch-4
• 
Production & Accounting
Branch-5
• 
• 
• 
• 
• 
• 
• 
• 
Backup Management
Scheduler
Auto Backup
Restore
Download
Delete
Retention Policy
Role Matrix Documentation
12
Branch-6 (Upcoming)
• 
• 
• 
• 
• 
Security
Login
User Management
JWT
Audit Logs
CURRENT PROJECT STATUS
Backend Completion: 95%
Frontend Completion: 90%
Database Completion: 95%
Overall Project Completion: 95%
Production Readiness: Ready for Internal Usage
Pending Enterprise Features:
• 
• 
• 
• 
• 
• 
Security
Audit Logs
Notifications
Cloud Backup
Mobile App
Multi Company Support


=======================================
Missing Requirements / Enhancements
1. Customer Master Enhancement
Dynamic State Dropdown

Status: Required

Purpose:
Instead of manual state entry, user should select state from predefined Indian State list.

Benefits:

Prevent spelling mistakes.
Correct GST calculations.
Consistent reporting.

Business Flow:
Customer Creation → Select State → Save Customer.

2. Vendor Master Enhancement
Dynamic State Dropdown

Status: Required

Purpose:
Vendor state should be selected from dropdown instead of free text.

Benefits:

Accurate GST calculations.
Standardized vendor records.

Business Flow:
Vendor Creation → Select State → Save Vendor.

3. GST-Based State Auto Detection
Future Enhancement

Purpose:
Automatically detect State from first two digits of GST Number.

Example:

27 → Maharashtra

29 → Karnataka

24 → Gujarat

Business Flow:
GST Entered → State Auto Populate → User Verification.

4. Delivery Challan Number Series Control
Status: Required

Purpose:
Allow company to start Delivery Challan numbering from any desired number.

Example:

Current Software:
DC-1500

New LedgerWorks ERP:
DC-1501

instead of

DC-00001

Benefits:

Easy migration from existing ERP.
Continuous document numbering.
5. Invoice Number Series Control
Status: Required

Purpose:
Allow invoice sequence to begin from user-defined number.

Example:

INV-5600

INV-5601

INV-5602

Benefits:

Business continuity.
Existing software migration support.
6. Migration Support Module
Status: Required

Purpose:
Support businesses migrating from existing software.

Features:

Custom Invoice Series
Custom DC Series
Opening Customer Outstanding
Opening Vendor Outstanding
Opening Stock Import
7. Delivery Challan Print Format Enhancement
Status: Required

Current Columns:

Sr No
Item
HSN
Quantity
Rate
CGST
SGST
IGST
Amount
Required Format

Keep Only:

Sr No
Item Name
HSN
Quantity

Remove:

Rate
CGST
SGST
IGST
Amount

Business Reason:
Delivery Challan is dispatch document only.

Price information should remain hidden.

8. Delivery Challan Print Variants
Status: Future Enhancement
Print Without Rates

Dispatch Copy

Print With Rates

Internal Copy

Multiple Dispatch Copies
Original
Duplicate
Transport Copy
Office Copy
9. Invoice From Delivery Challan
Status: Required

Purpose:
Generate Sales Invoice directly from Delivery Challan.

Business Flow:

Delivery Challan

↓

Dispatch

↓

Invoice Generation

↓

Accounting Entry

10. Multi Company Support
Status: Future Enhancement

Purpose:
Single application supporting multiple companies.

Features:

Company Selection
Separate Invoice Series
Separate GST Numbers
Separate Financial Reports
11. Inventory Enhancements
Future Enhancements
Stock Reservation

Reserve stock against Sales Order.

Negative Stock Control

Prevent stock from going below zero.

Stock Aging Report

Identify slow-moving inventory.

Godown/Warehouse Management

Multiple stock locations.

12. Purchase Module Enhancements
Purchase Return

Return purchased material to vendor.

Purchase Approval Workflow

Approval before stock posting.

Vendor Outstanding Tracking

Track payable balances.

Purchase Aging Report

Track pending vendor payments.

13. Sales Process Enhancements
Quotation

Create customer quotations.

Sales Order

Convert quotation to sales order.

Proforma Invoice

Generate advance invoice.

Invoice From Sales Order

Convert SO directly into invoice.

Customer Outstanding Aging

Track overdue receivables.

14. Backup System Enhancements
Portable Backup Support

Application should work with embedded MySQL.

Embedded MySQL Support

No external MySQL installation required.

Backup Path Configuration

User-defined backup location.

Backup Verification

Verify generated backup integrity.

15. Deployment Enhancements
One Click Startup

Desktop Shortcut:

LedgerWorks ERP

Automatically starts:

MySQL
Spring Boot
Browser
EXE Installer

Windows installation package.

Auto Database Initialization

Database creation during first launch.

16. Audit Enhancements
Document Number Change Log

Track:

Invoice number changes
DC number changes
Master data modifications
17. Reports Enhancements
Customer Ledger Summary
Vendor Ledger Summary
Item Wise Sales Report
Item Wise Purchase Report
Monthly Sales Report
Monthly Purchase Report
GST Summary Report
18. Production Module Enhancements
Bill of Material (BOM)
Production Costing
Work Orders
Finished Goods Cost Calculation
Summary

The most important missing requirements from your current FSD are:

Dynamic State Dropdown (Customer)
Dynamic State Dropdown (Vendor)
GST Auto State Detection
Custom Delivery Challan Number Series
Custom Invoice Number Series
Migration Support
Delivery Challan Print Without Rate/Tax/Amount
Invoice From Delivery Challan
Multi Company Support
Purchase Return
Quotation
Sales Order
Proforma Invoice
Customer Outstanding Aging
Portable Backup + Embedded MySQL
One-Click Desktop Deployment