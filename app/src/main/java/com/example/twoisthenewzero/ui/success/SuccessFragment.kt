package com.example.twoisthenewzero.ui.success

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.twoisthenewzero.databinding.FragmentSuccessBinding


class SuccessFragment : Fragment() {
    private var _binding: FragmentSuccessBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)

        _binding!!.successExtendedFab.setOnClickListener {
            startContactsIntent()
        }

        return binding.root
    }

    private fun startContactsIntent() {
        val i = Intent()
        i.action = Intent.ACTION_VIEW
        i.data = Uri.parse("content://contacts/people/")
        startActivity(i)
    }
}