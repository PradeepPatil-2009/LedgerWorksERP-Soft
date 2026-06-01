LedgerWorks ERP
Complete Functional Specification Document (FSD)
Version 2.0 – Developer Handover Edition
1. PROJECT OVERVIEW
Introduction
LedgerWorks ERP is a web-based Enterprise Resource Planning (ERP) application developed for small and
medium manufacturing, trading and service businesses.
The system centralizes business operations including:
• 
• 
• 
• 
• 
• 
• 
• 
• 
Customer Management
Vendor Management
Inventory Management
Production Management
Delivery Challan Management
Sales & Invoicing
Accounting
GST Reporting
Backup & Recovery
The application is designed to eliminate manual processes, improve data accuracy and provide real-time
business visibility.
Technology Stack
Frontend
• 
• 
• 
React JS
Axios
React Router
Backend
• 
• 
• 
Spring Boot
Spring Data JPA
REST APIs
1
Database
• 
MySQL 8
Major Modules
• 
• 
• 
• 
• 
• 
• 
• 
• 
• 
• 
• 
Master Management
Inventory Management
Purchase Management
Sales Management
Production Management
Delivery Challan Management
Accounting
GST
Dashboard
Backup Management
Security
Reporting
2. SYSTEM ARCHITECTURE
Architecture Flow
Frontend (React)
↓
REST API Layer
↓
Spring Boot Services
↓
Repository Layer
↓
MySQL Database
2
Benefits
Separation of Concerns
Frontend and backend are independently maintainable.
Scalability
Application can support future modules without redesign.
Easy Deployment
Application can be deployed using:
• 
• 
• 
Spring Boot JAR
Embedded MySQL
Windows EXE Launcher
3. DEPLOYMENT ARCHITECTURE
Current Production Deployment
LedgerWorks ERP is deployed using:
• 
• 
• 
• 
Spring Boot Fat JAR
Embedded MySQL Database
Windows Batch Startup
Browser App Mode
Startup Flow
StartERP.bat
↓
Start MySQL
↓
Start Spring Boot
↓
3
Open Browser
↓
Load ERP Dashboard
Future Deployment
Windows EXE Launcher
Purpose:
Provide one-click startup experience.
Benefits:
• 
• 
Easy client installation
Reduced technical dependency
4. MASTER MODULES
Master Modules store all reusable business information.
4.1 CUSTOMER MASTER
Status
Completed
Purpose
Stores customer information used throughout the ERP.
Fields
• 
• 
Customer Name
GST Number
4
• 
• 
• 
• 
• 
• 
Address
Mobile Number
Email
State
City
Contact Person
Business Usage
Used in:
• 
• 
• 
• 
Delivery Challan
Invoice
Outstanding Reports
GST Reports
Implemented Features
• 
• 
• 
• 
• 
Add Customer
Edit Customer
Delete Customer
Search Customer
Customer Listing
Business Flow
Customer Creation
↓
Customer Saved
↓
Available in DC
↓
Available in Invoice
↓
5
Available in Reports
Future Improvements
Dynamic State Dropdown
Purpose:
Prevent manual state entry errors.
GST Based State Detection
Purpose:
Auto populate state using GST number.
Example:
27XXXXXXXXXXXX
↓
Maharashtra
Customer Credit Limit
Purpose:
Control outstanding exposure.
Customer Opening Balance
Purpose:
Support migration from existing ERP systems.
Customer Category
Purpose:
Classification of customers.
6
4.2 VENDOR MASTER
Status
Completed
Purpose
Stores supplier information.
Business Usage
• 
• 
• 
Purchase Entry
Vendor Reports
Outstanding Tracking
Implemented Features
• 
• 
• 
• 
Add Vendor
Edit Vendor
Delete Vendor
Search Vendor
Future Improvements
Dynamic State Dropdown
GST Based State Detection
Vendor Opening Balance
Vendor Rating
Vendor Performance Analysis
Vendor Payment Terms
4.3 ITEM MASTER
Status
Completed
7
Purpose
Central inventory database.
Fields
• 
• 
• 
• 
• 
• 
Item Name
HSN Code
Unit
Rate
GST %
Opening Stock
Business Usage
• 
• 
• 
• 
• 
Purchase
Production
Delivery Challan
Sales
Inventory Reports
Implemented Features
• 
• 
• 
• 
Add Item
Edit Item
Delete Item
Search Item
Future Improvements
• 
• 
• 
• 
Barcode Support
QR Code Support
Item Images
Batch Tracking
4.4 STATE MASTER
Status
Planned (Branch-6)
8
Purpose
Maintain centralized state information.
Business Usage
• 
• 
• 
Customer Master
Vendor Master
GST Reporting
Planned Features
• 
• 
• 
• 
State Listing
State Code Mapping
GST Mapping
Auto State Detection Support
Benefits
• 
• 
• 
Consistent state data
Better GST reporting
Reduced manual errors
9
5. INVENTORY MANAGEMENT
Inventory Management controls stock movement throughout the ERP.
Every purchase, production, sales invoice and delivery activity impacts inventory.
5.1 STOCK INCREASE
Status
Completed
Purpose
Increase stock after:
• 
• 
• 
Purchase Entry
Production Completion
Opening Stock Entry
Business Flow
Purchase Entry
↓
Stock Ledger Update
↓
Inventory Increase
↓
Available For Sale
1
Benefits
• 
• 
• 
Real-time stock availability
Accurate inventory valuation
Better production planning
Implemented Features
• 
• 
• 
• 
Automatic stock update
Stock ledger posting
Quantity tracking
Item-wise stock monitoring
5.2 STOCK DECREASE
Status
Completed
Purpose
Reduce stock after:
• 
• 
• 
Sales Invoice
Material Issue
Stock Consumption
Business Flow
Invoice Created
↓
Stock Ledger Update
↓
Inventory Decrease
2
Benefits
• 
• 
• 
Accurate stock tracking
Prevention of over-selling
Better inventory visibility
Implemented Features
• 
• 
• 
Automatic stock reduction
Invoice linked stock movement
Production consumption tracking
5.3 LOW STOCK REPORT
Status
Completed
Purpose
Identify items below minimum stock levels.
Business Benefits
• 
• 
• 
Prevent stock shortages
Improve purchase planning
Reduce production delays
Implemented Features
• 
• 
Low stock listing
Item-wise stock visibility
3
Future Improvements
Automatic Purchase Suggestions
Purpose:
Automatically suggest purchase quantity based on stock level.
Reorder Level Alerts
Purpose:
Notify user before stock reaches critical level.
Warehouse Management
Purpose:
Manage inventory across multiple warehouses.
Godown Management
Purpose:
Track stock location-wise.
Stock Transfer
Purpose:
Move stock between warehouses.
Physical Stock Verification
Purpose:
Compare system stock vs physical stock.
Stock Adjustment
Purpose:
Correct inventory discrepancies.
4
6. PURCHASE MODULE
Status
Completed
Purpose
Manage procurement of raw materials and finished goods.
Business Flow
Vendor
↓
Purchase Entry
↓
Stock Increase
↓
Accounting Entry
Implemented Features
Purchase Entry
Create purchase transactions.
Vendor Selection
Select vendor from master.
Item Selection
Add purchased items.
5
Automatic Stock Update
Purchased quantity automatically increases stock.
Benefits
• 
• 
• 
Accurate procurement tracking
Inventory synchronization
Vendor-wise purchase reporting
Future Improvements
Purchase Return
Purpose:
Return defective or excess materials.
Purchase Approval Workflow
Purpose:
Approval process before final posting.
Purchase Order (PO)
Purpose:
Create formal order before purchase.
Business Flow:
Purchase Order
↓
Vendor Supply
↓
Purchase Entry
6
Goods Receipt Note (GRN)
Purpose:
Confirm material receipt before purchase posting.
Vendor Outstanding Aging
Purpose:
Track overdue vendor payments.
Vendor Payment Tracking
Purpose:
Monitor payment status.
7. SALES MODULE
Sales Module controls invoicing, customer billing and receivables.
7.1 SALES INVOICE
Status
Completed
Purpose
Generate customer invoices.
Business Flow
Customer
↓
7
Invoice
↓
Stock Reduction
↓
Accounting Entry
↓
Outstanding Calculation
Implemented Features
Create Invoice
Generate customer invoice.
Invoice Listing
View all invoices.
Invoice Search
Search by customer or invoice number.
Outstanding Calculation
Calculate customer pending balance.
Benefits
• 
• 
• 
Faster billing
Accurate stock reduction
Improved receivable tracking
8
Future Improvements
PDF Invoice
Generate downloadable invoice PDF.
Email Invoice
Send invoice through email.
WhatsApp Invoice
Share invoice through WhatsApp.
Quotation
Create quotation before sales order.
Business Flow:
Quotation
↓
Customer Approval
↓
Sales Order
↓
Invoice
Sales Order
Manage confirmed customer orders.
Proforma Invoice
Generate non-accounting invoice before actual billing.
9
Invoice From Sales Order
Automatically create invoice from approved order.
Customer Outstanding Aging
Analyze overdue receivables.
7.2 INVOICE OUTSTANDING
Status
Completed
Purpose
Track customer pending payments.
Benefits
• 
• 
Better collection management
Improved cash flow monitoring
Implemented Features
• 
• 
Outstanding tracking
Customer balance calculation
Future Improvements
Automated Reminder System
Purpose:
Notify customers about overdue payments.
10
7.3 INVOICE PAYMENT
Status
Completed
Purpose
Record customer payments.
Business Flow
Customer Payment
↓
Receipt Entry
↓
Outstanding Update
↓
Ledger Update
Benefits
• 
• 
Accurate customer balance
Real-time payment tracking
Future Improvements
Online Payment Gateway
Purpose:
Accept online customer payments.
11
8. PRODUCTION MODULE
Status
Completed
Purpose
Manage manufacturing operations.
Implemented Features
Production Entry
Record finished goods production.
Production Listing
View production transactions.
Material Issue
Issue raw materials for production.
Business Flow
Raw Material
↓
Material Issue
↓
Production Entry
↓
Finished Goods Stock Increase
12
Benefits
• 
• 
• 
Manufacturing tracking
Finished goods inventory control
Raw material consumption visibility
Future Improvements
BOM (Bill Of Material)
Purpose:
Define raw material requirements.
Production Costing
Purpose:
Calculate manufacturing cost.
Work Orders
Purpose:
Production planning and execution.
Batch Tracking
Purpose:
Track production batch history.
Machine Tracking
Purpose:
Monitor machine utilization.
13
Production Loss Tracking
Purpose:
Track wastage and production losses.
9. DELIVERY CHALLAN MODULE
Status
Completed
Purpose
Dispatch goods before invoice generation.
Business Flow
Customer
↓
Delivery Challan
↓
Material Dispatch
↓
Invoice Generation
Implemented Features
Challan Creation
Generate DC.
14
Challan Listing
View DC records.
Customer Mapping
Link challan with customer.
Benefits
• 
• 
• 
Supports dispatch-first businesses
Simplifies logistics process
Improves dispatch tracking
Current Delivery Challan Format
Columns:
• 
• 
• 
• 
Sr No
Item Name
HSN
Quantity
Removed Columns
• 
• 
• 
• 
• 
Rate
CGST
SGST
IGST
Amount
Reason:
Pricing is not always required during dispatch.
Future Improvements
Delivery Challan Number Series
Purpose:
15
Custom numbering for each client.
Custom Starting Number
Purpose:
Continue numbering from old software.
Example:
Old Software DC Number
↓
550
New ERP DC Starts From
↓
551
Invoice Conversion From DC
Purpose:
Generate invoice directly from challan.
Print Without Rates
Purpose:
Hide pricing during dispatch.
Print With Rates
Purpose:
Enable commercial dispatch copies.
16
Multiple Dispatch Copies
Purpose:
Generate transporter/customer/internal copies.
Client Migration Support
Purpose:
Continue numbering from previous software.
E-Way Bill Integration
Purpose:
Generate transport compliance documents.
10. ACCOUNTING MODULE
Status
Completed
Purpose
Provide complete financial accounting.
17
Implemented Reports
Ledger Accounts
Ledger Statement
Trial Balance
Profit & Loss
Balance Sheet
Cash Flow
Benefits
• 
• 
• 
Complete financial visibility
Business performance monitoring
Regulatory reporting
Future Improvements
Auto Journal Posting
Year End Closing
Cost Centers
Department Wise Accounting
11. GST MODULE
Status
Completed
Purpose
Simplify GST compliance.
18
Implemented Features
GST Report
GST Analytics
Benefits
• 
• 
GST monitoring
Tax visibility
Future Improvements
GSTR-1 Export
GSTR-3B Export
GST Reconciliation
GST Validation
GST Auto State Detection Integration
12. DASHBOARD MODULE
Status
Completed
Purpose
Provide business summary on a single screen.
Displayed Metrics
• 
• 
• 
Total Sales
Outstanding Amount
Overdue Amount
19
Overdue Count
• 
Benefits
• 
• 
• 
Faster decision making
Executive overview
Business monitoring
Future Improvements
Graphs
Monthly Trends
KPI Dashboard
Production Dashboard
Inventory Dashboard
GST Dashboard
Management MIS Dashboard
20
13. BACKUP MANAGEMENT MODULE
Status
Completed
Purpose
Protect business data against accidental loss, system failure, hardware failure and database corruption.
The backup module ensures that business operations can be restored quickly in case of any disaster.
Implemented Features
Manual Backup
User can create backup on demand.
Business Flow:
Backup Button
↓
Database Export
↓
Backup File Creation
↓
Backup History Update
Startup Auto Backup
Whenever ERP starts:
StartERP.bat
1
↓
MySQL Startup
↓
Spring Boot Startup
↓
Automatic Backup
Daily Scheduled Backup
Automatic backup generated through scheduler.
Purpose:
Ensure business continuity.
Backup History
View all available backups.
Download Backup
Download backup file locally.
Delete Backup
Remove unnecessary backup files.
Restore Backup
Restore previous database state.
Retention Policy
Keep Last 30 Backups.
2
Older backups removed automatically.
Business Benefits
• 
• 
• 
• 
Disaster Recovery
Data Protection
Migration Support
Audit Support
Future Improvements
Cloud Backup
Google Drive Integration
AWS S3 Integration
Azure Storage Integration
Backup Encryption
Protect backup files using encryption.
Backup Verification
Verify backup integrity before restore.
Portable Backup Support
Support embedded MySQL deployments.
Embedded MySQL Support
Allow ERP deployment without installing MySQL separately.
Backup Path Configuration
Allow admin to select backup location.
3
Example:
D:\LedgerWorksBackup
E:\Backup
Network Location
Backup Health Check
Validate backup usability automatically.
14. DOCUMENT NUMBER MANAGEMENT
Status
Planned (Branch-6)
Purpose
Support migration from existing ERP systems.
Allow clients to continue document numbering from previous software.
Business Problem
Client already using another ERP.
Example:
Old Software DC Number:
550
Old Software Invoice Number:
320
Without configuration:
4
New ERP starts from:
1
Result:
Duplicate numbering.
Solution
Allow custom starting numbers.
Planned Features
Delivery Challan Starting Number
Example:
551
Invoice Starting Number
Example:
321
Purchase Number Series
Receipt Number Series
Payment Voucher Number Series
Financial Year Based Numbering
Example:
5
INV-25-26-0001
INV-25-26-0002
Benefits
• 
• 
• 
Easy Migration
Number Continuity
Client Specific Configuration
15. USER MANAGEMENT MODULE
Status
Planned (Branch-6)
Purpose
Manage ERP users securely.
Planned Features
User Creation
Create system users.
Password Management
Change user passwords.
User Status
Enable or Disable users.
Role Assignment
Assign predefined roles.
6
Login History
Track login activity.
Password Expiry
Force password changes periodically.
User Locking
Lock users after multiple failed attempts.
Benefits
• 
• 
• 
Security
Accountability
Controlled Access
16. ROLE BASED ACCESS CONTROL (RBAC)
Status
Planned (Branch-6)
Roles
ADMIN
Full Access
Permissions:
• 
• 
• 
• 
Create Users
Delete Users
Backup Restore
System Configuration
7
ACCOUNTANT
Full Accounting Access
Restrictions:
• 
• 
Cannot Delete Backup
Cannot Restore Backup
USER
Operational Access
Allowed:
• 
• 
• 
• 
Sales
Purchase
Production
Delivery Challan
Restricted:
• 
Security Settings
VIEWER
Read Only Access
Cannot modify records.
Benefits
• 
• 
• 
Controlled access
Data security
Reduced operational risk
17. SECURITY MODULE
Status
8
Planned (Branch-6)
Purpose
Protect ERP against unauthorized access.
Planned Features
Spring Security
JWT Authentication
Token based security.
Login Screen
Secure authentication.
Password Encryption
BCrypt based password protection.
Session Management
Prevent unauthorized sessions.
API Protection
Secure backend APIs.
Benefits
• 
• 
• 
Data Protection
Secure Authentication
Compliance Readiness
9
18. AUDIT LOG MODULE
Status
Planned
Purpose
Track every important activity performed in ERP.
Examples
Customer Created
Store:
• 
• 
• 
User
Date
Time
Invoice Deleted
Track who deleted invoice.
Backup Restored
Track restore operations.
User Login
Track login history.
Benefits
• 
• 
• 
Traceability
Accountability
Audit Readiness
10
19. NOTIFICATION MODULE
Status
Planned
Features
Payment Reminder
Notify customer payment due.
Low Stock Alert
Notify inventory shortages.
Backup Status Alert
Notify backup success/failure.
GST Due Reminder
Notify GST filing deadlines.
Benefits
• 
• 
• 
Better follow-up
Improved compliance
Reduced operational delays
20. CLOUD SERVICES
Status
Planned
11
Features
Google Drive Backup
AWS S3 Backup
Azure Storage Backup
Benefits
Protection against:
• 
• 
• 
Hardware Failure
Local Disk Failure
Ransomware Risks
21. MOBILE APPLICATION
Status
Future Phase
Supported Platforms
Android
iOS
12
Planned Features
Dashboard
Sales Entry
Stock Lookup
Reports
Outstanding Tracking
Benefits
• 
• 
• 
Remote access
Faster decisions
Mobility
22. MULTI COMPANY SUPPORT
Status
Future Phase
Purpose
Allow multiple companies in one ERP.
13
Features
Company Creation
Company Switching
Separate GST Details
Separate Reports
Separate Financial Years
Separate Number Series
Benefits
• 
• 
• 
Multi-business management
Shared infrastructure
Scalability
23. CLIENT MIGRATION STRATEGY
Status
Planned
Purpose
Migrate existing clients from other software.
14
Supported Migration Data
Customers
Vendors
Items
Opening Stock
Outstanding Balances
Invoice Series
Delivery Challan Series
Benefits
• 
• 
• 
Faster onboarding
Reduced manual work
Easier ERP adoption
24. BRANCH-WISE DEVELOPMENT HISTORY
Branch-1
Initial Setup
• 
• 
• 
Spring Boot Setup
Database Setup
React Setup
Branch-2
Masters
• 
• 
• 
Customer Master
Vendor Master
Item Master
15
Branch-3
Commercial Operations
• 
• 
• 
Purchase
Sales
Inventory
Branch-4
Manufacturing & Accounting
• 
• 
• 
Production
Accounting Reports
GST Reports
Branch-5
Backup Management
• 
• 
• 
• 
• 
• 
• 
Manual Backup
Auto Backup
Scheduler
Restore
Download
Delete
Retention Policy
Deployment Enhancements
• 
• 
• 
• 
Embedded MySQL
React Production Build
Spring Boot Fat JAR
Client Deployment
Branch-6 (Upcoming)
• 
• 
• 
• 
• 
Security
User Management
JWT
Audit Logs
Document Number Management
16
• 
• 
• 
Dynamic State Dropdown
GST Auto State Detection
Warehouse Management
25. KNOWN ISSUES & LESSONS LEARNED
Backup Path Dependency
Some deployments require custom mysqldump configuration.
Embedded MySQL Compatibility
Backup module must support embedded MySQL paths.
Client Migration Requirement
Clients often require:
• 
• 
Existing Invoice Number Continuation
Existing DC Number Continuation
State Entry Standardization
Manual state entry can create GST reporting inconsistencies.
Solution:
Dynamic State Dropdown.
26. DEVELOPER HANDOVER NOTES
Current Completion Status
Backend Completion: 95%
Frontend Completion: 90%
17
Database Completion: 95%
Overall Completion: 95%
Production Readiness
Ready For Internal Usage
Ready For Pilot Client Deployment
Pending Enterprise Features
• 
• 
• 
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
Document Number Management
Dynamic State Dropdown
Warehouse Management
Final Vision
LedgerWorks ERP aims to become a complete ERP platform covering:
• 
• 
• 
• 
• 
• 
• 
• 
• 
Manufacturing
Trading
Accounting
Inventory
GST Compliance
Production
Dispatch
Reporting
Multi Company Operations
through a scalable Spring Boot + React architecture suitable for both small and medium businesses.
18
APPENDIX A – ADDITIONAL TECHNICAL &
ENTERPRISE REQUIREMENTS
A1. DATABASE ARCHITECTURE
Status
Planned Documentation
Purpose
Provide database reference for future developers.
The database layer is the foundation of LedgerWorks ERP and stores all transactional and master data.
Core Database Tables
Customer Master
customers
Purpose:
Store customer information.
Vendor Master
vendors
Purpose:
Store supplier information.
Item Master
items
1
Purpose:
Store inventory information.
Purchase
purchase purchase_items
Purpose:
Store purchase transactions.
Sales
invoice invoice_items
Purpose:
Store customer invoices.
Delivery Challan
delivery_challan delivery_challan_items
Purpose:
Store dispatch information.
Production
production
Purpose:
Store manufacturing transactions.
User Management
users roles
2
Purpose:
Authentication and authorization.
Backup Management
backup_history
Purpose:
Track backup activities.
Benefits
• 
• 
• 
Easier maintenance
Faster onboarding
Better troubleshooting
A2. REST API ARCHITECTURE
Status
Implemented
Purpose
Provide standardized communication between React frontend and Spring Boot backend.
Architecture
React Frontend
↓
REST API
↓
3
Spring Boot
↓
MySQL
Major APIs
Customer APIs
/api/customers
Functions:
• 
• 
• 
• 
Create Customer
Update Customer
Delete Customer
Search Customer
Vendor APIs
/api/vendors
Item APIs
/api/items
Purchase APIs
/api/purchases
Invoice APIs
/api/invoices
Delivery Challan APIs
/api/delivery-challans
4
Production APIs
/api/production
Backup APIs
/api/backups
Benefits
• 
• 
• 
Modular architecture
Easy frontend integration
Easy mobile app integration
A3. VALIDATION FRAMEWORK
Status
Partially Implemented
Purpose
Prevent invalid business data.
Customer Validation
Customer Name
Mandatory
GST Number
Must be unique.
Mobile Number
Must contain valid digits.
5
Email
Valid email format.
Vendor Validation
Same validations as customer.
Item Validation
Item Name
Mandatory
HSN
Mandatory
Quantity
Cannot be negative
Invoice Validation
Customer Mandatory
Invoice Items Mandatory
Quantity Greater Than Zero
Benefits
• 
• 
Data consistency
Reduced user errors
6
A4. OPENING BALANCE MIGRATION
Status
Planned
Purpose
Support migration from existing software.
Supported Opening Balances
Customer Opening Balance
Outstanding Receivable
Vendor Opening Balance
Outstanding Payable
Item Opening Stock
Inventory Balance
Cash Opening Balance
Cash In Hand
Bank Opening Balance
Bank Ledger Balance
Benefits
• 
• 
Faster ERP adoption
Smooth migration
7
A5. FINANCIAL YEAR MANAGEMENT
Status
Planned
Purpose
Maintain accounting data year-wise.
Example
2025-26
2026-27
2027-28
Business Usage
Reports
Year wise reports.
Numbering
Invoice numbering.
Delivery Challan numbering.
GST
Financial year reporting.
8
Benefits
• 
• 
Regulatory compliance
Better accounting control
A6. ATTACHMENT MANAGEMENT
Status
Future Phase
Purpose
Store supporting business documents.
Supported Attachments
Customer Documents
GST Certificate
PAN
Agreement
Vendor Documents
Vendor Registration
GST Certificate
Invoice Attachments
Signed Documents
9
Production Documents
Quality Reports
Inspection Reports
Benefits
• 
• 
Centralized document storage
Easy auditing
A7. REPORT EXPORT FRAMEWORK
Status
Partially Implemented
Purpose
Export business reports.
Export Formats
PDF
Invoice
Delivery Challan
Reports
Excel
Inventory Reports
GST Reports
Outstanding Reports
10
CSV
Bulk data export
Benefits
• 

