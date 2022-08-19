package com.baec23.upcycler.di

import android.content.Context
import com.baec23.upcycler.repository.DataStoreRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
    fun provideDataStoreRepository(@ApplicationContext context: Context) =
        DataStoreRepository(context)

    @Singleton
    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideAppEventChannel() = Channel<AppEvent>()
}