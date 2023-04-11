package com.dhruv.fillsharing.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData

import com.dhruv.fillsharing.util.User
import com.dhruv.fillsharing.util.LoginType
import com.dhruv.fillsharing.util.Note
import com.dhruv.fillsharing.util.UploadType
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class NoteRepo : CoroutineScope {
    private val TAG = "NoteRepo"
    var isPasswordCorrect = false
    var res = MutableLiveData<Pair<User?, Boolean>>()
    var curuser: User? = null
    val db = Firebase.firestore
    val storage = Firebase.storage
    var isSuccessfull = MutableLiveData<Boolean>()
    var publicNotelistAll = MutableLiveData<MutableList<Note>>()

    fun registerUser(user: User): Boolean {
        var isRegister = false
        db.collection("users").document(user.phone).set(user)
            .addOnSuccessListener {
                isRegister = true
            }
            .addOnFailureListener { e ->
                isRegister = false
            }
        return isRegister
    }

    fun uploadFileInStorage(file: Uri, phone: String, type: UploadType, name: String, ): MutableLiveData<Boolean> {

        launch {
            val fileref = storage.reference.child("files/${UUID.randomUUID().toString()}")
            val uploadTask = fileref.putFile(file!!)
            uploadTask.addOnFailureListener {
                Log.d(TAG, "Upload is failed")
            }.addOnSuccessListener {
                Log.d(TAG, "Upload is successfull")
                fileref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "URL ${it.toString()}")
                    isSuccessfull = storeFileInFirebase(phone, type, name, it.toString())
                }
            }
        }
        return isSuccessfull
    }

    private fun storeFileInFirebase(
        phone: String,
        type: UploadType,
        name: String,
        url: String,
    ): MutableLiveData<Boolean> {
        var isSuccessfullnote = MutableLiveData<Boolean>()
        launch {
            when (type) {
                UploadType.PUBLIC -> {
                    val id = UUID.randomUUID().toString()
                    var note = Note(noteId = id, noteName = name, noteUrl = url, userPhone = phone)
                    db.collection("public").document("$id").set(note)
                        .addOnSuccessListener {
                            Log.e(TAG, "Note Saved Successfully")
                            isSuccessfullnote.postValue(true)
                        }
                }
                UploadType.PRIVATE -> {
                    val id = UUID.randomUUID().toString()
                    var note = Note(noteId = id, noteName = name, noteUrl = url, userPhone = phone)
                    var list: MutableList<Note> = mutableListOf()
                    list.add(note)
                    db.collection("private").document("note").collection(phone).add(note)
                        .addOnSuccessListener {
                            Log.e(TAG, "Note Saved Successfully")
                            isSuccessfullnote.postValue(true)
                        }
                }
            }
        }
        return isSuccessfullnote
    }

    suspend fun loginUser(credentials: Triple<LoginType, String, String>) {
        launch {
            db.collection("users").get().addOnSuccessListener {
                if (!it.isEmpty) {
                    if (credentials.first == LoginType.PHONE_NUMBER) {
                        res.postValue(checkWithPhone(it, credentials))
                    } else if (credentials.first == LoginType.USER_ID) {
                        res.postValue(checkWithUserId(it, credentials))
                    }
                }
            }.addOnFailureListener {
                Log.e(TAG, "${it.printStackTrace()}")
            }
        }.join()
    }

    fun fetchPrivateNotes(phone: String): MutableLiveData<MutableList<Note>> {
        launch {
            db.collection("private").document("note").collection(phone)
                .addSnapshotListener { value, error ->
                    val notes = mutableListOf<Note>()
                    if (value != null) {
                        if (!value.documents.isEmpty()) {
                            value!!.documents!!.map {
                                if (it.exists()) {
                                    it?.let {
                                        val data = it.data
                                        val note = Note(
                                            data!!["noteId"].toString(),
                                            data["noteName"].toString(),
                                            data["noteUrl"].toString(),
                                            data["userPhone"].toString()
                                        )
                                        notes.add(note)
                                    }
                                    publicNotelistAll.postValue(notes)
                                } else {
                                    publicNotelistAll.postValue(mutableListOf())
                                }
                            }
                        } else {
                            publicNotelistAll.postValue(mutableListOf())
                        }

                    }
                }
        }
        return publicNotelistAll
    }

    fun fetchAllPublicNotes(): MutableLiveData<MutableList<Note>> {
        launch {
            db.collection("public").addSnapshotListener { snapshot, e ->
                val notes = mutableListOf<Note>()
                snapshot?.documents?.map {
                    it?.let {
                        val data = it.data
                        val note = Note(
                            data!!["noteId"].toString(),
                            data["noteName"].toString(),
                            data["noteUrl"].toString(),
                            data["userPhone"].toString()
                        )
                        notes.add(note)
                    }
                    Log.e(TAG, "${notes}")

                    publicNotelistAll.postValue(notes)
                }
            }

        }
        return publicNotelistAll
    }

    fun checkWithPhone(list: QuerySnapshot, credentials: Triple<LoginType, String, String>,
    ): Pair<User?, Boolean> {

        for (user in list) {
            if (user.id.equals(credentials.second)) {
                if (user.data.get("password")!!.equals(credentials.third)) {
                    Log.e(TAG, "User Register")
                    user.data?.let {
                        curuser = User(
                            it.get("uid").toString(),
                            it.get("phone").toString(),
                            it.get("password").toString(),
                            it.get("name").toString()
                        )
                        isPasswordCorrect = true
                    }
                    Log.d(TAG, "$curuser")
                    return Pair(curuser, true)
                } else {
                    isPasswordCorrect = false
                }
            }
        }
        return Pair(curuser, isPasswordCorrect)
    }

    fun checkWithUserId(
        list: QuerySnapshot,
        credentials: Triple<LoginType, String, String>,
    ): Pair<User?, Boolean> {
        var curuser: User? = null
        var isPasswordCorrect = false
        for (user in list) {
            if (user.data.get("uid")!!.equals(credentials.second)) {
                if (user.data.get("password")!!.equals(credentials.third)) {
                    Log.e(TAG, "User Register")
                    user.data?.let {
                        curuser = User(
                            it.get("uid").toString(),
                            it.get("phone").toString(),
                            it.get("password").toString(),
                            it.get("name").toString()
                        )
                    }
                    isPasswordCorrect = true
                } else {
                    isPasswordCorrect = false
                }
            }
        }
        return Pair(curuser, isPasswordCorrect)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}