package com.example.filip.cunnyedgetest

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.*

class EdgeDetectionBlendFilter(scaledSource: Bitmap) : GPUImageFilterGroup() {
    init {
        addFilter(GPUImageThresholdEdgeDetection().apply {
            setLineSize(3f)
            setThreshold(0.88f)
        })
        addFilter(GPUImageColorBlendFilter().apply {
            bitmap = scaledSource
        })
    }

    fun setLineSize(size: Float) {
        (filters.first { it is GPUImageThresholdEdgeDetection } as GPUImageThresholdEdgeDetection).setLineSize(size)
    }

    fun setThreshold(threshold: Float) {
        (filters.first { it is GPUImageThresholdEdgeDetection } as GPUImageThresholdEdgeDetection).setThreshold(threshold)
    }
}