Easy sharing
Better analysis
A8. ADVANCED DASHBOARD
Status
Future Phase
Purpose
Provide real-time business monitoring.
Additional Widgets
Today's Sales
Today's Production
Today's Dispatch
Today's Collections
Low Stock Alerts
Pending Invoices
Pending Payments
Benefits
• 
Faster management decisions
11
A9. SCHEDULER FRAMEWORK
Status
Partially Implemented
Purpose
Automate recurring business activities.
Implemented
Daily Backup Scheduler
Startup Backup Scheduler
Future Schedulers
Payment Reminder Scheduler
GST Reminder Scheduler
Low Stock Alert Scheduler
Outstanding Reminder Scheduler
Benefits

• 
Reduced manual effort
Better compliance
A10. FUTURE ENTERPRISE FEATURES
Status
Future Roadmap
12
Email Integration
Send invoices through email.
WhatsApp Integration
Share invoices and reminders.
SMS Integration
Critical alerts and reminders.
Approval Workflow
Purchase approvals.
Invoice approvals.
Maker Checker Process
Data verification before approval.
E-Invoice Integration
GST compliant invoicing.
E-Way Bill Integration
Transport compliance.
Barcode Printing
Inventory identification.
13
QR Code Printing
Quick item tracking.
Multi Branch Support
Support multiple business locations.
Benefits
• 
• 
Enterprise readiness
Regulatory compliance
Business scalability
FINAL PROJECT COMPLETENESS
Current Functional Coverage:
• 
• 
Masters
Inventory
Purchase
Sales
Production
Delivery Challan
Accounting
GST
Dashboard
Backup
Deployment
Security Roadmap
Migration Strategy
Overall Functional Documentation Coverage:
Approximately 99%
Ready For:

