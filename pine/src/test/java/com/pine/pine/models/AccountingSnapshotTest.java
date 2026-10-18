package com.pine.pine.models;

import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountingSnapshotTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("snapshots")
    void extractsProviderAccounts(String filename, Class<? extends Accounting> snapshotType,
                                 List<String> expectedIds, List<String> expectedNames) throws IOException {
        var snapshot = (Accounting) SnapshotHelper.asSnapshot(filename, snapshotType);
        var accounts = snapshot.getAccounts();

        assertEquals(expectedIds, accounts.stream().map(Account::getId).toList());
        assertEquals(expectedNames, accounts.stream().map(Account::getName).toList());
    }

    static Stream<Arguments> snapshots() {
        return Stream.of(
                Arguments.of("ember-accounting.9001.json", EmberSnapshot.class,
                        List.of("BANK", "COSTS"), List.of("Settlement bank", "Service costs")),
                Arguments.of("ember-accounting.9002.json", EmberSnapshot.class,
                        List.of("BANK", "COSTS", "TAX"),
                        List.of("Main settlement bank", "Service costs", "Tax balance")),
                Arguments.of("northstar-books.7.json", NorthstarSnapshot.class,
                        List.of("account-bank", "account-revenue"), List.of("Operating bank", "Product revenue")),
                Arguments.of("northstar-books.8.json", NorthstarSnapshot.class,
                        List.of("account-bank", "account-receivables"),
                        List.of("Main operating bank", "Trade receivables")),
                Arguments.of("paper-ledger.41.json", PaperLedgerSnapshot.class,
                        List.of("urn:paper:account:cash", "urn:paper:account:expenses"),
                        List.of("Cash account", "Operating expenses")),
                Arguments.of("paper-ledger.42.json", PaperLedgerSnapshot.class,
                        List.of("urn:paper:account:cash", "urn:paper:account:expenses", "urn:paper:account:payables"),
                        List.of("Primary cash account", "Operating expenses", "Trade payables")),
                Arguments.of("paper-ledger.43.json", PaperLedgerSnapshot.class,
                        List.of("urn:paper:account:cash", "urn:paper:account:expenses", "urn:paper:account:payables"),
                        List.of("Treasury cash", "Operating expenses reopened", "Trade payables")),
                Arguments.of("paper-ledger.44.json", PaperLedgerSnapshot.class,
                        Arrays.asList("urn:paper:account:cash", null),
                        List.of("Cash changed by quarantined snapshot", "Record without a stable identifier")),
                Arguments.of("paper-ledger.45.json", PaperLedgerSnapshot.class,
                        List.of("urn:paper:account:cash", "urn:paper:account:payables", "urn:paper:account:expenses"),
                        List.of("Corporate cash", "Trade payables", "Operating expenses reopened")));
    }
}
