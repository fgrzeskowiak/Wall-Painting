package com.example.filip.cunnyedgetest

import android.graphics.*
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.touches
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import jp.co.cyberagent.android.gpuimage.GPUImageColorBlendFilter
import jp.co.cyberagent.android.gpuimage.GPUImageThresholdEdgeDetection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val disposable = SerialDisposable()

    lateinit var source: Bitmap
    lateinit var scaledSource: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val screenWidth: Int = with(Point()) {
            windowManager.defaultDisplay.getSize(this)
            println("Screen: $x")
            x
        }

        source = BitmapFactory.decodeResource(resources, R.drawable.appunite, BitmapFactory.Options().apply { inScaled = false })

        scaledSource = Bitmap.createScaledBitmap(source, screenWidth, (source.height * (screenWidth.toFloat() / source.width)).toInt(), false)

        val processedImageObservable: Observable<Bitmap> = Single.fromCallable {
            gpu_image_view.apply {
                filter = GPUImageThresholdEdgeDetection().apply {
                    setLineSize(1.5f)
                    setThreshold(0.88f)
                }
                setImage(scaledSource)
                layoutParams.height = scaledSource.height
                requestLayout()
            }

            val edgeBitmap = gpu_image_view.gpuImage.bitmapWithFilterApplied.replaceColor(Color.WHITE, Color.TRANSPARENT)

            gpu_image_view.apply {
                filter = GPUImageColorBlendFilter().apply {
                    bitmap = edgeBitmap
                }
                requestRender()
            }
            edgeBitmap
        }
//                .map { SobelEdgeDetector().detect(scaledSource.width, scaledSource.height, scaledSource.rowBytes, it) }
//                .map { writeEdges(scaledSource.width, scaledSource.height, it)}
                .toObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .share()

        val clickEventObservable: Observable<Pair<MotionEvent, Bitmap>> = gpu_image_view.touches(Predicate { it.action == MotionEvent.ACTION_DOWN })
                .withLatestFrom(processedImageObservable, BiFunction { event: MotionEvent, bitmap: Bitmap -> event to bitmap })

        val dupa2 = ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_background, theme)

        val floodFill = FloodFill("DUpa", dupa2)

        disposable.setFrom(
                clickEventObservable
                        .switchMap { (event, bitmap) ->
                            Observable.fromCallable {
                                val displayTouch = PointF().apply { set(event.x, event.y) }
//                                val pixelTouch = PointF().apply {
//                                    set(main_layout.top + displayTouch.x / room_image.width * main_layout.width, main_layout.top + displayTouch.y / room_image.height * main_layout.height)
//                                }

                                floodFill.apply {
                                    setBitmapConfiguration(bitmap.width, bitmap.height)
                                    start(bitmap, displayTouch)
                                }

                                println("Event Coords ${event.x}, ${event.y}")
                                bitmap
                            }
                        }
                        .subscribe {
                            gpu_image_view.requestRender()
                            gpu_image_view.filter = GPUImageColorBlendFilter().apply {
                                bitmap = it
                            }
                        },
                change_color_button.clicks()
                        .subscribe {
                            floodFill.changePaintColor(with(Random()) { Color.argb(255, nextInt(256), nextInt(256), nextInt(256)) })
                        }
        )

    }

    fun Bitmap.replaceColor(fromColor: Int, targetColor: Int): Bitmap {

        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        for (x in pixels.indices) {
            pixels[x] = if (pixels[x] == fromColor) targetColor else pixels[x]
        }

        return Bitmap.createBitmap(width, height, config).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.empty()
    }
}
