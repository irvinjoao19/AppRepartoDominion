package com.dsige.reparto.dominion.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Usuario
import com.dsige.reparto.dominion.data.viewModel.UsuarioViewModel
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.fragments.MainFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var usuarioViewModel: UsuarioViewModel
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var usuarioId: Int = 0
    private var logout: String = "off"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUI()
    }

    private fun bindUI() {
        usuarioViewModel =
            ViewModelProvider(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, { u ->
            if (u != null) {
                getUser(u)
                setSupportActionBar(toolbar)
                val toggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()
                navigationView.setNavigationItemSelectedListener(this@MainActivity)
                fragmentByDefault(MainFragment.newInstance(usuarioId))
                message()
            } else {
                goLogin()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Sincronizar" -> dialogFunction(1, "Al sincronizar eliminando todo tus avances. Deseas Sincronizar ?")
            "Inicio de Actividades" -> changeFragment(
                MainFragment.newInstance(usuarioId), item.title.toString()
            )
            "Envio de Pendientes" -> dialogFunction(2, "Enviar Pendientes ?")
            "Servicio Gps" -> Util.executeGpsWork(this)
            "Cerrar Sesión" -> dialogFunction(3, "Al cerrar Sesión estaras eliminando todo tus avances. \nDeseas Salir ?")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textView)
        textViewTitle.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun changeFragment(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
        supportActionBar!!.title = title
    }

    private fun fragmentByDefault(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, f)
            .commit()
        supportActionBar!!.title = "Inicio de Actividades"
    }

    private fun getUser(u: Usuario) {
        val header = navigationView.getHeaderView(0)
        header.textViewName.text = u.operario_Nombre
        header.textViewEmail.text = String.format("Versión: %s", Util.getVersion(this))
        usuarioId = u.iD_Operario
    }


    private fun goLogin() {
        if (logout == "off") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun message() {
        usuarioViewModel.mensajeSuccess.observe(this, { s ->
            if (s != null) {
                closeLoad()
                if (s == "Close") {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Util.toastMensaje(this, s)
                }
            }
        })
        usuarioViewModel.mensajeError.observe(this@MainActivity, { s ->
            if (s != null) {
                closeLoad()
                Util.snackBarMensaje(window.decorView, s)
            }
        })
    }

    private fun dialogFunction(tipo: Int, title: String) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage(title)
            .setPositiveButton("SI") { dialog, _ ->
                when (tipo) {
                    1 -> {
                        load("Sincronizando..")
                        usuarioViewModel.sync(usuarioId, Util.getVersion(this))
                    }
                    2 -> {
                        Util.executeRepartoWork(this)
                        Util.dialogMensaje(this,"Mensaje","Los registros se estan enviando en segundo plano asegure de tener buena señal.")
                    }
                    3 -> {
                        logout = "on"
                        Util.closeGpsWork(this)
                        load("Cerrando Session")
                        usuarioViewModel.logout()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }
}