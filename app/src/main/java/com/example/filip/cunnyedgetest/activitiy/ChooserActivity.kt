package com.example.filip.cunnyedgetest.activitiy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filip.cunnyedgetest.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chooser.*


class ChooserActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_SELECT_FILE = 1
    }

    private val resourceSubject = PublishSubject.create<Int>()
    private val disposable = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        office_button.setOnClickListener {
            resourceSubject.onNext(R.drawable.appunite)
        }

        building_button.setOnClickListener {
            resourceSubject.onNext(R.drawable.drzwi)
        }

        room1_button.setOnClickListener {
            resourceSubject.onNext(R.drawable.pokoj1)
        }

        room2_button.setOnClickListener {
            resourceSubject.onNext(R.drawable.pokoj2)
        }

        choose_image_button.setOnClickListener {
            openFileChooser()
        }

        disposable.set(CompositeDisposable(
            resourceSubject.subscribe {
                startActivity(PaintingActivity.newIntent(this, it))
            }
        ))
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
            .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_SELECT_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            intent?.data?.let {
                contentResolver?.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                startActivity(PaintingActivity.newIntent(this, selectedFile = it))
            }
        }
    }
}