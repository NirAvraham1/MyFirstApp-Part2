package com.example.myapplication_part2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class SensorMovementController(
    context: Context,
    private val listener: SensorMovementListener,
    private val getDelay: () -> Long,
    private val setDelay: (Long) -> Unit
) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var timestamp: Long = 0L

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = -event.values[0]
            val y = event.values[1]
            calculateTilt(x, y)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun calculateTilt(x: Float, y: Float) {
        if (System.currentTimeMillis() - timestamp >= 400) {
            timestamp = System.currentTimeMillis()

            if (abs(x) >= 4) {
                if (x > 0) {
                    listener.onMoveLeft()
                } else {
                    listener.onMoveRight()
                }
            }

            if (y <= -2) {
                val currentDelay = getDelay()
                if (currentDelay > 200L) {
                    setDelay((currentDelay - 100L).coerceAtLeast(200L))
                }
                listener.onTiltForward()
            } else if (y >= 2) {
                val currentDelay = getDelay()
                if (currentDelay < 2000L) {
                    setDelay((currentDelay + 100L).coerceAtMost(1400L))
                }
                listener.onTiltBackward()
            }
        }
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
