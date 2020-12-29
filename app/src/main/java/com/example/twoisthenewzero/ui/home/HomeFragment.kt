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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private val selectPhoneNumber = 500
    private val writeContactPermissionCode = 550
    private var isRevertFormat = false
    lateinit var mAdView : AdView

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        MobileAds.initialize(requireContext()) {}

        mAdView = _binding!!.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

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
                            it1, getString(R.string.please_select_option),
                            Snackbar.LENGTH_SHORT
                        ).show()
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
            PackageManager.PERMISSION_GRANTED
            || context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS),
                writeContactPermissionCode
            )
        }

        return binding.root
    }

    private fun startContactPickerActivity() {
        val item = KontactPickerItem().apply {
            imageMode = ImageMode.UserImageMode                      //Default is None
            selectionTickView = SelectionTickView.SmallView     //Default is SmallView
            selectionMode =
                SelectionMode.Multiple               //Default is SelectionMode.Multiple
            themeResId = R.style.Theme_TwoIsTheNewZero
        }
        KontactPicker().startPickerForResult(this, item, selectPhoneNumber)  //RequestCode
    }

    private fun pickAllContacts() {
        KontactPicker.getAllKontactsWithUri(this.activity) {
            //Handle the contactList : MutableList<MyContacts>
            // Handle this list
            if (it.any()) {
                navigateToConfirmationFragment(it as ArrayList<MyContacts>)
            } else {
                Toast.makeText(context, getString(R.string.no_contacts_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == selectPhoneNumber) {
            val list = KontactPicker.getSelectedKontacts(data)  //ArrayList<MyContacts>

            if (list != null && list.size > 0) {
                navigateToConfirmationFragment(list)
            }
        }
    }

    private fun navigateToConfirmationFragment(list: ArrayList<MyContacts>) {
        val action = HomeFragmentDirections.actionNavHomeToConfirmationFragment(
            list.toTypedArray(),
            isRevertFormat
        )
        findNavController().navigate(action)
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