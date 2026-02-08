package com.rakshith.boti;

import com.rakshith.boti.lexer.Scanner;
import com.rakshith.boti.lexer.Token;
import com.rakshith.boti.parser.Parser;
import com.rakshith.boti.ast.Stmt;
import com.rakshith.boti.interpreter.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the Boti interpreter.
 */
public final class Boti {

    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: boti [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, StandardCharsets.UTF_8));
        if (HadError.hadError()) System.exit(65);
        if (HadError.hadRuntimeError()) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        try (var reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
             var bufferedReader = new BufferedReader(reader)) {
            for (;;) {
                System.out.print("> ");
                String line = bufferedReader.readLine();
                if (line == null) break;
                run(line);
                HadError.reset();
            }
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (HadError.hadError()) return;

        interpreter.interpret(statements);
    }
}
