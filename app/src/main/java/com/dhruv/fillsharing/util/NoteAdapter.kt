package com.dhruv.fillsharing.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.dhruv.fillsharing.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NoteAdapter(var list: MutableList<Note>, var context : Context) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val TAG = "NoteAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var phone = itemView.findViewById<TextView>(R.id.user)
        var saveBt = itemView.findViewById<ImageView>(R.id.saveBt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.note_child,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun downloadFile(url: String,title:String){
        val manager = context.getSystemService() as DownloadManager?
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setTitle(title)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title)
        manager!!.enqueue(request)
        Toast.makeText(context,"Download started",Toast.LENGTH_SHORT).show()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = list.get(position)
        holder.title.setText(note.noteName)
        Firebase.firestore.collection("users").document(note.userPhone).get().addOnSuccessListener {
            Log.d(TAG, "${it?.data?.get("name")?.toString()}")
            val name = it?.data?.get("name")?.toString()
            holder.phone.setText(name)
        }
       holder.saveBt.setOnClickListener{
            downloadFile(note.noteUrl,note.noteName)
        }

    }


}