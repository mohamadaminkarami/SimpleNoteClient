package com.example.simplenote.di

import com.example.simplenote.data.network.AuthInterceptor
import com.example.simplenote.data.network.TokenManager
import com.example.simplenote.data.network.AuthRemoteDataSource
import com.example.simplenote.data.preferences.AuthPreferences
import com.example.simplenote.data.network.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        @Named("refresh") authApiService: AuthApiService,
        authPreferences: AuthPreferences
    ): AuthRemoteDataSource {
        return AuthRemoteDataSource(authApiService, authPreferences)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        authPreferences: AuthPreferences,
        authRemoteDataSource: AuthRemoteDataSource
    ): TokenManager {
        return TokenManager(authPreferences, authRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authPreferences: AuthPreferences,
        authRemoteDataSource: AuthRemoteDataSource
    ): AuthInterceptor {
        return AuthInterceptor(authPreferences, authRemoteDataSource)
    }
} 