# Boti Language — Syntax and Programming Guide

This document describes the **syntax** of Boti and how to **write programs**.

---

## 1. Basics

- **Statements** end with a semicolon `;`.
- **Comments** start with `//` and go to the end of the line.
- **Identifiers** are names for variables, functions, and parameters: letters, digits, `_`; must not start with a digit.

```boti
// This is a comment.
print "hello";
```

---

## 2. Values and literals

| Kind    | Example        | Description        |
|---------|----------------|--------------------|
| Number  | `42`, `3.14`   | Double-precision   |
| String  | `"hello"`      | Double-quoted      |
| Boolean | `true`, `false`|                   |
| Nil     | `nil`          | Absence of value   |

```boti
print 100;
print 2.5;
print "Boti";
print true;
print nil;
```

---

## 3. Expressions

### 3.1 Arithmetic and comparison

| Operator | Meaning   | Example      |
|----------|-----------|--------------|
| `+` `-` `*` `/` | Add, subtract, multiply, divide | `1 + 2`, `10 / 3` |
| `-` (unary) | Negate number | `-5` |
| `==` `!=` | Equal, not equal | `a == b` |
| `<` `<=` `>` `>=` | Less, less-or-equal, greater, greater-or-equal | `x < 10` |

- `+` can also concatenate strings: `"Hello " + "world"`.
- Comparison operators require two numbers.

```boti
print 5 + 3;
print 10 - 2;
print 4 * 3;
print 15 / 4;
print -7;
print 1 + 2 == 3;
print "hi" + " there";
```

### 3.2 Logical

| Operator | Meaning | Example   |
|----------|---------|-----------|
| `!`      | Not     | `!false`  |
| `and`    | Logical and | `a and b` |
| `or`     | Logical or  | `a or b`  |

```boti
print !false;
print true and true;
print false or true;
```

### 3.3 Grouping

Use `(` `)` to control order of evaluation:

```boti
print (1 + 2) * 3;
```

### 3.4 Variables (read and assign)

- **Read:** use the variable name: `x`, `name`.
- **Assign:** `name = value;` (this is a statement).

```boti
var x = 10;
print x;
x = 20;
print x;
```

---

## 4. Statements

### 4.1 Print

Output a value. This is how you see results.

```boti
print 1 + 2;
print "Hello, Boti!";
```

### 4.2 Variables

Declare with optional initializer. Names must be declared before use (in that block or an outer one).

```boti
var a = 1;
var b = 2;
var sum = a + b;
print sum;
```

### 4.3 Blocks

Group statements with `{` `}`. Blocks create a new scope.

```boti
{
  var x = 1;
  print x;
}
{
  var x = 2;
  print x;
}
```

### 4.4 If / else

Condition must be in parentheses. One statement per branch, or use a block.

```boti
var n = 3;
if (n > 0) {
  print "positive";
} else {
  print "zero or negative";
}
```

### 4.5 While

Condition in parentheses. Body is one statement or a block.

```boti
var i = 0;
while (i < 3) {
  print i;
  i = i + 1;
}
```

### 4.6 For

Classic C-style loop: `for (init; condition; increment) body`. All three parts are optional.

```boti
for (var i = 0; i < 3; i = i + 1) {
  print i;
}
```

### 4.7 Functions

Define with `fun name(param1, param2, ...) { body }`. Call with `name(arg1, arg2, ...)`.

```boti
fun greet(name) {
  print "Hello, " + name + "!";
}
greet("Boti");

fun add(a, b) {
  return a + b;
}
print add(1, 2);
```

- **Return:** `return value;` exits the function and gives the value. `return;` returns `nil`.

### 4.8 Classes (syntax only)

Class declarations are parsed but **not fully implemented** in the interpreter yet. Syntax for reference:

```boti
class Name {
  methodName() {
    return 0;
  }
}

class Child < Parent {
  methodName() {
    return 1;
  }
}
```

---

## 5. How to write programs

### Run from a file

Save your code in a file (e.g. `program.boti`) and run:

```bash
java -jar target/boti-1.0-SNAPSHOT.jar program.boti
```

Or:

```bash
mvn exec:java -q -Dexec.mainClass="com.rakshith.boti.Boti" -Dexec.args="program.boti"
```

### REPL

Run without arguments for the interactive prompt. Type one or more statements; end each with `;`. Use `print` to see values.

```text
> print 1 + 2;
3
> var x = 10;
> print x;
10
```

### Program structure

1. **Entry point:** Boti runs from top to bottom. No special `main`; the first statement is the first one run.
2. **Define then use:** Declare variables and functions before you use them (or in an earlier line).
3. **Use blocks for scope:** Use `{ }` when you want local variables that don’t leak out.
4. **Use `print` for output:** Only `print expr;` produces visible output.

### Example: simple script

```boti
// program.boti
fun sayHi(who) {
  print "Hi, " + who + "!";
}

var name = "world";
sayHi(name);

var i = 0;
while (i < 3) {
  print i;
  i = i + 1;
}
```

### Example: function with return

```boti
fun max(a, b) {
  if (a > b) {
    return a;
  } else {
    return b;
  }
}

print max(10, 20);
print max(5, 3);
```

### Example: for loop

```boti
for (var i = 0; i < 5; i = i + 1) {
  print i * 2;
}
```

---

## 6. Syntax summary

| Category   | Syntax / keyword |
|-----------|-------------------|
| Comment   | `//` to end of line |
| Literals  | `42`, `3.14`, `"text"`, `true`, `false`, `nil` |
| Unary     | `!`, `-` |
| Binary    | `+` `-` `*` `/` `==` `!=` `<` `<=` `>` `>=` |
| Logical   | `and`, `or` |
| Grouping  | `( expr )` |
| Variable  | `name`, `name = expr` |
| Print     | `print expr;` |
| Variable decl. | `var name = expr;` or `var name;` |
| Block     | `{ stmt; stmt; ... }` |
| If        | `if ( expr ) stmt else stmt` |
| While     | `while ( expr ) stmt` |
| For       | `for ( init ; cond ; inc ) stmt` |
| Function  | `fun name ( params ) { body }` |
| Return    | `return expr;` or `return;` |
| Call      | `name ( args )` |
| Class     | `class Name { methods }` or `class Name < Super { methods }` (not fully implemented) |

---

## 7. Limits and rules

- **Semicolons:** Required after every statement (e.g. after `print expr`, `var x = 1`, `x = 2`, `return x`).
- **Parameters / arguments:** Up to 255 per function call or definition.
- **Types:** Values are numbers, strings, booleans, nil, or functions. No static types; operations are checked at runtime (e.g. `"a" + 1` is not supported; both sides of `+` must be numbers or strings).
- **Undefined variables:** Using or assigning to a name that was never declared in the current or outer scope is a runtime error.

You now have the full syntax and a practical guide to writing Boti programs.
