package com.app.iotdevicemonitoring.network

import com.app.iotdevicemonitoring.data.models.ApiResponse
import com.app.iotdevicemonitoring.data.models.DeviceStatusResponse
import com.app.iotdevicemonitoring.data.models.MotionRequest
import com.app.iotdevicemonitoring.data.models.ScheduleRequest
import com.app.iotdevicemonitoring.data.models.ToggleRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

private const val BASE_URL = "https://4a043c3f-20cc-4d15-9f8e-cf0524f22af3.mock.pstmn.io"
interface ApiService {
    @PUT("api/device/toggle")
    suspend fun toggleDevice(@Body request: ToggleRequest): ApiResponse

    @POST("api/device/schedule")
    suspend fun scheduleDevice(@Body request: ScheduleRequest): ApiResponse

    @GET("api/device/status")
    suspend fun getStatus(): DeviceStatusResponse

    @PUT("api/device/motion")
    suspend fun motionSensor(@Body request: MotionRequest): ApiResponse

    @GET("getEnvironmentData")
    suspend fun getEnvironmentData(): EnvironmentResponse

    @GET("detectPerson")
    suspend fun detectPerson(): PersonDetectionResponse
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

data class EnvironmentResponse(
    val success: Boolean,
    val temperature: Float,
    val humidity: Float
)

data class PersonDetectionResponse(
    val success: Boolean,
    val isPersonDetected: Boolean
)
