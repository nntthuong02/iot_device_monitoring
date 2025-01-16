package com.app.iotdevicemonitoring.data.models

data class LightUpdateRequest(
    val lights: LightData
)

data class LightData(
    val den1: Int,
    val den2: Int,
    val den3: Den3Data
)

data class Den3Data(
    val status: Int,
    val time: String // "HH:mm:ss"
)
data class LightStatusResponse(
    val success: Boolean,
    val lights: LightData
)
data class SensorUpdateRequest(
    val den4: Int,
    val temperature: Float,
    val humidity: Float
)
data class SensorStatusResponse(
    val success: Boolean,
    val den4: Int,
    val temperature: Float,
    val humidity: Float
)
data class ApiResponse(
    val success: Boolean,
    val message: String
)

data class Light4UpdateRequest(
    val den4: Int // 1: Bật, 0: Tắt
)
data class Light4StatusResponse(
    val success: Boolean,
    val den4: Int // Trạng thái đèn 4 (0 hoặc 1)
)