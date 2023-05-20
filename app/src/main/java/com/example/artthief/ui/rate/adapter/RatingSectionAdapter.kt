package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.SectionRatingContainerBinding
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.ui.rate.data.RecyclerViewSection

class RatingSectionAdapter(
    private val context: Context,
    private val artworkClickListener: ArtworkClickListener,
    private val sections: List<RecyclerViewSection>
) : RecyclerView.Adapter<RatingSectionAdapter.ViewHolder>() {

    class ViewHolder(
        viewBinding: SectionRatingContainerBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                return ViewHolder(
                    SectionRatingContainerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        /**
         * calculate number of artworks in prior sections
         * so [artworkClickListener] has correct index
         */
        val sectionAmounts = mutableListOf(0, 0, 0, 0, 0, 0)
        sections.forEach {
            if (it.rating != 0) sectionAmounts[it.rating - 1] = it.artworks.size
        }
        for (j in 4 downTo 0) {
            sectionAmounts[j] = sectionAmounts[j] + sectionAmounts[j+1]
        }

        val section = sections[i]
        SectionRatingContainerBinding.bind(viewHolder.itemView).apply {
            val star1 = this.ivArtworkStar1
            val star2 = this.ivArtworkStar2
            val star3 = this.ivArtworkStar3
            val star4 = this.ivArtworkStar4
            val star5 = this.ivArtworkStar5
            val unratedTitle = this.tvRatingSectionName

            if (section.rating < 5) star5.visibility = View.GONE
            if (section.rating < 4) star4.visibility = View.GONE
            if (section.rating < 3) star3.visibility = View.GONE
            if (section.rating < 2) star2.visibility = View.GONE
            if (section.rating < 1) {
                star1.visibility = View.GONE
                unratedTitle.visibility = View.VISIBLE
            }

            // TODO: implement compare button functionality
            // TODO: change "Compare" text to "Sorted" after section has been completely compared
            val compareButton = this.bCompareButton
            if (section.rating > 0 && section.artworks.size > 1) {
                compareButton.visibility = View.VISIBLE
            } else {
                compareButton.visibility = View.INVISIBLE
            }

            // TODO: fix scroll bar positioning
            val recyclerView = this.rvRatingSection
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
                artworks = section.artworks,
                context = context,
                numPriorArtworks = sectionAmounts[section.rating]
            )
            recyclerView.adapter = adapter
        }
    }

    override fun getItemCount() = sections.size
}
