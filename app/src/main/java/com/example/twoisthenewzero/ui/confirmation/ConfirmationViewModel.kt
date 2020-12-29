package com.example.twoisthenewzero.ui.confirmation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.deepakkumardk.kontactpickerlib.model.MyContacts
import com.deepakkumardk.kontactpickerlib.util.log
import com.example.twoisthenewzero.helper.ContactsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmationViewModel(
    private val listOfContacts: Array<MyContacts>,
    isRevertFormat: Boolean,
    private val application: Application
) : ViewModel() {
    var mListOfSelectedContacts = listOfContacts
    var mIsRevertFormat = isRevertFormat

    private var _isWorkDone: MutableLiveData<Boolean> = MutableLiveData()
    val isWorkDone: LiveData<Boolean>
        get() = _isWorkDone

    private var _isError: MutableLiveData<Boolean> = MutableLiveData()
    val isError: LiveData<Boolean>
        get() = _isError

    init {
        Log.i("GameViewModel", "GameViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }

    fun migrateContacts() {
        viewModelScope.launch {
            // Coroutine that will be canceled when the ViewModel is cleared.
            migrateContactsAsync()
        }
    }

    private suspend fun migrateContactsAsync() = withContext(Dispatchers.Default) {
        // Handle this list
        _isWorkDone.postValue(false)

        //This is to avoid flashing effects in the UI when showing the migration animation
        Thread.sleep(3_000)

        if (listOfContacts.any()) {
            try {
                val contactIds = mutableListOf<String>()
                for (contact in listOfContacts) {
                    val contactIdImmutable = contact.contactId
                    if (contactIdImmutable != null) {
                        contactIds += contactIdImmutable
                    }
                }

                val contactsService = ContactsService(application)
                val contactInfo = contactsService.getRawContactIdByContactId(
                    contactIds
                )
                contactsService.updateContactPhoneById(contactInfo, mIsRevertFormat)
                _isWorkDone.postValue(true)
                _isError.postValue(false)
            } catch (ex: Exception) {
                _isWorkDone.postValue(false)
                _isError.postValue(true)
            }
        }
    }

}