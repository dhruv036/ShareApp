package com.dhruv.fillsharing.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.util.Note
import com.dhruv.fillsharing.util.UploadType
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DashBoardViewModel(val noteRepo: NoteRepo, val context: Context) : ViewModel(), CoroutineScope {

    var notes = MutableLiveData<MutableList<Note>>()

    fun uploadFile(uri: Uri, phone: String, type: UploadType, name: String) {
        noteRepo.uploadFileInStorage(uri, phone, type, name).observeForever(Observer {
            if (it!!) showMessage("Upload Successfull") else showMessage("Upload Failed")
        })

    }

    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

     init {
         fetchAllNotes()
     }

    fun fetchPrivateNotes(phone: String){
       notes = noteRepo.fetchPrivateNotes(phone)
    }


    fun fetchAllNotes() {
       notes = noteRepo.fetchAllPublicNotes()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}