package com.example.shortly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.link_list.view.*

class ShortLinkAdapter(
    private val links: MutableList<ShortLink>
) : RecyclerView.Adapter<ShortLinkAdapter.LinkViewHolder>() {
    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
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
        holder.itemView.apply {
            tv_longLink.text = curLink.long_url
            tv_shortenedLink.text = curLink.short_url
            ib_deleteLink.setOnClickListener {
                curLink.delete_check = true
                deleteLink()
            }
        }

    }

    override fun getItemCount(): Int {
        return links.size
    }

    fun addLink(link:ShortLink){
        links.add(link)
        notifyItemInserted(links.size - 1)
    }

    fun deleteLink(){
        links.removeAll { link ->
            link.delete_check
        }
        notifyDataSetChanged()
    }
}