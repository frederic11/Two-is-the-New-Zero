package com.example.twoisthenewzero.ui.confirmation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deepakkumardk.kontactpickerlib.model.MyContacts

class ConfirmationViewModelFactory(
    private val listOfContacts: Array<MyContacts>,
    private val isRevertFormat: Boolean,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfirmationViewModel::class.java)) {
            return ConfirmationViewModel(listOfContacts,isRevertFormat, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
