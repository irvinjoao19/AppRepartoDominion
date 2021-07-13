package com.dsige.reparto.dominion.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.helper.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import java.lang.Exception

class PreviewCameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getString("nameImg", ""))
        }
    }

    private fun bindUI(nameImg: String) {
        textViewImg.text = nameImg
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val f = File(Util.getFolder(this), nameImg)
                Picasso.get().load(f)
                    .into(imageView, object : Callback {
                        override fun onError(e: Exception?) {}
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }
                    })
            }, 800)
        }
        imgClose.setOnClickListener{
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}