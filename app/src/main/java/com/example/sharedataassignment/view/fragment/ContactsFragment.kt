package com.example.sharedataassignment.view.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedataassignment.R
import com.example.sharedataassignment.adapter.ContactAdapter
import com.example.sharedataassignment.model.ContactModel
import kotlinx.android.synthetic.main.fragment_contacts.view.*


class ContactsFragment : Fragment() {
    companion object {
        var contactList = ArrayList<ContactModel>()
    }
    private lateinit var viewContact: View
   private var rationalFlagREAD: Boolean = false
    private lateinit var  alertPermission:AlertDialog

      private fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 1)
        }
    }

    private fun isPermissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        } else false
    }

    private fun navigateToSettingScreen() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList()
                } else {
                    rationalFlagREAD = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
                    if (!rationalFlagREAD) {
                        navigateToSettingScreen()
                    }else{
                        return
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getContactList() {
        contactList.clear()
        viewContact.pbWaiting.visibility = View.VISIBLE
        val cr: ContentResolver = context?.contentResolver!!
        val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sort)
        var phoneNo: String
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String =
                    cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String =
                    cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
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
                            contactList.add(
                                ContactModel(
                                    name,
                                    phoneNo,
                                    Uri.parse("android.resource://com.example.sharedataassignment/drawable/ic_person").toString()
                                )
                            )
                        }
                    }
                    phoneCur.close()
                }
            }
        }
        cur?.close()
        Handler(Looper.getMainLooper()).postDelayed({
            setAdapter()
        }, 5000)
    }


    override fun onResume() {
        super.onResume()
       if(isPermissionsGranted()){
           alertPermission.dismiss()
          getContactList()
       }else{
           alertPermission.show()
       }
    }

    private fun setAdapter() {
        val contactAdapter = ContactAdapter(requireContext(), contactList)
        viewContact.rvContactsList.layoutManager = LinearLayoutManager(requireContext())
        viewContact.rvContactsList.adapter = contactAdapter
        viewContact.pbWaiting.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertPermission=AlertDialog.Builder(requireContext())
            .setMessage("Require Permission")
            .setPositiveButton("Continue", ({ _: DialogInterface, _: Int ->
                requestContactPermission()
            })).setNegativeButton("Cancel",({ dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })).setCancelable(false)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewContact = inflater.inflate(R.layout.fragment_contacts, container, false)
        return viewContact
    }

}