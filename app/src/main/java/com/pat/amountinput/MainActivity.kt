package com.pat.amountinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private var separatorIndex: Int? = null

    private var oldPrefix: String? = null
    private var oldSuffix: String? = null
    private var cursorPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        amountInput = findViewById(R.id.amountInputEditText)
        amountInput.transformationMethod = null
        startValue()
        formatValue()
    }

    private fun formatValue() {
        amountInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
                separatorIndex = charSequence.toString().indexOf(SEPARATOR)
                oldPrefix = charSequence.substring(0, separatorIndex!!)
                oldSuffix = charSequence.substring(separatorIndex!! + 1)
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                amountInput.removeTextChangedListener(this)
                separatorIndex = editable.toString().indexOf(SEPARATOR)


                val prefix = editable.substring(0, separatorIndex!!)
                val suffix = editable.substring(separatorIndex!! + 1)
                cursorPosition = amountInput.selectionStart

                amountInput.setText(formatAmount(prefix, suffix))
                try {
                    if (cursorPosition == separatorIndex!! + 1) amountInput.setSelection(separatorIndex!!)
                     amountInput.setSelection(cursorPosition!!)
                } catch (e: Exception) {
                }
                amountInput.addTextChangedListener(this)

            }
        })
    }

    private fun checkChangedValue(oldPrefix: String?, prefix: String?): ChangedType =
        if(oldPrefix != prefix) ChangedType.PREFIX else ChangedType.SUFFIX


    private fun formatAmount(prefix: String, suffix: String) : String {
        val stringBuilder = StringBuilder()

        if(prefix.isEmpty()) return stringBuilder.append(DEFAULT_PREFIX).append(SEPARATOR).append(oldSuffix).toString()

        val formattedOldPrefix = DecimalFormat("#,###").format(oldPrefix?.replace(",", "")?.toBigDecimal())
        val formattedPrefix = DecimalFormat("#,###").format(prefix.replace(",", "").toBigDecimal())

        val changedValue = checkChangedValue(formattedOldPrefix, formattedPrefix)

        return if(changedValue == ChangedType.PREFIX) stringBuilder.append(formattedPrefix).append(SEPARATOR).append(oldSuffix).toString()
        else if (changedValue == ChangedType.SUFFIX && suffix.length > 3) stringBuilder.append(formattedOldPrefix).append(SEPARATOR).append(suffix.substring(0,3)).toString()
        else if(changedValue == ChangedType.SUFFIX && suffix.isEmpty()) stringBuilder.append(formattedOldPrefix).append(SEPARATOR).append(DEFAULT_SUFFIX).toString()
        else if(changedValue == ChangedType.SUFFIX) stringBuilder.append(formattedOldPrefix).append(SEPARATOR).append(suffix).toString()
        else stringBuilder.append(formattedOldPrefix).append(SEPARATOR).append(oldSuffix).toString()
    }

    private fun startValue() = amountInput.setText("0.000")

    private enum class ChangedType {
        PREFIX,
        SUFFIX
    }

    companion object {
        const val SEPARATOR = "."
        const val DEFAULT_PREFIX = "0"
        const val DEFAULT_SUFFIX = "000"
    }
}