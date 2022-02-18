package com.example.shortly


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    private fun makeCopy(button: Button,isCopied:Boolean){

        if(isCopied){
            button.setBackgroundResource(R.drawable.ic_copied_button_vector)
            button.text = "COPIED!"
        }else{
            button.setBackgroundResource(R.drawable.ic_button_vector)
            button.text = "COPY"
        }
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

            copy_button.setOnClickListener {

                makeCopy(copy_button,curLink.is_copied)

                val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val textToCopy = tv_shortenedLink.text
                val clip: ClipData = ClipData.newPlainText("Copied Url", textToCopy)
                clipboard.setPrimaryClip(clip)


                curLink.is_copied = !curLink.is_copied
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

    private fun deleteLink(){
        links.removeAll { link ->
            link.delete_check
        }
        notifyDataSetChanged()
    }








}