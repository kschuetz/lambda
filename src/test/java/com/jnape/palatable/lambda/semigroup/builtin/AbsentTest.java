package com.jnape.palatable.lambda.semigroup.builtin;

import com.jnape.palatable.lambda.semigroup.Semigroup;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.semigroup.builtin.Absent.absent;
import static org.junit.Assert.assertEquals;

public class AbsentTest {

    @Test
    public void semigroup() {
        Absent<Integer>    absent   = absent();
        Semigroup<Integer> addition = Integer::sum;

        assertEquals(just(3), absent.apply(addition, just(1), just(2)));
        assertEquals(nothing(), absent.apply(addition, nothing(), just(1)));
        assertEquals(nothing(), absent.apply(addition, just(1), nothing()));
        assertEquals(nothing(), absent.apply(addition, nothing(), nothing()));
    }
}