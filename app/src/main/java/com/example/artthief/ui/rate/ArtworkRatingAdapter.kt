package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork
import com.squareup.picasso.Picasso

class ArtworkRatingAdapter(
    private val artworkClickListener: ArtworkClickListener
) : RecyclerView.Adapter<ArtworkRatingAdapter.ViewHolder>() {

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

    /**
     * The artworks that our Adapter will show
     */
    var artworks: List<ArtThiefArtwork> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    interface ArtworkClickListener {
        fun onArtworkClicked(position: Int, view: View)
    }

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
                val position: Int = adapterPosition
                artworkClickListener.onArtworkClicked(position, itemView)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.art_card_view, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        Picasso
            .get()
//            .load(artworks[i].image_small)
            .load("https://artthief.zurka.com/images/Small/12345S-22.jpg")
            .into(viewHolder.artworkImage)

        viewHolder.artworkTitle.text = artworks[i].title
        viewHolder.artworkArtist.text = artworks[i].artist
        viewHolder.artworkMedia.text = artworks[i].media

        val width = artworks[i].width.toString()
        val height = artworks[i].height.toString()
        val dimensions = "$width\" by $height\""
        viewHolder.artworkDimensions.text = dimensions

        // TODO: remove stars rating from this text field after infrastructure is in place
        val showId = artworks[i].showID
        val stars = artworks[i].stars.toString()
        val showIdAndStars = "$showId + $stars"
        viewHolder.artworkShowId.text = showIdAndStars
    }

    override fun getItemCount(): Int {
        // TODO: item count depends on how many sections there are (1-6 sections), might not be necessary
        return artworks.size
    }
}
