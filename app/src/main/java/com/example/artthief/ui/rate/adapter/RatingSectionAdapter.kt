package com.example.artthief.ui.rate.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artthief.R
import com.example.artthief.databinding.SectionRatingContainerBinding
import com.example.artthief.ui.rate.data.ArtworkClickListener
import com.example.artthief.ui.rate.data.CompareClickListener
import com.example.artthief.ui.rate.data.RecyclerViewSection
import com.example.artthief.ui.rate.data.SwipeUpdateArtworkDeleted
import kotlin.math.roundToInt

// TODO: list artworks in order based on order field
class RatingSectionAdapter(
    private val context: Context,
    private val artworkClickListener: ArtworkClickListener,
    private val compareClickListener: CompareClickListener,
    private val sections: List<RecyclerViewSection>,
    private val swipeUpdateArtworkDeleted: SwipeUpdateArtworkDeleted
) : RecyclerView.Adapter<RatingSectionAdapter.ViewHolder>() {

    private lateinit var artworkAdapter: ArtworkAdapter
    private lateinit var sectionAmounts: MutableList<Int>

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
        sectionAmounts = mutableListOf(0, 0, 0, 0, 0, 0)
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
            // TODO: change "Compare" text to "Sorted" after section has been completely compared (shared preferences - defaults to not sorted)
            // TODO: once compare functionality is implemented, update all `update rating` call sites to adjust for changes
            val compareButton = this.bCompareButton
            if (section.rating > 0 && section.artworks.size > 1) {
                compareButton.visibility = View.VISIBLE
                compareButton.setOnClickListener {
                    compareClickListener.onCompareClicked(section.rating)
                }
            } else {
                compareButton.visibility = View.INVISIBLE
            }

            val recyclerView = this.rvRatingSection
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false

            val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.layoutManager = layoutManager

            val orderedSectionArtworks = section.artworks.sortedBy {
                it.order
            }

            artworkAdapter = ArtworkAdapter(
                artworkClickListener = artworkClickListener,
                artworks = orderedSectionArtworks,
                context = context,
                numPriorArtworks = sectionAmounts[section.rating]
            )
            recyclerView.apply {
                adapter = artworkAdapter
            }

            val swipeHelper = configureSwipeHelper(sectionAmounts[section.rating])
            swipeHelper.attachToRecyclerView(recyclerView)
        }
    }

    override fun getItemCount() = sections.size

    private fun configureSwipeHelper(sectionAmount: Int): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                artworkAdapter.notifyItemRemoved(pos)
                swipeUpdateArtworkDeleted.updateArtworkDeleted(
                    pos + sectionAmount,
                    direction
                )
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val resources = context.resources
                val width = resources.displayMetrics.widthPixels

                when {
                    kotlin.math.abs(dX) < width / 3 -> canvas.drawColor(Color.GRAY)
                    dX > width / 3 -> canvas.drawColor(Color.GREEN)
                    else -> canvas.drawColor(Color.RED)
                }

                val textMargin = resources
                    .getDimension(R.dimen.text_margin)
                    .roundToInt()

                val deleteIcon = resources.getDrawable(R.drawable.ic_deleted_24dp)
                deleteIcon.bounds = Rect(
                    width - textMargin - deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + textMargin + 8,
                    width - textMargin,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight + textMargin + 8
                )

                val archiveIcon = resources.getDrawable(R.drawable.ic_archive_24dp)
                archiveIcon.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin + 8,
                    textMargin + archiveIcon.intrinsicWidth,
                    viewHolder.itemView.top + archiveIcon.intrinsicHeight + textMargin + 8
                )

                if (dX > 0) archiveIcon.draw(canvas) else deleteIcon.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
    }
}
