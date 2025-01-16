package com.app.iotdevicemonitoring.network

import com.app.iotdevicemonitoring.data.models.ApiResponse
import com.app.iotdevicemonitoring.data.models.Light4StatusResponse
import com.app.iotdevicemonitoring.data.models.Light4UpdateRequest
import com.app.iotdevicemonitoring.data.models.LightStatusResponse
import com.app.iotdevicemonitoring.data.models.LightUpdateRequest
import com.app.iotdevicemonitoring.data.models.SensorStatusResponse
import com.app.iotdevicemonitoring.data.models.SensorUpdateRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//private const val BASE_URL = "https://4a043c3f-20cc-4d15-9f8e-cf0524f22af3.mock.pstmn.io"
private const val BASE_URL = "http://192.168.114.68:8000"
//private const val BASE_URL = "http://192.168.1.8:8000"
private const val BASE2 = "http://localhost:8000"
// API Service
interface ApiService {

    // 1. Lấy trạng thái đèn 1, 2, 3
    @GET("/api/lights/status")
    suspend fun getLightStatus(): LightStatusResponse

    // 2. Cập nhật trạng thái đèn 1, 2, 3
    @POST("/api/lights/update")
    suspend fun updateLightStatus(@Body request: LightUpdateRequest): ApiResponse

    // 3. Cập nhật cảm biến và đèn 4
    @POST("/api/sensor/update")
    suspend fun updateSensorData(@Body request: SensorUpdateRequest): ApiResponse

    // 4. Lấy trạng thái cảm biến và đèn 4
    @GET("/api/sensor/status")
    suspend fun getSensorStatus(): SensorStatusResponse

    // 5. Cập nhật trạng thái đèn 4
    @POST("/api/sensor/toggle")
    suspend fun toggleLight4(@Body request: Light4UpdateRequest): ApiResponse

    @GET("/api/sensor/light4/status")
    suspend fun getLight4Status(): Light4StatusResponse

}

// Tạo Retrofit Instance
object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}



