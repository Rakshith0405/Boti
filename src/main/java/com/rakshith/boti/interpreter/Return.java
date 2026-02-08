package com.rakshith.boti.interpreter;

/**
 * Control flow: return from a function (caught by BotiFunction.call).
 */
public class Return extends RuntimeException {

    public final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
