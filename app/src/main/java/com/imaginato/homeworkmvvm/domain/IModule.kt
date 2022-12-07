package com.imaginato.homeworkmvvm.domain

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.imaginato.homeworkmvvm.BuildConfig
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.data.local.login.UserDatabase
import com.imaginato.homeworkmvvm.data.remote.login.LoginApi
import com.imaginato.homeworkmvvm.data.remote.login.NetworkErrorInterceptor
import com.imaginato.homeworkmvvm.ui.base.IApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object IModule {

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideAppContext() = IApp.instance

    @Singleton
    @Provides
    fun provideHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )

    @Singleton
    @Provides
    fun provideApiUserInterface(retrofitClient: OkHttpClient.Builder): LoginApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(retrofitClient))
            .build().create(LoginApi::class.java)

    private fun getOkHttpClient(build: OkHttpClient.Builder): OkHttpClient {
        return build.connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(provideNetworkErrorInterceptor(provideGson()))
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkErrorInterceptor(
        gson: Gson
    ): NetworkErrorInterceptor =
        NetworkErrorInterceptor(
            IApp.instance.resources.getString(
                R.string.no_internet_connection
            ), gson
        )

    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app, UserDatabase::class.java,
        "USER_DATABASE"
    ).allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideUserDao(db: UserDatabase) = db.userDao
}