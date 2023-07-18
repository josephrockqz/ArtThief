package com.example.artthief.ui.rate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork
import com.squareup.picasso.Picasso

internal class ArtworkGridAdapter(
    private val artworks: List<ArtThiefArtwork>,
    private val artworkImageSize: Int
) : RecyclerView.Adapter<ArtworkGridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.iv_artImageGridView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.grid_item,
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemImageView.maxWidth = artworkImageSize
        holder.itemImageView.maxHeight = artworkImageSize
        holder.itemImageView.adjustViewBounds = true

        Picasso
            .get()
            .load(artworks[position].image_small)
            .into(holder.itemImageView)
    }

    override fun getItemCount(): Int = artworks.size
}
