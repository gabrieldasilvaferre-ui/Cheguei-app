package com.cheguei.app
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
class MainActivity : AppCompatActivity() {
    private val LOCATION_REQUEST = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btnCheguei)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        btn.setOnClickListener {
            val phone = edtPhone.text.toString().trim()
            if (phone.isEmpty()) {
                Toast.makeText(this, "Digite o número do WhatsApp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkLocationAndSend(phone)
        }
    }
    private fun checkLocationAndSend(phone: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
            return
        }
        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                val msg = "Cheguei! 📍 https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
                sendWhatsApp(phone, msg)
            } else {
                Toast.makeText(this, "Não consegui pegar localização, tente novamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendWhatsApp(phone: String, msg: String) {
        val cleanPhone = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val fullPhone = if (cleanPhone.startsWith("55")) cleanPhone else "55$cleanPhone"
        val uri = Uri.parse("https://wa.me/$fullPhone?text=${Uri.encode(msg)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try { startActivity(intent) } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp não instalado", Toast.LENGTH_SHORT).show()
        }
    }
}
