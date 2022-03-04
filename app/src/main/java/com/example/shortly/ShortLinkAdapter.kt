package com.example.shortly


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shortly.databinding.ActivityMainBinding
import com.example.shortly.databinding.LinkListBinding
import kotlinx.android.synthetic.main.link_list.view.*

private var selectedPosition: Int = -1
private lateinit var helper: HistoryHelper

class ShortLinkAdapter(
    private var links: ArrayList<ShortLink>,
    private var context: Context
): RecyclerView.Adapter<ShortLinkAdapter.LinkViewHolder>(), Filterable{
    private var originalLinks: ArrayList<ShortLink> = links


    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(char: CharSequence?): FilterResults {
                val filteredList = ArrayList<ShortLink>()

                val charSearch:String =  char.toString()
                if(charSearch.isEmpty()){
                    filteredList.addAll(originalLinks)
                    Log.e("filter",filteredList.toString())
                }
                else{
                    val pattern = charSearch.lowercase().trim()

                    for (link in originalLinks){
                        if(link.long_url!!.lowercase().contains(pattern)){
                            filteredList.add(link)
                        }
                    }
                    Log.e("original",originalLinks.toString())
                    Log.e("filter2",filteredList.toString())
                }
                val results = FilterResults()
                results.values = filteredList

                return results
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                links.clear()
                links.addAll(results!!.values as ArrayList<ShortLink>)
                notifyDataSetChanged()
            }
        }
    }

    class LinkViewHolder(val binding: LinkListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ShortLink){
            binding.linkItem = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        helper = HistoryHelper(parent.context)
        val listBinding = LinkListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LinkViewHolder(
            listBinding
            )
    }

     fun getLink():String{
        return links.toString()
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(links[position])

        val curLink = links[position]


        if (curLink.is_copied) {
            holder.itemView.apply {
                isSelected = true
                copy_button.setBackgroundResource(R.drawable.ic_copied_button_vector)
                copy_button.text = context.getString(R.string.copied_button)
            }
        } else {
            holder.itemView.apply {
                isSelected = false
                copy_button.setBackgroundResource(R.drawable.ic_button_vector)
                copy_button.text = context.getString(R.string.copy_button)
            }
        }

        holder.binding.apply {

            // go to url
            tvShortenedLink.setOnClickListener {
                (context as MainActivity).goToUrl(tvShortenedLink.text.toString())
            }

            // delete item
            iwDeleteLink.setOnClickListener {
                curLink.delete_check = true
                (context as MainActivity).removeUrl()
                //deleteLink()
                if (links.isEmpty()) {
                    (context as MainActivity).goMainScreen()
                }
            }

            // copy url
            copyButton.setOnClickListener {

                for (link in links){
                    link.is_copied = false
                }
                for (link in originalLinks){
                    link.is_copied = false
                }

                curLink.is_copied = true

                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition)
                selectedPosition = holder.adapterPosition
                notifyItemChanged(selectedPosition)

                val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val textToCopy = tvShortenedLink.text.toString()
                val clip: ClipData = ClipData.newPlainText("Copied Url", textToCopy)
                clipboard.setPrimaryClip(clip)
            }

            // share shortened url
            iwShareLink.setOnClickListener {
                (context as MainActivity).shareUrl(curLink.short_url!!)
            }

        }
    }

    fun setData(newList: MutableList<ShortLink>){
        val oldList = links
        val diffUtil = DiffUtilHelper(oldList,newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        links = newList as ArrayList<ShortLink>
        diffResults.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return links.size
    }

}