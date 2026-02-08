package com.rakshith.boti.interpreter;

import com.rakshith.boti.lexer.Token;

/**
 * Thrown when the interpreter hits a runtime error (e.g. type error, undefined variable).
 */
public class RuntimeError extends RuntimeException {

    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
