package com.example.shortly

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.shortly.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding
private var flag = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            shortenButton.setOnClickListener {

                // if edit text is empty

                if (editLink.getText().toString().equals("")) {
                    editLink.setHint(getString(R.string.empty_edit))
                    editLink.setTextSize(17F)
                    editLink.setHintTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.edit_link
                        )
                    )
                    editLink.setBackgroundResource(R.drawable.ic_empty_edit)
                    flag = 1
                }

                // shorten link

            }

            editLink.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    editLink.setHint("")
                    if (flag == 1) {
                        editLink.setBackgroundResource(R.drawable.ic_edit_vector)
                        flag = 0
                    }
                }
            }

            val keyListener = View.OnKeyListener { p0, p1, p2 ->
                if (p2.action == KeyEvent.ACTION_DOWN && p1 == KeyEvent.KEYCODE_ENTER) {
                    hideSoftKeyboard()
                    editLink.clearFocus()
                    true
                } else {
                    false
                }
            }
            editLink.setOnKeyListener(keyListener)
        }
    }

    fun Activity.hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}