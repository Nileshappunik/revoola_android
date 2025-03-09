package com.revoola.fragment.friends

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databinding.*
import com.revoola.fragment.friends.adapter.RLContactsAdapter
import com.revoola.fragment.friends.model.EmailFilterInviteData
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFindOnRevoolaInviteItem
import com.revoola.fragment.friends.model.RLSyncContactFilterModel
import com.revoola.model.RLContactModel
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragFindOnRevoola : RLBaseFragment() {
    val TAG: String = RLFragFindOnRevoola::class.java.simpleName
    lateinit var fragBinding: RlFragFingOnRevoolaBinding
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private val CONTACTS_PERMISSION_CODE = 1
    private   var currentUser:String = ""
    private var contactsList = mutableListOf<RLContactModel>()
    private val emailList = mutableListOf<String>()

    private val binding by lazy {
        RlFragFingOnRevoolaBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_fing_on_revoola, container) as RlFragFingOnRevoolaBinding
        currentUser= RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFindOnRevoola" )
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        fragBinding.toolbar.tvTitle.setText(R.string.searchfriends)
        fragBinding.toolbar.ivBack.setOnClickListener { RLcloseFragment()}
        RLCheckContactPermission()
        fragBinding.txtSyncContact.setOnClickListener {
            RLCheckContactPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fetchEmailContacts()
                }
            }
        }
    }

    private fun RLCheckContactPermission(){
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
        } else {
            // Permission is already granted, so fetch the contacts
            //fetchContacts()
            fetchEmailContacts()
        }
    }
    private fun fetchEmailContacts() {
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                // Query email addresses instead of phone numbers
                val emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?", arrayOf(contactId), null)

                if (emailCursor != null) {
                    while (emailCursor.moveToNext()) {
                        val email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                        if (!email.isNullOrEmpty()){
                            emailList.add(email)
                            contactsList.add(RLContactModel(contactName ?: "N/A", email ?: "N/A"))
                        }
                    }
                    emailCursor.close()
                }
            }
            cursor.close()
            //Api Call
            RLEmailFilterApiCall()
        }
    }

    private fun RLEmailFilterApiCall() {
        val request = listOf(RLEmailFilterRequestModel(syncContactNew = RLSyncContactFilterModel(currentUser = currentUser,email=emailList.toSet().toMutableList())))
        RLTools.RlLogDPrint(TAG,"Email Filter Request: $request")
        viewModel.RLFindOnRevoolaEmailFilter(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Email Filter Success: ${response.type}")
                        RLHandleApiResponse(response.text)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Email Filter Fail: ${response.type}")
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Email Filter Catch: ${e.message}")
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Email Filter Error: ${error.message}")
            }
        }
    }

    private fun RLHandleApiResponse(cardData: EmailFilterInviteData) {
        val combinedList = mutableListOf<RLFindOnRevoolaInviteItem>()
        contactsList = contactsList.distinctBy { it.phoneNumber }.toMutableList()
        // Create a new list where `isInvite` is set to true if the email is in `emailList`
         contactsList.forEach{ contact ->
            if (contact.phoneNumber in cardData.emailsToInvite){
                combinedList.add(RLFindOnRevoolaInviteItem.RLInvite(contact))
            }
        }
        cardData.uidsToInvite.forEach { user ->
            combinedList.add(RLFindOnRevoolaInviteItem.RLFollow(user))
        }
        val myAdapter = RLContactsAdapter(activity,combinedList) { selectedItem ->
            // Handle the item click here
            when (selectedItem) {
                is RLFindOnRevoolaInviteItem.RLFollow -> {
                    // Handle the UserInvite item (EmailFilterUserInvite)
                    val followUser = selectedItem.user
                    RLTools.RlLogDPrint(TAG,"Selected Follow: ${followUser.username}")
                }
                is RLFindOnRevoolaInviteItem.RLInvite -> {
                    // Handle the ContactInvite item (RLContactModel)
                    val inviteContact = selectedItem.contact
                    RLTools.RlLogDPrint(TAG,"Selected Invite: ${inviteContact.name}")
                }

            }
        }


        // Set up RecyclerView with fetched email contacts
        fragBinding.listSyncContacts.layoutManager = LinearLayoutManager(requireContext())
        fragBinding.listSyncContacts.adapter = myAdapter

        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                myAdapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}