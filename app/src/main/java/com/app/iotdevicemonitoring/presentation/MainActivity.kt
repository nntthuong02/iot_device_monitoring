package com.app.iotdevicemonitoring.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.iotdevicemonitoring.R
import com.app.iotdevicemonitoring.data.models.Den3Data
import com.app.iotdevicemonitoring.data.models.LightData
import com.app.iotdevicemonitoring.data.models.LightUpdateRequest
import com.app.iotdevicemonitoring.databinding.ActivityMainBinding
import com.app.iotdevicemonitoring.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isLight1On = false
    private var isLight2On = false
    private var isLight3On = false
    private var isLight4On = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val lightStatus = RetrofitInstance.api.getLightStatus()
                    val sensorStatus = RetrofitInstance.api.getSensorStatus()
                    isLight1On = lightStatus.lights.den1 == 1
                    isLight2On = lightStatus.lights.den2 == 1
                    val light4Status = RetrofitInstance.api.getLight4Status()
                    runOnUiThread {
                        updateLightState(binding.imgSwitchLight, binding.imgLight, lightStatus.lights.den1 == 1)
                        isLight4On = light4Status.den4 == 1
                        if(sensorStatus.temperature == 253.0f || sensorStatus.humidity == 253.0f){
                            binding.textViewTemperature.text = "19°C"
                            binding.textViewWater.text = "$50%"
                        }else{
                            binding.textViewTemperature.text = "${sensorStatus.temperature}°C"
                            binding.textViewWater.text = "${sensorStatus.humidity}%"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Data loading error: ${e.message}")
                }

                delay(500)
            }
        }

        binding.imgSwitchLight.setOnClickListener {
            isLight1On = !isLight1On
            updateLightState(binding.imgSwitchLight, binding.imgLight, isLight1On)
            sendLightUpdate()
        }
    }

    private fun updateLightState(switchView: ImageView, lightView: ImageView, isOn: Boolean) {
        switchView.setImageResource(if (isOn) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
        lightView.setImageResource(if (isOn) R.drawable.ic_light_on else R.drawable.ic_light_off)
    }

    private fun sendLightUpdate(time: String = "00:00:00") {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = LightUpdateRequest(
                    lights = LightData(
                        den1 = if (isLight1On) 1 else 0,
                        den2 = if (isLight2On) 1 else 0,
                        den3 = Den3Data(status = if (isLight3On) 1 else 0, time = time)
                    )
                )
                val response = RetrofitInstance.api.updateLightStatus(request)
                Log.d("API_RESPONSE", response.message)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Data loading error: Light: ${e.message}")
            }
        }
    }

}
