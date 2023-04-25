(defn div
  ([operand] (/ 1.0 operand))
  ([first & rest] (/ (double first) (apply * rest))))
(defn sumexp-operator [& operands]
  (apply + (mapv #(Math/exp (double %)) operands)))
(defn softmax-operator [& operands]
  (/ (Math/exp (double (first operands))) (apply sumexp-operator operands)))
(defn bit-impl-operator [a b]
  (java.lang.Double/longBitsToDouble (bit-or
                                       (bit-not (java.lang.Double/doubleToLongBits a))
                                       (java.lang.Double/doubleToLongBits b))))
(defn bit-iff-operator [a b]
  (java.lang.Double/longBitsToDouble (bit-not (bit-xor
                                                (java.lang.Double/doubleToLongBits a)
                                                (java.lang.Double/doubleToLongBits b)))))
(defn bit-logical [operator]
  (fn [a b]
    (java.lang.Double/longBitsToDouble (operator
                                         (java.lang.Double/doubleToLongBits a)
                                         (java.lang.Double/doubleToLongBits b)))))

(def constant constantly)

(defn variable [name]
  (fn [vars]
    (get vars name)))

(defn math-ext [extOperator]
  (fn [& operands]
    (fn [vars]
      (apply extOperator (map #(% vars) operands)))))

(def add (math-ext +))
(def subtract (math-ext -))
(def multiply (math-ext *))
(def divide (math-ext div))
(def negate subtract)
(def sumexp (math-ext sumexp-operator))
(def softmax (math-ext softmax-operator))

(def FUNCTION_OPERATIONS
  {'+       add
   '-       subtract
   '*       multiply
   '/       divide
   'negate  negate
   'sumexp  sumexp
   'softmax softmax
   })



(load-file "proto.clj")

(def evaluate (method :evaluate))
(def toString (method :toString))
(def toStringInfix (method :toStringInfix))
(def diff (method :diff))

(declare ZERO)
(defn Constant [value]
  {:evaluate      (constantly value)
   :toString      (constantly (str value))
   :toStringInfix (constantly (str value))
   :diff          (constantly ZERO)
   })
(def ZERO (Constant 0))
(def ONE (Constant 1))

(defn Variable [name]
  {:evaluate      (fn [this vars] (get vars (str (Character/toLowerCase (first name)))))
   :toString      (constantly (str name))
   :toStringInfix (constantly (str name))
   :diff          (fn [this diffName]
                    (if (= name diffName)
                      ONE
                      ZERO))})

(def _operator (field :operator))
(def _token (field :token))
(def _diff (field :operatorDiff))

(def mathOperation
  {:evaluate      (fn [this vars]
                    (apply (_operator this) (mapv #(evaluate % vars) (this :operands))))
   :toString      (fn [this]
                    (str "(" (_token this) " " (clojure.string/join " " (map #(toString %) (this :operands))) ")"))
   :toStringInfix (fn [this]
                    (if (== 1 (count (this :operands)))
                      (str (_token this) "(" (toStringInfix (first (this :operands))) ")")
                      (str "(" (clojure.string/join
                                 (str " " (_token this) " ")
                                 (map #(toStringInfix %) (this :operands))) ")")))
   :diff          (fn [this name]
                    ((_diff this) (this :operands) (mapv #(diff % name) (this :operands))))
   })

(defn makeMathOperation [operator token operatorDiff]
  (constructor
    (fn [this & operands] (assoc this :operands operands))
    (assoc mathOperation :operator operator :token token :operatorDiff operatorDiff)))

(def Add (makeMathOperation + "+" (fn [x dx] (apply Add dx))))
(def Subtract (makeMathOperation - "-" (fn [x dx] (apply Subtract dx))))
(def Negate (makeMathOperation - "negate" (fn [x dx] (apply Negate dx))))

(declare Multiply)
(defn iter-diff-mul [x dx]
  (let [rest-x (rest x)]
    (if (== 1 (count x))
      (first dx)
      (Add
        (Multiply (first dx) (apply Multiply rest-x))
        (Multiply (first x) (iter-diff-mul rest-x (rest dx)))))))
(def Multiply (makeMathOperation * "*" (fn [x dx] (iter-diff-mul x dx))))
(def Divide (makeMathOperation div "/" (fn [x dx]
                                         (let [rest-x (rest x)
                                               denom (apply Multiply rest-x)]
                                           (if (== 1 (count x))
                                             (let [first-x (first x)] (Negate (Divide
                                                                                (first dx)
                                                                                (Multiply first-x first-x))))
                                             (Divide
                                               (Subtract
                                                 (Multiply (first dx) (apply Multiply rest-x))
                                                 (Multiply (first x) (iter-diff-mul rest-x (rest dx))))
                                               (Multiply denom denom)))))))

(def Exp (makeMathOperation #(Math/exp (double %)) "exp" (fn [x dx] (Multiply (Exp x) dx))))

(defn sumExpMulOperand [x dx]
  (if (== 0 (count x))
    ZERO
    (Add (Multiply (Exp (first x)) (first dx)) (sumExpMulOperand (rest x) (rest dx)))))

(def Sumexp (makeMathOperation sumexp-operator "sumexp" sumExpMulOperand))
(def Softmax (makeMathOperation softmax-operator "softmax" (fn [x dx]
                                                             (Divide
                                                               (Subtract
                                                                 (Multiply (Exp (first x)) (first dx))
                                                                 (Multiply (sumExpMulOperand x dx) (apply Softmax x)))
                                                               (apply Sumexp x)))))

(def BitAnd (makeMathOperation (bit-logical bit-and) "&" nil))
(def BitOr (makeMathOperation (bit-logical bit-or) "|" nil))
(def BitXor (makeMathOperation (bit-logical bit-xor) "^" nil))
(def BitImpl (makeMathOperation bit-impl-operator "=>" nil))
(def BitIff (makeMathOperation bit-iff-operator "<=>" nil))

(def OBJECT_OPERATIONS
  {'+       Add
   '-       Subtract
   '*       Multiply
   '/       Divide
   'negate  Negate
   'sumexp  Sumexp
   'softmax Softmax
   '&       BitAnd
   '|       BitOr
   (symbol "^") BitXor
   '=>      BitImpl
   '<=>     BitIff
   })

(defn abstractParser [CONSTANT VARIABLE OPERATIONS]
  (fn [stringExpression]
    ((fn parseString [expression]
       (cond
         (number? expression) (CONSTANT expression)
         (symbol? expression) (VARIABLE (str expression))
         :else (apply (get OPERATIONS (first expression)) (map parseString (rest expression)))))
     (read-string stringExpression))))

(def parseFunction (abstractParser constant variable FUNCTION_OPERATIONS))
(def parseObject (abstractParser Constant Variable OBJECT_OPERATIONS))


(load-file "parser.clj")

(def *all-chars (mapv char (range 0 128)))
(def *digit (+char (apply str (filter #(Character/isDigit %) *all-chars))))
(def *space (+char (apply str (filter #(Character/isWhitespace %) *all-chars))))
(def *ws (+ignore (+star *space)))
(def *number (+str (+plus *digit)))

(def *constant (+seqf (comp Constant read-string) (+str (+seq (+opt (+char "-")) *number (+char ".") *number))))
(def *variable (+seqf Variable (+str (+plus (+char "xyzXYZ")))))
(def *constant-or-variable (+or *constant *variable))

(defn *get-tokens [& tokens]
  (letfn [(*get-token [token]
            (apply +seqf (constantly token) (mapv #(+char (str %)) (str token))))]
    (+seqf OBJECT_OPERATIONS (apply +or (mapv *get-token tokens)))))

(defn *binary-operation [direction]
  (letfn [(fold
            ([operand] operand)
            ([first token second & rest] (apply fold (apply token (direction [first second])) rest)))]
    (fn [*next & tokens]
      (+map
        (comp (partial apply fold) direction)
        (+seqf cons *next (+map (partial apply concat) (+star (+seq *ws (apply *get-tokens tokens) *ws *next))))))))

(def *left-associative (*binary-operation identity))
(def *right-associative (*binary-operation reverse))

(declare *expression)
(def *unary-operation (+or
                        *constant-or-variable
                        (+seqf #(%1 %2) (*get-tokens 'negate) *ws (delay *unary-operation))
                        (+seqn 1
                               (+char "(")
                               (delay *expression)
                               (+char ")"))))
(def *mul-div (*left-associative *unary-operation '* '/))
(def *add-sub (*left-associative *mul-div '+ '-))
(def *bit-and (*left-associative *add-sub '&))
(def *bit-or (*left-associative *bit-and '|))
(def *bit-xor (*left-associative *bit-or (symbol "^")))
(def *bit-impl (*right-associative *bit-xor '=>))
(def *bit-iff (*left-associative *bit-impl '<=>))
(def *expression (+seqn 0 *ws *bit-iff *ws))

(def parseObjectInfix (+parser *expression))
