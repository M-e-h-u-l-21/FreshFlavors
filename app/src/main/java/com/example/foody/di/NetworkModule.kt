package com.example.foody.di

import com.example.foody.util.Constants.Companion.BASE_URL
import com.example.foody.data.network.FoodRecipesAPI
import com.example.foody.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


//Created because we want to provide instance of our retrofit to our remote data source and hilt will do that for us

@Module
@InstallIn(MyApplication::class)  // All bindings will be present in application component
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient():OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15,TimeUnit.SECONDS)
            .connectTimeout(15,TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory():GsonConverterFactory{
        return GsonConverterFactory.create()
    }


    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory // basically hilt will search all functions with return type as GsonConverterFactory
    ):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    // We have told how to give foodrecipesAPI but we need to also tell how to provide retrofit as it is also in parameter
    @Singleton // Mentions that only single instance of each dependency will be present
    //  Using application scope for this FoodRecipesAPI
    @Provides // Because we are using retrofit which is an external library and not created by us
    fun provideApiService(retrofit: Retrofit) : FoodRecipesAPI { // By defining return type we are basically telling which class will be injected later on
        return retrofit.create(FoodRecipesAPI::class.java)
    }

}