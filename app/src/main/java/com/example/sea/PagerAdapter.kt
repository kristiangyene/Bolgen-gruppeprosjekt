package com.example.sea

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount() = 4

    override fun getItem(p0: Int): Fragment {
        return when(p0) {
            0 -> NowFragment()
            1 -> HourlyFragment()
            2 -> WeeklyFragment()
            else -> {
                MapFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Nå"
            1 -> "Time"
            2 -> "Uke"
            else -> {
                "Kart"
            }
        }
    }

}