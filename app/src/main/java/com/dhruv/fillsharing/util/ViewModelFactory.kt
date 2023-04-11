package com.dhruv.fillsharing.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.ui.viewmodel.DashBoardViewModel
import com.dhruv.fillsharing.ui.viewmodel.MainViewModel
import com.dhruv.fillsharing.ui.viewmodel.RegisterAndLoginViewModel

class ViewModelFactory(private val repo: NoteRepo,val context: Context, private val viewModel: ViewModelTypes) :ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            when(viewModel.name){
                ViewModelTypes.MAINVIEW_MODEL.name ->{
                    return MainViewModel(repo,context) as T
                }
                ViewModelTypes.DASHBOARD_VIEWMODEL.name ->{
                    return DashBoardViewModel(repo,context) as T
                }
                ViewModelTypes.REGISTER_AND_LOGIN_VIEWMODEL.name->{
                    return RegisterAndLoginViewModel(repo,context) as T
                }
                else ->{
                    return super.create(modelClass)
                }

            }
    }

}