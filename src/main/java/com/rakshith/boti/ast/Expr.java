package com.rakshith.boti.ast;

import com.rakshith.boti.lexer.Token;

/**
 * Base type for all expression AST nodes.
 */
public sealed interface Expr permits
        Expr.Assign, Expr.Binary, Expr.Call, Expr.Grouping,
        Expr.Literal, Expr.Logical, Expr.Unary, Expr.Variable {

    <R> R accept(ExprVisitor<R> visitor);

    record Binary(Expr left, Token operator, Expr right) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitBinary(this); }
    }

    record Grouping(Expr expression) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitGrouping(this); }
    }

    record Literal(Object value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitLiteral(this); }
    }

    record Unary(Token operator, Expr right) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitUnary(this); }
    }

    record Variable(Token name) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitVariable(this); }
    }

    record Assign(Token name, Expr value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitAssign(this); }
    }

    record Logical(Expr left, Token operator, Expr right) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitLogical(this); }
    }

    record Call(Expr callee, Token paren, java.util.List<Expr> arguments) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitCall(this); }
    }
}
