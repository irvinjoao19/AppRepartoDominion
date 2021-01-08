package com.dsige.reparto.dominion.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dsige.reparto.dominion.ui.fragments.*

abstract class TabLayoutAdapter {

    class TabLayoutRecibo(fm: FragmentManager, private val numberOfTabs: Int, var repartoId: Int, var recibo: String, var operarioId: Int, var cliente: String, var validation: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralFragment.newInstance(repartoId, recibo, operarioId, cliente, validation)
                1 -> FirmFragment.newInstance(repartoId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}