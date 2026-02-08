package com.rakshith.boti.ast;

import com.rakshith.boti.lexer.Token;

import java.util.List;

/**
 * Base type for all statement AST nodes.
 */
public sealed interface Stmt permits
        Stmt.Block, Stmt.Expression, Stmt.If, Stmt.Print,
        Stmt.Var, Stmt.While, Stmt.Function, Stmt.Return, Stmt.Class {

    <R> R accept(StmtVisitor<R> visitor);

    record Expression(Expr expression) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitExpression(this); }
    }

    record Print(Expr expression) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitPrint(this); }
    }

    record Var(Token name, Expr initializer) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitVar(this); }
    }

    record Block(List<Stmt> statements) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitBlock(this); }
    }

    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitIf(this); }
    }

    record While(Expr condition, Stmt body) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitWhile(this); }
    }

    record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitFunction(this); }
    }

    record Return(Token keyword, Expr value) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitReturn(this); }
    }

    record Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) implements Stmt {
        @Override public <R> R accept(StmtVisitor<R> visitor) { return visitor.visitClass(this); }
    }
}
