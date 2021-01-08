package com.dsige.reparto.dominion.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.ui.fragments.CameraFragment

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val b = intent.extras
        if (b != null) {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CameraFragment.newInstance(
                        b.getInt("repartoId"),
                        b.getInt("tipo"),
                        b.getString("cuentaContrato")!!,
                        b.getString("direccion")!!
                    )
                )
                .commit()
        }
    }
}