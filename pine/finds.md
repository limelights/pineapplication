* Accounts do not have a reliable identifier. It's easy to make it globally unique.
  * In order to preserve renames, updates etc., we need to have a composite key like "ember+installation+number", "northstar+connectionId+id" and "paperledger+source-connection+uid"?
* Missing or empty collections wasnt explicitly stated on how to handle. I've decided to reject in order not to delete data based on incomplete input.
* Older snapshot revisions undo newer snapshot revisions.
* Revisions differ between suppliers; either normalize into own model or keep.
  * Revisions decides if allowed to import new account information.

Design choices:
* All nullpointers are to be avoided with Optionals 
* Revisions are always roll forward to keep old accounts from surfacing.