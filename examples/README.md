# Boti Example Programs

Real-world style programs written in Boti. Edit the configuration variables at the top of each file, then run from the **project root** (the `Lox` folder):

```bash
# Option 1: Maven (no package step). Keep the closing quote on -Dexec.args!
mvn exec:java -q -Dexec.mainClass="com.rakshith.boti.Boti" -Dexec.args="examples/compound_interest.boti"

# Option 2: JAR (run "mvn package" first if the jar doesn't exist)
mvn package
java -jar target/boti-1.0-SNAPSHOT.jar examples/compound_interest.boti
```

| Program | Real use | What it does |
|--------|----------|----------------|
| **compound_interest.boti** | Savings, investments | Future value and year-by-year growth from principal, rate, and years |
| **loan_payment.boti** | Mortgages, loans | Monthly payment and total interest for a fixed-rate loan |
| **unit_converter.boti** | Daily conversions | Temperature (C/F/K), distance (km/mi), mass (kg/lb); includes a small table |
| **math_toolbox.boti** | Learning, quick math | Factorial, GCD, primality check, Fibonacci |
| **quadratic_solver.boti** | Math, physics | Solves ax² + bx + c = 0 (real roots) |
| **bmi_calculator.boti** | Health | BMI from weight/height and category; table for one weight at several heights |
| **primes_up_to_n.boti** | Number theory | Lists all primes up to N and counts them |
| **growth_table.boti** | Investing | Growth over time with a simple ASCII bar chart |
| **tip_and_split.boti** | Restaurants | Tip amount, total bill, and per-person share |
| **break_even.boti** | Business | Break-even units and revenue from fixed cost, price, and unit cost |

Each file has a short header with configuration variables you can change (e.g. principal, rate, years, weight, bill amount).
