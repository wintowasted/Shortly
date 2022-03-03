package com.example.shortly

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortly.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var flag = 0
    private lateinit var linkAdapter: ShortLinkAdapter
    private lateinit var urls:ArrayList<ShortLink>
    private lateinit var helper: HistoryHelper
    private lateinit var loading: LoadingDialog
    private var search: SearchView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loading = LoadingDialog(this)
        helper = HistoryHelper(this)

        urls = helper.loadHistory()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        linkAdapter = ShortLinkAdapter(urls,this)

        linkAdapter.setData(urls.toMutableList())

        binding.rvShortenedLinks.adapter = linkAdapter
        binding.rvShortenedLinks.layoutManager = LinearLayoutManager(this)
        binding.rvShortenedLinks.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.adapter_margin).toInt()
            )
        )

        if (urls.isNotEmpty()) {
            goHistoryScreen()
        }

        binding.apply {
            shortenButton.setOnClickListener {
                hideSoftKeyboard()
                shortenLink()
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
                    shortenLink()
                    true
                }
                else {
                    false
                }
            }
            editLink.setOnKeyListener(keyListener)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

            menuInflater.inflate(R.menu.main_menu, menu)

            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val item = menu?.findItem(R.id.searchView)
            search = item?.actionView as SearchView
            search!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            search!!.imeOptions = EditorInfo.IME_ACTION_DONE

            search!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    linkAdapter.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    linkAdapter.filter.filter(newText)
                    return false
                }
            })
            return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteAll ->{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Are You Sure!")
                builder.setMessage("Do you want to delete all history?")
                builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    clearUrls()
                }
                builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
                }
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(!search!!.isIconified){
            search!!.isIconified = true
            return
        }
        super.onBackPressed()
    }

    fun clearUrls(){
        urls.clear()
        linkAdapter.setData(urls.toMutableList())
        helper.saveHistory(urls)
        goMainScreen()
    }
    private fun Activity.hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


    fun goMainScreen() {
        hideSoftKeyboard()
        binding.apply {
            shortlyHeader.visibility = View.VISIBLE
            shortlyImage.visibility = View.VISIBLE
            textHeader.visibility = View.VISIBLE
            textDescription.visibility = View.VISIBLE
            rvShortenedLinks.visibility = View.GONE
            gradientView.visibility = View.GONE
            toolbar.visibility = View.GONE
            appBarLayout.visibility = View.GONE
            collapsingLayout.visibility = View.GONE
        }
    }

    private fun goHistoryScreen() {
        binding.apply {
            shortlyHeader.visibility = View.GONE
            shortlyImage.visibility = View.GONE
            textHeader.visibility = View.GONE
            textDescription.visibility = View.GONE
            rvShortenedLinks.visibility = View.VISIBLE
            gradientView.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
            appBarLayout.visibility = View.VISIBLE
            collapsingLayout.visibility = View.VISIBLE
        }
    }

    private fun shortenLink() {

        // if edit text is empty

        binding.apply {
            if (editLink.text.isEmpty()) {
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

                val longUrl = editLink.text.toString()

                // launch url shortener api
                if (longUrl.isNotEmpty()) {

                    loading.startLoading()
                    lifecycleScope.launchWhenCreated {
                        val response = try {
                            RetrofitInstance.api.getShortenUrl(longUrl)
                        } catch (e: Exception) {
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
                                "Check your internet connection",
                                Toast.LENGTH_LONG
                            ).show()
                            return@launchWhenCreated
                        }

                        // if successful result received from api
                        if (response.isSuccessful && response.body() != null) {

                            if (urls.isEmpty())
                                goHistoryScreen()

                            loading.isDismiss()
                            val shortenedUrl = response.body()!!.result.full_short_link

                            val link = ShortLink(longUrl, shortenedUrl)
                            urls.add(link)
                            Log.e("before",linkAdapter.getLink())
                            linkAdapter.setData(urls.toMutableList())
                            Log.e("after",linkAdapter.getLink())
                            //linkAdapter.notifyDataSetChanged()
                            //linkAdapter.addLink(link)

                            helper.saveHistory(urls)


                            editLink.text.clear()
                            editLink.setHintTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.edit_hint
                                )
                            )
                            editLink.hint = getString(R.string.hint)
                        }

                        // if result is not found

                        else {
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
            editLink.clearFocus()
        }
    }

    fun shareUrl(shortUrl:String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(EXTRA_TEXT,shortUrl)
        val chooser = Intent.createChooser(intent,"Share using...")
        startActivity(chooser)
    }

    fun goToUrl(url:String){
       val uri = Uri.parse(url)
       startActivity(Intent(Intent.ACTION_VIEW,uri))
    }

    fun removeUrl(){
        urls.removeAll { link ->
            link.delete_check
        }
        linkAdapter.setData(urls.toMutableList())
        helper.saveHistory(urls)
    }


}