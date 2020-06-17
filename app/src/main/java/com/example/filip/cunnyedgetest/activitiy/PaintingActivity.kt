package com.example.filip.cunnyedgetest.activitiy

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
        fun newIntent(context: Context, @DrawableRes resource: Int): Intent = Intent(context, PaintingActivity::class.java)
            .putExtra(RESOURCE_EXTRA, resource)
    }

    private val disposable = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disposable.setFrom(
            painting_image_view.dispose(),
            change_color_button.clicks()
                .subscribe { painting_image_view.changeColor() }
        )

        painting_image_view.setImage(intent.getIntExtra(RESOURCE_EXTRA, -1))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.empty()
    }
}
