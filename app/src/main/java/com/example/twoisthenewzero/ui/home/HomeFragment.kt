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
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deepakkumardk.kontactpickerlib.KontactPicker
import com.deepakkumardk.kontactpickerlib.model.ImageMode
import com.deepakkumardk.kontactpickerlib.model.KontactPickerItem
import com.deepakkumardk.kontactpickerlib.model.SelectionMode
import com.deepakkumardk.kontactpickerlib.model.SelectionTickView
import com.deepakkumardk.kontactpickerlib.util.log
import com.example.twoisthenewzero.R
import com.example.twoisthenewzero.helper.ContactsService


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val selectPhoneNumber = 500
    private val writeContactPermissionCode = 550
    private lateinit var selectContactsRadioGroup: RadioGroup
    private lateinit var chooseSpecificContactRadioButton: RadioButton
    private lateinit var selectAllContactRadioButton: RadioButton
    private lateinit var chooseSpecificContactToRevertRadioButton: RadioButton
    private lateinit var selectAllContactToRevertRadioButton: RadioButton
    private lateinit var nextMenuItem: MenuItem
    private var isRevertFormat = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        selectContactsRadioGroup = root.findViewById(R.id.selectContactsRadioGroup)
        chooseSpecificContactRadioButton = root.findViewById(R.id.chooseSpecificContactRadioButton)
        selectAllContactRadioButton = root.findViewById(R.id.selectAllContactRadioButton)
        chooseSpecificContactToRevertRadioButton = root.findViewById(R.id.chooseSpecificContactToRevertRadioButton)
        selectAllContactToRevertRadioButton = root.findViewById(R.id.selectAllContactToRevertRadioButton)


        var myContext = context
        if(myContext != null){
            if (ContextCompat.checkSelfPermission(myContext,
                    Manifest.permission.WRITE_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_CONTACTS), writeContactPermissionCode)
            }
        }
        return root
    }

    fun startContactPickerActivity(){
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        nextMenuItem = menu.findItem(R.id.next_item);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == selectPhoneNumber) {
            val list = KontactPicker.getSelectedKontacts(data)  //ArrayList<MyContacts>
            // Handle this list
            if (list != null && list.any()) {
                var myContext = context;
                Toast.makeText(
                    myContext,
                    "You Selected: " + list.size + " Contacts",
                    Toast.LENGTH_SHORT
                ).show()

                var contactIds = listOf<String>()
                for (contact in list) {
                    val contactIdImmutable = contact.contactId
                    if (contactIdImmutable != null) {
                        contactIds += contactIdImmutable
                    }
                }

                if (myContext != null) {
                    var contactsService = ContactsService(myContext)
                    var contactInfo = contactsService.getRawContactIdByContactId(
                        contactIds
                    )
                    log(
                        "This is the log: myContactId => $contactInfo"
                    )
                    contactsService.updateContactPhoneById(contactInfo, isRevertFormat)
                }
            } else {
                Toast.makeText(context, "No Contacts were Selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            writeContactPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((context?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.WRITE_CONTACTS)
                        } ==
                                PackageManager.PERMISSION_GRANTED)) {
                        //Do Something
                        nextMenuItem.isEnabled = true
                        nextMenuItem.title = "NEXT"
                    }
                } else {
                    //Do Something else
                    nextMenuItem.isEnabled = false
                    nextMenuItem.title = "Please Grant Contact Access"
                }
                return
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.next_item -> {
                when (selectContactsRadioGroup.checkedRadioButtonId) {
                    chooseSpecificContactRadioButton.id -> {
                        isRevertFormat = false
                        startContactPickerActivity()
                    }
                    selectAllContactRadioButton.id -> {
                        isRevertFormat = false
                        pickAllContacts()
                    }
                    chooseSpecificContactToRevertRadioButton.id -> {
                        isRevertFormat = true
                        startContactPickerActivity()
                    }
                    selectAllContactToRevertRadioButton.id -> {
                        isRevertFormat = true
                        pickAllContacts()
                    }
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}