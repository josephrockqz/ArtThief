package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.artthief.databinding.GridviewItemBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.squareup.picasso.Picasso

// TODO: fix bug where zoom slider doesn't work sometimes
// TODO: standardize gridview item max height and width
// TODO: be able to drag and drop artworks in grid view
// TODO: dynamically update artwork rating based on dragging and dropping
internal class ArtworkGridAdapter(
    private val artworks: List<ArtThiefArtwork>
) : BaseAdapter() {

    private lateinit var itemBinding: GridviewItemBinding
    private lateinit var holder: ViewHolder

    override fun getCount(): Int {
        return artworks.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun areAllItemsEnabled(): Boolean = false

    override fun isEnabled(position: Int): Boolean = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        if (convertView == null) {
            itemBinding = GridviewItemBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
            holder = ViewHolder(itemBinding)
            holder.view = itemBinding.root
            holder.view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        Picasso
            .get()
            .load(artworks[position].image_small)
            .into(holder.binding.ivArtImageGridView)

        return holder.view;
    }

    private class ViewHolder(val binding: GridviewItemBinding) {
        var view: View = binding.root
    }
}
