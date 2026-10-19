# Pine

Pine imports accounting account catalogues from Ember Accounting, Northstar Books,
and Paper Ledger snapshots. It uses Java 21, Spring Boot, and SQLite, with a browser
interface for viewing imported accounts.

## Requirements and Java setup

Use macOS, Linux, or Windows with WSL, with Git, curl 7.76.0 or newer, zip, and
unzip installed.
Install Java **21** using [SDKMAN](https://sdkman.io/install/):

```sh
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk list java
```

Choose a Temurin Java 21 identifier from the list, then press `q` to exit. Replace
`<identifier>` in both commands below with that identifier:

```sh
sdk install java <identifier>
sdk use java <identifier>
java -version
```

Check that `java -version` reports Java 21. `sdk use` selects Java for the current
terminal; see [SDKMAN usage](https://sdkman.io/usage/) for setting a default.

## Install and start

```sh
git clone https://github.com/limelights/pineapplication.git
cd pineapplication
./mvnw spring-boot:run
```

The included Maven wrapper downloads Maven and project dependencies on its first
run, so a separate Maven installation is unnecessary.

Once startup completes, open [http://localhost:8080/](http://localhost:8080/).
A fresh database starts empty; import a fixture below to populate it. Press
`Ctrl+C` in the server terminal to stop the application.

SQLite creates `pine.db` in the repository root and keeps data between restarts.
No separate database server is required. Optionally set `PINE_DATABASE_URL` to a
different SQLite JDBC URL, such as `jdbc:sqlite:/tmp/pine-demo.db`.

## Import a fixture

With the application running, open another terminal in the repository root and
post a supplied fixture:

```sh
curl --fail-with-body \
  --header 'Content-Type: application/json' \
  --data-binary @src/main/resources/pca/fixtures/paper-ledger.41.json \
  http://localhost:8080/api/snapshots/company-demo
```

The response contains `"status":"complete"` and `"type":"paper-ledger"`. Check
the recorded import outcome separately:

```sh
curl --fail-with-body \
  http://localhost:8080/api/snapshots/company-demo/status
```

On a fresh database, the status response reports provider `paperledger`, revision
`41`, status `SUCCESS`, and an empty `failures` list. The POST response's `complete`
alone does not establish success: the recorded outcome can also be `PARTIAL` or
`FAILURE`.

Refresh the browser to see the imported accounts. More fixtures are available in
`src/main/resources/pca/fixtures/`. Import successive snapshots in ascending
revision order for each provider connection; older revisions are rejected.

## Tests

From the repository root:

```sh
./mvnw test
```

## Questions, findings and unfinished work

Original notes copied verbatim from [finds.md](finds.md). They include earlier
observations that may differ from the current behavior.

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