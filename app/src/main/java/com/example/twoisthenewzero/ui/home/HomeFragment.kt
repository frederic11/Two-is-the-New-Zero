package com.example.twoisthenewzero.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deepakkumardk.kontactpickerlib.KontactPicker
import com.deepakkumardk.kontactpickerlib.model.*
import com.deepakkumardk.kontactpickerlib.util.log
import com.example.twoisthenewzero.R
import com.example.twoisthenewzero.databinding.FragmentHomeBinding
import com.example.twoisthenewzero.helper.ContactsService
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val selectPhoneNumber = 500
    private val writeContactPermissionCode = 550
    private var isRevertFormat = false

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        _binding!!.homeExtendedFab.setOnClickListener {
            when (_binding!!.selectContactsRadioGroup.checkedRadioButtonId) {
                _binding!!.chooseSpecificContactRadioButton.id -> {
                    isRevertFormat = false
                    startContactPickerActivity()
                }
                _binding!!.selectAllContactRadioButton.id -> {
                    isRevertFormat = false
                    pickAllContacts()
                }
                _binding!!.chooseSpecificContactToRevertRadioButton.id -> {
                    isRevertFormat = true
                    startContactPickerActivity()
                }
                _binding!!.selectAllContactToRevertRadioButton.id -> {
                    isRevertFormat = true
                    pickAllContacts()
                }
                else -> {
                    view?.let { it1 ->
                        Snackbar.make(
                            it1, "Please Select an Option",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_CONTACTS
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_CONTACTS), writeContactPermissionCode
            )
        }

        return binding.root
    }

    fun startContactPickerActivity() {
        val item = KontactPickerItem().apply {
            imageMode = ImageMode.TextMode                      //Default is None
            selectionTickView = SelectionTickView.SmallView     //Default is SmallView
            selectionMode =
                SelectionMode.Multiple               //Default is SelectionMode.Multiple
        }
        KontactPicker().startPickerForResult(this, item, selectPhoneNumber)  //RequestCode
    }

    fun pickAllContacts() {
        KontactPicker.getAllKontactsWithUri(this.activity) {
            //Handle the contactList : MutableList<MyContacts>
            // Handle this list
            if (it.any()) {
                var myContext = context;
                Toast.makeText(
                    myContext,
                    "You Selected: " + it.size + " Contacts",
                    Toast.LENGTH_SHORT
                ).show()

                var contactIds = listOf<String>()
                for (contact in it) {
                    val contactIdImmutable = contact.contactId
                    if (contactIdImmutable != null) {
                        contactIds += contactIdImmutable
                    }
                }

                if (myContext != null) {
                    var contactsService = ContactsService(myContext)
                    log(
                        "This is the log: myContactId => " + contactsService.getRawContactIdByContactId(
                            contactIds
                        ).toString()
                    )
                }
            } else {
                Toast.makeText(context, "No Contacts were Selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == selectPhoneNumber) {
            val list = KontactPicker.getSelectedKontacts(data)  //ArrayList<MyContacts>

            if (list != null && list.size > 0) {
                val action = HomeFragmentDirections.actionNavHomeToConfirmationFragment(
                    list.toTypedArray(),
                    isRevertFormat
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            writeContactPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((context?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.WRITE_CONTACTS
                            )
                        } ==
                                PackageManager.PERMISSION_GRANTED)) {
                        //Do Something
                        _binding!!.homeExtendedFab.isEnabled = true
                    }
                } else {
                    //Do Something else
                    _binding!!.homeExtendedFab.isEnabled = false
                }
                return
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}