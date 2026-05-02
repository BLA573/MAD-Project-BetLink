# BetLink MVP Plan and Execution

Source used: `BetLink_Project_Proposal_Final.docx` (Project sections 5, 6, 7, 8, 13 and appendices).

## MVP Goal
Build a focused Android-first app that helps users **find and reserve affordable short-stay accommodation** with a simple trust-first flow.

## Scope Chosen from Proposal (Must-have + pilot-ready)
1. Account access (light login)
2. Listing browsing with search and filters (location + price + verification)
3. Booking request flow
4. Booking status confirmation
5. Host-side request approval/rejection dashboard

Out of scope for this implementation phase: maps SDK, push/SMS integration, payment, media upload backend, reviews persistence.

## Step-by-step Execution

### Step 1 - Convert starter app to pure Java Android UI stack
- Removed Compose/Kotlin-specific setup.
- Enabled Java + ViewBinding + XML layouts.
- Updated dependency catalog for AppCompat, Material, RecyclerView.

### Step 2 - Implement account access entry
- Added `MainActivity` as MVP launcher and flow selector.
- Added `LoginActivity` with basic role selection (Traveler/Host).

### Step 3 - Implement traveler search and filters
- Added `SearchActivity`.
- Added in-memory data layer (`MockRepository`, `Listing`).
- Implemented query, max price, and verified-only filtering.

### Step 4 - Implement listing details and booking request
- Added `ListingDetailsActivity`.
- Added `BookingRequest` model and creation workflow.
- Captured check-in/check-out and created pending booking request.

### Step 5 - Implement booking status + host journey
- Added `BookingStatusActivity` for traveler confirmation updates.
- Added `HostDashboardActivity` for host actions.
- Added booking approval/rejection flow using shared repository state.

## Deliverables in this phase
- Java-only app source for all newly implemented logic.
- XML layouts for all MVP screens and list rows.
- String resources for user-facing content.
- Manifest/activity wiring for full end-to-end demo flow.

## Next MVP increments (proposal-aligned)
1. Map pin/location integration.
2. Real authentication and backend API.
3. Listing photo upload and verification workflow.
4. Reviews and host listing management.
5. Notification channel (push/SMS).

