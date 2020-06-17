package com.example.filip.cunnyedgetest.activitiy

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.example.filip.cunnyedgetest.R
import com.example.filip.cunnyedgetest.helpers.empty
import com.example.filip.cunnyedgetest.helpers.setFrom
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.activity_main.*

class PaintingActivity : AppCompatActivity() {

    companion object {
        private const val RESOURCE_EXTRA = "resource_extra"
        private const val URI_EXTRA = "uri_extra"
        fun newIntent(context: Context, @DrawableRes resource: Int? = null, selectedFile: Uri? = null): Intent =
            Intent(context, PaintingActivity::class.java)
                .putExtra(RESOURCE_EXTRA, resource)
                .putExtra(URI_EXTRA, selectedFile)
    }

    private val disposable = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disposable.setFrom(
            gpu_image_view.dispose(),
            gpu_image_view.setImage(intent.getIntExtra(RESOURCE_EXTRA, -1))
                .subscribe(),
            change_color_button.clicks()
                .subscribe { gpu_image_view.changeColor() }
        )
<<<<<<< Updated upstream
=======

        painting_image_view.setPaintingImage(
            if (intent.hasExtra(URI_EXTRA)) {
                getBitmap(intent?.extras?.get(URI_EXTRA) as? Uri)
            } else {
                BitmapFactory.decodeResource(
                    resources,
                    intent.getIntExtra(RESOURCE_EXTRA, -1),
                    BitmapFactory.Options().apply { inScaled = false }
                )
            }
        )
>>>>>>> Stashed changes
    }

    private fun getBitmap(uri: Uri?) = MediaStore.Images.Media.getBitmap(contentResolver, uri)

    override fun onDestroy() {
        super.onDestroy()
        disposable.empty()
    }
}
