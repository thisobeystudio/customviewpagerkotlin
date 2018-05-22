package com.thisobeystudio.customviewpagerkotlin.indicator

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context
import android.support.annotation.DimenRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View

import com.thisobeystudio.customviewpagerkotlin.viewpager.CustomViewPager

import android.support.constraint.ConstraintSet.BOTTOM
import android.support.constraint.ConstraintSet.END
import android.support.constraint.ConstraintSet.START
import android.support.constraint.ConstraintSet.TOP
import com.thisobeystudio.customviewpagerkotlin.R

class CustomIndicator(context: Context?, parent: ConstraintLayout?, viewPager: CustomViewPager?) {

    private var mAdapter: IndicatorsRecyclerViewAdapter? = null

    private var mRecyclerView: RecyclerView? = null

    // if mIndicatorsHeightMode is set to WRAP, this param will be ignored.
    private var mMaxVisibleIndicatorRows = 1

    private var mIndicatorsPositionMode = POSITION_FLOAT_BOTTOM
    private var mIndicatorsAdjustMode = MODE_CLAMPED_HEIGHT

    private var mParent: ConstraintLayout?

    companion object {

        private const val TAG = "CustomIndicator"

        // not using enums
        const val POSITION_FLOAT_TOP = 0
        const val POSITION_FLOAT_BOTTOM = 1
        const val POSITION_INCLUDE_TOP = 2
        const val POSITION_INCLUDE_BOTTOM = 3

        // from 1 to infinite based on rows count
        const val MODE_WRAP_HEIGHT = 4
        // itemHeight * (margin * 2) * maxVisibleIndicatorRows
        const val MODE_FIXED_HEIGHT = 5
        // from 1 to maxVisibleIndicatorRows
        const val MODE_CLAMPED_HEIGHT = 6
    }

    init {
        if (context == null || parent == null || viewPager == null) {
            Log.e(TAG, """Can NOT init CustomIndicator.
                | One or more of the Constructors parameters are null.""".trimMargin())
            this.mParent = null
        } else {
            this.mParent = parent
            initRecyclerView(context, viewPager)
        }
    }

    private fun initRecyclerView(context: Context?,
                                 viewPager: CustomViewPager?) {

        if (context == null || mParent == null || viewPager == null) return

        val inflater = LayoutInflater.from(context)

        mRecyclerView = inflater.inflate(R.layout.indicators_view, mParent, false) as RecyclerView

        if (mRecyclerView == null) {
            Log.e(TAG, "Can NOT find a Layout named id: indicators_view")
            return
        }

        // set layout manager
        // initial spawn count to ONE
        val glm = GridLayoutManager(context, 1)
        mRecyclerView!!.layoutManager = glm

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView!!.setHasFixedSize(false)

        // this makes scroll smoothly
        mRecyclerView!!.isNestedScrollingEnabled = false

        val totalCount = viewPager.realCount
        val selection = viewPager.currentItem

        // specify an adapter
        mAdapter = IndicatorsRecyclerViewAdapter(context, totalCount, selection)

        // set recyclerView adapter
        mRecyclerView!!.adapter = mAdapter

        mRecyclerView!!.scrollToPosition(selection)

        // set recyclerView VISIBLE
        mRecyclerView!!.visibility = View.VISIBLE

        calcItemsPerRow(context, viewPager, totalCount)
    }

    private fun calcItemsPerRow(context: Context?,
                                viewPager: ViewPager?,
                                totalCount: Int) {

        if (mRecyclerView == null || context == null || viewPager == null || totalCount <= 0)
            return

        // Since the indicators container width is based on viewPagers width,
        // use post to get viewPagers real width.
        viewPager.post(Runnable {
            if (viewPager.adapter == null) {
                return@Runnable
            }

            val indicatorItemSize = getDimension(context, R.dimen.indicator_item_size)
            val margin = getDimension(context, R.dimen.indicator_horizontal_margin).times(2)

            val width = viewPager.width
            val maxPossibleWidth = width.minus(margin)
            var maxItemsPerRow = maxPossibleWidth.div(indicatorItemSize)

            // this will keep indicators centered horizontally
            if (maxItemsPerRow > totalCount) maxItemsPerRow = totalCount

            if (maxItemsPerRow < 1) return@Runnable

            // set final spawn count
            (mRecyclerView!!.layoutManager as GridLayoutManager).spanCount = maxItemsPerRow

            updateIndicatorsContainerHeight(context,
                    indicatorItemSize,
                    totalCount,
                    maxItemsPerRow)

            if (!mRecyclerView!!.isAttachedToWindow) updateConstraints(viewPager)
        })
    }

