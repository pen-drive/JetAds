# <img src="docs/images/badge.png" alt="JetAds" width="64"/> JetAds

[![Maven Central](https://img.shields.io/maven-central/v/io.github.pen-drive/jet-ads)](https://search.maven.org/artifact/io.github.pen-drive/jet-ads)
[![License](https://img.shields.io/github/license/karacca/beetle)](https://www.apache.org/licenses/LICENSE-2.0)
[![Android API](https://img.shields.io/badge/api-21%2B-green)](https://android-arsenal.com/api?level=21)


## Easy Ads for Jetpack Compose

Easily integrate and manage ads in your Jetpack Compose apps with a library that simplifies AdMob implementation. It’s like 'Plug and Earn!'

> [!CAUTION]
> If you're not using test IDs, remember to add your device/emulator as a [test device](https://developers.google.com/admob/android/test-ads#enable_test_devices).

## Installation

The easiest way to start using JetAds is to add it as a Gradle dependency in your app module's `build.gradle` file.

```kotlin
implementation("io.github.pen-drive:jet-ads:<version>")
```

> [!TIP]
> There's no need to add the [AdMob/Google Ads](https://developers.google.com/admob/android/quick-start) dependency, as it's already included in your project through transitive dependency.

### Add AdMob meta-data

After adding the library, add the following meta-data to your `AndroidManifest.xml` file:

```xml
<manifest>
  <application>
    <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
    <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
  </application>
</manifest>
```
> [!TIP]
> Change to your app's APPLICATION_ID! The ID above is a test ID provided by [AdMob](https://developers.google.com/admob/android/quick-start#:~:text=%3Cmanifest%3E%0A%20%20%3Capplication%3E%0A%20%20%20%20%3C!%2D%2D%20Sample%20AdMob%20app%20ID%3A%20ca%2Dapp%2Dpub%2D3940256099942544~3347511713%20%2D%2D%3E%0A%20%20%20%20%3Cmeta%2Ddata%0A%20%20%20%20%20%20%20%20android%3Aname%3D%22com.google.android.gms.ads.APPLICATION_ID%22%0A%20%20%20%20%20%20%20%20android%3Avalue%3D%22ca%2Dapp%2Dpub%2Dxxxxxxxxxxxxxxxx~yyyyyyyyyy%22/%3E%0A%20%20%3C/application%3E%0A%3C/manifest%3E).

### Initialize the ads

```kotlin
class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeAds(this) // Initialize the ads

        setContent {
            Greeting(name = "Android")
        }
    }
}
```

### <img src="docs/images/banner.svg" alt="Banner Ad" width="64" height="64" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 64px;">Banner</span>

To add an adaptive banner, simply place this composable where you want to show it:

```kotlin
AdaptiveBanner("YOUR_BANNER_ID")
```

To position the banner at the bottom of the screen:

> [!TIP]
> Banners are typically positioned at the bottom of the screen. To do this, you can:
> ```kotlin
>   Scaffold(
>     modifier = Modifier.fillMaxSize(),
>     bottomBar = { AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER) }) { innerPadding ->
>     //content
>     }
> ```

### <img src="docs/images/interstitial.svg" alt="Interstitial Ad" width="64" height="64" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 64px;">Interstitials</span>

Call this composable at some point before showing the Interstitial:

```kotlin
LoadInterstitial("YOUR_INTERSTITIAL_ID")
```

To show the Interstitial:

```kotlin
val interstitialsController: InterstitialsController = InterstitialsControllerFactory.admobController()
interstitialsController.show("PREVIOUSLY_LOADED_INTERSTITIAL_ID", activityContext)
```

### <img src="docs/images/rewarded.svg" alt="Rewarded Ad" width="64" height="64" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 64px;">Rewardeds</span>

Call this composable at some point before showing the Rewarded:

```kotlin
LoadRewarded("YOUR_REWARDED_ID")
```

To show the Rewarded:

```kotlin
val rewardsController: RewardsController = RewardedControllerFactory.admobController()
rewardsController.show("PREVIOUSLY_LOADED_REWARDED_ID", activity) { rewardedItem ->
    // Logic to handle the reward
}
```

## <img src="docs/images/appOpen.svg" alt="App Open Ad" width="64" height="64" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 64px;">Open Ads</span>

Add OpenAdSetup to your activity and use the corresponding delegation!

Method to show ads whenever the app enters the foreground:

```kotlin
class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer(),
    OpenAdSetup by AppOpenAdManagerFactory.admobAppOpenInitializer() // <-- for app open ads
{
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAds(this)

        registerAppOpenAd("YOUR_APP_OPEN_ID", this)  // <-- for app open ads

        setContent {
            //Compose content
        }
    }
}
```

Method to show ads whenever the app enters the foreground, and on [cold start](https://developers.google.com/admob/android/app-open#coldstart), i.e., the first time the app starts. This is the recommended method:

```kotlin
class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer(),
    OpenAdSetup by AppOpenAdManagerFactory.admobAppOpenInitializer() // <-- for app open ads
{
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAds(this)

        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            keepSplashScreen
        }
      
        registerAppOpenAd(AdMobTestIds.APP_OPEN,
          this,
          showOnColdStart = true,
            closeSplashScreen = {
                keepSplashScreen = false
            })  // <-- for app open ads

        setContent {
            //Compose content
        }
    }
}
```
> [!CAUTION]
> The **Show on [cold start](https://developers.google.com/admob/android/app-open#coldstart)** feature was specifically designed to work **only** with the [SplashScreen API](https://www.youtube.com/watch?v=abthd7DOfdw).
> Using a splash screen as a regular activity may not function as expected.
>
> Note: This behavior may change in future versions.



## Test IDs

The library provides the `AdMobTestIds` object to access all [AdMob test IDs](https://developers.google.com/admob/android/test-ads):

```kotlin
object AdMobTestIds {
  const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
  const val ADAPTIVE_BANNER = "ca-app-pub-3940256099942544/9214589741"
  const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
  const val REWARDED = "ca-app-pub-3940256099942544/5224354917"
  const val NATIVE = "ca-app-pub-3940256099942544/2247696110"
  const val FIXED_SIZE_BANNER = "ca-app-pub-3940256099942544/6300978111"
  const val REWARDED_INTERSTITIAL = "ca-app-pub-3940256099942544/5354046379"
  const val NATIVE_VIDEO = "ca-app-pub-3940256099942544/1044960115"
}
```

> [!TIP]
> Take a look at the app module in this repository; there you can see more advanced ways to use this library.


## Upcoming features (possibly)

- Native ads
- Mediation

## Contributing

Contributions of new features or bug fixes are always welcome. Please submit a new issue or pull request at any time.

When contributing, keep in mind:

- The library's philosophy is to be easy to use, always hiding complex implementations from users.
- Contributors must follow the 'Plug and Earn' principle, ensuring that this library remains simple and easy to use for developers.
- Be careful with memory leaks! The demo app already includes LeakCanary; always check if your change hasn't caused any memory leaks. You need to add [LeakCanary to the pipeline](https://square.github.io/leakcanary/ui-tests/#leak-detection-in-ui-tests).