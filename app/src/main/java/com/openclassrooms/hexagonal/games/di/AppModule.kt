package com.openclassrooms.hexagonal.games.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserManager
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.data.service.PostFakeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Provides
  @Singleton
  fun providePostApi(): PostApi {
    return PostFakeApi()
  }

  @Provides
  @Singleton
  fun provideUserRepository(): UserRepository {
    return UserRepository()
  }

  @Provides
  @Singleton
  fun provideUserManager(userRepository: UserRepository): UserManager {
    return UserManager(userRepository)
  }

  @Provides
  @Singleton
  fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
  }

  @Provides
  @Singleton
  fun providePostRepository(
    firestore: FirebaseFirestore,  // Only provide FirebaseFirestore here
    storage: FirebaseStorage // Provide FirebaseStorage if needed
  ): PostRepository {
    return PostRepository(firestore) // Pass only FirebaseFirestore to the PostRepository constructor
  }


  @Provides
  @Singleton
  fun provideFirebaseFirestore(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseStorage(): FirebaseStorage {
    return FirebaseStorage.getInstance()
  }
}
