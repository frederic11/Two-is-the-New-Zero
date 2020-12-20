package com.example.twoisthenewzero.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.deepakkumardk.kontactpickerlib.util.log
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
            val queryResultCount: Int = cursor.getCount()
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
    fun updateContactPhoneByName(
        contactId: String,
        phoneNumber: String
    ): Int {
        var ret = 0
        val contentResolver: ContentResolver = context.contentResolver

        // Get raw contact id by display name.
        //val rawContactId = getRawContactIdByContactId(contactId, phoneNumber)

        val rawContactId = Long.MIN_VALUE

        // Update data table phone number use contact raw contact id.
        ret = if (rawContactId > -1) {
            // Update mobile phone number.
            updatePhoneNumber(
                contentResolver,
                rawContactId,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                "66666666666666"
            )

            // Update work mobile phone number.
            updatePhoneNumber(
                contentResolver,
                rawContactId,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE,
                "8888888888888888"
            )

            // Update home phone number.
            updatePhoneNumber(
                contentResolver,
                rawContactId,
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
                "99999999999999999"
            )
            1
        } else {
            0
        }
        return ret
    }


    /* Update phone number with raw contact id and phone type.*/
    private fun updatePhoneNumber(
        contentResolver: ContentResolver,
        rawContactId: Long,
        phoneType: Int,
        newPhoneNumber: String
    ) {
        // Create content values object.
        val contentValues = ContentValues()

        // Put new phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)

        // Create query condition, query with the raw contact id.
        val whereClauseBuf = StringBuffer()

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID)
        whereClauseBuf.append("=")
        whereClauseBuf.append(rawContactId)

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ")
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE)
        whereClauseBuf.append(" = '")
        val mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        whereClauseBuf.append(mimetype)
        whereClauseBuf.append("'")

        // Specify phone type.
        whereClauseBuf.append(" and ")
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE)
        whereClauseBuf.append(" = ")
        whereClauseBuf.append(phoneType)

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        val dataUri = ContactsContract.Data.CONTENT_URI

        // Get update data count.
        val updateCount =
            contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null)
    }
}