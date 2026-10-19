* Accounts do not have a reliable identifier. It's easy to make it globally unique.
  * In order to preserve renames, updates etc., we need to have a composite key like "ember+installation+number", "northstar+connectionId+id" and "paperledger+source-connection+uid"?
* Missing or empty collections wasnt explicitly stated on how to handle. I've decided to reject in order not to delete data based on incomplete input.
* Older snapshot revisions undo newer snapshot revisions.
* Revisions differ between suppliers; either normalize into own model or keep.
  * Revisions decides if allowed to import new account information. 
  * If superseded versions should become inactive, the behavior is valid. 
    * Tradeoff is losing their historical active status.

Design choices:
* All nullpointers are to be avoided with Optionals 
* Revisions are always roll forward to keep old accounts from surfacing.

Unfinished work:
* Dimension and dimension values.
  * They follow the rough outline as the accounts.
    * Create a wrapper or a similar behavior interface. Store in database.
      * The Question becomes: are all dimensions viable for all types of account. Roughly yes, most implements regionality.
* Accounts do *not* provide any functionality around what accounts contain. 