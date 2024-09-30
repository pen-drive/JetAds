<h1><img src="docs/images/badge.png" alt="JetAds" width="32"/> JetAds </h1>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.pen-drive/jet-ads)](https://search.maven.org/artifact/io.github.pen-drive/jet-ads)
[![License](https://img.shields.io/github/license/karacca/beetle)](https://www.apache.org/licenses/LICENSE-2.0)


Plug adn Earn


## `Easy Ads` for Jetpack Compose

Adicione, gerencie ads de maneira facil nos seus apps jetpack compose. Uma lib para tornar mais facil
a implementacao do admob.



>[!CAUTION]
> Esta lib vai te ensentivar a usar seus proprios ids tão lembre de adicionar seu dispositivo/emulador
> como um [dispositivo de test](https://developers.google.com/admob/android/test-ads#enable_test_devices)


### Installation

The easiest way to get started using JetAds is to add it as a gradle dependency in the build.gradle file
of your app module.

```gradle
implementation 'io.github.pen-drive:jet-ads:1.0.1'
```

or if your project using `build.gradle.kts`

```kotlin
implementation("io.github.pen-drive:jet-ads:1.0.1")
```


> [!TIP]
> Não precisa adicionar a depencia do Admob, poi ela ja é adicionada ao seu projeto por meio de 
> dependencia transitiva.


### Adicione admob meta-data

    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/>

>[!TIP]
> Altere pelo APPLICATION_ID do seu app! o APPLICATION_ID é um de teste fornecido pelo
> [admob](https://developers.google.com/admob/android/test-ads)





### inicialize ads

    class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer() {
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            
            initializeAds(this) // <-- initialize ads
            
    
            setContent {
    
                Greeting(
                    name = "Android",
                )
    
            }
        }
    }



### Banner
    
       AdaptiveBanner({ID DO SEU BANNER})

Esta é a maneira de posicionar o banner n aparte de baixo da tela:

        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER)}) { innerPadding ->
            Greeting(name = "Android",modifier = Modifier.padding(innerPadding))
        }






### Interstitials

chame este composable em algum ponte antes do momente de mostrar o Interstitial

    LoadInterstitial({ID DO SEU INTERSTITIAL})

Para mostrar o Interstitial:

    val interstitials: Interstitials = InterstitialsFactory.admobInterstitial()
    interstitials.show({ID DO REWARDED PREVIAMENTE CARREGADO}, {ACTIVITY CONTEXT})



### Rewardeds

chame este composable em algum ponte antes do momente de mostrar o Rewarded

    LoadRewarded(AdMobTestIds.REWARDED)


Para mostrar o Rewarded:

    val rewarded: Rewarded = RewardedFactory.admobRewarded()
    rewarded.show(event.adId, event.activity, onRewarded = { rewardedItem -> }) 



### Open Adds
Usando delegacao na MainActivity adicione:  AdsInitializer by AdmobInitializer():

    class MainActivity : AppCompatActivity(), AdsInitializer by AdmobInitializer(){}



### Ids para Testes
A lib fornce o objeto AdMobTestIds por meio dele você consegue acessar todos os ids de [test do admob](https://developers.google.com/admob/android/test-ads):

    object AdMobTestIds {
        const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
        const val ADAPTIVE_BANNER = "ca-app-pub-3940256099942544/9214589741"
        const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED = "ca-app-pub-3940256099942544/5224354917"
    }







# Proximas features, possivelmente!

 * Native ads
 * Mediação



### Contributing

The contribution of new features or bug fixes is always welcome. <br />
Please submit new issue or pull request anytime. Contribua com a lib: Esta lib tem a filosofia de ser facil de usar, deve-sempre esconder as implemenações
dos users da lib, sempre senguin a filosofia "Plug and Earn". Ao contribuir tenha cuidado com memory leaks! no
App de Demostração ja esta adicionado o LeakCanary, sempre cheque se sua alteração não provocou algum memoty leak.
