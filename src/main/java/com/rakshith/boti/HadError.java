package com.rakshith.boti;

/**
 * Tracks and reports compile-time and runtime errors (for exit codes and stderr).
 */
public final class HadError {
    private static boolean hadError;
    private static boolean hadRuntimeError;

    public static void setError() { hadError = true; }
    public static void setRuntimeError() { hadRuntimeError = true; }
    public static boolean hadError() { return hadError; }
    public static boolean hadRuntimeError() { return hadRuntimeError; }
    public static void reset() { hadError = false; hadRuntimeError = false; }

    public static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void report(int line, String message) {
        report(line, "", message);
    }

    public static void reportRuntime(String message, int line) {
        System.err.println(message + "\n[line " + line + "]");
        hadRuntimeError = true;
    }
}
