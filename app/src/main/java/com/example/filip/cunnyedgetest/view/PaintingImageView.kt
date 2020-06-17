package com.example.filip.cunnyedgetest.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.filip.cunnyedgetest.floodfill.FloodFill
import com.jakewharton.rxbinding2.view.touches
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.cyberagent.android.gpuimage.GPUImageColorBlendFilter
import jp.co.cyberagent.android.gpuimage.GPUImageThresholdEdgeDetection
import jp.co.cyberagent.android.gpuimage.GPUImageView
import java.util.*

class PaintingImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GPUImageView(context, attrs) {

    private val imageSetSubject = PublishSubject.create<Unit>()
    private val screenWidth: Int by lazy { resources.displayMetrics.widthPixels }
    private lateinit var edgeBitmap: Bitmap
    private val floodFill = FloodFill("FloodFill", null)

    val dispose: () -> CompositeDisposable = {
        CompositeDisposable(
            imageSetSubject
                .switchMap { edgeObservable }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    edgeBitmap = it
                    filter = GPUImageColorBlendFilter().apply {
                        bitmap = edgeBitmap
                    }
                },
            touches(Predicate { it.action == MotionEvent.ACTION_DOWN })
                .subscribe { imageTouched(it) }
        )
    }

    fun setPaintingImage(source: Bitmap) {
        val scaledBitmap = Bitmap.createScaledBitmap(
            source,
            screenWidth,
            (source.height * (screenWidth.toFloat() / source.width)).toInt(),
            false
        )

        //Applies the edge detection layer
        filter = GPUImageThresholdEdgeDetection().apply {
            setLineSize(1.5f)
            setThreshold(0.88f)
        }

        layoutParams.height = scaledBitmap.height
        setImage(scaledBitmap)

        imageSetSubject.onNext(Unit)
    }

    /**
     * Edge detection returns white image with black lines where an edge was detected,
     * so white needs to be turned into transparent to show the normal image below.
     * Changing color of every pixel is resourceful, so it needs to be done on a separate thread.
     */
    private val edgeObservable = Observable
        .fromCallable {
            gpuImage.bitmapWithFilterApplied
                .replaceColor(Color.WHITE, Color.TRANSPARENT)
        }
        .subscribeOn(Schedulers.computation())

    private fun imageTouched(event: MotionEvent) {
        val displayTouch = PointF().apply { set(event.x, event.y) }

        floodFill.apply {
            setBitmapConfiguration(edgeBitmap.width, edgeBitmap.height)
            start(edgeBitmap, displayTouch)
        }

        requestRender()

        filter = GPUImageColorBlendFilter().apply { //New color layer needs to be blend with the original image
            bitmap = edgeBitmap
        }
    }

    fun changeColor() {
        floodFill.changePaintColor(
            with(Random()) {
                Color.argb(255, nextInt(256), nextInt(256), nextInt(256))
            }
        )
    }

    private fun Bitmap.replaceColor(fromColor: Int, targetColor: Int): Bitmap {

        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        for (x in pixels.indices) {
            pixels[x] = if (pixels[x] == fromColor) targetColor else pixels[x]
        }

        return Bitmap.createBitmap(width, height, config).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }
}