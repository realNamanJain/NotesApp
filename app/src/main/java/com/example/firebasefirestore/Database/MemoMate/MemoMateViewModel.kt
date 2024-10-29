package com.example.firebasefirestore.Database.MemoMate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ViewModel for managing MemoMate data
class MemoMateViewModel(private val repository: MemoMateRepository) : ViewModel() {

    // MutableLiveData to hold the list of notes
    private val _notesList = MutableLiveData<List<MemoMate>>(emptyList())
    // Exposed as LiveData to observe changes
    val notesList: LiveData<List<MemoMate>> = _notesList

    // MutableLiveData for the currently selected note
    private val _currentNote = MutableLiveData<MemoMate?>(null)
    // Exposed as LiveData
    val currentNote: LiveData<MemoMate?> = _currentNote

    // Holds the ID of the current note
    private var _currentNoteId: String? = null
    // Public property to get and set the currentNoteId
    var currentNoteId: String?
        get() = _currentNoteId
        set(value) {
            _currentNoteId = value // Allows external modification of the note ID
        }

    // MutableLiveData to manage loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    // Exposed as LiveData
    val isLoading: LiveData<Boolean> = _isLoading

    // Function to add a new note
    fun addNote(name: String, title: String, content: String, onResult: (Boolean) -> Unit) {
        val note = MemoMate(title = title, content = content) // Create a new note object

        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            // Add note through the repository
            val success = repository.addNote(name, note)
            if (success) fetchAllNotes(name) // Refresh notes after successful addition
            onResult(success) // Return the result through the callback
            _isLoading.value = false // Set loading state to false
        }
    }

    // Function to fetch all notes for a specific user
    fun fetchAllNotes(name: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            // Fetch notes through the repository
            repository.fetchAllNotes(name) { notes ->
                _notesList.value = notes // Update notes list
                _isLoading.value = false // Stop loading after fetching notes
            }
        }
    }

    // Function to fetch a note by its title
    fun fetchNoteWithTitle(name: String, title: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            // Fetch the note from the repository
            val note = repository.fetchNotesWithTitle(name, title)
            _currentNote.value = note // Assign it to _currentNote
            currentNoteId = note?.id // Assign document ID if note exists
            _isLoading.value = false // Set loading state to false
        }
    }

    // Function to update an existing note
    fun updateNote(name: String, documentId: String, content: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            // Update the note through the repository
            val success = repository.updateNote(name, documentId, content)
            if (success) {
                fetchAllNotes(name) // Refresh notes after update
            }
            onResult(success) // Return the result through the callback
            _isLoading.value = false // Set loading state to false
        }
    }
}