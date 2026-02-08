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

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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
        Terminal terminal = null;
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (Exception ignored) {
            // Not a TTY (e.g. piped input); fall back to plain readLine
        }
        if (terminal != null) {
            try {
                LineReader lineReader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .build();
                for (;;) {
                    String line;
                    try {
                        line = lineReader.readLine("> ");
                    } catch (UserInterruptException e) {
                        continue; // Ctrl+C: clear line and prompt again
                    } catch (EndOfFileException e) {
                        break;    // Ctrl+D: exit
                    }
                    if (line == null) break;
                    run(line.trim());
                    HadError.reset();
                }
            } finally {
                try { terminal.close(); } catch (IOException ignored) { }
            }
        } else {
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
