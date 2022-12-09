# Simplex Calculator

This is a simple Java console application for linear programming problems using the Two-Phase-Simplex method.

**The calculator can detect optimal solutions to any linear problem. It supports two modes:**
* **Exact:** Calculation with simplified fractions
* **Rounded**: Calculation with numbers rounded to a certain mantissa length after each step. The mantissa length can be chosen by the user.

### Valid input:
* Fractions: Input as fraction or integer, e.g. *'123'* or *'123/321'*
* Decimal numbers: Input as decimal number or integer, e.g. *'123.4'* or *'123'*
* Number separation: Numbers should be separated by a comma, e.g.  *'123,321'*
* Constraints: Put a relation sign before the target function value, e.g. *'123,-432>234'*

### Features to be implemented:
* Detection for problems without an optimal solution
* Input for equalities with *'='*
* Support for minimisation problems
