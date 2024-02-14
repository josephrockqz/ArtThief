package com.joerock.artthief.ui.rate.adapter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joerock.artthief.R
import com.joerock.artthief.databinding.SectionRatingContainerBinding
import com.joerock.artthief.ui.rate.data.ArtworkClickListener
import com.joerock.artthief.ui.rate.data.CompareClickListener
import com.joerock.artthief.ui.rate.data.RecyclerViewSection
import com.joerock.artthief.ui.rate.data.SwipeUpdateArtworkDeleted
import kotlin.math.roundToInt

class RatingSectionAdapter(
    private val context: Context,
    private val artworkClickListener: ArtworkClickListener,
    private val compareClickListener: CompareClickListener,
    private val resources: Resources,
    private val sections: List<RecyclerViewSection>,
    private val sharedPreferences: SharedPreferences,
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
        // TODO: use worker thread here
        Thread(Runnable {
            sectionAmounts = mutableListOf(0, 0, 0, 0, 0, 0)
            sections.forEach {
                if (it.rating != 0) sectionAmounts[it.rating - 1] = it.artworks.size
            }
            for (j in 4 downTo 0) {
                sectionAmounts[j] = sectionAmounts[j] + sectionAmounts[j+1]
            }
            val section = sections[i]
//            SectionRatingContainerBinding.bind(viewHolder.itemView).apply {
//                val star1 = this.ivArtworkStar1
//                val star2 = this.ivArtworkStar2
//                val star3 = this.ivArtworkStar3
//                val star4 = this.ivArtworkStar4
//                val star5 = this.ivArtworkStar5
//                val unratedTitle = this.tvRatingSectionName
//
//                if (section.rating < 5) {
//                    star5.post {
//                        star5.visibility = View.GONE
//                    }
//                }
//                if (section.rating < 4) {
//                    star4.post {
//                        star4.visibility = View.GONE
//                    }
//                }
//                if (section.rating < 3) {
//                    star3.post {
//                        star3.visibility = View.GONE
//                    }
//                }
//                if (section.rating < 2) {
//                    star2.post {
//                        star2.visibility = View.GONE
//                    }
//                }
//                if (section.rating < 1) {
//                    star1.post {
//                        star1.visibility = View.GONE
//                    }
//                    unratedTitle.post {
//                        unratedTitle.visibility = View.VISIBLE
//                    }
//                }
//
//                implementCompareButtonLogic(this, section)
//
//                val recyclerView = this.rvRatingSection
//                val layoutManager = LinearLayoutManager(
//                    context,
//                    LinearLayoutManager.VERTICAL,
//                    false
//                )
//                recyclerView.post {
//                    recyclerView.setHasFixedSize(true)
//                    recyclerView.isNestedScrollingEnabled = false
//                    recyclerView.layoutManager = layoutManager
//                }
//
//                artworkAdapter = ArtworkAdapter(
//                    artworkClickListener = artworkClickListener,
//                    artworks = section.artworks,
//                    context = context,
//                    numPriorArtworks = sectionAmounts[section.rating]
//                )
//                val swipeHelper = configureSwipeHelper(sectionAmounts[section.rating])
//                recyclerView.post {
//                    recyclerView.adapter = artworkAdapter
//                    swipeHelper.attachToRecyclerView(recyclerView)
//                }
//            }
        })
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

    private fun implementCompareButtonLogic(
        sectionRatingContainerBinding: SectionRatingContainerBinding,
        section: RecyclerViewSection
    ) {
        val compareButton = sectionRatingContainerBinding.bCompareButton
        if (section.rating > 0 && section.artworks.size > 1) {
            when (section.rating) {
                1 -> {
                    val isOneStarSectionSorted = sharedPreferences.getBoolean("one_stars_sorted", false)
                    if (isOneStarSectionSorted) {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button_sorted)
                    } else {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button)
                    }
                }
                2 -> {
                    val isTwoStarSectionSorted = sharedPreferences.getBoolean("two_stars_sorted", false)
                    if (isTwoStarSectionSorted) {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button_sorted)
                    } else {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button)
                    }
                }
                3 -> {
                    val isThreeStarSectionSorted = sharedPreferences.getBoolean("three_stars_sorted", false)
                    if (isThreeStarSectionSorted) {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button_sorted)
                    } else {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button)
                    }
                }
                4 -> {
                    val isFourStarSectionSorted = sharedPreferences.getBoolean("four_stars_sorted", false)
                    if (isFourStarSectionSorted) {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button_sorted)
                    } else {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button)
                    }
                }
                5 -> {
                    val isFiveStarSectionSorted = sharedPreferences.getBoolean("five_stars_sorted", false)
                    if (isFiveStarSectionSorted) {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button_sorted)
                    } else {
                        compareButton.text = resources.getString(R.string.rate_fragment_compare_button)
                    }
                }
            }
            compareButton.visibility = View.VISIBLE
            compareButton.setOnClickListener {
                compareClickListener.onCompareClicked(section.rating)
                when (section.rating) {
                    1 -> {
                        with (sharedPreferences.edit()) {
                            putBoolean("one_stars_sorted", false)
                            apply()
                        }
                    }
                    2 -> {
                        with (sharedPreferences.edit()) {
                            putBoolean("two_stars_sorted", false)
                            apply()
                        }
                    }
                    3 -> {
                        with (sharedPreferences.edit()) {
                            putBoolean("three_stars_sorted", false)
                            apply()
                        }
                    }
                    4 -> {
                        with (sharedPreferences.edit()) {
                            putBoolean("four_stars_sorted", false)
                            apply()
                        }
                    }
                    5 -> {
                        with (sharedPreferences.edit()) {
                            putBoolean("five_stars_sorted", false)
                            apply()
                        }
                    }
                }
            }
        } else {
            compareButton.visibility = View.INVISIBLE
        }
    }
}
