package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;

public class SpliceSourceState<A> {
    private int startOffset;
    private final int replaceCount;
    private Iterator<A> source;
    private SpliceSourceState<A> next;

    public SpliceSourceState(int startOffset, int replaceCount, Iterator<A> source, SpliceSourceState<A> next) {
        this.startOffset = startOffset;
        this.replaceCount = replaceCount;
        this.source = source;
        this.next = next;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void decStartOffset() {
        startOffset -= 1;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getReplaceCount() {
        return replaceCount;
    }

    public Iterator<A> getSource() {
        return source;
    }

    public SpliceSourceState<A> getNext() {
        return next;
    }

    public void setNext(SpliceSourceState<A> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "[" + startOffset + ":" + replaceCount + ":" + (source.hasNext() ? "+" : "_")
                + "] ";
    }
}
