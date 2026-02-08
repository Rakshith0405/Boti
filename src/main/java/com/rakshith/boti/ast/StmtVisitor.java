package com.rakshith.boti.ast;

/**
 * Visitor for statement AST nodes.
 */
public interface StmtVisitor<R> {
    R visitBlock(Stmt.Block stmt);
    R visitClass(Stmt.Class stmt);
    R visitExpression(Stmt.Expression stmt);
    R visitFunction(Stmt.Function stmt);
    R visitIf(Stmt.If stmt);
    R visitPrint(Stmt.Print stmt);
    R visitReturn(Stmt.Return stmt);
    R visitVar(Stmt.Var stmt);
    R visitWhile(Stmt.While stmt);
}
