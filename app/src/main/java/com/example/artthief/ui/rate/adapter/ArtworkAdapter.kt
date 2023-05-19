package com.example.artthief.ui.rate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.ArtCardViewBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.squareup.picasso.Picasso

class ArtworkAdapter(
    private val artworkClickListener: ArtworkClickListener,
    private val artworks: List<ArtThiefArtwork>,
    private val numPriorArtworks: Int = 0
) : RecyclerView.Adapter<ArtworkAdapter.ViewHolder>() {

    /**
     * First level of our network result which looks like:
        [
            {
                "artThiefID":2012345,
                "showID":"259",
                "title":"Math Class",
                "artist":"Jeanne Garant",
                "media":"Oil",
                "image_large":"https://artthief.zurka.com/images/Large/12345L-22.jpg",
                "image_small":"https://artthief.zurka.com/images/Small/12345S-22.jpg",
                "width":38,
                "height":38,
                "taken":true
            }
            ...
        ]
     */

    inner class ViewHolder(
        viewBinding: ArtCardViewBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        init {
            itemView.setOnClickListener {
                artworkClickListener.onArtworkClicked(
                    adapterPosition + numPriorArtworks,
                    itemView
                )
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            ArtCardViewBinding
                .inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        ArtCardViewBinding.bind(viewHolder.itemView).apply {
            Picasso
                .get()
                .load(artworks[i].image_small)
                .into(this.ivArtImage)

            this.tvArtTitle.text = artworks[i].title
            this.tvArtArtist.text = artworks[i].artist
            this.tvArtMedia.text = artworks[i].media
            this.tvArtDimensions.text = artworks[i].dimensions
            this.tvArtShowId.text = artworks[i].showID
        }
    }

    override fun getItemCount() = artworks.size
}
