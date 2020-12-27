package com.example.twoisthenewzero.ui.confirmation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.deepakkumardk.kontactpickerlib.util.log
import com.example.twoisthenewzero.R
import com.example.twoisthenewzero.databinding.ConfirmationFragmentBinding
import com.example.twoisthenewzero.databinding.FragmentHomeBinding
import com.example.twoisthenewzero.helper.ContactsService
import com.example.twoisthenewzero.ui.home.HomeFragmentDirections
import java.util.concurrent.Executor

class ConfirmationFragment : Fragment() {

    companion object {
        fun newInstance() = ConfirmationFragment()
    }

    private lateinit var viewModel: ConfirmationViewModel
    val args: ConfirmationFragmentArgs by navArgs()

    private var _binding: ConfirmationFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmationFragmentBinding.inflate(inflater, container, false)
        _binding!!.successExtendedFab.setOnClickListener {
            migrateContacts()
        }
        return return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfirmationViewModel::class.java)
        // TODO: Use the ViewModel

        val listOfSelectedContacts = args.listOfSelectedContacts

        // Handle this list
        if (listOfSelectedContacts.any()) {
            _binding!!.numberOfSelectedContactsText.text = listOfSelectedContacts.size.toString()

            if (listOfSelectedContacts.size > 1) {
                _binding!!.contactsText.text = "Contacts"
            } else {
                _binding!!.contactsText.text = "Contact"
            }
        } else {
            Toast.makeText(context, "No Contacts were Selected", Toast.LENGTH_SHORT).show()
        }
        log("confirmation fragment contact List => $listOfSelectedContacts")
    }

    private fun migrateContacts() {
        val listOfSelectedContacts = args.listOfSelectedContacts
        val isRevertFormat = args.isRevertFormat

        // Handle this list
        if (listOfSelectedContacts.any()) {
            var myContext = context;

            val contactIds = mutableListOf<String>()
            for (contact in listOfSelectedContacts) {
                val contactIdImmutable = contact.contactId
                if (contactIdImmutable != null) {
                    contactIds += contactIdImmutable
                }
            }

            if (myContext != null) {
                val contactsService = ContactsService(myContext)
                val contactInfo = contactsService.getRawContactIdByContactId(
                    contactIds
                )
                log(
                    "This is the log: myContactId => $contactInfo"
                )
                contactsService.updateContactPhoneById(contactInfo, isRevertFormat)

                val action =
                    ConfirmationFragmentDirections.actionConfirmationFragmentToSuccessFragment()
                findNavController().navigate(action)
            }
        } else {
            Toast.makeText(context, "No Contacts were Selected", Toast.LENGTH_SHORT).show()
        }
    }
}