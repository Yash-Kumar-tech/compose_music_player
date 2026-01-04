package com.ykt.musicplayer.di

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.ykt.musicplayer.data.playback.MediaSessionManager
import com.ykt.musicplayer.data.playback.NotificationManagerProvider
import com.ykt.musicplayer.data.repository.GenreRepositoryImpl
import com.ykt.musicplayer.data.repository.LanguageRepositoryImpl
import com.ykt.musicplayer.data.repository.RecentlyPlayedRepositoryImpl
import com.ykt.musicplayer.data.repository.SectionRepositoryImpl
import com.ykt.musicplayer.data.repository.SettingsRepository
import com.ykt.musicplayer.data.repository.SongRepositoryImpl
import com.ykt.musicplayer.data.repository.PlaylistRepositoryImpl
import com.ykt.musicplayer.domain.repository.PlaylistRepository
import com.ykt.musicplayer.domain.repository.SectionRepository
import com.ykt.musicplayer.domain.repository.SongRepository
import com.ykt.musicplayer.utils.APPWRITE_PROJECT_ID
import com.ykt.musicplayer.utils.APPWRITE_PUBLIC_ENDPOINT
import com.ykt.musicplayer.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.appwrite.Client
import io.appwrite.services.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppwriteClient(
        @ApplicationContext context: Context
    ): Client {
        return Client(context)
            .setEndpoint(APPWRITE_PUBLIC_ENDPOINT)
            .setProject(APPWRITE_PROJECT_ID)
    }

    @Provides
    @Singleton
    fun provideStorage(client: Client): Storage = Storage(client)

    @Provides
    @Singleton
    fun provideNetworkMonitor(app: Application): NetworkMonitor = NetworkMonitor(app)

    @Provides
    @Singleton
    fun provideSongRepository(
        firestore: FirebaseFirestore,
        storage: Storage
    ): SongRepositoryImpl = SongRepositoryImpl(firestore, storage)

    @Provides
    @Singleton
    fun provideRecentlyPlayedRepository(
        firestore: FirebaseFirestore
    ): RecentlyPlayedRepositoryImpl = RecentlyPlayedRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideGenreRepository(
        firestore: FirebaseFirestore
    ): GenreRepositoryImpl = GenreRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideLanguageRepository(
        firestore: FirebaseFirestore
    ): LanguageRepositoryImpl = LanguageRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideSectionRepository(): SectionRepository = SectionRepositoryImpl()

    @Provides
    @Singleton
    fun providePlaylistRepository(
        firestore: FirebaseFirestore
    ): PlaylistRepository = PlaylistRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.dataStoreFile("app_settings.preferences_pb") }
        )
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        return ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(audioAttributes, true)
            playWhenReady = true
            setSeekParameters(SeekParameters.CLOSEST_SYNC)
        }
    }

    @Provides
    @Singleton
    fun provideNotificationManagerProvider(
        @ApplicationContext context: Context
    ): NotificationManagerProvider = NotificationManagerProvider(context)

    @Provides
    @Singleton
    fun provideMediaSessionManager(
        @ApplicationContext context: Context
    ): MediaSessionManager = MediaSessionManager(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository = SettingsRepository(dataStore)

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}