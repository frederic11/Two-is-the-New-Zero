package com.example.twoisthenewzero.ui.confirmation

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.deepakkumardk.kontactpickerlib.util.hide
import com.deepakkumardk.kontactpickerlib.util.log
import com.deepakkumardk.kontactpickerlib.util.show
import com.example.twoisthenewzero.R
import com.example.twoisthenewzero.databinding.ConfirmationFragmentBinding

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
            try{
                showLoadingUI()
                viewModel.migrateContacts()
            }
            catch (ex: Exception){
                hideLoadingUI()
                val action =
                    ConfirmationFragmentDirections.actionConfirmationFragmentToFailureFragment()
                findNavController().navigate(action)
            }
        }
        return return binding.root
    }

    fun showLoadingUI(){
        _binding!!.mainView.hide()
        _binding!!.successExtendedFab.hide()
        _binding!!.animationGroup.show()
    }

    fun hideLoadingUI(){
        _binding!!.mainView.show()
        _binding!!.successExtendedFab.show()
        _binding!!.animationGroup.hide()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModelFactory = ConfirmationViewModelFactory(args.listOfSelectedContacts, args.isRevertFormat, requireNotNull(this.activity).application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConfirmationViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.isWorkDone.observe(viewLifecycleOwner, Observer {
            if(it){

                val action =
                    ConfirmationFragmentDirections.actionConfirmationFragmentToSuccessFragment()
                findNavController().navigate(action)
            }
        })

        viewModel.isError.observe(viewLifecycleOwner, Observer {
            if(it){
                val action =
                    ConfirmationFragmentDirections.actionConfirmationFragmentToFailureFragment()
                findNavController().navigate(action)
            }
        })

        // Handle this list
        if (viewModel.mListOfSelectedContacts.any()) {
            _binding!!.numberOfSelectedContactsText.text = viewModel.mListOfSelectedContacts.size.toString()
            if (viewModel.mListOfSelectedContacts.size > 1) {
                _binding!!.contactsText.text = "Contacts"
            } else {
                _binding!!.contactsText.text = "Contact"
            }

            if (args.isRevertFormat){
                binding!!.infoText.text = getString(R.string.warning_description_to_old_format)
            } else {
                binding!!.infoText.text = getString(R.string.warning_description_to_new_format)
            }

        } else {
            Toast.makeText(context, "No Contacts were Selected", Toast.LENGTH_SHORT).show()
        }
    }


}