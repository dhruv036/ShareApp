package com.dhruv.fillsharing.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruv.fillsharing.ui.DashBoardActivity
import com.dhruv.fillsharing.util.User
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.util.LoginType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RegisterAndLoginViewModel(val repo: NoteRepo,val context:Context): ViewModel() {
    private val TAG = "RegisterAndLoginViewMod"
    val name = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val phone = MutableLiveData<String>()

    fun onRegisterUser(){
        Log.e("user"," register name ${name.value} , phone ${phone.value} , password ${password.value}")
          val uid = 10000 + Random().nextInt(90000)
           if (name.value != null && phone.value != null && password.value != null){
               val phoneLen = phone.value!!.length
               if (phoneLen == 10){
                  val isRegister : Boolean = repo.registerUser(
                       User(
                       uid = uid.toString(),
                       password = password.value!!,
                       phone = phone.value!!,
                       name = name.value!!
                        )
                   )
                   val result = if (isRegister)  "Register" else "not Register"
                   showMessage("User $result")
               }else{ showMessage("Wrong Phone Number") }
           }else{ showMessage("Incorrect Details") }
    }

    fun onLoginUser(){
        if (!phone.value.isNullOrEmpty()){
            if (phone.value!!.length == 5){
                if (!password.value.isNullOrEmpty()){
                    loginWithUidFromFirebase(phone.value!!,password.value!!)
                }
            }else if (phone.value!!.length == 10){
                if (!password.value.isNullOrEmpty()){
                    loginWithPhoneFromFirebase(phone.value!!,password.value!!)
                }
            }else{
                showMessage("Wrong number")
            }
        }else{
            showMessage("Please enter number")
        }
    }

    private fun loginWithPhoneFromFirebase(phone:String, password:String){
        viewModelScope.launch(Dispatchers.Main) {
            repo.loginUser(Triple(LoginType.PHONE_NUMBER,phone,password))
            repo.res.observeForever( androidx.lifecycle.Observer {
                Log.d(TAG, "${it.first}")
                if (it!!.second){
                    showMessage("Right Password")
                    val intent = Intent(context, DashBoardActivity::class.java)
                    intent.putExtra("phone",it.first!!.phone)
                    intent.putExtra("name",it.first!!.name)
                    context.startActivity(intent)
                } else {
                    showMessage("Wrong Password")
                }
            })
        }
    }

    private fun loginWithUidFromFirebase(phone:String, password:String){
        viewModelScope.launch(Dispatchers.Main) {
            repo.loginUser(Triple(LoginType.USER_ID,phone,password))
            repo.res.observeForever( androidx.lifecycle.Observer {
                Log.d(TAG, "${it.first}}")
                if (it!!.second){
                    showMessage("Right Password")
                    val intent = Intent(context, DashBoardActivity::class.java)
                    intent.putExtra("phone",it.first!!.phone)
                    intent.putExtra("name",it.first!!.name)
                    context.startActivity(intent)
                } else {
                    showMessage("Wrong Password")
                }
            })
        }
    }

    fun showMessage( message : String){
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
    }

}