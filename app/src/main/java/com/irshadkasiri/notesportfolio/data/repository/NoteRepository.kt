package com.irshadkasiri.notesportfolio.data.repository

import com.irshadkasiri.notesportfolio.data.local.NoteDao
import com.irshadkasiri.notesportfolio.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    fun getFavoriteNotes(): Flow<List<NoteEntity>>
    fun getNotesByCategory(category: String): Flow<List<NoteEntity>>
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun deleteNoteById(id: Long)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    override fun getFavoriteNotes(): Flow<List<NoteEntity>> = noteDao.getFavoriteNotes()
    override fun getNotesByCategory(category: String): Flow<List<NoteEntity>> = noteDao.getNotesByCategory(category)
    override fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)
    override suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    override suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    override suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    override suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)
    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = noteDao.updateFavoriteStatus(id, isFavorite)
}
