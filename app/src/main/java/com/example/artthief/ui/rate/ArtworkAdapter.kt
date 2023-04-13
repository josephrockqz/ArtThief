package com.example.artthief.ui.rate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.domain.ArtThiefArtwork

class ArtworkAdapter : RecyclerView.Adapter<ArtworkAdapter.ViewHolder>() {

/**
 * First level of our network result which looks like:

 * [
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView

        var itemTitle: TextView
        var itemArtist: TextView
        var itemMedium: TextView

        init {
            itemImage = itemView.findViewById(R.id.rv_image)

            itemTitle = itemView.findViewById(R.id.text1)
            itemArtist = itemView.findViewById(R.id.text2)
            itemMedium = itemView.findViewById(R.id.text3)

            itemView.setOnClickListener {
//                var position: Int = adapterPosition
//                val context = itemView.context
//                val intent = Intent(context, RateFragment::class.java).apply {
//                    putExtra("NUMBER", position)
//                    putExtra("CODE", itemKode.text)
//                    putExtra("CATEGORY", itemKategori.text)
//                    putExtra("CONTENT", itemIsi.text)
//                }
//                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.art_card_view, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemImage.setImageResource(R.drawable.my_image)

        viewHolder.itemTitle.text = artworks[i].title
    }

    override fun getItemCount(): Int {
        return artworks.size
    }
}
