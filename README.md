# <img src="docs/images/badge.png" alt="JetAds" width="32"/> JetAds

[![Maven Central](https://img.shields.io/maven-central/v/io.github.pen-drive/jet-ads)](https://search.maven.org/artifact/io.github.pen-drive/jet-ads)
[![License](https://img.shields.io/github/license/karacca/beetle)](https://www.apache.org/licenses/LICENSE-2.0)

Plug and Earn

## Easy Ads for Jetpack Compose

Adicione e gerencie anúncios de maneira fácil nos seus apps Jetpack Compose. Uma biblioteca para
tornar mais simples a implementação do AdMob.

> [!CAUTION]
> SE voce não estiver usando IDs de teste, então lembre-se de adicionar seu dispositivo/emulador
> como
>
um [dispositivo de teste](https://developers.google.com/admob/android/test-ads#enable_test_devices).

## Instalação

A maneira mais fácil de começar a usar o JetAds é adicioná-lo como uma dependência Gradle no
arquivo `build.gradle` do seu módulo de app.

```kotlin
implementation("io.github.pen-drive:jet-ads:1.0.1")
```

> [!TIP]
> Não é necessário adicionar a dependência do AdMob, pois ela já é incluída no seu projeto por meio
> de dependência transitiva.

### Adicione meta-data do AdMob

Adicione o seguinte no seu arquivo `AndroidManifest.xml`:

```xml

<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-3940256099942544~3347511713" />
```

> [!TIP]
> Altere para o APPLICATION_ID do seu app! O ID acima é um ID de teste fornecido
> pelo [AdMob](https://developers.google.com/admob/android/test-ads).

### Inicialize os anúncios

```kotlin
class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeAds(this) // Inicializa os anúncios

        setContent {
            Greeting(name = "Android")
        }
    }
}
```

### <img src="docs/images/banner.svg" alt="Banner Ad" width="40" height="40" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 40px;">Banner</span>

Para adicionar um banner adaptativo:

```kotlin
AdaptiveBanner("SEU_ID_DE_BANNER")
```

Para posicionar o banner na parte inferior da tela:

```kotlin
Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = { AdaptiveBanner(AdMobTestIds.ADAPTIVE_BANNER) }
) { innerPadding ->
    Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
}
```

### <img src="docs/images/interstitial.svg" alt="Interstitial Ad" width="40" height="40" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 40px;">Interstitials</span>

Chame este composable em algum ponto antes do momento de mostrar o Interstitial:

```kotlin
LoadInterstitial("SEU_ID_DE_INTERSTITIAL")
```

Para mostrar o Interstitial:

```kotlin
val interstitials: Interstitials = InterstitialsFactory.admobInterstitial()
interstitials.show("ID_DO_INTERSTITIAL_PREVIAMENTE_CARREGADO", activityContext)
```

### <img src="docs/images/rewarded.svg" alt="Rewarded Ad" width="40" height="40" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 40px;">Rewardeds</span>

Chame este composable em algum ponto antes do momento de mostrar o Rewarded:

```kotlin
LoadRewarded(AdMobTestIds.REWARDED)
```

Para mostrar o Rewarded:

```kotlin
val rewarded: Rewarded = RewardedFactory.admobRewarded()
rewarded.show(adId, activity) { rewardedItem ->
    // Lógica para lidar com a recompensa
}
```

## <img src="docs/images/appOpen.svg" alt="App Open Ad" width="64" height="64" style="vertical-align: middle; margin-right: 10px;"/><span style="display: inline-block; vertical-align: middle; line-height: 40px;">Open Ads</span>

Adicione OpenAdSetup a sua activtity e use a delecagçao corespondente!

Maneira para mostrar ads sempre que o app entrar no primeiro plano

```kotlin
class MainActivity : ComponentActivity(),
    AdsInitializer by AdsInitializeFactory.admobInitializer(),
    OpenAdSetup by AppOpenAdManagerFactory.admobAppOpenInitializer() // <-- for app open ads
{


    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAds(this)

        registerAppOpenAd(AdMobTestIds.APP_OPEN, this)  // <-- for app open ads

        setContent {
            SampleJetAdsTheme {
                //content
            }
        }
    }


}
```

Maneira para mostrar ads sempre que o app entrar no primeiro plano, e em [cold start](https://developers.google.com/admob/android/app-open#coldstart), ou seje, na
primeira vez que o app entra. Esta é a maneira recomendada por quando.

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
          showOnColdStart = false,
            closeSplashScreen = {
                keepSplashScreen = false
            })  // <-- for app open ads

        setContent {
            SampleJetAdsTheme {

            }
        }
    }


}
```

Sera preciso adicionar a api de [splashScreen](https://www.youtube.com/watch?v=abthd7DOfdw) ou alguma outra solucao de splash scren, pois mostrar o anuncio em cold start sem a 
splash screen resulta em um atraso na exeibição do ad, o que pode incomodar o user. Vale dar uma lida nas [melhores praticas para app open ads](https://support.google.com/admob/answer/9341964)


### IDs para Testes

A biblioteca fornece o objeto `AdMobTestIds` para acessar todos os IDs
de [teste do AdMob](https://developers.google.com/admob/android/test-ads):

```kotlin
object AdMobTestIds {
    const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
    const val ADAPTIVE_BANNER = "ca-app-pub-3940256099942544/9214589741"
    const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED = "ca-app-pub-3940256099942544/5224354917"
}
```

## Próximas features (possivelmente)

- Native ads
- Mediação

## Contribuindo

Contribuições de novas funcionalidades ou correções de bugs são sempre bem-vindas. Por favor,
submeta uma nova issue ou pull request a qualquer momento.

Ao contribuir, tenha em mente:

- A filosofia da biblioteca é ser fácil de usar, sempre escondendo as implementações complexas dos
  usuários.
- Siga o princípio "Plug and Earn".
- Tenha cuidado com memory leaks! O app de demonstração já inclui o LeakCanary; sempre verifique se
  sua alteração não provocou algum vazamento de memória. Precisa adicionar
  o [leakCanary a pipeline](https://square.github.io/leakcanary/ui-tests/#leak-detection-in-ui-tests) 