package com.example.twoisthenewzero.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
    private val SelectPhoneNumber = 500

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val button: Button = root.findViewById(R.id.button)
        val buttonTransformAll: Button = root.findViewById(R.id.button2)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
            button.text = it
        })

        button.setOnClickListener {
            val item = KontactPickerItem().apply {
                imageMode = ImageMode.TextMode                      //Default is None
                selectionTickView = SelectionTickView.SmallView     //Default is SmallView
                selectionMode =
                    SelectionMode.Multiple               //Default is SelectionMode.Multiple
            }
            KontactPicker().startPickerForResult(this, item, SelectPhoneNumber)  //RequestCode
        }

        buttonTransformAll.setOnClickListener {
            KontactPicker.getAllKontactsWithUri(this.activity) {
                //Handle the contactList : MutableList<MyContacts>
                // Handle this list
                if (it != null && it.any()) {
                    var myContext = context;
                    Toast.makeText(
                        myContext,
                        "You Selected: " + it.size + " Contacts",
                        Toast.LENGTH_SHORT
                    ).show()

                    var contactIds = listOf<String>()
                    for(contact in it){
                        val contactIdImmutable = contact.contactId
                        if(contactIdImmutable != null){
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

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SelectPhoneNumber) {
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
                for(contact in list){
                    val contactIdImmutable = contact.contactId
                    if(contactIdImmutable != null){
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
}