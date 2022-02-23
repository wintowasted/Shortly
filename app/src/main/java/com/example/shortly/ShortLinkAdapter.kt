package com.example.shortly


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.link_list.view.*
//textinputlayout ve textinputedittext

private var selectedPosition: Int = -1
private lateinit var helper: HistoryHelper

class ShortLinkAdapter(
    private val context: Context,
    private val links: MutableList<ShortLink>
) : RecyclerView.Adapter<ShortLinkAdapter.LinkViewHolder>() {
    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        helper = HistoryHelper(parent.context)
        return LinkViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.link_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {


        val curLink = links[position]
        //selectedPosition == position
        if (curLink.is_copied) {
            holder.itemView.apply {
                isSelected = true
                copy_button.setBackgroundResource(R.drawable.ic_copied_button_vector)
                copy_button.text = "COPIED!"
                //curLink.is_copied = false
            }
        } else {
            holder.itemView.apply {
                isSelected = false
                copy_button.setBackgroundResource(R.drawable.ic_button_vector)
                copy_button.text = "COPY"
            }
        }

        holder.itemView.apply {
            tv_longLink.text = curLink.long_url
            tv_shortenedLink.text = curLink.short_url

            ib_deleteLink.setOnClickListener {
                curLink.delete_check = true
                deleteLink()
                helper.saveHistory(links as ArrayList<ShortLink>)
                if (links.isEmpty()) {
                    (context as MainActivity).goMainScreen()
                }
            }

            copy_button.setOnClickListener {
                curLink.is_copied = true
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition)
                selectedPosition = holder.adapterPosition
                notifyItemChanged(selectedPosition)

                val clipboard =
                    holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val textToCopy = tv_shortenedLink.text
                val clip: ClipData = ClipData.newPlainText("Copied Url", textToCopy)
                clipboard.setPrimaryClip(clip)


            }

        }
    }

    override fun getItemCount(): Int {
        return links.size
    }

    fun addLink(link: ShortLink) {
        links.add(link)
        notifyItemInserted(links.size - 1)
    }

    private fun deleteLink() {
        links.removeAll { link ->
            link.delete_check
        }
        notifyDataSetChanged()
    }

}