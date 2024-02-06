package com.github.dimkolya.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {
    private lateinit var resultView: TextView

    private lateinit var button0: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button

    private lateinit var buttonComma: Button

    private lateinit var buttonUnary: Button
    private lateinit var buttonDiv: Button
    private lateinit var buttonMul: Button
    private lateinit var buttonSub: Button
    private lateinit var buttonAdd: Button

    private lateinit var buttonEvaluate: Button

    private lateinit var buttonC: Button
    private lateinit var buttonAC: Button

    private enum class OPERATORS {
        NONE, DIV, MUL, SUB, ADD
    }

    private var currentOperator: OPERATORS = OPERATORS.NONE
    private var bufferedOperand: BigDecimal? = null
    private var buffer: BigDecimal = BigDecimal.ZERO
    private var current: StringBuilder? = null
    private var wasComma: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultView = findViewById(R.id.resultView)

        resultView.movementMethod = ScrollingMovementMethod()
        resultView.setHorizontallyScrolling(true)

        button0 = findViewById(R.id.button0)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)

        buttonComma = findViewById(R.id.buttonComma)

        buttonUnary = findViewById(R.id.buttonUnary)
        buttonDiv = findViewById(R.id.buttonDiv)
        buttonMul = findViewById(R.id.buttonMul)
        buttonSub = findViewById(R.id.buttonSub)
        buttonAdd = findViewById(R.id.buttonAdd)

        buttonEvaluate = findViewById(R.id.buttonEvaluate)

        buttonC = findViewById(R.id.buttonC)
        buttonAC = findViewById(R.id.buttonAC)

        if (current == null) {
            resultView.text = "0"
        } else {
            resultView.text = current!!.toString()
        }

        button0.setOnClickListener {
            if (current == null) {
                current = StringBuilder("0")
            } else if (wasComma || (current!!.first() != '0' && !current!!.startsWith("-0"))) {
                addCharToCurrent('0')
            }
            resultView.text = current!!.toString()
        }
        button1.setOnClickListener {
            addCharWithCheckLeadingZeros('1')
        }
        button2.setOnClickListener {
            addCharWithCheckLeadingZeros('2')
        }
        button3.setOnClickListener {
            addCharWithCheckLeadingZeros('3')
        }
        button4.setOnClickListener {
            addCharWithCheckLeadingZeros('4')
        }
        button5.setOnClickListener {
            addCharWithCheckLeadingZeros('5')
        }
        button6.setOnClickListener {
            addCharWithCheckLeadingZeros('6')
        }
        button7.setOnClickListener {
            addCharWithCheckLeadingZeros('7')
        }
        button8.setOnClickListener {
            addCharWithCheckLeadingZeros('8')
        }
        button9.setOnClickListener {
            addCharWithCheckLeadingZeros('9')
        }

        buttonComma.setOnClickListener {
            if (!wasComma) {
                checkCurrentNull()
                addCharToCurrent('.')
                wasComma = true
            }
        }
        buttonUnary.setOnClickListener {
            if (current == null) {
                current = StringBuilder("-0")
            } else if (current!!.first() == '-') {
                current!!.deleteCharAt(0)
            } else {
                current!!.insert(0, "-")
            }
            resultView.text = current!!.toString()
        }
        buttonDiv.setOnClickListener {
            clickOperator(OPERATORS.DIV)
        }
        buttonMul.setOnClickListener {
            clickOperator(OPERATORS.MUL)
        }
        buttonSub.setOnClickListener {
            clickOperator(OPERATORS.SUB)
        }
        buttonAdd.setOnClickListener {
            clickOperator(OPERATORS.ADD)
        }
        buttonEvaluate.setOnClickListener {
            evaluate()
        }
        buttonC.setOnClickListener {
            current = StringBuilder("0")
            resultView.text = "0"
        }
        buttonAC.setOnClickListener {
            clearAll()
        }
    }

    private fun markButton(button: Button) {
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        button.setTextColor(ContextCompat.getColor(this, R.color.orange))
    }

    private fun markButton(operator: OPERATORS) {
        when (operator) {
            OPERATORS.DIV -> markButton(buttonDiv)
            OPERATORS.MUL -> markButton(buttonMul)
            OPERATORS.SUB -> markButton(buttonSub)
            OPERATORS.ADD -> markButton(buttonAdd)
            OPERATORS.NONE -> {}
        }
    }

    private fun remarkButton(button: Button) {
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.orange)
        button.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun checkMarkedButton(button: Button): Boolean {
        return button.textColors == ContextCompat.getColorStateList(this, R.color.orange)
    }

    private fun getMarked(): OPERATORS {
        return when (currentOperator) {
            OPERATORS.DIV -> if (checkMarkedButton(buttonDiv)) OPERATORS.DIV else OPERATORS.NONE
            OPERATORS.MUL -> if (checkMarkedButton(buttonMul)) OPERATORS.MUL else OPERATORS.NONE
            OPERATORS.SUB -> if (checkMarkedButton(buttonSub)) OPERATORS.SUB else OPERATORS.NONE
            OPERATORS.ADD -> if (checkMarkedButton(buttonAdd)) OPERATORS.ADD else OPERATORS.NONE
            OPERATORS.NONE -> {
                OPERATORS.NONE
            }
        }
    }

    private fun remarkCurrentOperator() {
        when (currentOperator) {
            OPERATORS.DIV -> remarkButton(buttonDiv)
            OPERATORS.MUL -> remarkButton(buttonMul)
            OPERATORS.SUB -> remarkButton(buttonSub)
            OPERATORS.ADD -> remarkButton(buttonAdd)
            OPERATORS.NONE -> {}
        }
    }

    private fun checkCurrentNull() {
        if (current == null) {
            current = StringBuilder("0")
            remarkCurrentOperator()
        }
    }

    private fun addCharToCurrent(char: Char) {
        checkCurrentNull()
        current!!.append(char)
        resultView.text = current!!.toString()
    }

    private fun addCharWithCheckLeadingZeros(char: Char) {
        checkCurrentNull()
        if (!wasComma && current!!.startsWith("0") || current!!.startsWith("-0")) {
            current!!.setLength(current!!.length - 1)
        }
        current!!.append(char)
        resultView.text = current!!.toString()
    }

    private fun clickOperator(operator: OPERATORS) {
        markButton(operator)
        if (current != null) {
            evaluate()
        }
        currentOperator = operator
        current = null
        bufferedOperand = null
        wasComma = false
    }

    private fun evaluate() {
        if (current != null && currentOperator == OPERATORS.NONE) {
            buffer = current!!.toString().toBigDecimal()
            resultView.text = buffer.toPlainString()
            current = null
            wasComma = false
            return
        }
        if (currentOperator == OPERATORS.NONE) {
            return
        }
        if (bufferedOperand == null && current != null) {
            bufferedOperand = current!!.toString().toBigDecimal()
            current = null
            wasComma = false
        } else if (bufferedOperand == null && current == null) {
            bufferedOperand = buffer
        } else if (bufferedOperand != null && current != null) {
            buffer = current!!.toString().toBigDecimal()
            current = null
            wasComma = false
        }
        when (currentOperator) {
            OPERATORS.DIV -> {
                if (bufferedOperand!!.compareTo(BigDecimal.ZERO) == 0) {
                    clearAll()
                    resultView.setText(R.string.errorMessage)
                } else {
                    buffer = buffer.divide(bufferedOperand!!, 6, BigDecimal.ROUND_HALF_UP)
                        .stripTrailingZeros()
                    resultView.text = buffer.toPlainString()
                }
            }
            OPERATORS.MUL -> {
                buffer = buffer.multiply(bufferedOperand!!).stripTrailingZeros()
                resultView.text = buffer.toPlainString()
            }
            OPERATORS.SUB -> {
                buffer = buffer.subtract(bufferedOperand!!).stripTrailingZeros()
                resultView.text = buffer.toPlainString()
            }
            OPERATORS.ADD -> {
                buffer = buffer.add(bufferedOperand!!).stripTrailingZeros()
                resultView.text = buffer.toPlainString()
            }
            OPERATORS.NONE -> {
                buffer = bufferedOperand!!
                resultView.text = buffer.toPlainString()
            }
        }
        remarkCurrentOperator()
    }

    private fun clearAll() {
        remarkCurrentOperator()
        currentOperator = OPERATORS.NONE
        bufferedOperand = null
        buffer = BigDecimal.ZERO
        current = null
        resultView.text = "0"
        wasComma = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_OPERATOR, currentOperator)
        outState.putSerializable(MARKED_OPERATOR, getMarked())
        outState.putSerializable(BUFFERED_OPERAND, bufferedOperand)
        outState.putSerializable(BUFFER, buffer)
        if (current == null) {
            outState.putString(CURRENT, "")
        } else {
            outState.putString(CURRENT, current!!.toString())
        }
        outState.putBoolean(WAS_COMMA, wasComma)
        outState.putString(RESULT, resultView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentOperator = savedInstanceState.getSerializable(CURRENT_OPERATOR) as OPERATORS
        markButton(savedInstanceState . getSerializable (MARKED_OPERATOR) as OPERATORS)
        bufferedOperand = savedInstanceState.getSerializable(BUFFERED_OPERAND) as BigDecimal?
        buffer = savedInstanceState.getSerializable(BUFFER) as BigDecimal
        val currentString = savedInstanceState.getString(CURRENT)
        current = if (currentString!!.compareTo("") == 0) null else StringBuilder(currentString)
        wasComma = savedInstanceState.getBoolean(WAS_COMMA)
        val temp = savedInstanceState.getString(RESULT)
        if (temp == null) {
            resultView.text = "0"
        } else {
            resultView.text = temp
        }
    }

    companion object {
        private const val CURRENT_OPERATOR = "dimkolya.MainActivity.currentOperator"
        private const val MARKED_OPERATOR = "dimkolya.MainActivity.markedOperator"
        private const val BUFFERED_OPERAND = "dimkolya.MainActivity.bufferedOperand"
        private const val BUFFER = "dimkolya.MainActivity.buffer"
        private const val CURRENT = "dimkolya.MainActivity.current"
        private const val WAS_COMMA = "dimkolya.MainActivity.wasComma"
        private const val RESULT = "dimkolya.MainActivity.result"
    }
}