package com.example.venues.base.di

import com.example.venues.BuildConfig
import com.example.venues.data.local.VenueRepository
import com.example.venues.data.local.models.ClientData
import com.example.venues.data.local.repo.MainVenueRepository
import com.example.venues.data.network.api.Endpoints
import com.example.venues.data.network.interceptor.EndpointInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.foursquare.com/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideInterceptorClient(endpointInterceptor: EndpointInterceptor): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(endpointInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideColorEndpoint(gson: Gson, client: OkHttpClient): Endpoints =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Endpoints::class.java)
    @Singleton
    @Provides
    fun provideClientData(): ClientData =
        ClientData(clientId = BuildConfig.CLIENT_ID, clientSecret = BuildConfig.CLIENT_SECRET)

    @Singleton
    @Provides
    fun provideVenueRepository(endpoints: Endpoints, clientData: ClientData) : VenueRepository = MainVenueRepository(endpoints, clientData)

}