package com.dsige.reparto.dominion.ui.activities

import android.os.Bundle
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.adapters.TabLayoutAdapter
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_form_reparto.*

class FormRepartoActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_reparto)
        val b = intent.extras
        if (b != null) {
            bindUI(
                b.getInt("repartoId"),
                b.getString("recibo")!!,
                b.getInt("operarioId"),
                b.getString("cliente")!!,
                b.getInt("validation")
            )
        } else {
            bindUI(123, "123", 1, "irvin", 1)
        }
    }

    private fun bindUI(
        repartoId: Int, recibo: String, operarioId: Int, cliente: String, validation: Int
    ) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "CheckList"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        if (validation == 2) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
        }

        val tabLayoutAdapter = TabLayoutAdapter.TabLayoutRecibo(
            supportFragmentManager,
            tabLayout.tabCount, repartoId, recibo, operarioId, cliente, validation
        )
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
                Util.hideKeyboard(this@FormRepartoActivity)
            }
        })
    }
}