Future Developer Handover
Client Demonstration
Production Deployment
GitHub Documentation
14
Standard Report List
Reports

Customer Report
Vendor Report
Item Report
Stock Report
Low Stock Report
Purchase Report
Sales Report
Delivery Challan Report
Production Report
GST Report
Ledger Report
Trial Balance
Profit & Loss
Balance Sheet
Cash Flow

15
Error Handling Standards list
Validation Errors
Required Field Missing
Duplicate GST
Duplicate Invoice Number
Invalid Quantity
Invalid Backup Path

# APPENDIX B – FINAL DOCUMENTATION ENHANCEMENTS

## B1. APPLICATION NAVIGATION STRUCTURE

Purpose:

Provide screen navigation reference for future developers.

Application Menu Structure:

Dashboard

├── Masters
│   ├── Customer Master
│   ├── Vendor Master
│   ├── Item Master
│   └── State Master (Planned)

├── Inventory

├── Purchase

├── Sales

├── Production

├── Delivery Challan

├── Accounting

├── GST

├── Reports

├── Backup

└── Administration

Benefits:

* Faster developer onboarding
* Better understanding of UI flow

---

## B2. DATABASE NAMING STANDARDS

Purpose:

Maintain consistency across development.

Standards:

Table Names:

customer_master
vendor_master
item_master

