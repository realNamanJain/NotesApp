package com.example.firebasefirestore.Database.MVVM

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    private val _notesList = MutableStateFlow<List<Notes>>(emptyList())
    val notesList: StateFlow<List<Notes>> = _notesList

    private val _currentNote = MutableStateFlow<Notes?>(null)
    val currentNote: StateFlow<Notes?> = _currentNote

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addNote(
        context: Context,
        name: String,
        title: String,
        content: String
    ) {
        val note = Notes(title = title, content = content)
        viewModelScope.launch {
            val success = repository.addNote(name, note)
            if (success) {
                fetchAllNotes(context, name) // Refresh notes after adding
            }
        }
    }

    fun fetchAllNotes(context: Context, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _notesList.value = repository.fetchAllNotes(name)
            _isLoading.value = false
        }
    }

    fun fetchNoteWithTitle(
        context: Context,
        name: String,
        title: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val (note, _) = repository.fetchNotesWithTitle(name, title)
            _currentNote.value = note
            _isLoading.value = false
        }
    }

    fun updateNote(
        context: Context,
        name: String,
        documentId: String,
        title: String,
        content: String
    ) {
        val note = Notes(title, content)
        viewModelScope.launch {
            val success = repository.updateNote(name, documentId, note)
            if (success) {
                fetchAllNotes(context, name) // Refresh notes after update
            }
        }
    }
}