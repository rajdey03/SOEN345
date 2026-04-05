# E2E Test Suite

This folder contains Maestro end-to-end tests for the Android app.

## App ID

`com.example.ticketreservationapp`

## Prerequisites

- Android emulator or device is running.
- The backend is running and reachable from the emulator at `http://10.0.2.2:8080`.
- The app is installed on the emulator/device.
- Maestro CLI is installed.

## Quick Start

From the repo root:

```powershell
npm test
```

That command runs the full Maestro suite in `e2e/run-all.yaml` and auto-generates unique default test data for registration and admin event names.

## Useful Scripts

From the repo root:

```powershell
npm test
npm run e2e
npm run e2e:admin:create
npm run e2e:admin:edit
npm run e2e:admin:cancel
npm run e2e:customer:register
npm run e2e:customer:login
npm run e2e:customer:reserve
npm run e2e:customer:cancel
npm run e2e:validation
```

## Optional Custom Test Data

If you want to override the defaults:

```powershell
$env:TEST_EMAIL="diego.qa.custom@example.com"
$env:TEST_PHONE="+15145550222"
$env:TEST_PASSWORD="password123"
$env:TEST_EVENT_TITLE="Diego QA Event 2026"
$env:UPDATED_EVENT_TITLE="Diego QA Event 2026 Updated"
$env:DEMO_ORGANIZER_ID="e8fedc08-e40c-40a7-b003-074543dee3f8"
npm test
```

The admin create flow uses the organizer ID `44444444-4444-4444-4444-444444444444` by default.

## Coverage

- Customer registration
- Customer login
- Event browsing
- Event filtering/search
- Ticket reservation
- Reservation viewing
- Reservation cancellation
- Admin portal access
- Admin event creation
- Admin event editing
- Admin event cancellation
- Basic validation/error checks

## Main Suite

- `run-all.yaml`: full regression path

## Individual Flows

- `admin-create-event.yaml`
- `admin-edit-event.yaml`
- `admin-cancel-event.yaml`
- `customer-register.yaml`
- `customer-login.yaml`
- `customer-browse-and-reserve.yaml`
- `customer-view-and-cancel-reservation.yaml`
- `validation-checks.yaml`
