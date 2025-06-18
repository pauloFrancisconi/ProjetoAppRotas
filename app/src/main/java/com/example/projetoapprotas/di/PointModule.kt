package com.example.projetoapprotas.di

import com.example.projetoapprotas.data.repository.PointRepository
import com.example.projetoapprotas.data.repository.PointRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PointModule {
    @Binds
    @Singleton
    fun bindRepo(impl: PointRepositoryImpl): PointRepository
}
