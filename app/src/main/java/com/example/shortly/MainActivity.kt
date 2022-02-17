package com.example.shortly

import android.app.Activity
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortly.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.link_list.*


private lateinit var binding: ActivityMainBinding
private var flag = 0
private lateinit var linkAdapter: ShortLinkAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        linkAdapter = ShortLinkAdapter(mutableListOf())
        rv_shortenedLinks.adapter = linkAdapter
        rv_shortenedLinks.layoutManager = LinearLayoutManager(this)
        rv_shortenedLinks.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.adapter_margin).toInt()
            )
        )

        binding.apply {
            shortenButton.setOnClickListener {

                hideSoftKeyboard()

                // if edit text is empty

                if (editLink.text.toString() == "") {
                    editLink.hint = getString(R.string.empty_edit)
                    editLink.textSize = 17F
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
                else {
                    imageView.visibility = View.GONE
                    imageView2.visibility = View.GONE
                    textView.visibility = View.GONE
                    textView2.visibility = View.GONE
                    tvHistory.visibility = View.VISIBLE
                    rvShortenedLinks.visibility = View.VISIBLE
                    val longUrl = editLink.text.toString()
                    if (longUrl.isNotEmpty()) {
                        val link = ShortLink(longUrl)
                        linkAdapter.addLink(link)
                        editLink.text.clear()
                        editLink.setHintTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.edit_hint
                            )
                        )
                        editLink.hint = getString(R.string.hint)
                    }


                }
            }

            editLink.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editLink.hint = ""
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