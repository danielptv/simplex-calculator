<h1 align="center">Simplex Calculator</h1>

<p align="center">
A console application for linear programming problems solving.
</p>

<p align="center">
  <a href="https://github.com/danielptv/simplex-calculator">
    <img src="https://github.com/danielptv/simplex-calculator/assets/93288603/09e2e1fc-f315-4623-bcd3-a7af5c865516" alt="Simplex Calculator">
  </a>
</p>

## About

The app uses the traditional simplex method as well as the two-phase method, where applicable. This program serves
educational purposes.

**Features:**

* **Optimal solutions:** Find an optimal solutions to any linear problem (if an optimal solution does exist).
* **Special problems:** Detect infeasible and unbounded problems as well as problems with multiple solutions.
* **Exact or rounded:** Calculate exact result with fractions or rounded results using decimals of variable mantissa
  length.
* **Intermediate tableaus:** Intermediate tableaus are shown and the pivot element is highlighted for each iteration.
  This way you can check the results you obtained when solving the problem manually.

**Enjoy!**

## How To Run

### Prerequisites

The app is implemented using Spring Shell and requires a recent version of Java:

* [Java](https://adoptium.net/temurin/releases/?version=20)

If you are using Windows a modern terminal and a recent version of PowerShell are also highly recommended:

* [Windows Terminal](https://github.com/microsoft/terminal)
* [PowerShell](https://github.com/PowerShell/PowerShell)

### Run as JAR

You can run the app by downloading the JAR file attached to the latest release and executing it in your terminal:

````bash
java -jar simplex-calc.jar
````

## How To Use

For general guidance and command documentation use the provided `help` command or `calc --help` for simplex
specific documentation.

### Simplex

You can start a simplex calculation by using the `calc` command:

````bash
calc --var <number of variables> --const <number of constraints> --round <false or mantissa length>
# i. e.
calc --var 2 --const 5 --round false
````

**Available options:**

| Option        | Meaning                                                                  |
|---------------|--------------------------------------------------------------------------|
| --var or -v   | Number of variables, i.e. [2].                                           |
| --const or -c | Number of constraints, i. e. [3].                                        |
| --round or -r | Mantissa length to round to, i.e. [2]. Pass [false] to disable rounding. |
| --min or -m   | Pass to minimize the problem, omit otherwise.                            |
| --help or -h  | Help for the command.                                                    |

When prompted, enter the objective function and all restrictions as comma seperated lists. Do not enter the slack
variables.

**For example, the linear problem ...**

````text
max f(x) = 6x₁ + 4x₂
Subject to:
    1x₁ + 2x₂ ≤ 3000
    2x₁ + 1x₂ ≤ 3000
    1x₁       ≤ 1100
          1x₂ ≤ 1200
    1x₁       ≥ 500
    x₁,x₂ ≥ 0
````

**... will translate to:**

````text
Objective function: 6,4
Constraint 1: 1,2<3000
Constraint 2: 2,1<3000
Constraint 3: 1,0<1100
Constraint 4: 0,1<1200
Constraint 5: 1,0>500
````

After entering all the data the solution to the problem will be calculated and the intermediate tables will be
displayed.
The last table for the example above will look like this:

````text
ITERATION 3
┌───────┬────┬─────┬──────┬──────┬─────┬────┬─────╥───────┐
│   J   │ x1 │ x2  │  s1  │  s2  │ s3  │ s4 │ s5  ║   f   │
╞═══════╪════╪═════╪══════╪══════╪═════╪════╪═════╬═══════╡
│   z   │ 0  │  0  │ 2/3  │ 8/3  │  0  │ 0  │  0  ║ 10000 │
╞═══════╪════╪═════╪══════╪══════╪═════╪════╪═════╬═══════╡
│ s3[5] │ 0  │  0  │ 1/3  │ -2/3 │  1  │ 0  │  0  ║  100  │
│ x2[2] │ 0  │  1  │ 2/3  │ -1/3 │  0  │ 0  │  0  ║ 1000  │
│ s5[7] │ 0  │  0  │ -1/3 │ 2/3  │  0  │ 0  │  1  ║  500  │
│ s4[6] │ 0  │  0  │ -2/3 │ 1/3  │  0  │ 1  │  0  ║  200  │
│ x1[1] │ 1  │  0  │ -1/3 │ 2/3  │  0  │ 0  │  0  ║ 1000  │
└───────┴────┴─────┴──────┴──────┴─────┴────┴─────╨───────┘

OPTIMAL SOLUTION
f(x)˟ = 10000
x₁˟ = 1000
x₂˟ = 1000
````

### Interpreting Solutions

The optimal solution is displayed at the very end of the output. To further interpret the solution you should also look
at the last tableau as it contains the vector of the reduced costs as well as other important information.

### Calculation Modes

Calculation modes are controlled by the `--round` option described above. Following modes are available:

* **Exact:** Calculation with simplified fractions (`--round false`).
* **Rounded**: Calculation with decimals rounded to a variable mantissa using optimal rounding and "round half to even".
  Rounding takes place after each step of the calculation (`--round [int]`).

### Number Input

Numbers can be entered as integer, fraction or decimal number, e.g. `123`, `123/321` or `123.321`.
