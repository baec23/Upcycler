package com.baec23.upcycler.di

import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideUserRepository(firestore: FirebaseFirestore) =
        UserRepository(firestore)

    @Singleton
    @Provides
    fun provideJobRepository(firestore: FirebaseFirestore, storage: FirebaseStorage) =
        JobRepository(firestore, storage)

    @Singleton
    @Provides
    fun provideAppEventChannel() = Channel<AppEvent>()

    @Singleton
    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()
}