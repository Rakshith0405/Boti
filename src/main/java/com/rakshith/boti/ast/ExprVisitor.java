package com.rakshith.boti.ast;

/**
 * Visitor for expression AST nodes.
 */
public interface ExprVisitor<R> {
    R visitAssign(Expr.Assign expr);
    R visitBinary(Expr.Binary expr);
    R visitCall(Expr.Call expr);
    R visitGrouping(Expr.Grouping expr);
    R visitLiteral(Expr.Literal expr);
    R visitLogical(Expr.Logical expr);
    R visitUnary(Expr.Unary expr);
    R visitVariable(Expr.Variable expr);
}
