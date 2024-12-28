package com.openclassrooms.hexagonal.games.di

import android.content.Context
import android.content.SharedPreferences
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

  /**
   * Provides a Singleton instance of SharedPreferences.
   *
   * @param context The application context provided by Hilt.
   * @return A SharedPreferences instance with the name "settings".
   */
  @Provides
  @Singleton
  fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
  }
}
