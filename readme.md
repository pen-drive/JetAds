<h1><img src="docs/images/badge.png" alt="JetAds" width="32"/> JetAds </h1>

[![Maven Central](https://img.shields.io/maven-central/v/com.karacca/beetle)](https://search.maven.org/artifact/com.karacca/beetle)
[![License](https://img.shields.io/github/license/karacca/beetle)](https://www.apache.org/licenses/LICENSE-2.0)


Plug adn Earn


Adicione e gerencia ads de maneira facil nos seus apps jetpack compose,
se precisar desligar os ads por motivos como user comprou para remover ads, ou algo semenlhante 
voce consegue facilmente desligar todos os ads do app com somente: 

    ftonOffads.now()

Tenha em mente que carregar todos os ads de uma unica so vez, poderia acarretar eem problemas dee performace e 
possivelmente disperdicion de recursos, por exmeplo, Carregar todos os Rewardeds na MainScreen para serem usadas
posteriormente, caso o usere entre eem um fluxo para onde sera solicidado o rewarded eele nunca sera exibido
oque seria um disperdicio d erecurso, ent carregga Rewardes ee interstitals um pouco antees de ondee elees 
forem ser exibidos.



## Testes
A lib fornce o objeto AdMobTestIds, fornecendo todos os ids de [test do admob](https://developers.google.com/admob/android/test-ads):

    object AdMobTestIds {
        const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
        const val ADAPTIVE_BANNER = "ca-app-pub-3940256099942544/9214589741"
        const val FIXED_SIZE_BANNER = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_ADS = "ca-app-pub-3940256099942544/5224354917"
        const val REWARDED_INTERSTITIAL = "ca-app-pub-3940256099942544/5354046379"
        const val NATIVE = "ca-app-pub-3940256099942544/2247696110"
        const val NATIVE_VIDEO = "ca-app-pub-3940256099942544/1044960115"
    }



## Como Usar:

*Adicione*

    implementation platform('androidx.compose:compose-bom:2024.08.00')

*iniciialise a biblioteca*

Adicione admob meta-data

    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/>




>[!WARNING]
> não esqueca de alterar pelo APPLICATION_ID do seu app! o APPLICATION_ID é um de teste fornecido pelo 
> [admob](https://developers.google.com/admob/android/test-ads)


>[!CAUTION]
> Esta lib vai te ensentivar a usar seus proprios ids tão lembre de adicionar seu dispositivo/emulador
> como um [dispositivo de test](https://developers.google.com/admob/android/test-ads#enable_test_devices)



# Como usar cada ad

Para *Intertitial e Rewardeds* você deve chamar o loader deles um pouco antes de onde eles serão mostrados

Tanto o *Intertitial* quanto *Rewardeds* terão seus loaders e seus ManagerImpl, é pelos managers que você usa
para mostra-los



## Rewarded

### Carregar anuncio premiado
para se carregar um anúncio premiado voce deve um pouco antes de onde ele vai ser usado chamar este composable

    RewardedLoader(AdMobTestIds.REWARDED_ADS)

posteriormente quando se for mostrar realmente o ads voce deve usar:

    RewardedAdmobManagerImpl.show(AdMobTestIds.REWARDED_ADS, this@MainActivity)


## Intertitials


## Banner
Para usar o banner somente coloque ele onde voce quiser usalo

     Banner(AdMobTestIds.ADAPTIVE_BANNER)

> [!TIP]
> Exite o parametro safeTopMarginDp serve para adicionar um marge de seguranca em cima do banner para
> evitar clicks intencionais. Caso necessário é possivel também alterar o cor da safe area.

## Open Adds
Usando delegacao na MainActivity adicione:  AdsInitializer by AdmobInitializer():   
    
    class MainActivity : AppCompatActivity(), AdsInitializer by AdmobInitializer(){}


# Proximas features, possivelmente!

 * Native ads
 * Mediação


# Duvidas?
Clone este projeto e de uma olhada em como esta lib foi utilizada dentro do app



# contribua

Contribua com a lib: Esta lib tem a filosofia de ser facil de usar, deve-sempre esconder as implemenações 
dos users da lib, sempre senguin a filosofia "Plug and Earn". Ao contribuir tenha cuidado com memory leaks! no
App de Demostração ja esta adicionado o LeakCanary, sempre cheque se sua alteração não provocou algum memoty leak.
