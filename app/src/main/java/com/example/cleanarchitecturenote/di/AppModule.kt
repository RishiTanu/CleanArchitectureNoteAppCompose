package com.example.cleanarchitecturenote.di

import android.app.Application
import androidx.room.Room
import com.example.cleanarchitecturenote.feature_note.data.data_source.NoteDatabase
import com.example.cleanarchitecturenote.feature_note.data.repository.NoteRepositoryImpl
import com.example.cleanarchitecturenote.feature_note.domain.repository.NoteRepository
import com.example.cleanarchitecturenote.feature_note.domain.use_case.AddNoteUseCase
import com.example.cleanarchitecturenote.feature_note.domain.use_case.DeleteNoteUseCase
import com.example.cleanarchitecturenote.feature_note.domain.use_case.GetNoteIdUseCase
import com.example.cleanarchitecturenote.feature_note.domain.use_case.GetNotesUseCase
import com.example.cleanarchitecturenote.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return Room.databaseBuilder(
            application,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }


    @Provides
    @Singleton
    fun providesNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNoteUseCase = GetNotesUseCase(repository),
            deleteNoteUseCase = DeleteNoteUseCase(repository),
            addNote = AddNoteUseCase(noteRepository = repository),
            getNoteIdUseCase = GetNoteIdUseCase(noteRepository = repository)
        )
    }
}