package com.dhruv.fillsharing.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.dhruv.fillsharing.ui.LoginActivity
import com.dhruv.fillsharing.ui.RegisterActivity
import com.dhruv.fillsharing.repository.NoteRepo

class MainViewModel(val noteRepo: NoteRepo,val context:Context) : ViewModel() {

    fun goToSignUp(){
//        Toast.makeText(context,"Sign Up successfull",Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(  context, RegisterActivity::class.java))
    }

    fun goToSignIn(){
//        Toast.makeText(context,"Sign In successfull",Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(  context, LoginActivity::class.java))

    }
}