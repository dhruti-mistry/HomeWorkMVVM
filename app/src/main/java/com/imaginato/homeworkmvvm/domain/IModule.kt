package com.imaginato.homeworkmvvm.domain

import com.google.gson.Gson
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.data.remote.login.LoginApi
import com.imaginato.homeworkmvvm.data.remote.login.NetworkErrorInterceptor
import com.imaginato.homeworkmvvm.ui.base.IApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
            .baseUrl("https://private-222d3-homework5.apiary-mock.com/api/")
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


//    @Singleton
//    @Provides
//    fun provideYourDatabase(
//        @ApplicationContext app: Context
//    ) = Room.databaseBuilder(
//        app, SantaDatabase::class.java,
//        AppConstant.DB_NAME
//    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).allowMainThreadQueries().build()
//
//    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE UsersTicket ADD COLUMN CardHolderName TEXT")
//            database.execSQL("ALTER TABLE UsersTicket ADD COLUMN CreditCardLast4 TEXT")
//            database.execSQL("ALTER TABLE UsersTicket ADD COLUMN DateOfPurchase TEXT")
//        }
//    }
//
//    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE UsersTicket ADD COLUMN PlanName TEXT")
//        }
//    }
//
//    @Singleton
//    @Provides
//    fun provideUserDao(db: SantaDatabase) = db.getUserDao()
}