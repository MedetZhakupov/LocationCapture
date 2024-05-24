# Android library to periodically capture location of the device and send it over to the server.

![](https://jitpack.io/v/MedetZhakupov/LocationCapture.svg)

To use the library add it to your `settings.gradle.kts`:
```kotlin
repositories {
    maven {  
	  url = uri("https://jitpack.io")  
	  content { includeGroup("com.github.MedetZhakupov") }  
	}
 }
```

and:
```kotlin
dependencies {
  implementation("com.github.MedetZhakupov:LocationCapture:${version}")
}
```

## How to use

```kotlin
val locationManager = LocationManagerBuilder(context)
	.setCoroutineScope(coroutineScope)
	.setApiKey(apiKey)
	.build()


locationManager.startPeriodicUpdates(TimeUnit.SECONDS.toMillis(30))

locationManager.stopUpdates()

locationManager.requestSingleUpdate(  
    onSuccess = {},  
    onError = {}  
)
```
