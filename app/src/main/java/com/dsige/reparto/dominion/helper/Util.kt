package com.dsige.reparto.dominion.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dsige.reparto.dominion.BuildConfig
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.ui.workManager.BatteryWork
import com.dsige.reparto.dominion.ui.workManager.GpsWork
import com.dsige.reparto.dominion.ui.workManager.RepartoWork
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.*

object Util {
    private var FechaActual: String? = ""
    private const val img_height_default = 800
    private const val img_width_default = 600

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun formatToYesterdayOrToday(date: String): String {
        var day = "Ult. Llamada"
        if (date.isNotEmpty()) {

//            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a")
//            sdf.timeZone = TimeZone.getTimeZone("GMT")
//            val dateTime = sdf.parse(date)
            val dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").parse(date)

            val calendar = Calendar.getInstance()
            calendar.time = dateTime!!
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("HH:mm:ss aaa")

            day =
                if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    "HOY " + timeFormatter.format(dateTime)
                } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(
                        Calendar.DAY_OF_YEAR
                    ) == yesterday.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    "AYER " + timeFormatter.format(dateTime)
                } else {
                    date
                }
        }

        return day
    }

    @SuppressLint("SimpleDateFormat")
    fun getFirstDay(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = 1
        return sdf.format(calendar.time)
    }

    fun getMonthCurrent(): Int {
        val c = Calendar.getInstance()
//        var month: String = String.format("%s", c.get(Calendar.MONTH) + 1)
//        if (month.length < 2) {
//            month = "0$month"
//        }
        return c.get(Calendar.MONTH) + 1
    }

    @SuppressLint("SimpleDateFormat")
    fun getLastaDay(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return sdf.format(calendar.time)
    }

    fun getFormatDate(date: Date): String {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a")
        return format.format(date)
    }

    fun getFecha(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
//        return "05/10/2019"
    }

    fun getHora(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm aaa")
        return format.format(date)
    }

    fun getFechaActual(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return format.format(date)
    }

    fun getHoraActual(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm:ss aaa")
        return format.format(date)
    }

    fun getFechaEditar(): String? {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return FechaActual
    }

    fun getFechaActualForPhoto(id: String, tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return id + "_" + tipo + "_" + FechaActual
    }


    fun getFotoName(id: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return String.format("Foto%s_%s.jpg", id, FechaActual)
    }

    fun toggleTextInputLayoutError(textInputLayout: TextInputLayout, msg: String?) {
        textInputLayout.error = msg
        textInputLayout.isErrorEnabled = msg != null
    }

    // TODO SOBRE ADJUNTAR PHOTO

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel = FileOutputStream(destFile).channel
        if (source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination.close()
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var result = ""
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        @SuppressLint("Recycle") val cursor =
            context.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
        }
        return result
    }

    fun getFolder(context: Context): File {
        val folder = File(context.getExternalFilesDir(null)!!.absolutePath)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                folder.mkdir()
            }
        }
        return folder
    }

    private fun getDateTimeFormatString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    fun getDateTimeFormatString(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }


    private fun shrinkBitmap(file: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true
        val heightRatio =
            ceil((options.outHeight / img_height_default.toFloat()).toDouble())
                .toInt()
        val widthRatio =
            ceil((options.outWidth / img_width_default.toFloat()).toDouble())
                .toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            options.inSampleSize = heightRatio.coerceAtLeast(widthRatio)
        }

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }


    fun getVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getImei(context: Context): String {
        val deviceUniqueIdentifier: String
        val telephonyManager: TelephonyManager? =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        deviceUniqueIdentifier = if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            }
        } else {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return deviceUniqueIdentifier
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("token", "empty")
    }

    fun getNotificacionValid(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("update", "")
    }

    fun updateNotificacionValid(context: Context) {
        context.getSharedPreferences("TOKEN", MODE_PRIVATE).edit().putString("update", "").apply()
    }

    fun snackBarMensaje(view: View, mensaje: String) {
        val mSnackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction("Ok") { mSnackbar.dismiss() }
        mSnackbar.show()
    }

    fun toastMensaje(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }

    fun dialogMensaje(context: Context, title: String, mensaje: String) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    // TODO CLOSE TECLADO

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(edit: EditText, context: Context) {
        edit.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        // TODO FOR FRAGMENTS
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getDateDialogText(context: Context, text: TextView) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"
            text.text = fecha
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getDateDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"
            input.setText(fecha)
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getHourDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val timePickerDialog =
            TimePickerDialog(context, { _, hourOfDay, minute ->
                val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                val minutes = if (minute < 10) "0$minute" else minute.toString()
                val day = if (hourOfDay < 12) "a.m." else "p.m."
                input.setText(String.format("%s:%s %s", hour, minutes, day))
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    fun getCompareFecha(fechaInicial: String, fechaFinal: String): Boolean {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        var date1 = Date()
        try {
            date1 = format.parse(fechaFinal)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var date2 = Date()
        try {
            date2 = format.parse(fechaInicial)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date1.before(date2)
    }

    fun getDateDialog(context: Context, view: View, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"

            if (!getCompareFecha(getFecha(), fecha)) {
                input.setText(fecha)
            } else {
                snackBarMensaje(view, "Fecha Propuesta no debe ser menor a la fecha actual")
            }

        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun getTextHTML(html: String): Spanned {
//        textViewTotal.setText(
//            Util.getTextHTML(String.format("%s %s", "<font color='#4CAF50'>Total</font> : ", b.showTotal())),
//            TextView.BufferType.SPANNABLE
//        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }

        //    Util.getTextHTML("<font color='red'>Cant. Galones</font> : " + h.cantidad),
        //                BufferType.SPANNABLE
    }

    fun getTextStyleHtml(html: String, input: TextView) {
        input.setText(
            getTextHTML(html),
            TextView.BufferType.SPANNABLE
        )
    }


    fun isNumeric(strNum: String): Boolean {
        try {
            val d = Integer.parseInt(strNum)
//            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }

    fun isDecimal(s: String): Boolean {
        try {
            val d = s.toDouble()
//            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }

    @Throws(IOException::class)
    fun deleteDirectory(file: File) {
        if (file.isDirectory) {
            val f: Array<File>? = file.listFiles()
            if (f != null) {
                for (ct: File in f) {
                    ct.delete()
                }
            }
        }
    }

    fun deletePhoto(photo: String, context: Context) {
        val f = File(getFolder(context), photo)
        if (f.exists()) {
            val uriSavedImage = FileProvider.getUriForFile(
                context, BuildConfig.APPLICATION_ID + ".fileprovider", f
            )
            context.contentResolver.delete(uriSavedImage, null, null)
            f.delete()
        }
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    fun calculationByDistance(StartP: Location, EndP: LatLng): Double {
        val radius = 6371 * 1000  // radius of earth in Km * meters
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        return kmInDec.toDouble()
    }

    fun calculationByDistance(StartP: Location, EndP: Location): Double {
        val radius = 6371 * 1000  // radius of earth in Km * meters
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        return kmInDec.toDouble()
    }

    fun getAngleImage(
        context: Context,
        photoPath: String,
        fecha: String,
        direccion: String,
        coordenadas: String
    ): String {
        try {
            val ei = ExifInterface(photoPath)
            val degree: Int = when (ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }
            return rotateImage(context, degree, photoPath, fecha, direccion, coordenadas)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }

    private fun rotateImage(
        context: Context,
        degree: Int,
        imagePath: String,
        fecha: String,
        direccion: String,
        coordenadas: String
    ): String {
        try {
            var b: Bitmap = shrinkBitmap(imagePath)
            val matrix = Matrix()
            matrix.setRotate(degree.toFloat())
            b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)

            val text = String.format(
                "%s\n%s\n%s",
                if (fecha.isEmpty()) getDateTimeFormatString(Date(File(imagePath).lastModified())) else String.format(
                    "%s %s",
                    fecha,
                    getHoraActual()
                ),
                direccion,
                coordenadas
            )
            b = drawTextToBitmap(context, b, text)

            val fOut = FileOutputStream(imagePath)
            b.compress(Bitmap.CompressFormat.JPEG, 70, fOut)
            fOut.flush()
            fOut.close()
            b.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }


    private fun drawTextToBitmap(
        gContext: Context,
        b: Bitmap,
        gText: String
    ): Bitmap {
        var bitmap = b
        var bitmapConfig = bitmap.config

        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = Color.WHITE
        // text size in pixels
        paint.textSize = 19f
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // draw text to the Canvas center
        val bounds = Rect()
        var noOfLines = 0
        for (line in gText.split("\n").toTypedArray()) {
            noOfLines++
        }
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val x = 20
        var y: Float = (bitmap.height - bounds.height() * noOfLines).toFloat()
        val mPaint = Paint()
        mPaint.color = ContextCompat.getColor(gContext, R.color.transparentBlack)
        mPaint.strokeWidth = 10f
        val left = 0
        val top = bitmap.height - bounds.height() * (noOfLines + 1)
        val right = bitmap.width
        val bottom = bitmap.height
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        for (line in gText.split("\n").toTypedArray()) {
            val textPaint = TextPaint()
            val txt =
                TextUtils.ellipsize(line, textPaint, (y * 0.45).toFloat(), TextUtils.TruncateAt.END)
            canvas.drawText(txt.toString(), x.toFloat(), y, paint)
            y += paint.descent() - paint.ascent()
        }
        return bitmap
    }

    fun getLocationName(
        context: Context,
        input: TextInputEditText,
        location: Location,
        progressBar: ProgressBar
    ) {
        val nombre = arrayOf("")
        try {
            val addressObservable = Observable.just(
                Geocoder(context).getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )[0]
            )
            addressObservable.subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Address> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(address: Address) {
                        nombre[0] = address.getAddressLine(0)
                    }

                    override fun onError(e: Throwable) {
//                        toastMensaje(context, context.getString(R.string.try_again))
                        progressBar.visibility = View.GONE
                    }

                    override fun onComplete() {
                        input.setText(nombre[0])
                        progressBar.visibility = View.GONE
                    }
                })
        } catch (e: IOException) {
            toastMensaje(context, e.toString())
            progressBar.visibility = View.GONE
        }
    }

    fun getFolderAdjunto(
        file: String, context: Context, data: Intent
    ): Completable {
        return Completable.fromAction {
            val imagepath = getFolder(context).toString() + "/" + file
            val f = File(imagepath)
            if (!f.exists()) {
                try {
                    f.createNewFile()
                    copyFile(File(getRealPathFromURI(context, data.data!!)), f)
//                    shrinkBitmapOnlyReduceCamera2(imagepath)
//                    getAngleImage(imagepath)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // execute services
    fun executeRepartoWork(context: Context) {
//        val downloadConstraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
        // Define the input data for work manager
//        val data = Data.Builder()
//        data.putInt("tipo", tipo)

        // Create an one time work request
        val downloadImageWork = OneTimeWorkRequest
            .Builder(RepartoWork::class.java)
//          .setInputData(data.build())
//          .setConstraints(downloadConstraints)
            .build()
        WorkManager.getInstance(context).enqueue((downloadImageWork))
    }

    fun executeGpsWork(context: Context) {
//        val downloadConstraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
        val locationWorker =
            PeriodicWorkRequestBuilder<GpsWork>(15, TimeUnit.MINUTES)
//                .setConstraints(downloadConstraints)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "Gps-Work",
                ExistingPeriodicWorkPolicy.REPLACE,
                locationWorker
            )
        toastMensaje(context, "Servicio Gps Activado")
    }

    fun closeGpsWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("Gps-Work")
    }

    fun executeBatteryWork(context: Context) {
//        val downloadConstraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
        val locationWorker =
            PeriodicWorkRequestBuilder<BatteryWork>(15, TimeUnit.MINUTES)
//                .setConstraints(downloadConstraints)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "Battery-Work",
                ExistingPeriodicWorkPolicy.REPLACE,
                locationWorker
            )
    }

    fun closeBatteryWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("Battery-Work")
    }

    fun createImageFile(name: String, context: Context): File {
        return File(getFolder(context), "$name.jpg").apply {
            absolutePath
        }
    }

    fun getPhotoAdjunto(
        nameImg: String, context: Context, direccion: String,
        latitud: String, longitud: String, id: Int
    ): Observable<Photo> {
        return Observable.create {
            val f = File(getFolder(context), "$nameImg.jpg")
            if (f.exists()) {
                val coordenadas = "Latitud : $latitud  Longitud : $longitud"
                getAngleImage(context, f.absolutePath, "", direccion, coordenadas)
                val photo = Photo(
                    0,
                    id,
                    "$nameImg.jpg",
                    getFechaActual(),
                    5,
                    1,
                    latitud,
                    longitud
                )
                it.onNext(photo)
                it.onComplete()
            }

        }
    }

    fun getDateFirmReparto(id: Int, tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSS")
        val fechaActual = format.format(date)
        return String.format("Firm_%s_%s_%s.jpg", id, tipo, fechaActual)
    }

    fun getDateFirmReconexiones(id: Int, tipo: Int, f: String): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSS")
        val fechaActual = format.format(date)
        return String.format("Firm(%s)_%s_%s_%s.jpg", f, id, tipo, fechaActual)
    }

    fun comprimirImagen(pathFile: String) {
        val imagen = shrinkBitmap(pathFile)
        copyBitmatToFile(pathFile, imagen)
    }

    private fun copyBitmatToFile(filename: String, bitmap: Bitmap) {
        try {
            val f = File(filename)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
        } catch (ex: IOException) {
            ex.message
        }
    }

    fun getMobileDataState(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cmClass = Class.forName(cm.javaClass.name)
        val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.isAccessible = true // Make the method callable
        // get the setting for "mobile data"
        return method.invoke(cm) as Boolean
    }
}