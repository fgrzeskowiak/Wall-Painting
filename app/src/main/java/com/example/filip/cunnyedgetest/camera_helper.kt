package com.example.filip.cunnyedgetest

import android.hardware.Camera

interface CameraHelper {
    fun open(cameraId: Int): Camera
    fun getOrientation(cameraId: Int): Int
}

@SuppressWarnings("deprecation")
class CameraOld : CameraHelper {

    private lateinit var camera: Camera
    override fun open(cameraId: Int): Camera = Camera.open(cameraId)

    override fun getOrientation(cameraId: Int): Int = with(Camera.CameraInfo()) {
        Camera.getCameraInfo(cameraId, this)
        orientation
    }
}