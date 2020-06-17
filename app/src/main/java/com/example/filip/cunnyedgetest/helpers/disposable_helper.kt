package com.example.filip.cunnyedgetest.helpers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable

fun SerialDisposable.setFrom(vararg disposables: Disposable) = set(CompositeDisposable(disposables.asList()))

fun SerialDisposable.empty() = set(Disposables.empty())
