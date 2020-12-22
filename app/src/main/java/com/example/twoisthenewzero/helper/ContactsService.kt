package com.example.twoisthenewzero.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.deepakkumardk.kontactpickerlib.util.log
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


class ContactsService(
    private val context: Context
) {

    fun getRawContactIdByContactId(
        contactIds: List<String>
    ): HashMap<Long, String> {
        val contentResolver: ContentResolver = context.contentResolver

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        var conditionalInStatement = StringBuffer()
        for (contactId in contactIds) {
            conditionalInStatement.append("'$contactId',")
        }

        // Create where condition clause.
        val whereClause =
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " IN (" + conditionalInStatement.removeSuffix(",") + ")"
        //"and " + ContactsContract.CommonDataKinds.Phone.DATA + " = '" + phoneNumber + "'"

        // Query raw contact id through RawContacts uri.
        val rawContactUri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        // Return the query cursor.
        val cursor: Cursor? =
            contentResolver.query(rawContactUri, null, whereClause, null, null)

        var contactInfo: HashMap<Long, String> = hashMapOf<Long, String>()
        if (cursor != null) {
            // Get contact count that has same contactId, generally it should be one.
            val queryResultCount: Int = cursor.count
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst()

                // Get raw_contact_id.

                if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)) != null)
                contactInfo[cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))] =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))

                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)) != null)
                    contactInfo[cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))] =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))

                }
            }
        }
        return contactInfo
    }


    /*
     * Update contact phone number by contactId.
     * Return update contact number, commonly there should has one contact be updated.
     */
    fun updateContactPhoneById(
        contactInfo: HashMap<Long, String>
    ): Boolean {
        var ret = 0
        val contentResolver: ContentResolver = context.contentResolver

        // Get raw contact id by display name.
        //val rawContactId = getRawContactIdByContactId(contactId, phoneNumber)

        for(contact in contactInfo){
            return try{
                updatePhoneNumber(contentResolver, contact.key, contact.value)
                true
            } catch (exception: Exception){
                log(exception.toString())
                false
            }
        }
        return false
    }

    /* Update phone number with raw contact id and phone type.*/
    private fun updatePhoneNumber(
        contentResolver: ContentResolver,
        rawContactId: Long,
        newPhoneNumber: String
    ) {
        // Create content values object.
        val contentValues = ContentValues()

        // Put new phone number value.
        //contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
        contentValues.put(ContactsContract.Data.DATA1, "+96176888888")
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

        // Create query condition, query with the raw contact id.
        val whereClauseBuf = StringBuffer()

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data._ID)
        whereClauseBuf.append(" = ")
        whereClauseBuf.append(rawContactId)

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        val dataUri = ContactsContract.Data.CONTENT_URI

        // Get update data count.
        val updateCount =
            contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null)

        log("Update Count: $updateCount")
    }
}