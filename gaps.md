# Assignment gaps

Remaining work against [ASSIGNMENT.md](src/main/resources/pca/ASSIGNMENT.md).
Account importing, browsing, description editing, revision protection, and import
status are already implemented.

## 1. Dimensions and dimension values

Implement persistence, refresh, availability, browsing, and local descriptions
for all three providers. Currently dimensions and dimension values are parsed
but never imported into the persisted catalogue.

## 2. Protect existing data during malformed imports

Define and test a policy that preserves a trustworthy catalogue when an import
contains invalid records. An import marked `FAILURE` can currently deactivate
existing accounts and advance the accepted revision. Preserve the last known good
catalogue when a malformed snapshot cannot safely be applied.

## 3. Improve change and failure explanations

Explain renames, deactivation, removal, and reactivation instead of only reporting
successful ingestion. Ensure malformed requests that fail before import
processing also produce useful failure information.

## 4. Harden and improve test suite

Add regression coverage for description editing and persistence across refreshes,
catalogue availability, dimension and dimension value synchronization, and
malformed-import recovery. Verify these behaviors through persistence-backed
tests and the relevant HTTP endpoints.

## 5. Complete the repeatable demonstration

Document successive fixture imports showing changed names and availability,
preserved descriptions, rejected older revisions, malformed input, and recovery.
Include dimension behavior once implemented. The README currently demonstrates
only an initial successful import.

## 6. Document current decisions

Add accurate behavior, assumptions, and tradeoffs alongside the preserved notes.
Those notes incorrectly suggest older revisions overwrite newer ones and empty
collections are rejected. Explain the chosen malformed-snapshot policy and the
remaining scope limits while retaining the verbatim findings section.

Account browsing already works through the UI. Replacing the stub
`GET /api/accounts` with JSON is an interface choice; a separate JSON browsing
endpoint is not explicitly required by the assignment.
