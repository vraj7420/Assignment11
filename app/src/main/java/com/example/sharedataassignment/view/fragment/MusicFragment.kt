package com.example.sharedataassignment.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedataassignment.R
import com.example.sharedataassignment.adapter.MusicAdapter
import com.example.sharedataassignment.model.MusicModel
import com.example.sharedataassignment.view.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_music.view.*


class MusicFragment : Fragment() {
    private lateinit var getFileAccessPermission: ActivityResultLauncher<Intent>
    private  var musicList=ArrayList<MusicModel>()
    private lateinit var viewMusic:View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFileAccessPermission = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        loadMusic()
                    }
                }
            }
        }
    }

    private fun storagePermission() {
        if (isPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                loadMusic()
            }
        } else {
            tackPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExternalStorage = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            readExternalStorage == PackageManager.PERMISSION_GRANTED
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun loadMusic() {
        val cr: ContentResolver = context?.contentResolver!!
        val allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? = cr.query(allSongsUri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songName: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val artistName: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    musicList.add(MusicModel(songName,artistName))

                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        setAdapter()
    }


    private fun tackPermission() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse(String.format("package%s",requireContext().packageName))
                getFileAccessPermission.launch(intent)

            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                getFileAccessPermission.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(context as MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                loadMusic()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package",requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        storagePermission()
    }

     private fun setAdapter(){
         val musicAdapter=MusicAdapter(requireContext(),musicList)
         viewMusic.rvMusic?.layoutManager =LinearLayoutManager(requireContext())
         viewMusic.rvMusic.adapter=musicAdapter
     }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMusic=inflater.inflate(R.layout.fragment_music, container, false)
        return viewMusic
    }
}