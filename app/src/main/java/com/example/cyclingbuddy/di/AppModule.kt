package com.example.cyclingbuddy.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.cyclingbuddy.util.Constants.KEY_NAME
import com.example.cyclingbuddy.util.Constants.SHARED_PREFERENCES_NAME
import com.example.cyclingbuddy.db.CyclingBuddyDatabase
import com.example.cyclingbuddy.util.Constants.KEY_EMAIL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        CyclingBuddyDatabase::class.java,
        "cycling_buddy_database"
    ).build()

    @Singleton
    @Provides
    fun provideRideDao(db: CyclingBuddyDatabase) = db.getRideDao()

    @Singleton
    @Provides
    fun provideJourneyDao(db: CyclingBuddyDatabase) = db.getJourneyDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

}