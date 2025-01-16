package com.app.iotdevicemonitoring.presentation

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.iotdevicemonitoring.R
import com.app.iotdevicemonitoring.data.models.Den3Data
import com.app.iotdevicemonitoring.data.models.Light4UpdateRequest
import com.app.iotdevicemonitoring.data.models.LightData
import com.app.iotdevicemonitoring.data.models.LightUpdateRequest
import com.app.iotdevicemonitoring.databinding.ActivityMainBinding
import com.app.iotdevicemonitoring.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isLight1On = false
    private var isLight2On = false
    private var isLight3On = false
    private var isLight4On = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy trạng thái thiết bị từ server
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) { // Vòng lặp vô hạn
                try {
                    val lightStatus = RetrofitInstance.api.getLightStatus()
                    val sensorStatus = RetrofitInstance.api.getSensorStatus()
                    isLight1On = lightStatus.lights.den1 == 1
                    isLight2On = lightStatus.lights.den2 == 1
                    val light4Status = RetrofitInstance.api.getLight4Status()
                    // Cập nhật giao diện người dùng
                    runOnUiThread {
                        updateLightState(binding.switchDen1, binding.imgLight1, lightStatus.lights.den1 == 1)
//                        updateLightState(binding.switchDen2, binding.imgLight2, lightStatus.lights.den2 == 1)
//                        updateLight3State(binding.imgLight3, lightStatus.lights.den3.status == 1)
//
//                        // Cập nhật đèn 4
//                        updateLight4State(binding.switchDen4, light4Status.den4 == 1)
                        isLight4On = light4Status.den4 == 1
                        if(sensorStatus.temperature == 253.0f || sensorStatus.humidity == 253.0f){
                            binding.textView.text = "${sensorStatus.temperature}°C"
                            binding.textView2.text = "${sensorStatus.humidity}%"
//                            binding.textView.text = "19°C"
//                            binding.textView2.text = "$50%"qq
                        }else{
                            binding.textView.text = "${sensorStatus.temperature}°C"
                            binding.textView2.text = "${sensorStatus.humidity}%"
                        }
//                        if(lightStatus.lights.den3.status == 0){
//                            binding.switchDen3.text = "Hẹn giờ tắt"
//                        }

                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Lỗi tải dữ liệu: ${e.message}")
                }

                // Chờ 500ms trước khi gọi lại API
                delay(500)
            }
        }

        // Xử lý sự kiện bật/tắt đèn
        binding.switchDen1.setOnClickListener {
            isLight1On = !isLight1On
            updateLightState(binding.switchDen1, binding.imgLight1, isLight1On)
            sendLightUpdate()
        }

//        binding.switchDen2.setOnClickListener {
//            isLight2On = !isLight2On
//            updateLightState(binding.switchDen2, binding.imgLight2, isLight2On)
//            sendLightUpdate()
//        }
//
//        binding.switchDen3.setOnClickListener {
//            isLight3On = !isLight3On
//            updateLight3State(binding.imgLight3, isLight3On)
//            sendLightUpdate()
//        }
//
//        // Hẹn giờ cho đèn 3
//        binding.switchDen3.setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
//            val currentMinute = calendar.get(Calendar.MINUTE)
//
//            TimePickerDialog(
//                this,
//                { _, selectedHour, selectedMinute ->
//                    val time = String.format("%02d:%02d:00", selectedHour, selectedMinute)
//                    isLight3On = true
////                    updateLightState(binding.switchDen3, binding.imgLight3, isLight3On)
//                    updateLight3State(binding.imgLight3, isLight3On)
//                    sendLightUpdate(time)
//                },
//                currentHour,
//                currentMinute,
//                true
//            ).show()
//        }
//
//        binding.switchDen3.setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
//            val currentMinute = calendar.get(Calendar.MINUTE)
//
//            // Hiển thị TimePickerDialog
//            TimePickerDialog(
//                this,
//                { _, selectedHour, selectedMinute ->
//                    // Định dạng thời gian đã chọn thành "HH:mm:00"
//                    val selectedTime = String.format("%02d:%02d:00", selectedHour, selectedMinute)
//
//                    // Gửi dữ liệu thời gian đến backend
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        try {
//                            val request = LightUpdateRequest(
//                                lights = LightData(
//                                    den1 = if (isLight1On) 1 else 0,
//                                    den2 = if (isLight2On) 1 else 0,
//                                    den3 = Den3Data(status = 1, time = selectedTime)
//                                )
//                            )
//                            val response = RetrofitInstance.api.updateLightStatus(request)
//
//                            runOnUiThread {
//                                if (response.success) {
//                                    // Hiển thị giờ phút đã chọn
//                                    binding.switchDen3.text = String.format("%02d:%02d", selectedHour, selectedMinute)
//                                    binding.imgLight3.setImageResource(R.drawable.ic_light_on)
//                                    Toast.makeText(this@MainActivity, "Hẹn giờ thành công!", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    Toast.makeText(this@MainActivity, "Lỗi hẹn giờ!", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        } catch (e: Exception) {
//                            runOnUiThread {
//                                Toast.makeText(this@MainActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                },
//                currentHour,
//                currentMinute,
//                true
//            ).show()
//        }

//        // Nút bật/tắt tất cả đèn
//        binding.btnTurnOnAll.setOnClickListener {
//            isLight1On = true
//            isLight2On = true
//            sendLightUpdate()
//            updateAllLights(true)
//        }
//
//        binding.btnTurnOffAll.setOnClickListener {
//            isLight1On = false
//            isLight2On = false
//            sendLightUpdate()
//            updateAllLights(false)
//        }
//        binding.switchDen4.setOnClickListener {
//            isLight4On = !isLight4On
//            updateLight4State(binding.switchDen4, isLight4On) // Cập nhật giao diện
//            sendLight4Toggle(isLight4On) // Gửi yêu cầu lên BE
//        }


    }

    private fun updateLightState(switchView: ImageView, lightView: ImageView, isOn: Boolean) {
        switchView.setImageResource(if (isOn) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
        lightView.setImageResource(if (isOn) R.drawable.ic_light_on else R.drawable.ic_light_off)
    }

//    private fun updateAllLights(isOn: Boolean) {
//        updateLightState(binding.switchDen1, binding.imgLight1, isOn)
//        updateLightState(binding.switchDen2, binding.imgLight2, isOn)
//    }

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
                Log.e("API_ERROR", "Lỗi gửi dữ liệu đèn: ${e.message}")
            }
        }
    }
    private fun updateLight3State(lightView: ImageView, isOn: Boolean) {
        lightView.setImageResource(if (isOn) R.drawable.ic_light_on else R.drawable.ic_light_off)
    }

    private fun sendLight4Toggle(isOn: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Light4UpdateRequest(den4 = if (isOn) 1 else 0)
                val response = RetrofitInstance.api.toggleLight4(request)

                runOnUiThread {
                    if (response.success) {
                        Log.d("API_RESPONSE", response.message)
                        Toast.makeText(this@MainActivity, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Lỗi cập nhật đèn 4!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi gửi dữ liệu đèn 4: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateLight4State(switchView: ImageView, isOn: Boolean) {
        switchView.setImageResource(if (isOn) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
    }

}
