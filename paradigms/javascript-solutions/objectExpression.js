"use strict";

function Const(value) {
    this.value = value;
}
Const.ZERO = new Const(0);
Const.ONE = new Const(1);
Const.prototype.evaluate = function() {
    return this.value;
};
Const.prototype.toString = function() {
    return this.value.toString();
};
Const.prototype.prefix = function() {
    return this.value.toString();
};
Const.prototype.postfix = function() {
    return this.value.toString();
};
Const.prototype.diff = function() {
    return Const.ZERO;
};

function Variable(name) {
    this.name = name;
    this.index = Variable.VARIABLES[name];
}
Variable.VARIABLES = {
    "x": 0,
    "y": 1,
    "z": 2
};
Variable.prototype.evaluate = function(...args) {
    return args[this.index];
};
Variable.prototype.toString = function() {
    return this.name;
};
Variable.prototype.prefix = function() {
    return this.name;
};
Variable.prototype.postfix = function() {
    return this.name;
};
Variable.prototype.diff = function(name) {
    return this.name === name ? Const.ONE : Const.ZERO;
};

const MathOperation = function(...operands) {
    this.operands = operands;
};
MathOperation.prototype.evaluate = function(...args) {
    return this.operator(...this.operands.map(expression => expression.evaluate(...args)));
};
MathOperation.prototype.toString = function() {
    return this.operands.join(' ') + ' ' + this.token;
};
MathOperation.prototype.prefix = function() {
    return "(" + this.token + " " + this.operands.map(f => f.prefix()).join(' ') + ")";
};
MathOperation.prototype.postfix = function() {
    return "(" + this.operands.map(f => f.postfix()).join(' ') + " " + this.token + ")";
};
MathOperation.prototype.diff = function(name) {
    return this.operatorDiff(...this.operands, ...this.operands.map(f => f.diff(name)));
};

let OPERATIONS = {};

const makeMathOperation = function(operator, token, operatorDiff) {
    let Operation = function(...args) {
        MathOperation.call(this, ...args);
    };
    Operation.arity = operator.length;
    Operation.prototype = Object.create(MathOperation.prototype);
    Operation.prototype.constructor = Operation;
    Operation.prototype.operator = operator;
    Operation.prototype.token = token;
    Operation.prototype.operatorDiff = operatorDiff;
    OPERATIONS[token] = Operation;
    return Operation;
};

const Add = makeMathOperation((a, b) => a + b, "+",
    (a, b, da, db) => new Add(da, db));

const Subtract = makeMathOperation((a, b) => a - b, "-",
    (a, b, da, db) => new Subtract(da, db));

const Multiply = makeMathOperation((a, b) => a * b, "*",
    (a, b, da, db) => new Add(new Multiply(da, b), new Multiply(a, db)));

const Divide = makeMathOperation((a, b) => a / b, "/",
    (a, b, da, db) => new Divide(new Subtract(new Multiply(da, b), new Multiply(a, db)), new Multiply(b, b)));

const Negate = makeMathOperation(a => -a, "negate",
    (a, da) => new Negate(da));

const Gauss = makeMathOperation((a, b, c, x) => a * Math.exp((x - b) * (b - x) / (2 * c * c)),
    "gauss",
    function (a, b, c, x, da, db, dc, dx) {
        let subtractXB = new Subtract(x, b);
        return new Subtract(
            new Multiply(da, new Gauss(Const.ONE, b, c, x)),
            new Multiply(
                new Gauss(a, b, c, x),
                new Divide(
                    new Multiply(
                        subtractXB,
                        new Subtract(
                            new Multiply(c, new Subtract(dx, db)),
                            new Multiply(subtractXB, dc)
                        )
                    ),
                    new Multiply(c, new Multiply(c, c))
                )
            )
        )
    }
);

const sumexp = (...args) => args.reduce((a, b) => a + Math.exp(b), 0);

const Exp = makeMathOperation(x => Math.exp(x), "exp",
    (x, dx) => new Multiply(new Exp(x), dx));

