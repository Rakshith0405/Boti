package com.rakshith.boti.lexer;

import com.rakshith.boti.HadError;

import java.util.ArrayList;
import java.util.List;

import static com.rakshith.boti.lexer.TokenType.*;

/**
 * Scans source code and produces a list of tokens.
 */
public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start;
    private int current;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
            }
            case ' ', '\r', '\t' -> {}
            case '\n' -> line++;
            case '"' -> string();
            default -> {
                if (isDigit(c)) number();
                else if (isAlpha(c)) identifier();
                else HadError.report(line, "Unexpected character.");
            }
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            HadError.report(line, "Unterminated string.");
            return;
        }
        advance(); // closing "
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords().getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return current + 1 >= source.length() ? '\0' : source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        if (isAtEnd()) return '\0';
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        tokens.add(new Token(type, source.substring(start, current), literal, line));
    }

    private static java.util.Map<String, TokenType> keywords() {
        var m = new java.util.HashMap<String, TokenType>();
        m.put("and", AND);    m.put("class", CLASS); m.put("else", ELSE);
        m.put("false", FALSE); m.put("fun", FUN);   m.put("for", FOR);
        m.put("if", IF);      m.put("nil", NIL);    m.put("or", OR);
        m.put("print", PRINT); m.put("return", RETURN); m.put("super", SUPER);
        m.put("this", THIS);  m.put("true", TRUE);  m.put("var", VAR);
        m.put("while", WHILE);
        return m;
    }
}
