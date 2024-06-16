package com.harshittest.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harshittest.NewsDetailsAct
import com.harshittest.R
import com.harshittest.databinding.ItemNewsItemBinding
import com.harshittest.room.EntityArticle
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale


class MainNewsAdapter(
    val mContext: Context,
    private val onItemClick: (EntityArticle) -> Unit,
    private var arrayList: MutableList<EntityArticle> = mutableListOf()
) : RecyclerView.Adapter<MainNewsAdapter.MyViewHolder>(), Filterable {

    private var filteredListData: MutableList<EntityArticle> = arrayList.toMutableList()

    class MyViewHolder(var binding: ItemNewsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemNewsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_news_item, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredListData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filteredListData[position]
        holder.binding.txtTitle.text = item.title
        holder.binding.txtDes.text = item.description

        // Parse and format the date
        val formattedDate = item.publishedAt?.let { parseDate(it) }
        holder.binding.txtDate.text = formattedDate

        // Check if urlToImage is not empty or null
        val imagePath: String = item.urlToImage.orEmpty()  // or .toString() if it's nullable
        if (imagePath.isNotEmpty()) {
            Picasso.get().load(imagePath).placeholder(R.drawable.demo).into(holder.binding.imgThumbnail)
        } else {
            // Handle case where imagePath is empty or null
            holder.binding.imgThumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.demo))
        }
        holder.binding.rrMain.setOnClickListener {
            onItemClick(item)
        }
    }

    fun setData(data: MutableList<EntityArticle>) {
        arrayList = data
        filteredListData = arrayList.toMutableList()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                val filteredList = if (charString.isEmpty()) {
                    arrayList
                } else {
                    arrayList.filter {
                        (it.title?.contains(charString, true) ?: false) ||
                                (it.content?.contains(charString, true) ?: false)
                    }.toMutableList()
                }
                return FilterResults().apply { values = filteredList }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredListData = results?.values as? MutableList<EntityArticle> ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }

    // Helper method to parse and format the date
    private fun parseDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) } ?: ""
    }
}