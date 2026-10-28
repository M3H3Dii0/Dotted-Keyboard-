package com.dottedkeyboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class MyInputMethodService : InputMethodService() {

    private val rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"),
        listOf("123", "🌐", " ", ".", "⏎")
    )

    private val colors = listOf(
        "#FFB300",
        "#1E88E5",
        "#E53935",
        "#FB8C00",
        "#FFFFFF"
    )

    private var caps = false

    override fun onCreateInputView(): View {
        return buildKeyboard()
    }

    private fun buildKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(8, 12, 8, 12)
        }

        rows.forEach { rowItems ->
            val rowView = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0, 1f
                )
            }
            rowItems.forEach { label ->
                rowView.addView(makeKey(label))
            }
            root.addView(rowView)
        }
        return root
    }

    private fun makeKey(label: String): TextView {
        val ctx = this
        val colorIndex = label.hashCode().let { if (it < 0) -it else it } % colors.size
        val bgColor = Color.parseColor(colors[colorIndex])
        val textColor = if (bgColor == Color.WHITE) Color.BLACK else Color.WHITE

        val key = TextView(ctx).apply {
            text = if (caps) label.uppercase() else label
            gravity = Gravity.CENTER
            setTextColor(textColor)
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)

            background = dotBorderDrawable(bgColor)

            layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
            ).apply {
                setMargins(6, 6, 6, 6)
            }

            setOnClickListener { onKeyPress(label) }
        }
        return key
    }

    private fun dotBorderDrawable(fillColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fillColor)
            cornerRadius = 18f
            setStroke(3, Color.parseColor("#80FFFFFF"), 8f, 6f) 
        }
    }

    private fun onKeyPress(label: String) {
        val ic = currentInputConnection ?: return
        when (label) {
            "⌫" -> ic.deleteSurroundingText(1, 0)
            "⏎" -> ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
            " " -> ic.commitText(" ", 1)
            "⇧" -> { caps = !caps; setInputView(buildKeyboard()) }
            "🌐" -> { }
            "123" -> { }
            else -> ic.commitText(label, 1)
        }
    }
}
