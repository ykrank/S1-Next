package cl.monsoon.s1next.data.event;

public final class QuoteEvent {

    private final String quotePostId;
    private final String quotePostCount;

    public QuoteEvent(String quotePostId, String quotePostCount) {
        this.quotePostId = quotePostId;
        this.quotePostCount = quotePostCount;
    }

    public String getQuotePostId() {
        return quotePostId;
    }

    public String getQuotePostCount() {
        return quotePostCount;
    }
}
