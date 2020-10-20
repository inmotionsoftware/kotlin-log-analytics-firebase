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

import com.inmotionsoftware.logging.*

interface AnalyticsError {
    val message: String?
    val error: Throwable
}

interface AnalyticsEvent {
    val name: String
    val attributes: Map<String, String>?
}

data class AnalyticsScreen(var screenName: String, var screenClass: String) {
    val metadata: LoggerMetadata
        get() {
            return mutableMapOf(
                "screenName" to this.screenName.asLoggerMetadataValue(),
                "screenClass" to this.screenClass.asLoggerMetadataValue()
            )
        }
}

object LoggerSourceAnalytics {
    const val AnalyticsError = "AnalyticsError"
    const val AnalyticsEvent = "AnalyticsEvent"
    const val AnalyticsScreen = "AnalyticsScreen"
}

///
/// String+Analytics
///

val String.isSourceAnalytics: Boolean
    get() {
        return this.isSourceAnalyticsError || this.isSourceAnalyticsEvent || this.isSourceAnalyticsScreen
    }

val String.isSourceAnalyticsError: Boolean
    get() { return this == LoggerSourceAnalytics.AnalyticsError }

val String.isSourceAnalyticsEvent: Boolean
    get() { return this == LoggerSourceAnalytics.AnalyticsEvent }

val String.isSourceAnalyticsScreen: Boolean
    get() { return this == LoggerSourceAnalytics.AnalyticsScreen }

///
/// Map+LoggerMetadata
///

fun Map<String, String>.asLoggerMetadata(): LoggerMetadata {
    val metadata = LoggerMetadata(this.count())
    forEach { metadata[it.key] = it.value.asLoggerMetadataValue() }
    return metadata
}

///
/// LoggerMetadata+Analytics
///

val LoggerMetadata.screenName: String?
    get() {
        return when(val value = this["screenName"]) {
            is LoggerMetadataValue.String -> value.value
            else -> value.toString()
        }
    }

val LoggerMetadata.screenClass: String?
    get() {
        return when(val value = this["screenClass"]) {
            is LoggerMetadataValue.String -> value.value
            else -> value.toString()
        }
    }

///
/// Logger+Analytics
///

fun Logger.recordError(error: () -> AnalyticsError) {
    val err = error()
    err.message?.let(this::error)
    this.error(err.error.stackTraceToString(), location = __location(err.error))
}

fun Logger.recordEvent(event: () -> AnalyticsEvent) {
    val ev = event()
    this.info(
            message = ev.name,
            metadata = { ev.attributes?.asLoggerMetadata() },
            location = LogLocation(source = LoggerSourceAnalytics.AnalyticsEvent, file = "", function = "", line = 0)
        )
}

fun Logger.recordScreen(screen: () -> AnalyticsScreen) {
    this.info(
            message = "Screen View",
            metadata = {screen().metadata},
            location = LogLocation(source = LoggerSourceAnalytics.AnalyticsScreen, file = "", function = "", line = 0)
        )
}

fun Logger.recordScreen(name: String, screenClass: String) {
    this.recordScreen { AnalyticsScreen(name, screenClass) }
}
