package com.example.shortly

import android.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortly.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding
private var flag = 0
private lateinit var linkAdapter: ShortLinkAdapter
private lateinit var urls: ArrayList<ShortLink>
private lateinit var helper: HistoryHelper
private lateinit var loading: LoadingDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = LoadingDialog(this)
        helper = HistoryHelper(this)

        urls = helper.loadHistory()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        linkAdapter = ShortLinkAdapter(this, urls)
        binding.rvShortenedLinks.adapter = linkAdapter
        binding.rvShortenedLinks.layoutManager = LinearLayoutManager(this)
        binding.rvShortenedLinks.addItemDecoration(
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

                    shortlyHeader.visibility = View.GONE
                    shortlyImage.visibility = View.GONE
                    textHeader.visibility = View.GONE
                    textDescription.visibility = View.GONE
                    tvHistory.visibility = View.VISIBLE
                    rvShortenedLinks.visibility = View.VISIBLE


                    val longUrl = editLink.text.toString()
                    var shortenedUrl = ""

                    if (longUrl.isNotEmpty()) {

                        loading.startLoading()

                        lifecycleScope.launchWhenCreated {
                            val response = try {
                                RetrofitInstance.api.getShortenUrl(longUrl)
                            } catch (e: Exception) {
                                return@launchWhenCreated
                            }

                            if (response.isSuccessful && response.body() != null) {

                                loading.isDismiss()
                                shortenedUrl = response.body()!!.result.full_short_link

                                val link = ShortLink(longUrl, shortenedUrl)

                                linkAdapter.addLink(link)

                                helper.saveHistory(urls)

                                editLink.text.clear()
                                editLink.setHintTextColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.edit_hint
                                    )
                                )
                                editLink.hint = getString(R.string.hint)
                            } else {
                                loading.isDismiss()
                                editLink.text.clear()
                                editLink.setHintTextColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.edit_hint
                                    )
                                )
                                editLink.hint = getString(R.string.hint)
                                Toast.makeText(
                                    this@MainActivity,
                                    "Result is not found",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

            editLink.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editLink.hint = ""
                    editLink.isCursorVisible = true
                    if (flag == 1) {
                        editLink.setBackgroundResource(R.drawable.ic_edit_vector)
                        flag = 0
                    }
                } else {
                    editLink.isCursorVisible = false
                }
            }

            val keyListener = View.OnKeyListener { _, p1, p2 ->
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

    private fun Activity.hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    fun goMainScreen() {
        binding.apply {
            shortlyHeader.visibility = View.VISIBLE
            shortlyImage.visibility = View.VISIBLE
            textHeader.visibility = View.VISIBLE
            textDescription.visibility = View.VISIBLE
            tvHistory.visibility = View.GONE
            rvShortenedLinks.visibility = View.GONE
        }
    }
}