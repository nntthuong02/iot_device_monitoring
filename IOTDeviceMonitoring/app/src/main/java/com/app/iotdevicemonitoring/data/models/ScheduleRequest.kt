package com.app.iotdevicemonitoring.data.models

data class ScheduleRequest(val device: String, val status: Int, val hour: Int, val minute: Int)