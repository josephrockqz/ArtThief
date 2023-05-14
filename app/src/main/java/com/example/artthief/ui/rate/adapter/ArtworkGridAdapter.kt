package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.artthief.R
import com.example.artthief.databinding.GridviewItemBinding
import com.example.artthief.domain.ArtThiefArtwork
import com.squareup.picasso.Picasso

internal class ArtworkGridAdapter(
    private val artworks: List<ArtThiefArtwork>,
    private val context: Context,
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var itemBinding: GridviewItemBinding

    override fun getCount(): Int {
        return artworks.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }

        val artworkImageView = convertView!!.findViewById<ImageView>(R.id.iv_artImageGridView)
        Picasso
            .get()
//            .load(artworks[position].image_large)
            .load("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/1745px-Android_robot.svg.png")
            .into(artworkImageView)

        return convertView
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        // Return true for clickable, false for not
        return false
    }
}
