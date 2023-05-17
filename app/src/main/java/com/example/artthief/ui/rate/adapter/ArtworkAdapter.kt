package com.example.artthief.ui.rate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var artworkImage: ImageView
        var artworkTitle: TextView
        var artworkArtist: TextView
        var artworkMedia: TextView
        var artworkDimensions: TextView
        var artworkShowId: TextView

        init {
            artworkImage = itemView.findViewById(R.id.iv_artImage)
            artworkTitle = itemView.findViewById(R.id.tv_artTitle)
            artworkArtist = itemView.findViewById(R.id.tv_artArtist)
            artworkMedia = itemView.findViewById(R.id.tv_artMedia)
            artworkDimensions = itemView.findViewById(R.id.tv_artDimensions)
            artworkShowId = itemView.findViewById(R.id.tv_artShowId)

            itemView.setOnClickListener {
                val sectionPosition: Int = adapterPosition
                artworkClickListener.onArtworkClicked(
                    sectionPosition + numPriorArtworks,
                    itemView
                )
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val v = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.art_card_view, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        Picasso
            .get()
            .load(artworks[i].image_small)
            .into(viewHolder.artworkImage)

        viewHolder.artworkTitle.text = artworks[i].title
        viewHolder.artworkArtist.text = artworks[i].artist
        viewHolder.artworkMedia.text = artworks[i].media
        viewHolder.artworkDimensions.text = artworks[i].dimensions
        viewHolder.artworkShowId.text = artworks[i].showID
    }

    override fun getItemCount() = artworks.size
}
