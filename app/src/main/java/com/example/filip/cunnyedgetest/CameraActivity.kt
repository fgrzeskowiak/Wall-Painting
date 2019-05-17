package com.example.filip.cunnyedgetest

import android.Manifest
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageColorBlendFilter
import jp.co.cyberagent.android.gpuimage.GPUImageThresholdEdgeDetection
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST = 11
    private val gpuImage: GPUImage by lazy { GPUImage(this) }
    private val cameraHelper = CameraOld()
    private lateinit var camera: Camera
    private val rxPermissions by lazy { RxPermissions(this) }
    private val disposable = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        gpuImage.setGLSurfaceView(camera_surface_view)

        gpuImage.apply {
            //            setFilter(GPUImageThresholdEdgeDetection().apply {
//                setLineSize(1.5f)
//                setThreshold(0.88f)
//            })

        }

        val permissionsObservable: Observable<Boolean> = rxPermissions.observe(listOf(Manifest.permission.CAMERA))
                .distinctUntilChanged()
                .replay(1)
                .refCount()

        disposable.setFrom(
                permissionsObservable.filter { it }.subscribe {
                    setupCamera(0)
                    camera.setPreviewCallback { data, camera ->
                        println("DUPA DUPA DUPA DUPA DUPA DUPA DUPA")
                        with(BitmapFactory.decodeByteArray(data, 0, data.size)) {
                            gpuImage.apply {
                                setImage(this@with)
//                                setFilter(GPUImageColorBlendFilter().apply {
//                                    bitmap = this@with
//                                })
                                requestRender()
                            }
                            recycle()
                        }
                    }
                    camera.startPreview()
                },
                permissionsObservable.filter { !it }.subscribe {
                    rxPermissions.request({ ActivityCompat.requestPermissions(this, it.toTypedArray(), PERMISSIONS_REQUEST) }, listOf(Manifest.permission.CAMERA))
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.empty()
        releaseCamera()
    }

    private fun setupCamera(cameraId: Int) {
        try {
            camera = cameraHelper.open(cameraId)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
//        gpuImage.setUpCamera(camera, 90, false, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> rxPermissions.onRequestPermissionsResult(permissions, grantResults)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun releaseCamera() {
        camera.setPreviewCallback(null)
        camera.release()
    }
}