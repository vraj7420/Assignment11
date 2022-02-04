package com.example.sharedataassignment.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedataassignment.R
import com.example.sharedataassignment.adapter.ContactAdapter
import com.example.sharedataassignment.model.ContactModel
import com.example.sharedataassignment.view.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_contacts.view.*


class ContactsFragment : Fragment() {
    companion object {
        var contactList = ArrayList<ContactModel>()
    }

    private lateinit var viewContact: View

    private fun checkReadContactPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        } else {
            getContactList()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContactList()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                return
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }

        }
    }

    @SuppressLint("Range")
    private fun getContactList() {
        val cr: ContentResolver = context?.contentResolver!!
        val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sort)
        var phoneNo: String
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val photoIndex =
                        cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    val uri: String? = cur.getString(photoIndex)
                    val phoneCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )

                    while (phoneCur!!.moveToNext()) {
                        phoneNo =
                            phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        if (uri != null) {
                            contactList.add(ContactModel(name, phoneNo, uri))
                        } else {
                            contactList.add(ContactModel(name, phoneNo, Uri.parse("android.resource://com.example.sharedataassignment/drawable/ic_person").toString()))
                        }
                    }
                    phoneCur.close()
                }
            }
        }
        cur?.close()
        setAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreateView", "On Create")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "On Resume")
        checkReadContactPermission()

    }
    private  fun setAdapter(){
        val contactAdapter = ContactAdapter(requireContext(), contactList)
        viewContact.rvContactsList.layoutManager = LinearLayoutManager(requireContext())
        viewContact.rvContactsList.adapter = contactAdapter
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause", "On pause")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewContact = inflater.inflate(R.layout.fragment_contacts, container, false)
        Log.d("onCreateView", "On Created")
        return viewContact
    }
}