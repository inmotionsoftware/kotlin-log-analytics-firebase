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

import com.inmotionsoftware.logging.analytics.firebase.AnalyticsError
import com.inmotionsoftware.logging.analytics.firebase.AnalyticsEvent

sealed class SampleAnalyticsEvent(
    override val name: String,
    override val attributes: Map<String, String>?
): AnalyticsEvent {
    object SampleAnalyticsEvent1: SampleAnalyticsEvent(
        "SampleAnalyticsEvent1",
        mapOf("attr1" to "attr1 _value", "attr2" to "attr2_value")
    )
    object SampleAnalyticsEvent2: SampleAnalyticsEvent(
        "SampleAnalyticsEvent2",
        mapOf("attr1" to "attr1 _value", "attr2" to "attr2_value")
    )
}

sealed class SampleAnalyticsError(
    override val message: String?,
    override val error: Throwable
): AnalyticsError {
    data class SampleError1(override val error: Throwable): SampleAnalyticsError("SampleError1", error)
    data class SampleError2(override val error: Throwable): SampleAnalyticsError("SampleError2", error)
}

data class GenericAnalyticsError(override val message: String?, override val error: Throwable): AnalyticsError
