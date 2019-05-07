package testsupport.applicatives;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Bifunctor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public final class InvocationRecordingBifunctor<A, B> implements Bifunctor<A, B, InvocationRecordingBifunctor<?, ?>> {
    private final AtomicReference<Function<?, ?>> leftFn;
    private final AtomicReference<Function<?, ?>> rightFn;

    public InvocationRecordingBifunctor(AtomicReference<Function<?, ?>> leftFn,
                                        AtomicReference<Function<?, ?>> rightFn) {
        this.leftFn = leftFn;
        this.rightFn = rightFn;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C, D> InvocationRecordingBifunctor<C, D> biMap(Fn1<? super A, ? extends C> lFn,
                                                           Fn1<? super B, ? extends D> rFn) {
        leftFn.set(lFn);
        rightFn.set(rFn);
        return (InvocationRecordingBifunctor<C, D>) this;
    }
}
