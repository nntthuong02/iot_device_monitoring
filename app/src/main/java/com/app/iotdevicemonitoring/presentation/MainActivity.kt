package com.app.iotdevicemonitoring.presentation

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.iotdevicemonitoring.R
import com.app.iotdevicemonitoring.data.models.ToggleRequest
import com.app.iotdevicemonitoring.databinding.ActivityMainBinding
import com.app.iotdevicemonitoring.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Khai báo biến để lưu trữ CountDownTimer hiện tại
    private var activeCountDownTimer: CountDownTimer? = null
    private var isLight1On = false
    private var isLight2On = false
    private var isLight3On = false
    private var isLight4On = false
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.api.toggleDevice(ToggleRequest("den1", 1))
            if (response.success) {
                Log.d("API", "Device ${response.device} turned on")
            }
        }

        // Lấy trạng thái thiết bị
        GlobalScope.launch(Dispatchers.IO) {
            val status = RetrofitInstance.api.getStatus()
            Log.d("API", "Nhiệt độ: ${status.nhietDo}, Độ ẩm: ${status.doAm}")
        }

        // Handle switch actions using ImageView
        binding.switchDen1.setOnClickListener {
            isLight1On = !isLight1On
            updateLightState(binding.switchDen1, binding.imgLight1, isLight1On)
        }

        binding.switchDen2.setOnClickListener {
            isLight2On = !isLight2On
            updateLightState(binding.switchDen2, binding.imgLight2, isLight2On)
        }

        binding.switchDen3.setOnClickListener {
            isLight3On = !isLight3On
            updateLightState(binding.switchDen3, binding.imgLight3, isLight3On)
        }



        binding.switchDen4.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            // Hiển thị TimePickerDialog
            TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Tính toán thời gian đếm ngược (tính bằng milliseconds)
                    val selectedTimeMillis = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis

                    val currentTimeMillis = System.currentTimeMillis()
                    val countdownTimeMillis = selectedTimeMillis - currentTimeMillis

                    if (countdownTimeMillis > 0) {
                        // Hủy CountDownTimer trước đó nếu có
                        activeCountDownTimer?.cancel()

                        // Khởi tạo bộ đếm thời gian mới
                        activeCountDownTimer = object : CountDownTimer(countdownTimeMillis, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                // Tính giờ và phút còn lại
                                val remainingMinutes = millisUntilFinished / (1000 * 60)
                                val remainingSeconds = (millisUntilFinished / 1000) % 60
                                val timeRemaining = String.format("%02d:%02d", remainingMinutes, remainingSeconds)

                                // Cập nhật văn bản trong switchDen4
                                binding.switchDen4.text = timeRemaining
                                binding.imgLight4.setImageResource(R.drawable.ic_light_on)
                            }

                            override fun onFinish() {
                                binding.imgLight4.setImageResource(R.drawable.ic_light_off)

                                // Hành động khi hết giờ
                                binding.switchDen4.text = "Hết giờ"
                                Toast.makeText(this@MainActivity, "Thời gian đã hết!", Toast.LENGTH_SHORT).show()
                            }
                        }.start()
                    } else {
                        Toast.makeText(this, "Thời gian đã chọn không hợp lệ!", Toast.LENGTH_SHORT).show()
                    }
                },
                currentHour,
                currentMinute,
                true
            ).show()
        }



        // Turn on all lights
        binding.btnTurnOnAll.setOnClickListener {
            isLight1On = true
            isLight2On = true
            isLight3On = true
            isLight4On = true
            updateLightState(binding.switchDen1, binding.imgLight1, isLight1On)
            updateLightState(binding.switchDen2, binding.imgLight2, isLight2On)
            updateLightState(binding.switchDen3, binding.imgLight3, isLight3On)
//            updateLightState(binding.switchDen4, binding.imgLight4, isLight4On)
        }

        // Turn off all lights
        binding.btnTurnOffAll.setOnClickListener {
            isLight1On = false
            isLight2On = false
            isLight3On = false
            isLight4On = false
            updateLightState(binding.switchDen1, binding.imgLight1, isLight1On)
            updateLightState(binding.switchDen2, binding.imgLight2, isLight2On)
            updateLightState(binding.switchDen3, binding.imgLight3, isLight3On)
//            updateLightState(binding.switchDen4, binding.imgLight4, isLight4On)
        }
    }

    private fun updateLightState(switchView: ImageView, lightView: ImageView, isOn: Boolean) {
        switchView.setImageResource(if (isOn) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
        lightView.setImageResource(if (isOn) R.drawable.ic_light_on else R.drawable.ic_light_off)
    }
}