    private fun updateIndicatorsContainerHeight(context: Context?,
                                                indicatorItemSize: Int,
                                                totalCount: Int,
                                                maxItemsPerRow: Int) {

        if (context == null || mRecyclerView == null || mIndicatorsAdjustMode == MODE_WRAP_HEIGHT)
            return

        val params = mRecyclerView!!.layoutParams

        val height: Int
        val padding = getDimension(context, R.dimen.indicator_vertical_padding) * 2

        when (mIndicatorsAdjustMode) {
            MODE_FIXED_HEIGHT -> {
                height = indicatorItemSize * mMaxVisibleIndicatorRows + padding
                params.height = height
            }
            MODE_CLAMPED_HEIGHT -> {
                val rows = Math.ceil((totalCount / (maxItemsPerRow + 0f)).toDouble()).toInt()
                if (rows < mMaxVisibleIndicatorRows) {
                    height = rows * indicatorItemSize + padding
                    params.height = height
                } else {
                    height = indicatorItemSize * mMaxVisibleIndicatorRows + padding
                    params.height = height
                }
            }
            MODE_WRAP_HEIGHT -> {
                // nothing to do here, this should not be called...
            }
            else -> {
                // nothing to do here, this should not be called...
            }
        }
    }

    private fun getDimension(context: Context?, @DimenRes dimenID: Int): Int {
        return context?.resources?.getDimensionPixelOffset(dimenID) ?: 0
    }

    fun setIndicatorsMode(indicatorsPositionMode: Int, indicatorsAdjustMode: Int) {
        this.mIndicatorsPositionMode = indicatorsPositionMode
        this.mIndicatorsAdjustMode = indicatorsAdjustMode
    }

    fun setIndicatorCallbacks(
            indicatorCallbacks: IndicatorsRecyclerViewAdapter.IndicatorCallbacks?) {
        if (mAdapter == null || indicatorCallbacks == null) return
        mAdapter!!.setIndicatorCallbacks(indicatorCallbacks)
    }

    private fun updateConstraints(pager: ViewPager?) {

        if (mRecyclerView == null || mParent == null || pager == null) return

        val cs = ConstraintSet()
        cs.clone(mParent)

        // TransitionManager.beginDelayedTransition(mParent);

        cs.centerHorizontally(mRecyclerView!!.id, pager.id)
        // constraintSet.centerVertically(mRecyclerView.getId(), parent.getId());
        cs.constrainWidth(mRecyclerView!!.id, mRecyclerView!!.layoutParams.width)
        cs.constrainHeight(mRecyclerView!!.id, mRecyclerView!!.layoutParams.height)
        // constraintSet.setMargin(mRecyclerView.getId(), ConstraintSet.START, margin/2);

        when (mIndicatorsPositionMode) {
            POSITION_FLOAT_TOP -> {
                connectIndicatorsToParent(cs, TOP)
                connectIndicatorsToPager(cs, pager, TOP)
            }
            POSITION_FLOAT_BOTTOM -> {
                connectIndicatorsToParent(cs, BOTTOM)
                connectIndicatorsToPager(cs, pager, BOTTOM)
            }
            POSITION_INCLUDE_TOP -> {
                connectIndicatorsToParent(cs, TOP)
                connectPagerTopToIndicatorsBottom(cs, pager)
            }
            POSITION_INCLUDE_BOTTOM -> {
                connectIndicatorsToParent(cs, BOTTOM)
                connectPagerBottomToIndicatorsTop(cs, pager)
            }
            else -> Log.e(TAG, """CustomIndicators Position Mode not supported!
                | Please select a valid one.""".trimMargin())
        }

        connectIndicatorsToPager(cs, pager, START)
        connectIndicatorsToPager(cs, pager, END)

        mParent!!.addView(mRecyclerView)

        cs.applyTo(mParent)
    }

    private fun connectIndicatorsToParent(constraintSet: ConstraintSet?, pos: Int) {
        if (constraintSet == null) return
        constraintSet.connect(mRecyclerView!!.id, pos, mParent!!.id, pos, 0)
    }

    private fun connectIndicatorsToPager(constraintSet: ConstraintSet?, pager: ViewPager?, pos: Int) {
        if (constraintSet == null || pager == null) return
        constraintSet.connect(mRecyclerView!!.id, pos, pager.id, pos, 0)
    }

    private fun connectPagerTopToIndicatorsBottom(constraintSet: ConstraintSet?, pager: ViewPager?) {
        if (constraintSet == null || pager == null) return
        constraintSet.connect(pager.id, TOP, mRecyclerView!!.id, BOTTOM, 0)
    }

    private fun connectPagerBottomToIndicatorsTop(constraintSet: ConstraintSet?, pager: ViewPager?) {
        if (constraintSet == null || pager == null) return
        constraintSet.connect(pager.id, BOTTOM, mRecyclerView!!.id, TOP, 0)
    }

    // region Public Functions

    fun setMaxVisibleIndicatorRows(maxVisibleIndicatorRows: Int) {
        this.mMaxVisibleIndicatorRows = maxVisibleIndicatorRows
    }

    fun setCount(context: Context, viewPager: ViewPager, count: Int) {
        if (mAdapter == null) return
        calcItemsPerRow(context, viewPager, count)
        mAdapter!!.swapData(count)
    }

    fun updateSelection(newSelection: Int) {
        if (mRecyclerView == null || mAdapter == null) return
        mRecyclerView!!.scrollToPosition(newSelection)
        mAdapter!!.updateSelection(newSelection)
    }

    // endregion Public Functions
}
