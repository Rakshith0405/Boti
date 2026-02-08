# Boti Interpreter

Skeleton structure for the **Boti** language interpreter. Main class: **`com.rakshith.boti.Boti`**.

---

## Install Boti (for people who just want to run your language)

**One command** — same idea as installing Python or Java from the internet. Your friend runs this and then can use `boti`:

```bash
curl -sSL https://raw.githubusercontent.com/Rakshith0405/Boti/main/scripts/install.sh | bash
```

Then open a new terminal (or run `source ~/.zshrc` or `source ~/.bashrc`) and run:

```bash
boti                    # REPL
boti my_program.boti    # run a script
```

**Requirements for this to work:** You publish a release on GitHub with the right zip files (see **Distribution for your friends** below). Repo: [Rakshith0405/Boti](https://github.com/Rakshith0405/Boti).

**Manual install:** If they prefer not to pipe a script, they can download the zip for their OS from [GitHub Releases](https://github.com/Rakshith0405/Boti/releases), unzip it, and add the `bin` folder to their PATH. No Java or Maven needed.

---

## Run like Python (no “Java” in the command)

Users run Boti the same way they run Python: **`boti`** for the REPL, **`boti script.boti`** to run a file. They never need to type `java` or know that Java is behind it.

**One-time setup (after cloning or unpacking):**

```bash
mvn package
chmod +x bin/boti
```

**Add `bin` to your PATH**, or set `BOTI_HOME` to the Boti directory and add `$BOTI_HOME/bin` to PATH. Then:

```bash
boti                          # REPL
boti examples/compound_interest.boti
```

- **macOS / Linux:** use the `bin/boti` script. It runs the JAR with Java, or a native executable if you built one (see below).
- **Windows:** use `bin\boti.bat` or add `bin` to PATH and run `boti`.

**Optional – single executable (no Java required on the user’s machine):**  
If you build a native image, the launcher will use it when present so end users don’t need Java installed. This step is **optional**; the JAR + `bin/boti` launcher is enough for normal use.

The native build **requires GraalVM** (your current JDK is a regular OpenJDK, which does not include the `gu` / native-image tool). If you see *"‘gu’ tool was not found in your JAVA_HOME"*, do one of the following:

1. **Skip the native build**  
   Use only the JAR and launcher: `mvn package` (no `-Pnative`). Users run `boti` and `boti script.boti`; Java must be installed but they never type `java`.

2. **Install GraalVM and then build the native image**  
   - **macOS / Linux (SDKMAN):**  
     `sdk install java 21.0.2-graalce` then `sdk use java 21.0.2-graalce`  
   - **Or** download [GraalVM for Java 21](https://www.graalvm.org/downloads/) and set `JAVA_HOME` to the GraalVM directory.  
   Then in the same shell:

   ```bash
   mvn package -Pnative
   ```

   This produces `target/boti` (or `target/boti.exe` on Windows). The `bin/boti` launcher will use this binary when it exists.

## Distribution for your friends (no Java or Maven on their machine)

You want others to **install Boti from the internet** (like Java or Python) and then write and run `.boti` programs. They do **not** install or maintain Java or Maven.

**You (language author) do this when you release a version:**

1. **Build the distribution** (bundled JRE so their machine doesn’t need Java):

   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS; or set to your JDK 21 path
   ./scripts/build-dist.sh
   ```

2. **Publish a GitHub Release:** In your repo go to **Releases** → **Create a new release**. Tag e.g. `v1.0`, upload `boti-1.0-darwin.zip`, `boti-1.0-linux.zip`, and (if you have it) `boti-1.0-windows.zip`. Publish.

3. The install script already points at **Rakshith0405/Boti**. Once you publish a release with the zips, the one-line install will work.

**Your friend then:** **Option A** — run the install one-liner from **Install Boti** above (latest release, installs to `~/.local/boti`, PATH updated). **Option B** — download the zip for their OS from your GitHub Releases, unzip, add the `bin` folder to PATH.

They never see or install Java or Maven. The unzipped folder contains `bin/`, `lib/`, and `jre/`; the launcher uses the bundled `jre/` automatically.

## Language syntax and how to write programs

See **[docs/LANGUAGE.md](docs/LANGUAGE.md)** for:

- Full **syntax** (literals, operators, statements, functions, classes)
- **How to write programs** (files, REPL, examples, limits)

## Example programs (real use cases)

The **[examples/](examples/)** folder contains runnable Boti programs for real tasks:

| Program | Use case |
|--------|----------|
| `compound_interest.boti` | Savings / investment growth |
| `loan_payment.boti` | Mortgage / loan monthly payment |
| `unit_converter.boti` | Temperature, distance, weight |
| `math_toolbox.boti` | Factorial, GCD, primes, Fibonacci |
| `quadratic_solver.boti` | Solve ax² + bx + c = 0 |
| `bmi_calculator.boti` | BMI and category |
| `primes_up_to_n.boti` | List primes up to N |
| `growth_table.boti` | Investment growth with bar chart |
| `tip_and_split.boti` | Tip and bill split |
| `break_even.boti` | Break-even units and revenue |

See [examples/README.md](examples/README.md) for how to run them.

## Package layout

```
com.rakshith.boti
├── Boti.java             # Entry point (REPL + run file)
├── HadError.java         # Error tracking and reporting
├── lexer/
│   ├── Token.java
│   ├── TokenType.java
│   └── Scanner.java      # Source → tokens
├── parser/
│   └── Parser.java       # Tokens → AST
├── ast/
│   ├── Expr.java         # Expression nodes
│   ├── ExprVisitor.java
│   ├── Stmt.java         # Statement nodes
│   └── StmtVisitor.java
└── interpreter/
    ├── Interpreter.java  # AST → execution
    ├── Environment.java # Variable scope
    ├── RuntimeError.java
    ├── BotiCallable.java # Functions
    └── Return.java      # Return control flow
```

## Build and run

```bash
# Build (compile only)
mvn compile

# Run REPL (no script file)
mvn exec:java -q -Dexec.mainClass="com.rakshith.boti.Boti"

# Run a script (keep the closing quote!)
mvn exec:java -q -Dexec.mainClass="com.rakshith.boti.Boti" -Dexec.args="examples/compound_interest.boti"
```

To use the JAR directly (or the `boti` launcher, see **Run like Python** above):

```bash
mvn package
java -jar target/boti-1.0-SNAPSHOT.jar
java -jar target/boti-1.0-SNAPSHOT.jar examples/compound_interest.boti
```

## Quick example

```text
> print 1 + 2;
3
> var x = 10;
> print x * 2;
20
```

More examples and full syntax: [docs/LANGUAGE.md](docs/LANGUAGE.md).

---

## Publish to GitHub

To push this project to [Rakshith0405/Boti](https://github.com/Rakshith0405/Boti) (or your fork):

```bash
git init
git add .
git commit -m "Initial commit: Boti interpreter"
git branch -M main
git remote add origin https://github.com/Rakshith0405/Boti.git
git push -u origin main
```

After the first push, create a **Release** (tag e.g. `v1.0`) and upload the zips from `./scripts/build-dist.sh` so the one-line install works.
