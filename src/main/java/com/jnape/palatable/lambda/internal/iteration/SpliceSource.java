package com.jnape.palatable.lambda.internal.iteration;

public class SpliceSource<A> {
    private final Iterable<A> source;
    private final int startOffset;
    private final int replaceCount;

    public SpliceSource(Iterable<A> source, int startOffset, int replaceCount) {
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

    public SpliceSourceState<A> initState(SpliceSourceState<A> next) {
        return new SpliceSourceState<>(startOffset, replaceCount, source.iterator(), next);
    }

}
