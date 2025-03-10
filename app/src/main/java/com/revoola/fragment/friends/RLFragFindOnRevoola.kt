package com.revoola.fragment.friends

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
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
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databinding.*
import com.revoola.fragment.friends.adapter.RLContactsAdapter
import com.revoola.fragment.friends.model.EmailFilterInviteData
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFindOnRevoolaInviteItem
import com.revoola.fragment.friends.model.RLFriendsInsertApiPayload
import com.revoola.fragment.friends.model.RLInsertContactData
import com.revoola.fragment.friends.model.RLSyncContactFilterModel
import com.revoola.fragment.friends.model.RLUsersContactsMk2
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
       // RLCheckContactPermission()
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

    // Get all contacts with email addresses
    private fun fetchEmailContacts(){
        val contentResolver = requireContext().contentResolver
        // Define the columns we want to retrieve
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.ADDRESS
        )

        // Query the email table
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            // Get the column indices for the data we need
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

            // Loop through all email contacts
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val contactName = it.getString(nameIndex) ?: "No Name"
                val email = it.getString(emailIndex) ?: "No Email"

                if (!email.isEmpty() && !email.equals("No Email")){
                    emailList.add(email)
                    contactsList.add(RLContactModel(id,contactName , email))
                }
            }
        }
        //Api Call
        RLEmailFilterApiCall()

    }


    private fun RLCheckContactPermission(){
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
        } else {
            // Permission is already granted, so fetch the contacts
            fetchEmailContacts()
        }
    }

    private fun RLEmailFilterApiCall() {
        if (isAdded){
            RLBaseProgress.RLShowProgressDialog(requireActivity())
        }
        val request = listOf(RLEmailFilterRequestModel(syncContactNew = RLSyncContactFilterModel(currentUser = currentUser,email=emailList.toSet().toMutableList())))
        RLTools.RlLogDPrint(TAG,"Email Filter Request: $request")
        viewModel.RLFindOnRevoolaEmailFilter(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Email Filter Success: ${response.type}")
                        RLHandleApiResponse(response.text)
                    }else {
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogDPrint(TAG,"Email Filter Fail: ${response.type}")
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogDPrint(TAG,"Email Filter Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogDPrint(TAG,"Email Filter Error: ${error.message}")
            }
        }
    }

    private fun RLHandleApiResponse(cardData: EmailFilterInviteData) {
        val combinedList = mutableListOf<RLFindOnRevoolaInviteItem>()
        contactsList = contactsList.distinctBy { it.phoneNumber }.toMutableList()
        // Create a new list where `isInvite` is set to true if the email is in `emailList`
        cardData.uidsToInvite.forEach { user ->
            combinedList.add(RLFindOnRevoolaInviteItem.RLFollow(user))
        }
        contactsList.forEach{ contact ->
            if (contact.phoneNumber in cardData.emailsToInvite){
                combinedList.add(RLFindOnRevoolaInviteItem.RLInvite(contact))
            }
        }
        if (cardData.uidsToInvite.size>0){
            fragBinding.relaySynccontact.visibility=View.GONE
            fragBinding.ivTotalContactLay.visibility=View.VISIBLE
            fragBinding.ivTotalContactOnRevoola.setText("${cardData.uidsToInvite.size} ${ getString(R.string.contact_on_revoola) }")
        }else{
            fragBinding.relaySynccontact.visibility=View.VISIBLE
            fragBinding.ivTotalContactLay.visibility=View.GONE
        }

        val myAdapter = RLContactsAdapter(activity,combinedList) { selectedItem ->
            // Handle the item click here
            when (selectedItem) {
                is RLFindOnRevoolaInviteItem.RLFollow -> {
                    // Handle the UserInvite item (EmailFilterUserInvite)
                    val followUserData = selectedItem.user
                    RLTools.RlLogDPrint(TAG,"Selected Follow: ${followUserData.username}")
                    val contact_data= listOf(RLInsertContactData(
                        myidstatus = followUserData.myIdStatus,
                        contact_userid = followUserData.userId,
                        contact_email = followUserData.email,
                        contact_status = 2 ))
                    //RLInsertFriendsApiCall(contact_data)
                }
                is RLFindOnRevoolaInviteItem.RLInvite -> {
                    // Handle the ContactInvite item (RLContactModel)
                    val inviteContactData = selectedItem.contact
                    RLTools.RlLogDPrint(TAG,"Selected Invite: ${inviteContactData.name}")
                    val contact_data= listOf(RLInsertContactData(
                        myidstatus = "",
                        contact_userid = "",
                        contact_email = inviteContactData.phoneNumber,
                        contact_status = 1 ))
                   // RLInsertFriendsApiCall(contact_data)
                }

            }
        }

        // Set up RecyclerView with fetched email contacts
        fragBinding.listSyncContacts.layoutManager = LinearLayoutManager(requireContext())
        fragBinding.listSyncContacts.adapter = myAdapter
        RLBaseProgress.RLhideProgressDialog()
        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                myAdapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun RLInsertFriendsApiCall(contact_data: List<RLInsertContactData>) {
        //Contact Status:1- Invited , 2- Requested ,3- Accepted ,4- Blocked
        if (isAdded){
            RLBaseProgress.RLShowProgressDialog(requireActivity())
        }
        val request=  listOf(RLFriendsInsertApiPayload(
        users_contacts_mk2 = RLUsersContactsMk2(
            myid = currentUser,
            contact_data = contact_data)
        ))

        RLTools.RlLogDPrint(TAG,"Insert Friends Request: $request")
        viewModel.RLInsertFriendsData(request) { result ->
            result.onSuccess { response ->
                RLBaseProgress.RLhideProgressDialog()
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Insert Friends Success: ${response.type}")
                    }else {
                        RLTools.RlLogDPrint(TAG,"Insert Friends Fail: ${response.type}")
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Insert Friends Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogDPrint(TAG,"Insert Friends Error: ${error.message}")
            }
        }
    }
}