package com.rakshith.boti.interpreter;

import com.rakshith.boti.ast.Stmt;

import java.util.List;

/**
 * Something that can be called (e.g. function).
 */
public interface BotiCallable {

    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);

    /**
     * User-defined function.
     */
    record BotiFunction(Stmt.Function declaration, Environment closure) implements BotiCallable {
        @Override
        public int arity() {
            return declaration.params().size();
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Environment env = new Environment(closure);
            for (int i = 0; i < declaration.params().size(); i++) {
                env.define(declaration.params().get(i).lexeme(), arguments.get(i));
            }
            try {
                interpreter.executeBlock(declaration.body(), env);
            } catch (Return r) {
                return r.value;
            }
            return null;
        }
    }
}
