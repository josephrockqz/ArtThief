package com.joerock.artthief.ui.rate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joerock.artthief.R
import com.joerock.artthief.databinding.ArtCardViewBinding
import com.joerock.artthief.domain.ArtThiefArtwork
import com.joerock.artthief.ui.rate.data.ArtworkClickListener
import com.squareup.picasso.Picasso

class ArtworkAdapter(
    private val artworkClickListener: ArtworkClickListener,
    private val artworks: List<ArtThiefArtwork>,
    private val context: Context,
    private val numPriorArtworks: Int = 0
) : RecyclerView.Adapter<ArtworkAdapter.ViewHolder>() {

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
            /**
             * Set far right text to either: show ID, Taken, or Deleted
             * Note: if artwork is both taken & deleted, then display "Taken"
             */
            if (artworks[i].taken) {
                this.tvArtShowId.text = context.getString(R.string.art_card_taken)
                this.tvArtShowId.setTextColor(context.resources.getColor(R.color.red))
            } else if (artworks[i].deleted) {
                this.tvArtShowId.text = context.getString(R.string.art_card_deleted)
                this.tvArtShowId.setTextColor(context.resources.getColor(R.color.red))
            } else {
                this.tvArtShowId.text = artworks[i].showID
                this.tvArtShowId.setTextColor(context.resources.getColor(R.color.artwork_card_right_text))
            }
        }
    }

    override fun getItemCount() = artworks.size
}
