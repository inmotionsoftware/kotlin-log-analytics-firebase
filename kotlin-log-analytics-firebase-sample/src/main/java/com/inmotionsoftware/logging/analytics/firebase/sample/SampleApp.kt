/***************************************************************************
 * This source file is part of the kotlin-log-analytics-firebase           *
 * open source project.                                                    *
 *                                                                         *
 * Copyright (c) 2020-present, InMotion Software and the project authors   *
 * Licensed under the MIT License                                          *
 *                                                                         *
 * See LICENSE.txt for license information                                 *
 ***************************************************************************/

package com.inmotionsoftware.logging.analytics.firebase.sample

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inmotionsoftware.logging.android.AndroidLogcatHandler
import com.inmotionsoftware.logging.android.AndroidLogcatHandler.MetadataContentType
import com.inmotionsoftware.logging.LogHandler
import com.inmotionsoftware.logging.LoggingSystem
import com.inmotionsoftware.logging.MultiplexLogHandler
import com.inmotionsoftware.logging.analytics.firebase.FirebaseAnalyticsLogHandler

class SampleApp: Application() {

    private val firebaseAnalytics by lazy {FirebaseAnalytics.getInstance(this.applicationContext)}
    private val firebaseCrashlytics by lazy {FirebaseCrashlytics.getInstance()}

    override fun onCreate() {
        super.onCreate()

        // Configure LoggingSystem
        LoggingSystem.bootstrap { label ->
            val handlers = mutableListOf<LogHandler>()

            if (BuildConfig.DEBUG) {
                handlers.add(AndroidLogcatHandler(label, MetadataContentType.Public))
            } else {
                handlers.add(AndroidLogcatHandler(label, MetadataContentType.Private))
                handlers.add(FirebaseAnalyticsLogHandler(label, firebaseAnalytics, firebaseCrashlytics))
            }
            MultiplexLogHandler(handlers)
        }
    }

}
