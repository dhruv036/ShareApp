package com.dhruv.fillsharing.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dhruv.fillsharing.R
import com.dhruv.fillsharing.databinding.ActivityLoginBinding
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.ui.viewmodel.RegisterAndLoginViewModel
import com.dhruv.fillsharing.util.ViewModelFactory
import com.dhruv.fillsharing.util.ViewModelTypes

class LoginActivity : AppCompatActivity() {
    val repo: NoteRepo = NoteRepo()
    val viewModel : RegisterAndLoginViewModel by viewModels() {
        ViewModelFactory(repo,this, ViewModelTypes.REGISTER_AND_LOGIN_VIEWMODEL)
    }
    lateinit var  binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.myuser = viewModel
        binding.lifecycleOwner = this

    }
}