package com.dsige.reparto.dominion.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Reparto
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class PendingLocationMapsActivity : DaggerAppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    private var isFirstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_location_maps)

        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(p: GoogleMap) {
        mMap = p
        zoomToLocation("-12.036175", "-76.999561")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0f, this)
        isGPSEnabled()
        repartoViewModel.getRepartos().observe(this, {
            mMap.clear()
            for (s: Reparto in it) {
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                            .title(s.id_Reparto.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }
            }
        })

        mMap.setOnMarkerClickListener(this@PendingLocationMapsActivity)
    }

    private fun zoomToLocation(latitud: String, longitud: String) {
        val camera = CameraPosition.Builder()
            .target(LatLng(latitud.toDouble(), longitud.toDouble()))
            .zoom(10f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }

    private fun isGPSEnabled() {
        val gpsSignal = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!gpsSignal) {
            showInfoAlert()
        }
    }

    private fun showInfoAlert() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        builder.setTitle("GPS Signal")
        builder.setMessage("Necesitas tener habilitado la señal de GPS. Te gustaria habilitar la señal de GPS ahora ?.")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onLocationChanged(p: Location) {
        if (isFirstTime) {
            zoomToLocation(p.latitude.toString(), p.longitude.toString())
            isFirstTime = false
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onMarkerClick(p: Marker): Boolean {
        dialogResumen(p.title)
        return true
    }

    private fun dialogResumen(t: String) {
        val builder = android.app.AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.cardview_resumen_maps, null)

        val buttonGo = v.findViewById<Button>(R.id.buttonGo)
        val textViewTitle = v.findViewById<TextView>(R.id.textViewTitle)
        val textViewMedidor = v.findViewById<TextView>(R.id.textViewMedidor)
        val textViewContrato = v.findViewById<TextView>(R.id.textViewContrato)
        val textViewDireccion = v.findViewById<TextView>(R.id.textViewDireccion)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        repartoViewModel.getRepartoById(t.toInt()).observe(this, {
            if (it != null) {
                textViewTitle.text = String.format("Orden : %s", it.Cod_Orden_Reparto)
                textViewMedidor.text = String.format("Medidor :%s", it.Suministro_Medidor_reparto)
                textViewContrato.text = String.format("Contrato :%s", it.Suministro_Numero_reparto)
                textViewDireccion.text = it.Direccion_Reparto
                buttonGo.visibility = View.GONE

            }
        })
    }
}