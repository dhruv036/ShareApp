package com.dhruv.fillsharing.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhruv.fillsharing.R
import com.dhruv.fillsharing.databinding.ActivityDashBoardBinding
import com.dhruv.fillsharing.repository.NoteRepo
import com.dhruv.fillsharing.util.*
import com.dhruv.fillsharing.ui.viewmodel.DashBoardViewModel


class DashBoardActivity : AppCompatActivity() {
    private val TAG = "DashBoardActivity"
    lateinit var binding : ActivityDashBoardBinding
    val repo = NoteRepo()
    val viewmodel : DashBoardViewModel by viewModels {
        ViewModelFactory(repo =repo , context = this, viewModel = ViewModelTypes.DASHBOARD_VIEWMODEL)
    }
    var phone = ""
    val PICK_PDF_FILE_PRIVATE = 2
    val PICK_PDF_FILE_PUBLIC = 1
    lateinit var adapter1 : NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board)

        phone = intent.getStringExtra("phone").toString()
        val name = intent.getStringExtra("name")


        binding.notes = viewmodel
        binding.lifecycleOwner =this
        binding.nameLabel.setText("Hello $name")

        binding.addPublicFile.setOnClickListener{
            openFile1()
        }
        binding.addPrivateFile.setOnClickListener{
            openFile2()
        }

        binding.privateBt.setOnClickListener{
            viewmodel.fetchPrivateNotes(phone)
        }

        adapter1 = NoteAdapter(mutableListOf(),this)
        binding.docList.adapter = adapter1

       fetchData()
    }

    fun fetchData() {

            viewmodel.notes.observeForever( Observer {
                val list:MutableList<Note>
                if (it.isNullOrEmpty()){
                    list = mutableListOf()
                }else{
                    list = it
                }
                binding.docList.let {
                    it.layoutManager =LinearLayoutManager(applicationContext)
                    it.adapter = NoteAdapter(list,this@DashBoardActivity)
                }
            })

    }

    fun openFile1() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PICK_PDF_FILE_PUBLIC)
    }
    fun openFile2() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PICK_PDF_FILE_PRIVATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE_PUBLIC && resultCode == Activity.RESULT_OK){
            data?.data.also {
                it?.let {
                    val cursor: Cursor? = contentResolver.query(it, null, null, null, null, null)
                    var displayName: String =""
                    if (cursor!=null && cursor.moveToFirst()){
                         displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                        Toast.makeText(this,displayName,Toast.LENGTH_SHORT).show()
                    }
                    viewmodel.uploadFile(it,phone,UploadType.PUBLIC,displayName)
                }
            }
        }else if(requestCode == PICK_PDF_FILE_PRIVATE && resultCode == Activity.RESULT_OK){
            data?.data.also {
                it?.let {
                    val cursor: Cursor? = contentResolver.query(it, null, null, null, null, null)
                    var displayName =""
                    if (cursor!=null && cursor.moveToFirst()){
                         displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                        Toast.makeText(this,displayName,Toast.LENGTH_SHORT).show()
                    }
                    cursor?.close()
                    viewmodel.uploadFile(it,phone,UploadType.PRIVATE,displayName)
                }
            }
        }else{
            Toast.makeText(this,"Try Again",Toast.LENGTH_SHORT).show()
        }
    }
}