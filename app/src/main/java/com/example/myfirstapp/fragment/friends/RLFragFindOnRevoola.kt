package com.example.myfirstapp.fragment.friends

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.fragment.friends.adapter.RLContactsAdapter
import com.example.myfirstapp.model.RLContactModel
import com.example.myfirstapp.utils.RLPrefManager

class RLFragFindOnRevoola : RLBaseFragment() {
    val TAG: String = RLFragFindOnRevoola::class.java.simpleName
    lateinit var fragBinding: RlFragFingOnRevoolaBinding
    private val CONTACTS_PERMISSION_CODE = 1
    private val contactsList = mutableListOf<RLContactModel>()

    private val binding by lazy {
        RlFragFingOnRevoolaBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_fing_on_revoola, container) as RlFragFingOnRevoolaBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFindOnRevoola" )
        fragBinding.toolbar.tvTitle.setText(R.string.searchfriends)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
        } else {
            // Permission is already granted, so fetch the contacts
            fetchContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fetchContacts()
                }
            }
        }
    }

    private fun fetchContacts() {
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?", arrayOf(contactId), null)

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactsList.add(RLContactModel(contactName ?: "N/A", phoneNumber ?: "N/A"))
                        }
                        phoneCursor.close()
                    }
                }
            }
            cursor.close()
            fragBinding.listSyncContacts.layoutManager = LinearLayoutManager(requireContext())
            // Set the adapter once contacts are fetched
            val contactsAdapter = RLContactsAdapter(activity,contactsList)
            fragBinding.listSyncContacts.adapter = contactsAdapter

            fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    contactsAdapter.RLfilter(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

}