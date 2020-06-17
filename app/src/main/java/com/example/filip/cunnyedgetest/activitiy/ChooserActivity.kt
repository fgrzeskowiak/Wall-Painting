package com.example.filip.cunnyedgetest.activitiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filip.cunnyedgetest.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chooser.*

class ChooserActivity : AppCompatActivity() {
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

        disposable.set(CompositeDisposable(
            resourceSubject.subscribe {
                startActivity(PaintingActivity.newIntent(this, it))
            }
        ))
    }
}