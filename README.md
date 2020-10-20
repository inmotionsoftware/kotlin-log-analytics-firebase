# KotlinLogAnalyticsFirebase
 A logging backend for `KotlinLog` that sends analytics log messages to `Firebase`.

## Getting started

#### Adding the dependency

```
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    implementation "com.github.inmotionsoftware:kotlin-log:1.4.0"
    implementation "com.github.inmotionsoftware:kotlin-log-analytics-firebase:1.0.0"
    implementation "com.github.inmotionsoftware:kotlin-log-android:1.0.0"

    implementation "com.google.firebase:firebase-analytics"
    implementation "com.google.firebase:firebase-crashlytics"
}
```

#### Setup Firebase Analytics

Follow quickstart guide to set up Firebase Crashlytics in your app with the Firebase Crashlytics SDK.

https://firebase.google.com/docs/crashlytics/get-started?platform=android

#### Bootstrap KotlinLog

```kotlin
import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inmotionsoftware.logging.android.AndroidLogcatHandler
import com.inmotionsoftware.logging.android.AndroidLogcatHandler.MetadataContentType
import com.inmotionsoftware.logging.LogHandler
import com.inmotionsoftware.logging.LoggingSystem
import com.inmotionsoftware.logging.MultiplexLogHandler
import com.inmotionsoftware.logging.analytics.firebase.FirebaseAnalyticsLogHandler

class MyApp: Application() {
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(this.applicationContext) }
    private val firebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun onCreate() {
        super.onCreate()

        LoggingSystem.bootstrap { label ->
            val handlers = mutableListOf<LogHandler>()

            // Don't log analytics for DEBUG build
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
```

#### Implement AnalyticsEvent and AnalyticsError

Provide app specific analytics event and error implementations.

```kotlin
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
```

#### Let's log

```kotlin
// 1) let's import the logging API package
import com.inmotionsoftware.logging.Logger

// 2) we need to create a logger, the label works similarly to a DispatchQueue label
val logger = Logger(label="MyActivity")

// 3) we're now ready to use it
logger.recordEvent { SampleAnalyticsEvent.SampleAnalyticsEvent1 }
logger.recordEvent { SampleAnalyticsEvent.SampleAnalyticsEvent2 }

logger.recordScreen { AnalyticsScreen("MainActivityScreen", "MainActivity")  }
logger.recordScreen("MainActivityScreen", "MainActivity")

logger.recordError { SampleAnalyticsError.SampleError1(Throwable("Test sample error 1")) }
logger.recordError { SampleAnalyticsError.SampleError2(Throwable("Test sample error 2")) }
logger.recordError { GenericAnalyticsError("A generic error", Throwable("Test")) }
```

## License

MIT