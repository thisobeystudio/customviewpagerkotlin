package com.thisobeystudio.customviewpagerkotlin.viewpager

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log

import com.thisobeystudio.customviewpagerkotlin.indicator.CustomIndicator
import com.thisobeystudio.customviewpagerkotlin.indicator.IndicatorsRecyclerViewAdapter.IndicatorCallbacks
import com.thisobeystudio.customviewpagerkotlin.models.CustomFragment

import java.util.ArrayList

class CustomViewPager : ViewPager, IndicatorCallbacks {

    companion object {
        private const val TAG = "CustomViewPager"
    }

    constructor(context: Context) : super(context) {
        initCustomViewPagerOnPageChangeListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initCustomViewPagerOnPageChangeListener()
    }

    private fun initCustomViewPagerOnPageChangeListener() {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int,
                                        positionOffset: Float,
                                        posOffsetPixels: Int) {
                // nothing to do here...
            }

            override fun onPageSelected(position: Int) {
                this@CustomViewPager.updateIndicatorSelection(getRealPosition(position))
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE && isFirstHelperPageSelected) {
                    this@CustomViewPager.setCurrentItem(realCount.dec(), false)
                } else if (state == ViewPager.SCROLL_STATE_IDLE && isLastHelperPageSelected) {
                    this@CustomViewPager.setCurrentItem(realFirstPageIndex.dec(), false)
                }
            }
        })
    }

    // region Private values

    private val realFirstPageIndex: Int get() = 1

    private val realLastPageIndex: Int get() = realCount

    private val helperFirstPageIndex: Int get() = count - 1

    private val helperLastPageIndex: Int get() = 0

    // endregion Private values

    // region Public values

    val count: Int
        get() {
            return if (realCount <= 0) 0 else realCount.plus(2)
        }

    val realCount: Int
        get() {
            return adapter?.getRealCount() ?: 0
        }

    val isFirstRealPageSelected: Boolean
        get() {
            return super.getCurrentItem() == realFirstPageIndex
        }

    val isLastRealPageSelected: Boolean
        get() {
            return super.getCurrentItem() == realLastPageIndex
        }

    private val isFirstHelperPageSelected: Boolean
        get() {
            return super.getCurrentItem() == helperLastPageIndex
        }

    private val isLastHelperPageSelected: Boolean
        get() {
            return super.getCurrentItem() == helperFirstPageIndex
        }

    // endregion Public values

    // region Pager Fragments

    val fragments: ArrayList<Fragment?>?
        get() {
            return adapter?.fragments
        }

    val realFirstFragment: CustomFragment?
        get() {
            return getFragment(realFirstPageIndex)
        }

    val helperFirstFragment: CustomFragment?
        get() {
            return getFragment(helperFirstPageIndex)
        }

    val realLastFragment: CustomFragment?
        get() {
            return getFragment(realLastPageIndex)
        }

    val helperLastFragment: CustomFragment?
        get() {
            return getFragment(helperLastPageIndex)
        }

    fun getFragment(index: Int): CustomFragment? {
        return if (fragments == null || index < 0 || index >= fragments!!.size) null
        else fragments!![index] as CustomFragment?
    }

    // endregion Pager Fragments

    // region Pages Data

    /**
     * used to share data between first real page and first helper page.
     * null are not allowed, if you want to reset it, please use
     * [.clearFirstPageData] or [.clearPagesData]
     *
     */
    var firstPageData: Any = Any()

    /**
     * used to share data between last real page and last helper page.
     * null are not allowed, if you want to reset it, please use
     * [.clearLastPageData] or [.clearPagesData]
     *
     */
    var lastPageData: Any = Any()

    fun clearPagesData() {
        clearFirstPageData()
        clearLastPageData()
    }

    fun clearFirstPageData() {
        firstPageData = Any()
    }

    fun clearLastPageData() {
        lastPageData = Any()
    }

    fun setPageData(first: Boolean, last: Boolean, data: Any?) {

        when {
            first && last -> {
                firstPageData = data ?: return // update data
                lastPageData = firstPageData
                helperFirstFragment?.setHelperPageData(firstPageData) ?: return
                helperLastFragment?.setHelperPageData(firstPageData) ?: return
            }
            first -> {
                firstPageData = data ?: return // update data
                helperFirstFragment?.setHelperPageData(firstPageData) ?: return
            }
            last -> {
                lastPageData = data ?: return // update data
                helperLastFragment?.setHelperPageData(lastPageData) ?: return
            }
        }
    }

    fun getPageData(first: Boolean, last: Boolean): Any {
        return when {
            first && last -> firstPageData
            first -> firstPageData
            last -> lastPageData
            else -> Any()
        }
    }

    // endregion Pages Data

    // region Adapter

    override fun getAdapter(): CustomPagerAdapter? {
        return super.getAdapter() as CustomPagerAdapter?
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        try {
            super.setAdapter(adapter as CustomPagerAdapter?)
        } catch (e: ClassCastException) {
            val msg = """Please make sure to use (CustomViewPager.java)
            |instead of (ViewPager.java)
            |and (CustomPagerAdapter.java)
            |instead of (FragmentPagerAdapter.java or FragmentStatePagerAdapter.java)."""
                    .trimMargin()
            Log.e(TAG, msg)
            e.printStackTrace()
        }
    }

    // endregion Adapter

    // region Pager Selection

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item.inc(), smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item.inc())
    }

    override fun getCurrentItem(): Int {
        return getRealPosition(super.getCurrentItem())
    }

    private fun getRealPosition(position: Int): Int {
        return adapter?.getRealPosition(position) ?: return 0
    }

    // endregion Pager Selection

    // region Indicators

    private var mCustomIndicator: CustomIndicator? = null

    override fun onIndicatorClick(position: Int) {
        setCurrentItem(position, true)
    }

    fun initIndicators() {

        val parentConstraintLayout: ConstraintLayout? = parent as? ConstraintLayout?

        if (parentConstraintLayout == null) {
            Log.e(TAG, """Can NOT init CustomViewPager's Indicators.
                | Parent ConstraintLayout is null.""".trimMargin())
            return
        }

        mCustomIndicator = CustomIndicator(context, parentConstraintLayout, this)

        // set the indicator callbacks for onIndicatorClick
        mCustomIndicator?.setIndicatorCallbacks(this)
    }

    fun initIndicators(position: Int, adjustMode: Int) {
        initIndicators()
        setIndicatorsMode(position, adjustMode)
    }

    fun initIndicators(position: Int, adjustMode: Int, maxRows: Int) {
        initIndicators()
        setIndicatorsMode(position, adjustMode, maxRows)
    }

    fun setIndicatorsMode(position: Int, adjustMode: Int, maxRows: Int) {
        setIndicatorsMode(position, adjustMode)
        setMaxVisibleIndicatorRows(maxRows)
    }

    fun setIndicatorsMode(position: Int, adjustMode: Int) {
        mCustomIndicator?.setIndicatorsMode(position, adjustMode)
    }

    fun setMaxVisibleIndicatorRows(maxRows: Int) {
        mCustomIndicator?.setMaxVisibleIndicatorRows(maxRows)
    }

    private fun updateIndicatorSelection(position: Int) {
        mCustomIndicator?.updateSelection(position)
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
        mCustomIndicator?.setCount(context, this, realCount)
    }

    // endregion Indicators

    fun showThreePages() {
        val pages = 3
        val margin = 40
        val width = resources.displayMetrics.widthPixels
        val padding = width / pages + margin * 2 / pages
        clipChildren = true
        clipToPadding = false
        setPadding(padding, margin, padding, margin)
        pageMargin = margin
    }

}
