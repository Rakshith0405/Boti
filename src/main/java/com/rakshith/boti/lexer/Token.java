package com.rakshith.boti.lexer;

/**
 * A single token produced by the scanner.
 */
public record Token(TokenType type, String lexeme, Object literal, int line) {

    @Override
    public String toString() {
        return type + " " + lexeme + (literal != null ? " " + literal : "");
    }
}
