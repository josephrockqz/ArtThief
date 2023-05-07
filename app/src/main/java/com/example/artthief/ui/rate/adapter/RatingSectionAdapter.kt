package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.ui.rate.ArtworkClickListener
import com.example.artthief.ui.rate.data.RecyclerViewSection

class RatingSectionAdapter(
    private val context: Context,
    private val artworkClickListener: ArtworkClickListener,
    private val sections : List<RecyclerViewSection>
) : RecyclerView.Adapter<RatingSectionAdapter.ViewHolder>() {

    // TODO: make sections similar to this:
//    var artworks: List<ArtThiefArtwork> = emptyList()
//        @SuppressLint("NotifyDataSetChanged")
//        set(value) {
//            field = value
//            // Notify any registered observers that the data set has changed. This will cause every
//            // element in our RecyclerView to be invalidated.
//            notifyDataSetChanged()
//        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.section_rating_container, parent, false)
                return ViewHolder(view)
            }
        }

        fun bind(
            context: Context,
            section: RecyclerViewSection,
            artworkClickListener: ArtworkClickListener
        ) {
            
            val star1 = itemView.findViewById<ImageView>(R.id.iv_artworkStar1)
            val star2 = itemView.findViewById<ImageView>(R.id.iv_artworkStar2)
            val star3 = itemView.findViewById<ImageView>(R.id.iv_artworkStar3)
            val star4 = itemView.findViewById<ImageView>(R.id.iv_artworkStar4)
            val star5 = itemView.findViewById<ImageView>(R.id.iv_artworkStar5)
            val unratedTitle = itemView.findViewById<TextView>(R.id.tv_ratingSectionName)

            if (section.rating < 5) star5.visibility = View.GONE
            if (section.rating < 4) star4.visibility = View.GONE
            if (section.rating < 3) star3.visibility = View.GONE
            if (section.rating < 2) star2.visibility = View.GONE
            if (section.rating < 1) {
                star1.visibility = View.GONE
                unratedTitle.visibility = View.VISIBLE
            }

            // TODO: fix scroll bar positioning
            val recyclerView = itemView.findViewById<RecyclerView>(R.id.rv_ratingSection)
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false

            val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.layoutManager = layoutManager

            val adapter = ArtworkAdapter(
                artworkClickListener = artworkClickListener,
                artworks = section.artworks
            )
            recyclerView.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val section = sections[i]
        viewHolder.bind(context, section, artworkClickListener)
    }

    override fun getItemCount() = sections.size
}
