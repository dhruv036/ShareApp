package com.dhruv.fillsharing.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dhruv.fillsharing.R
import com.dhruv.fillsharing.databinding.ActivityMainBinding
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.ui.viewmodel.MainViewModel
import com.dhruv.fillsharing.util.ViewModelFactory
import com.dhruv.fillsharing.util.ViewModelTypes


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    val repo: NoteRepo = NoteRepo()
    val viewModel: MainViewModel by viewModels(){
        ViewModelFactory(repo, this,ViewModelTypes.MAINVIEW_MODEL)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.user = viewModel
        binding.lifecycleOwner =this
    }
}