package com.thisobeystudio.customviewpagerkotlin.viewpager

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

import com.thisobeystudio.customviewpagerkotlin.models.CustomIndexHelper
import com.thisobeystudio.customviewpagerkotlin.models.CustomFragment

import java.util.ArrayList

/**
 *
 *
 * Implementation of [PagerAdapter] that
 * uses a [CustomFragment] to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getRealCount] to have a working adapter.
 *
 */
@Suppress("ConstantConditionIf")
abstract class CustomFragmentStatePagerAdapter internal constructor(private val mFragmentManager: FragmentManager) : PagerAdapter() {

    private var mCurTransaction: FragmentTransaction? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    internal val fragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null

    /**
     * @return The real count of pages excluding the two extra pages. (first and last)
     */
    abstract fun getRealCount(): Int

    /**
     * Return the Fragment associated with a specified position.
     */
    protected abstract fun getItem(customIndexHelper: CustomIndexHelper): Fragment

    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID) {
            throw IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id")
        }
    }

    @SuppressLint("CommitTransaction")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (fragments.size > position) {
            val f = fragments[position]
            if (f != null) {
                return f
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val isRealFirst = isRealFirst(position)
        val isRealLast = isRealLast(position)
        val isHelperFirst = isHelperFirst(position)
        val isHelperLast = isHelperLast(position)

        val customIndexHelper = CustomIndexHelper(position,
                getRealPosition(position),
                isRealFirst,
                isRealLast,
                isHelperFirst,
                isHelperLast)

        val fragment = getItem(customIndexHelper)

        if (mSavedState.size > position) {
            val fss = mSavedState[position]
            if (fss != null) {
                fragment.setInitialSavedState(fss)
            }
        }
        while (fragments.size <= position) {
            fragments.add(null)
        }
        fragment.setMenuVisibility(false)
        fragment.userVisibleHint = false
        fragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment)

        return fragment
    }

    @SuppressLint("CommitTransaction")
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        while (mSavedState.size <= position) {
            mSavedState.add(null)
        }
        mSavedState[position] =
                if (fragment.isAdded)
                    mFragmentManager.saveFragmentInstanceState(fragment)
                else null

        fragments[position] = null

        mCurTransaction!!.remove(fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment?
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurrentPrimaryItem!!.userVisibleHint = false
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitNowAllowingStateLoss()
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        var state: Bundle? = null
        if (mSavedState.size > 0) {
            state = Bundle()
            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
            mSavedState.toTypedArray()
            state.putParcelableArray("states", fss)
        }
        for (i in fragments.indices) {
            val f = fragments[i]
            if (f != null && f.isAdded) {
                if (state == null) {
                    state = Bundle()
                }
                val key = "f$i"
                mFragmentManager.putFragment(state, key, f)
            }
        }
        return state
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            val bundle = state as Bundle?
            bundle!!.classLoader = loader
            val fss = bundle.getParcelableArray("states")
            mSavedState.clear()
            fragments.clear()
            if (fss != null) {
                for (i in fss.indices) {
                    mSavedState.add(fss[i] as? Fragment.SavedState)
                }
            }
            val keys = bundle.keySet()
            for (key in keys) {
                if (key.startsWith("f")) {
                    val index = Integer.parseInt(key.substring(1))
                    val f = mFragmentManager.getFragment(bundle, key)
                    if (f != null) {
                        while (fragments.size <= index) {
                            fragments.add(null)
                        }
                        f.setMenuVisibility(false)
                        fragments[index] = f
                    }
                }
            }
        }
    }

    /**
     *
     * Do NOT use [.getCount] to set the pages count, use [.getRealCount] instead.
     *
     * If using it, make sure to set it as follows: `return super.getCount();`
     *
     * Otherwise, first and last pages will be ignored, so they wont be available.
     *
     * @return The total count of pages including the two (first and last) extra pages.
     */
    override fun getCount(): Int {
        val realCount = getRealCount()
        return if (realCount <= 0) 0 else realCount.plus(2)
    }

    internal fun getRealPosition(pagerPosition: Int): Int {
        val realCount = getRealCount()
        // last page to first position.
        if (pagerPosition == 0) {
            return realCount.minus(1)
        }
        // first page to last position.
        return if (pagerPosition == realCount.plus(1)) {
            0
        } else pagerPosition.minus(1)
    }

    private fun isRealFirst(index: Int): Boolean {
        return index == 1
    }

    private fun isRealLast(index: Int): Boolean {
        return index == count.minus(2)
    }

    private fun isHelperFirst(index: Int): Boolean {
        return index == count.minus(1)
    }

    private fun isHelperLast(index: Int): Boolean {
        return index == 0
    }
}