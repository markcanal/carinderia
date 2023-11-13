package com.example.carinderia.data.repository

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.carinderia.BuildConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class GooglePlayService @Inject constructor(
    activity: Activity,
    private val appUpdateManager: AppUpdateManager
) : InstallStateUpdatedListener, DefaultLifecycleObserver {

    private val activity = activity as ComponentActivity
    private var type = AppUpdateType.FLEXIBLE
    private val dialog =
        MaterialAlertDialogBuilder(activity)
            .setTitle("Update is ready")
            .setMessage("The update has been downloaded.")
            .setPositiveButton("Install and restart") { _, _ ->
                appUpdateManager.completeUpdate()
            }
            .setNegativeButton("Cancel", null)
            .create()

    fun initialize() {
        if (!BuildConfig.DEBUG) {
            activity.lifecycle.addObserver(this)
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    when {
                        info.isFlexibleUpdateAllowed -> {
                            appUpdateManager.startUpdateFlow(
                                info,
                                activity,
                                AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
                            )
                            type = AppUpdateType.FLEXIBLE
                        }

                        info.isImmediateUpdateAllowed -> {
                            appUpdateManager.startUpdateFlow(
                                info,
                                activity,
                                AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                            )
                            type = AppUpdateType.IMMEDIATE
                        }
                    }
                }
            }
            appUpdateManager.registerListener(this)
        }
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) dialog.show()
    }

    override fun onResume(owner: LifecycleOwner) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (type == AppUpdateType.FLEXIBLE && info.installStatus() == InstallStatus.DOWNLOADED) {
                dialog.show()
            } else if (type == AppUpdateType.IMMEDIATE && info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlow(
                    info,
                    activity,
                    AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                )
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(this)
        owner.lifecycle.removeObserver(this)
    }
}