Primary Key:

id

Foreign Key Examples:

customer_id
vendor_id
item_id

Benefits:

* Easier maintenance
* Standardized database design

---

## B3. STANDARD REPORT CATALOG

Implemented Reports:

Customer Report

Vendor Report

Item Report

Stock Report

Low Stock Report

Purchase Report

Sales Report

Production Report

Delivery Challan Report

GST Report

Ledger Statement

Trial Balance

Profit & Loss

Balance Sheet

Cash Flow

Future Reports:

Customer Aging Report

Vendor Aging Report

Production Efficiency Report

Management MIS Report

---

## B4. VALIDATION & ERROR HANDLING STANDARDS

Purpose:

Ensure data integrity.

Validation Rules:

Customer Name Mandatory

Vendor Name Mandatory

GST Number Unique

HSN Mandatory

Quantity Greater Than Zero

Invoice Must Have Customer

Invoice Must Have At Least One Item

Error Messages:

Invalid GST Number

Duplicate Invoice Number

Invalid Quantity

Invalid Backup Path

Customer Not Found

Vendor Not Found

Benefits:

* Improved data quality
* Better user experience

---

## B5. CLIENT DEPLOYMENT CHECKLIST

Purpose:

Standardize deployment process.

Deployment Steps:

1. Install Java Runtime

2. Configure Embedded MySQL

3. Configure Backup Path

4. Start MySQL

5. Start Spring Boot

6. Open ERP Dashboard

7. Verify Login

8. Verify Customer Master

9. Verify Vendor Master

10. Verify Invoice Generation

11. Verify Delivery Challan

12. Verify Backup Creation

13. Verify Backup Restore

14. Verify PDF Generation

Benefits:

* Faster deployment
* Reduced support issues

---

## B6. CURRENT DEPLOYMENT LIMITATIONS

Backup Module Limitation:

Current backup process depends on valid mysqldump path configuration.

For Embedded MySQL deployments:

mysqldump location may vary.

Future Enhancement:

Automatic mysqldump detection.

Benefits:

* Easier installation
* Reduced configuration effort

---

## B7. DOCUMENT NUMBER CONFIGURATION

Administration Setting:

Document Number Management

Configurable By Admin:

Delivery Challan Starting Number

Invoice Starting Number

Purchase Starting Number

Receipt Voucher Starting Number

Payment Voucher Starting Number

Purpose:

Avoid database changes when client requests custom numbering.

Benefits:

* Easier client migration
* Faster implementation
* Flexible numbering strategy
