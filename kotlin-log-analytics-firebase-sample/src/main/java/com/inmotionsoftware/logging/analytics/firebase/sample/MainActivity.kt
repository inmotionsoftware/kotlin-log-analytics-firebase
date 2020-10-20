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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inmotionsoftware.logging.*
import com.inmotionsoftware.logging.analytics.firebase.AnalyticsScreen
import com.inmotionsoftware.logging.analytics.firebase.recordError
import com.inmotionsoftware.logging.analytics.firebase.recordEvent
import com.inmotionsoftware.logging.analytics.firebase.recordScreen

class MainActivity : AppCompatActivity() {
    private val logger = Logger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logger.recordEvent { SampleAnalyticsEvent.SampleAnalyticsEvent1 }
        logger.recordEvent { SampleAnalyticsEvent.SampleAnalyticsEvent2 }

        logger.recordScreen { AnalyticsScreen("MainActivityScreen", "MainActivity")  }
        logger.recordScreen("MainActivityScreen", "MainActivity")

        logger.recordError { SampleAnalyticsError.SampleError1(Throwable("Test sample error 1")) }
        logger.recordError { SampleAnalyticsError.SampleError2(Throwable("Test sample error 2")) }
        logger.recordError { GenericAnalyticsError("A generic error", Throwable("Test")) }

        logger.debug("A test debug message", metadata = {
            mutableMapOf(
                "test" to "testValue".asLoggerMetadataValue(),
                "test2" to "testValue2".asLoggerMetadataValue()
            )
        }, location = __location())

    }

}