const sumExpMulOperand = (args) => {
    let result = Const.ZERO;
    for (let i = 0; i < args.length / 2; i++) {
        result = new Add(result, new Multiply(new Exp(args[i]), args[args.length / 2 + i]));
    }
    return result;
};

const Sumexp = makeMathOperation(sumexp, "sumexp",
    (...args) => sumExpMulOperand(args));

const Softmax = makeMathOperation((...args) => Math.exp(args[0]) / sumexp(...args), "softmax",
    (...args) => {
        if (args.length === 0) {
            return Const.ZERO;
        }
        let halfLength = args.length / 2;
        let halfArgs = args.slice(0, halfLength);
        return new Divide(
            new Subtract(
                new Multiply(new Exp(args[0]), args[halfLength]),
                new Multiply(
                    sumExpMulOperand(args),
                    new Softmax(...halfArgs),
                )
            ),
            new Sumexp(...halfArgs)
        );
    }
);

const parse = reversePolishExpression => {
    const stack = [];
    for (let token of reversePolishExpression.split(' ').filter(element => element.length > 0)) {
        if (token in Variable.VARIABLES) {
            stack.push(new Variable(token));
        } else if (token in OPERATIONS) {
            let operation = OPERATIONS[token];
            stack.push(new operation(...stack.splice(stack.length - operation.arity)));
        } else {
            stack.push(new Const(parseFloat(token)));
        }
    }
    return stack.pop();
};

function ParserError(message) {
    Error.call(this, message);
    this.message = message;
}
ParserError.prototype = Object.create(Error.prototype);
ParserError.prototype.constructor = ParserError;
ParserError.prototype.name = "ParserError";

const makeParser = (takeOperator) => (source) => {
    let it = 0;
    const next = () => {
        while (it < source.length && source[it] === ' ') {
            it++;
        }
        if (it === source.length) {
            return null;
        }
        if (source[it] === '(' || source[it] === ')') {
            return source[it++];
        } else {
            let token = "";
            while (it < source.length && !(source[it] === ' ' || source[it] === '(' || source[it] === ')')) {
                token += source[it++];
            }
            return token;
        }
    };

    function parseExpression() {
        if (currentToken === null) {
            throw new ParserError("Unexpected eof.");
        } else if (currentToken === '(') {
            return parseOperation();
        } else if (currentToken in Variable.VARIABLES) {
            return new Variable(currentToken);
        } else if (!isNaN(currentToken)) {
            return new Const(Number(currentToken));
        } else {
            throw new ParserError("Expected constant or variable, found: " + currentToken
                + " at index " + (it - currentToken.length) + ".");
        }
    }
    const parseOperation = () => {
        let startIndex = it - 1;
        let operands = [];
        let operatorIndex;
        currentToken = next();
        while (currentToken !== ')' && currentToken !== null) {
            if (currentToken in OPERATIONS) {
                if (operatorIndex !== undefined) {
                    throw new ParserError("Found second operator in one bracket level at index " + startIndex + ".");
                }
                operands.push(currentToken);
                operatorIndex = it - currentToken.length;
            } else {
                operands.push(parseExpression());
            }
            currentToken = next();
        }
        if (currentToken === null) {
            throw new ParserError("Expected ')', found eof.");
        }
        let operatorToken = takeOperator(operands);
        if (!(operatorToken in OPERATIONS)) {
            throw new ParserError("Expected operator, found: " + operatorToken
                + " in one bracket level at index " + startIndex + ".");
        }
        let operator = OPERATIONS[operatorToken];
        if (operator.arity !== 0 && operands.length !== operator.arity) {
            throw new ParserError("Operands count error for operator '" + operatorToken + "' at index " + operatorIndex
                + ". Expected: " + operator.arity + ", found: " + operands.length + ".");
        }
        return new operator(...operands);
    };
    let currentToken = next();
    let result = parseExpression();
    currentToken = next();
    if (currentToken !== null) {
        throw new ParserError("Expected eof, found: " + currentToken + " at index " + it + ".");
    }
    return result;
};

const parsePrefix = makeParser(args => args.shift());
const parsePostfix = makeParser(args => args.pop());
