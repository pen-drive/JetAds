# Caminho: library/proguard-test-rules.pro

# Mantenha todas as classes usadas pelos testes, incluindo Robolectric e ComponentActivity
-keep class androidx.activity.** { *; }
-keep class androidx.compose.** { *; }
-keep class org.robolectric.** { *; }
-keep class androidx.test.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Mantenha todas as classes no pacote de testes
-keep class com.example.library.test.** { *; }

# Mantenha as classes dos mocks gerados pelo MockK
-keep class io.mockk.** { *; }
