# LedgerWorks ERP

# Branch-Develop - Missing Features Detailed Specification

--------------------------------------------------------------

# 1. COMPANY SETTINGS MODULE

## Status

Required (Branch -Develop)

---

## Purpose

Company Settings acts as the central configuration module of the ERP.

All company-related information used across invoices, reports, GST documents and printed forms is maintained here.

---

## Why This Module Is Required

Without centralized company information:

* Company details must be entered repeatedly.
* Printed documents become inconsistent.
* GST reports may contain incorrect information.
* Maintenance becomes difficult.

---

## Business Usage

Used In:

* Sales Invoice
* Delivery Challan
* Purchase Invoice
* GST Reports
* Balance Sheet
* Ledger Reports
* PDF Documents

---

## Features Required

### Company Name

Stores organization name.

---

### GST Number

Stores GST registration number.

---

### PAN Number

Stores PAN details.

---

### Address

Stores company address.

---

### Bank Details

Stores:

* Bank Name
* Account Number
* IFSC Code
* Branch Name

---

### Logo Upload

Used in:

* Invoice
* Delivery Challan
* Reports

---

### Footer Message

Printed automatically in documents.

---

## Business Flow

Admin

↓

Open Company Settings

↓

Enter Information

↓

Save

↓

Used Across ERP

---

## Advantages

* Centralized configuration
* Easier maintenance
* Professional documents
* Better GST compliance

---

## Supported Roles

### ADMIN

Full Access

### ACCOUNTANT

Read Only

### USER

No Access

---

## Backend Components Required

### Entity

CompanySettings

### Repository

CompanySettingsRepository

### Service

CompanySettingsService

### Controller

CompanySettingsController

### DTO

CompanySettingsDTO

---

## Frontend Pages Required

CompanySettingsPage.jsx

--------------------------------------------------------------

# 2. STATE MASTER MODULE

## Status

Required (Branch -Develop)

---

## Purpose

Maintain standardized Indian states and GST state codes.

---

## Why This Module Is Required

Manual state entry causes:

* Spelling errors
* Wrong GST reports
* Duplicate values

---

## Features Required

### State Name

### State Code

### GST Code

### Active Status

---

## Business Usage

Used In:

* Customer Master
* Vendor Master
* GST Module

---

## Business Flow

State Created

↓

Customer Entry

↓

Dropdown Selection

↓

Record Saved

---

## Advantages

* Standardization
* Better GST compliance
* Reduced manual errors

---

## Supported Roles

ADMIN

ACCOUNTANT

USER

VIEWER

---

## Backend Components

StateEntity

StateRepository

StateService

StateController

StateDTO

---

## Frontend Pages

StateMasterPage.jsx

--------------------------------------------------------------

# 3. GST AUTO STATE DETECTION

## Purpose

Automatically identify state using GST number.

---

## Example

GST:

27ABCDE1234F1Z5

↓

27

↓

Maharashtra

---

## Why Required

Prevents wrong state selection.

---

## Advantages

* Faster data entry
* Better GST accuracy
* Reduced mistakes

---

## Working

User enters GST Number

↓

First two digits extracted

↓

State identified

↓

Dropdown auto-selected

---

## Backend

GSTUtilityService

---

## Frontend

Customer Form

Vendor Form

--------------------------------------------------------------

# 4. NUMBER SERIES MANAGEMENT

## Status

Required

---

## Purpose

Maintain document numbering sequence.

---

## Supported Documents

* Invoice
* Purchase
* Delivery Challan
* Receipt Voucher
* Payment Voucher
* Journal Voucher
* Credit Note
* Debit Note

---

## Why This Module Is Required

Clients migrating from other software need numbering continuity.

---

## Example

Invoice Prefix:

INV

Current Number:

1050

Generated Number:

INV-1051

---

## Advantages

* Flexible numbering
* Easy migration
* Better document tracking

---

## Roles Supported

ADMIN Only

---

## Backend

NumberSeriesEntity

NumberSeriesRepository

NumberSeriesService

NumberSeriesController

---

## Frontend

NumberSeriesPage.jsx

--------------------------------------------------------------

