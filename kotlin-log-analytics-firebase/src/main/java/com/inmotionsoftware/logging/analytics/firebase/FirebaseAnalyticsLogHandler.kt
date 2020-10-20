/***************************************************************************
 * This source file is part of the kotlin-log-analytics-firebase           *
 * open source project.                                                    *
 *                                                                         *
 * Copyright (c) 2020-present, InMotion Software and the project authors   *
 * Licensed under the MIT License                                          *
 *                                                                         *
 * See LICENSE.txt for license information                                 *
 ***************************************************************************/

package com.inmotionsoftware.logging.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inmotionsoftware.logging.*

class FirebaseAnalyticsLogHandler(
    private val label: String,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : LogHandler {

    override var logLevel: LoggerLevel = LoggerLevel.Info

    override var metadata: LoggerMetadata = LoggerMetadata()

    override operator fun get(metadataKey: String): LoggerMetadataValue? {
        return this.metadata[metadataKey]
    }

    override operator fun set(metadataKey: String, value: LoggerMetadataValue) {
        this.metadata[metadataKey] = value
    }

    override fun log(
        level: LoggerLevel,
        message: LoggerMessage,
        metadata: LoggerMetadata?,
        source: String?,
        file: String?,
        function: String?,
        line: Int?
    ) {
        val logger = Logger("FirebaseAnalyticsLogHandler")
        if (source?.isSourceAnalyticsEvent == true) {
            firebaseAnalytics.logEvent(message.toString(), metadata?.asBundle())
        } else if (source?.isSourceAnalyticsScreen == true) {
            logger.debug("AnalyticsScreen: ${message.toString()}", {metadata})
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, metadata?.asBundle())
        } else if (source?.isSourceAnalyticsError == true || level == LoggerLevel.Error) {
            logger.debug("AnalyticsError: ${message.toString()}")
            firebaseCrashlytics.log(message.toString())
        }
    }

}

///
/// LoggerMetadata+Bundle
///

private fun LoggerMetadata.asBundle(): Bundle {
    val bundle = Bundle()
    this.entries.forEach { entry ->
        when(val value = entry.value) {
            is LoggerMetadataValue.String -> bundle.putString(entry.key, value.value)
            else -> {}
        }
    }
    return bundle
}
