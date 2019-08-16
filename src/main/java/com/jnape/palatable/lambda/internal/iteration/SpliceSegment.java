package com.jnape.palatable.lambda.internal.iteration;

public final class SpliceSegment<A> {

    private final Iterable<A> source;
    private final int startOffset;
    private final int replaceCount;

    public SpliceSegment(int startOffset, int replaceCount, Iterable<A> source) {
        this.source = source;
        this.startOffset = startOffset;
        this.replaceCount = replaceCount;
    }

    public Iterable<A> getSource() {
        return source;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getReplaceCount() {
        return replaceCount;
    }

}
