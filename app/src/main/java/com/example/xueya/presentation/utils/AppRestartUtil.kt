package com.example.xueya.presentation.utils

import android.content.Context
import android.content.Intent
import android.os.Process

object AppRestartUtil {
    /**
     * 重启应用
     */
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val restartIntent = Intent.makeRestartActivityTask(componentName)
        
        // 启动新的Activity
        context.startActivity(restartIntent)
        
        // 杀死当前进程
        Process.killProcess(Process.myPid())
    }
}