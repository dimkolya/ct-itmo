"use strict";

const cnst = c => () => c;

const pi = cnst(Math.PI);
const e = cnst(Math.E);

const CONSTANTS = {
    "pi": pi,
    "e": e
};

const VARIABLES = {
    "x": 0,
    "y": 1,
    "z": 2
};

const variable = name => {
    return (...values) => values[VARIABLES[name]];
}

const mathOperation = (operator) => {
    let operation = (...args) => (...values) => operator(...args.map(expression => expression(...values)));
    Object.defineProperty(operation, "arity", { value: operator.length });
    return operation;
}

const add = mathOperation((a, b) => a + b);
const subtract = mathOperation((a, b) => a - b);
const multiply = mathOperation((a, b) => a * b);
const divide = mathOperation((a, b) => a / b);
const negate = mathOperation(a => -a);
const avg3 = mathOperation((a, b, c) => (a + b + c) / 3);
const med5 = mathOperation((a, b, c, d, e) => [a, b, c, d, e].sort((a, b) => a - b)[2]);

const OPERATIONS = {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "negate": negate,
    "avg3": avg3,
    "med5": med5
};

const parse = revPolishExpr => {
    const stack = [];
    for (let token of revPolishExpr.split(' ').filter(element => element.length > 0)) {
        if (token in CONSTANTS) {
            stack.push(CONSTANTS[token]);
        } else if (token in VARIABLES) {
            stack.push(variable(token));
        } else if (token in OPERATIONS) {
            let operation = OPERATIONS[token];
            stack.push(operation(...stack.splice(stack.length - operation.arity)));
        } else {
            stack.push(cnst(parseFloat(token)));
        }
    }
    return stack.pop();
}

