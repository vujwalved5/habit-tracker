package com.example.habittracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.WorkManager
import com.example.habittracker.BuildConfig
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.remote.HabitApi
import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }

        // H4: Only log request/response bodies in debug builds to prevent
        // leaking PII, auth tokens, or user data to Logcat in production.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // H5: Explicit timeouts prevent indefinite hangs from slow/malicious servers.
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            // H1: Attach Bearer token auth header to every outgoing request
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.CLOUDFLARE_API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.CLOUDFLARE_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitApi(retrofit: Retrofit): HabitApi {
        return retrofit.create(HabitApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao, workManager: WorkManager): HabitRepository {
        return HabitRepositoryImpl(dao, workManager)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }
}