# 5. DATA IMPORT UTILITY

## Status

Required

---

## Purpose

Import bulk data using Excel or CSV.

---

## Import Supported

* Customers
* Vendors
* Items
* Opening Stock
* Opening Balance

---

## Working

Upload Excel

↓

Validation

↓

Preview

↓

Import

↓

Data Available

---

## Advantages

* Faster implementation
* Easy migration
* Reduced manual work

---

## Roles

ADMIN

ACCOUNTANT

---

## Backend

ImportController

ExcelParserService

CSVParserService

ImportService

---

## Frontend

ImportPage.jsx

--------------------------------------------------------------

# 6. OPENING STOCK MODULE

## Status

Required

---

## Purpose

Load existing stock before ERP Go-Live.

---

## Features

* Opening Quantity
* Opening Value
* Batch Import
* Excel Upload

---

## Working

Opening Stock Entered

↓

Validation

↓

Stock Ledger Updated

↓

Inventory Available

---

## Advantages

* Smooth migration
* Correct valuation
* Accurate reports

---

## Roles

ADMIN

ACCOUNTANT

---

## Backend

OpeningStockEntity

OpeningStockService

OpeningStockController

---

## Frontend

OpeningStockPage.jsx

--------------------------------------------------------------

# 7. INVOICE FROM DELIVERY CHALLAN

## Status

Required

---

## Purpose

Generate Invoice directly from Delivery Challan.

---

## Why Required

Avoid duplicate data entry.

---

## Working

Delivery Challan

↓

Approved

↓

Generate Invoice

↓

Invoice Created

↓

Stock Updated

↓

Accounting Entry

---

## Advantages

* Faster billing
* Reduced errors
* Improved productivity

---

## Roles

ADMIN

ACCOUNTANT

---

## Backend

InvoiceConversionService

InvoiceConversionController

---

## Frontend

ConvertInvoicePage.jsx

--------------------------------------------------------------

# 8. FINANCIAL YEAR MANAGEMENT

## Status

Required

---

## Purpose

Maintain transactions year-wise.

---

## Features

* Financial Year Creation
* Active Year
* Lock Previous Year
* Year End Closing
* Carry Forward Balances

---

## Advantages

* Better audit support
* Accurate reporting
* Regulatory compliance

---

## Roles

ADMIN

ACCOUNTANT

---

## Backend

FinancialYearEntity

FinancialYearService

FinancialYearController

---

## Frontend

FinancialYearPage.jsx

--------------------------------------------------------------

# 9. RECEIPT VOUCHER MODULE

## Purpose

Record money received from customers.

---

## Payment Modes

* Cash
* Bank
* UPI

---

## Working

Customer Payment

↓

Receipt Voucher

↓

Outstanding Reduced

↓

Ledger Updated

---

## Advantages

* Accurate customer balance
* Better tracking

---

## Roles

ADMIN

ACCOUNTANT

--------------------------------------------------------------

# 10. PAYMENT VOUCHER MODULE

## Purpose

Record payments made to vendors.

---

## Working

Vendor Payment

↓

Voucher Created

↓

Outstanding Updated

↓

Ledger Updated

---

## Advantages

* Better payable control
* Accurate accounting

--------------------------------------------------------------

# 11. CREDIT NOTE MODULE

## Purpose

Handle sales returns and discounts.

---

## Benefits

* Correct GST adjustment
* Customer balance accuracy

--------------------------------------------------------------

# 12. DEBIT NOTE MODULE

## Purpose

Handle purchase returns.

---

## Benefits

* Vendor accounting accuracy

--------------------------------------------------------------

# 13. CONTRA VOUCHER MODULE

## Purpose

Transfer money between cash and bank.

---

## Benefits

* Proper bank reconciliation
* Better cash management

-----------------------------------------------------------

# 14. AUDIT LOG MODULE

## Purpose

Track important system activities.

---

## Events Tracked

* Customer Created
* Invoice Deleted
* Backup Restored
* User Login
* Configuration Changes

---

## Advantages

* Compliance
* Accountability
* Audit Readiness

---

## Roles

ADMIN

---

# END
