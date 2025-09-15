package com.revoola.fragment.friends

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.*
import com.revoola.enumclass.FriendsAPIStatusType
import com.revoola.fragment.friends.adapter.RLContactsAdapter
import com.revoola.fragment.friends.model.EmailFilterInviteData
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFindOnRevoolaInviteItem
import com.revoola.fragment.friends.model.RLSyncContactFilterModel
import com.revoola.model.RLContactModel
import com.revoola.model.RLuserData
import com.revoola.moengage.RELMoengageManager
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragFindOnRevoola : RLBaseFragment() {
    private val TAG: String = RLFragFindOnRevoola::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private val CONTACTS_PERMISSION_CODE = 1
    private var contactsList = mutableListOf<RLContactModel>()
    private val emailList = mutableListOf<String>()
    private var getFullName: String =""
    private val currentUser by lazy {
        RLAuthManager().rl_getCurrentUser()?.uid?:""
    }
    private val fragBinding by lazy {
        RlFragFingOnRevoolaBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFindOnRevoola" )
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.toolbar.tvTitle.setText(R.string.searchfriends)
        fragBinding.toolbar.ivBack.setOnClickListener { rl_closeFragment()}
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_info)
        RLTools.RLhideShowHelpDialog(requireContext(), "find_on_revoola",  fragBinding.toolbar.ivNotification)

        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                getFullName = userData.firstName + " " + userData.lastName
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }
        fragBinding.txtSyncContact.setOnClickListener {
            rl_checkContactPermission()
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
                    contactsList.add(RLContactModel(id,contactName , email,"0"))
                }
            }
        }
        //Api Call
        rl_emailFilterApiCall()

    }

    private fun rl_checkContactPermission(){
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
        } else {
            // Permission is already granted, so fetch the contacts
            fetchEmailContacts()
        }
    }

    private fun rl_emailFilterApiCall() {
        if (isAdded){
            RLBaseProgress.rl_showProgressDialog(requireActivity())
        }
        val request = listOf(RLEmailFilterRequestModel(syncContactNew = RLSyncContactFilterModel(currentUser = currentUser,email=emailList.toSet().toMutableList())))
        RLTools.rl_logDPrint(TAG,"Email Filter Request: $request")
        viewModel.rl_findOnRevoolaEmailFilter(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Email Filter Success: ${response.type}")
                        rl_handleApiResponse(response.text)
                    }else {
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logDPrint(TAG,"Email Filter Fail: ${response.type}")
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logDPrint(TAG,"Email Filter Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logDPrint(TAG,"Email Filter Error: ${error.message}")
            }
        }
    }

    private fun rl_handleApiResponse(cardData: EmailFilterInviteData) {
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

        val myAdapter = RLContactsAdapter(requireActivity(),combinedList) { selectedItem,friendsAPIStatusType ->
            // Handle the item click here
            when (selectedItem) {
                is RLFindOnRevoolaInviteItem.RLFollow -> {
                    // Handle the UserInvite item (EmailFilterUserInvite)
                    val followUserData = selectedItem.user
                    RLTools.rl_logDPrint(TAG,"Selected Follow: ${Gson().toJson(followUserData)}")
                   val userData= RLuserData(
                       userid = followUserData.userId,
                       theirid = followUserData.userId,
                       myidstatus = friendsAPIStatusType.value,
                       theiridstatus = followUserData.theirIdStatus?:FriendsAPIStatusType.Invite.value,
                       first_name = followUserData.firstName,
                       last_name = followUserData.lastName,
                       username = followUserData.username,
                       avatar = followUserData.avatar,
                       myid = currentUser,
                       isSelected = false
                   )
                    updateStatusForUser(userData, friendsAPIStatusType)
                }
                is RLFindOnRevoolaInviteItem.RLInvite -> {
                    // Handle the ContactInvite item (RLContactModel)
                    val inviteContactData = selectedItem.contact
                    RLTools.rl_logDPrint(TAG,"Selected Invite: ${Gson().toJson(inviteContactData)}")
                    val userData= RLuserData(
                        userid = inviteContactData.id,
                        theirid = inviteContactData.id,
                        myidstatus = FriendsAPIStatusType.Invited.value,
                        theiridstatus = FriendsAPIStatusType.Invite.value,
                        first_name = inviteContactData.name,
                        last_name = "",
                        username = inviteContactData.name,
                        avatar = "",
                        myid = currentUser,
                        isSelected = false
                    )
                 updateStatusForUser(userData, friendsAPIStatusType)
                }
            }
        }
        // Set up RecyclerView with fetched email contacts
        fragBinding.listSyncContacts.layoutManager = LinearLayoutManager(requireContext())
        fragBinding.listSyncContacts.adapter = myAdapter
        RLBaseProgress.rl_hideProgressDialog()

        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                myAdapter.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateStatusForUser(user: RLuserData, status: FriendsAPIStatusType) {
        val params = mutableListOf<Map<String, Any>>()
        val user_userid = when (status) {
            FriendsAPIStatusType.Accepted, FriendsAPIStatusType.Blocked -> {
                user.theirid
            }else -> {
                user.userid
            }
        }

        val myUser = mapOf(
            "myidstatus" to if (status == FriendsAPIStatusType.Accepted) status.value else user.myidstatus,
            "contact_userid" to user_userid,
            "contact_status" to if (status == FriendsAPIStatusType.Accepted) user.theiridstatus else status.value
        )

        val otherUser = mapOf(
            "myidstatus" to if (status == FriendsAPIStatusType.Accepted) user.theiridstatus else status.value,
            "contact_userid" to RLAuthManager().rl_getCurrentUser()?.uid,
            "contact_status" to if (status == FriendsAPIStatusType.Accepted) status.value else user.myidstatus
        )

        val mySearchUser = mapOf(
            "myid" to RLAuthManager().rl_getCurrentUser()?.uid,
            "contact_data" to listOf(myUser)
        )

        val otherSearchUser = mapOf(
            "myid" to user_userid,
            "contact_data" to listOf(otherUser)
        )

        params.add(mapOf("users_contacts_mk2" to mySearchUser))
        params.add(mapOf("users_contacts_mk2" to otherSearchUser))

        RLTools.rl_logDPrint(TAG,"Insert Friends Request: ${Gson().toJson(params)}")
        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                RLBaseProgress.rl_hideProgressDialog()
                try {
                    RLTools.rl_logDPrint(TAG,"Insert Friends Success: ${Gson().toJson(response) }")
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Insert Friends Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logDPrint(TAG,"Insert Friends Error: ${error.message}")
            }
        }
        when (status) {
            FriendsAPIStatusType.Follow, FriendsAPIStatusType.Invite, FriendsAPIStatusType.Blocked -> {
                deleteDataForFriends(listOf(user_userid))
            }
            FriendsAPIStatusType.Invited, FriendsAPIStatusType.Requested -> {
                RELMoengageManager.sendRequest(user_userid,getFullName)
            }
            FriendsAPIStatusType.Accepted -> {
                saveDataForFriends(listOf(user_userid))
                RELMoengageManager.acceptRequest(user_userid,requireContext(),getFullName)
            }
        }
    }
    private fun saveDataForFriends(members: List<String>) {
        val currentUserId = RLAuthManager().rl_getCurrentUser()?.uid?:""

        for (member in members) {
            val user = mapOf(
                "userid" to currentUserId,
                "is_admin" to 0
            )
            val groupId = mapOf(
                "users" to listOf(user),
                "is_friends_group" to true,
                "group_id" to "${member}_friends"
            )
            val params = listOf(mapOf("group_users" to groupId))

            RLTools.rl_logDPrint(TAG,"save Data Friends Request: ${Gson().toJson(params)}")
            viewModel.rl_updateFriendsData(params) { result ->
                result.onSuccess { response ->
                    RLBaseProgress.rl_hideProgressDialog()
                    try {
                        RLTools.rl_logDPrint(TAG,"save Data  Friends Success: ${Gson().toJson(response) }")
                    }catch (e:Exception){
                        e.printStackTrace()
                        RLTools.rl_logDPrint(TAG,"save Data  Friends Catch: ${e.message}")
                    }
                }.onFailure { error ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logDPrint(TAG,"save Data  Friends Error: ${error.message}")
                }
            }
        }
    }
    private fun deleteDataForFriends(members: List<String>) {
        val currentUserId = RLAuthManager().rl_getCurrentUser()?.uid?:""
        val groupId = mapOf(
            "userid" to members,
            "groupid" to "${currentUserId}_friends"
        )
        val params = listOf(mapOf("delete" to groupId))
        RLTools.rl_logDPrint(TAG,"delete Data Friends Request: ${Gson().toJson(params)}")
        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                RLBaseProgress.rl_hideProgressDialog()
                try {
                    RLTools.rl_logDPrint(TAG,"delete Data  Friends Success: ${Gson().toJson(response) }")
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"delete Data  Friends Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logDPrint(TAG,"delete Data  Friends Error: ${error.message}")
            }
        }
    }
}