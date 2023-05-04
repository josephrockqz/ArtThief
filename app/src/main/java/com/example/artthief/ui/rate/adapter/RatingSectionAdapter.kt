package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.ui.rate.data.RecyclerViewSection

class RatingSectionAdapter(
    private val context: Context,
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

        fun bind(context: Context, section: RecyclerViewSection) {

            val sectionTitle = itemView.findViewById<TextView>(R.id.tv_ratingSectionName)
            val recyclerView = itemView.findViewById<RecyclerView>(R.id.rv_ratingSection)

            sectionTitle.text = section.label
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false

            val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.layoutManager = layoutManager

            val adapter = ArtworkAdapter(
//                object: ArtworkAdapter.ArtworkClickListener {
//                    override fun onArtworkClicked(position: Int, view: View) {
//                        showArtworkFragment(position)
//                    }
//                }
                section.artworks
            )
            recyclerView.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val section = sections[i]
        viewHolder.bind(context, section)
    }

    override fun getItemCount() = sections.size
}
