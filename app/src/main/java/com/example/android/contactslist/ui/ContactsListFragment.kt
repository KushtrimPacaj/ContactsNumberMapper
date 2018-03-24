/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.contactslist.ui

import android.content.ContentProviderOperation
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.contactslist.ContactsQuery
import com.example.android.contactslist.KContact
import com.example.android.contactslist.R
import java.util.*


/**
 * Fragments require an empty constructor.
 */
class ContactsListFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =// Inflate the list fragment layout
            inflater!!.inflate(R.layout.contact_list_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loaderManager.initLoader(ContactsQuery.QUERY_ID, Bundle.EMPTY, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor>? {

        // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            val contentUri = ContactsQuery.CONTENT_URI

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return CursorLoader(activity,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION, null,
                    ContactsQuery.SORT_ORDER)
        }

        Log.e("ContactsListFragment", "onCreateLoader - incorrect ID provided ($id)")
        return null
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        // This swaps the new cursor into the adapter.
        if (loader.id == ContactsQuery.QUERY_ID) {
            val contactArrayList = parseContacts(data)

            loaderManager.destroyLoader(ContactsQuery.QUERY_ID)

            makeMagic(contactArrayList)
        }

    }

    private fun makeMagic(contactArrayList: ArrayList<KContact>) {
        for (kContact in contactArrayList) {

            for (phoneNumber in kContact.phoneNumbers) {

                val monacoCode = "+377"
                val sloveniaCode = "+386"
                val kosovoCode = "+383"
                modifyCountryCode(phoneNumber, kContact, oldCode = monacoCode, newCode = kosovoCode)
                modifyCountryCode(phoneNumber, kContact, oldCode = sloveniaCode, newCode = kosovoCode)
            }
        }
    }

    private fun modifyCountryCode(phoneNumber: String, kContact: KContact, oldCode: String, newCode: String) {
        if (phoneNumber.contains(oldCode)) {

            val newPhoneNumber = phoneNumber.replace(oldCode, newCode)
            if (kContact.phoneNumbers.contains(newPhoneNumber)) {
                Log.d("Contacts", kContact.displayName + " , " + phoneNumber + " --> already modified " + newPhoneNumber)
            } else {
                Log.d("Contacts", kContact.displayName + " , " + phoneNumber + " --> " + newPhoneNumber)

                addNumber(kContact.rawId, newPhoneNumber);
            }
        } else {
            Log.d("Contacts", kContact.displayName + " , " + phoneNumber + " --> not modifying")
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) = Unit


    /**
     * This interface defines constants for the Cursor and CursorLoader, based on constants defined
     * in the [android.provider.ContactsContract.Contacts] class.
     */

    private fun parseContacts(data: Cursor): ArrayList<KContact> {

        val contactArrayList = ArrayList<KContact>()

        while (data.moveToNext()) {
            val contactId = data.getInt(ContactsQuery.ID)
            contactArrayList.add(KContact(
                    contactId,
                    getRawContactId(contactId),
                    data.getString(ContactsQuery.DISPLAY_NAME),
                    getPhoneNumbersOfContact(contactId),
                    data.getString(ContactsQuery.LOOKUP_KEY)
            )
            )
        }
        return contactArrayList

    }

    private fun getPhoneNumbersOfContact(contactId: Int): ArrayList<String> {
        val cr = activity.contentResolver

        val phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)

        val phoneNumbers = ArrayList<String>()
        if (phones != null) {
            while (phones.moveToNext()) {
                var number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                number = number.replace("\\s+".toRegex(), "")
                if (!phoneNumbers.contains(number)) {
                    phoneNumbers.add(number)
                }

            }
        }
        phones?.close()
        return phoneNumbers
    }


    fun addNumber(rawContactId: Int, number: String) {
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(Contacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build())
        try {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRawContactId(contactId: Int): Int {
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = ContactsContract.RawContacts.CONTACT_ID + "=?"
        val selectionArgs = arrayOf(contactId.toString())
        val c = activity.contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null)

        c!!.moveToFirst()
        val rawId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID))
        c.close()
        return rawId
    }

    fun openContact(kContact: KContact) {

        // Creates a contact lookup Uri from contact ID and lookup_key
        val mContactUri = Contacts.getLookupUri(kContact.id.toLong(), kContact.lookupKey)
        val intent = Intent(Intent.ACTION_EDIT, mContactUri)
        startActivity(intent)

    }

}
