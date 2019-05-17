package com.example.filip.cunnyedgetest

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import java.util.*


class RxPermissions(private val context: Context) {

    private val subjects: HashMap<String, PublishSubject<Boolean>> = HashMap(6)

    fun observe(permissions: List<String>): Observable<Boolean> {
        val observables = ArrayList<Observable<Boolean>>(permissions.size)
        for (permission in permissions) {
            val permissionSubject = createPermissionSubject(permission)
            observables.add(permissionSubject.startWith(isGranted(permission)))
        }

        return Observable.combineLatest(observables, permissionCheckerFunction())
    }


    fun request(requester: (permissions: List<String>) -> Unit, permissions: List<String>): Observable<Boolean> {
        val observables = ArrayList<Observable<Boolean>>(permissions.size)

        for (permission in permissions) {
            createPermissionSubject(permission)
        }

        val notGrantedPermissions = permissions.filterNot { isGranted(it) }
        if (notGrantedPermissions.isNotEmpty()) requester(notGrantedPermissions)

        for (permission in permissions) {
            val permissionSubject = subjects[permission]
            observables.add(permissionSubject?.startWith(isGranted(permission))
                    ?: Observable.just(false))
        }

        return Observable.combineLatest(observables, permissionCheckerFunction())
    }

    fun onRequestPermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        val size = permissions.size
        var i = 0
        while (i < size) {
            subjects[permissions[i]]?.onNext(grantResults[i] == PackageManager.PERMISSION_GRANTED)
            i++
        }
    }

    private fun createPermissionSubject(permission: String): PublishSubject<Boolean> {
        var permissionSubject = subjects[permission]
        if (permissionSubject == null) {
            permissionSubject = create()
            subjects[permission] = permissionSubject
        }
        return permissionSubject!!
    }

    private fun permissionCheckerFunction(): Function<Array<Any>, Boolean> {
        return Function {
            it
                    .filterNot { it as Boolean }
                    .forEach { return@Function false }
            true
        }
    }

    private fun isGranted(permission: String) = Build.VERSION.SDK_INT < 23 || isGranted60(permission)

    @TargetApi(Build.VERSION_CODES.M)
    private fun isGranted60(permission: String) = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

}