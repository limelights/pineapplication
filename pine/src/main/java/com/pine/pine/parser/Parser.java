package com.pine.pine.parser;

import com.pine.pine.models.EmberSnapshot;
import com.pine.pine.models.NorthstarSnapshot;
import com.pine.pine.models.PaperLedgerSnapshot;

public class Parser {
    public static ParseResult fromEmber(EmberSnapshot snapshot) {
        return new ParseResult("ember", ParseResult.COMPLETE);
    }

    public static ParseResult fromNorthStar(NorthstarSnapshot snapshot) {
        return new ParseResult("north-star", ParseResult.COMPLETE);
    }

    public static ParseResult fromPaperLedger(PaperLedgerSnapshot snapshot) {
        return new ParseResult("paper-ledger", ParseResult.COMPLETE);
    }
}
