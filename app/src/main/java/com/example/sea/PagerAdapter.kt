package com.example.sea

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount() = 3

    override fun getItem(p0: Int): Fragment {
        return when(p0) {
            0 -> NowFragment()
            1 -> HourlyFragment()
            else -> {
                WeeklyFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Nå"
            1 -> "Time"
            else -> {
                "Uke"
            }
        }
    }

}