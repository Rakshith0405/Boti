package com.rakshith.boti.interpreter;

import com.rakshith.boti.HadError;
import com.rakshith.boti.ast.Expr;
import com.rakshith.boti.ast.ExprVisitor;
import com.rakshith.boti.ast.Stmt;
import com.rakshith.boti.ast.StmtVisitor;
import com.rakshith.boti.lexer.Token;
import com.rakshith.boti.lexer.TokenType;

import java.util.List;

/**
 * Tree-walk interpreter: evaluates the AST.
 */
public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError e) {
            HadError.reportRuntime(e.getMessage(), e.token.line());
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Void visitExpression(Stmt.Expression stmt) {
        evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVar(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }
        environment.define(stmt.name().lexeme(), value);
        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block stmt) {
        executeBlock(stmt.statements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitIf(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            execute(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void visitWhile(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.body());
        }
        return null;
    }

    @Override
    public Void visitFunction(Stmt.Function stmt) {
        var function = new BotiCallable.BotiFunction(stmt, environment);
        environment.define(stmt.name().lexeme(), function);
        return null;
    }

    @Override
    public Void visitReturn(Stmt.Return stmt) {
        Object value = stmt.value() != null ? evaluate(stmt.value()) : null;
        throw new Return(value);
    }

    @Override
    public Void visitClass(Stmt.Class stmt) {
        environment.define(stmt.name().lexeme(), null);
        // TODO: bind methods, superclass
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment env) {
        Environment previous = this.environment;
        try {
            this.environment = env;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Object visitAssign(Expr.Assign expr) {
        Object value = evaluate(expr.value());
        environment.assign(expr.name(), value);
        return value;
    }

    @Override
    public Object visitBinary(Expr.Binary expr) {
        Object left = evaluate(expr.left());
        Object right = evaluate(expr.right());
        switch (expr.operator().type()) {
            case MINUS -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left - (double) right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator(), left, right);
                if ((double) right == 0) throw new RuntimeError(expr.operator(), "Division by zero.");
                return (double) left / (double) right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left * (double) right;
            }
            case PLUS -> {
                if (left instanceof Double l && right instanceof Double r) return l + r;
                if (left instanceof String || right instanceof String) return stringify(left) + stringify(right);
                throw new RuntimeError(expr.operator(), "Operands must be two numbers or two strings.");
            }
            case GREATER -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator(), left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL -> { return !isEqual(left, right); }
            case EQUAL_EQUAL -> { return isEqual(left, right); }
            default -> throw new RuntimeError(expr.operator(), "Unknown binary operator.");
        }
    }

    @Override
    public Object visitCall(Expr.Call expr) {
        Object callee = evaluate(expr.callee());
        List<Object> arguments = new java.util.ArrayList<>();
        for (Expr arg : expr.arguments()) {
            arguments.add(evaluate(arg));
        }
        if (!(callee instanceof BotiCallable function)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren(), "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGrouping(Expr.Grouping expr) {
        return evaluate(expr.expression());
    }

    @Override
    public Object visitLiteral(Expr.Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitLogical(Expr.Logical expr) {
        Object left = evaluate(expr.left());
        if (expr.operator().type() == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right());
    }

    @Override
    public Object visitUnary(Expr.Unary expr) {
        Object right = evaluate(expr.right());
        return switch (expr.operator().type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> {
                checkNumberOperand(expr.operator(), right);
                yield -(double) right;
            }
            default -> throw new RuntimeError(expr.operator(), "Unknown unary operator.");
        };
    }

    @Override
    public Object visitVariable(Expr.Variable expr) {
        return environment.get(expr.name());
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean b) return b;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double d) {
            String text = d.toString();
            return text.endsWith(".0") ? text.substring(0, text.length() - 2) : text;
        }
        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }
}
