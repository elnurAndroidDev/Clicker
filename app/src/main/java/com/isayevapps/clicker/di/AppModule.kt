package com.isayevapps.clicker.di

import LoggingInterceptor
import android.content.Context
import androidx.room.Room
import com.isayevapps.clicker.data.db.AppDatabase
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): DeviceDao {
        return database.deviceDao()
    }

    @Provides
    @Singleton
    fun provideCoordinateDao(database: AppDatabase): CoordinatesDao {
        return database.coordinateDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            // пробуем коннект максимум 200 мс
            .connectTimeout(300, TimeUnit.MILLISECONDS)
            // ждём ответа от сервера не более 500 мс
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            // для GET-запросов тело почти пустое, но пусть тоже 500 мс
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            // весь цикл запроса+ответа не более 800 мс
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            //.addInterceptor(LoggingInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("http:\\www.google.com")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}