package com.irshadkasiri.notesportfolio.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.irshadkasiri.notesportfolio.data.local.NoteDatabase
import com.irshadkasiri.notesportfolio.data.local.NoteEntity
import com.irshadkasiri.notesportfolio.data.repository.NoteRepository
import com.irshadkasiri.notesportfolio.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NoteViewModel(
    application: Application,
    private val repository: NoteRepository
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All Notes")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _editingNote = MutableStateFlow<NoteEntity?>(null)
    val editingNote: StateFlow<NoteEntity?> = _editingNote.asStateFlow()

    val notes: StateFlow<List<NoteEntity>> = combine(
        _searchQuery.debounce(200L).distinctUntilChanged(),
        _selectedCategory,
        _selectedTab
    ) { query, category, tab ->
        Triple(query, category, tab)
    }.flatMapLatest { (query, category, tab) ->
        when {
            tab == 1 -> repository.getFavoriteNotes()
            query.isNotBlank() -> repository.searchNotes(query)
            category != "All Notes" -> repository.getNotesByCategory(category)
            else -> repository.getAllNotes()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun startEditingNote(note: NoteEntity?) {
        _editingNote.value = note
    }

    fun saveNote(
        title: String,
        description: String,
        category: String,
        colorIndex: Int
    ) {
        if (title.isBlank()) {
            _uiMessage.value = "Please enter a note title."
            return
        }

        viewModelScope.launch {
            val current = _editingNote.value
            if (current != null) {
                val updatedNote = current.copy(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    colorIndex = colorIndex
                )
                repository.updateNote(updatedNote)
                _uiMessage.value = "Note updated successfully!"
                _editingNote.value = null
            } else {
                val newNote = NoteEntity(
                    title = title.trim(),
                    description = description.trim().ifBlank { "No description added." },
                    category = category,
                    colorIndex = colorIndex
                )
                repository.insertNote(newNote)
                _uiMessage.value = "Note created successfully!"
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            _uiMessage.value = "Note deleted."
            if (_editingNote.value?.id == note.id) {
                _editingNote.value = null
            }
        }
    }

    fun toggleFavorite(note: NoteEntity) {
        viewModelScope.launch {
            val newFavStatus = !note.isFavorite
            repository.toggleFavorite(note.id, newFavStatus)
            _uiMessage.value = if (newFavStatus) "Added to Favorites" else "Removed from Favorites"
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                val db = NoteDatabase.getDatabase(application)
                val repository = NoteRepositoryImpl(db.noteDao())
                return NoteViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
