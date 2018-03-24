package com.example.android.contactslist;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;

public interface ContactsQuery {

    // An identifier for the loader
    int QUERY_ID = 1;

    // A content URI for the Contacts table
    Uri CONTENT_URI = Contacts.CONTENT_URI;


    // The selection clause for the CursorLoader query. The search criteria defined here
    // restrict results to contacts that have a display name and are linked to visible groups.
    // Notice that the search on the string provided by the user is implemented by appending
    // the search string to CONTENT_FILTER_URI.
    @SuppressLint("InlinedApi")
    String SELECTION =
            (Contacts.DISPLAY_NAME_PRIMARY) +
                    "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

    // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
    // sort key allows for localization. In earlier versions. use the display name as the sort
    // key.
    @SuppressLint("InlinedApi")
    String SORT_ORDER =
            Contacts.SORT_KEY_PRIMARY;

    // The projection for the CursorLoader query. This is a list of columns that the Contacts
    // Provider should return in the Cursor.
    @SuppressLint("InlinedApi")
    String[] PROJECTION = {

            // The contact's row id
            Contacts._ID,

            // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
            // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
            // a "permanent" contact URI.
            Contacts.LOOKUP_KEY,

            Contacts.DISPLAY_NAME_PRIMARY,

            // In Android 3.0 and later, the thumbnail image is pointed to by
            // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
            // you generate the pointer from the contact's ID value and constants defined in
            // android.provider.ContactsContract.Contacts.
            Contacts.PHOTO_THUMBNAIL_URI,

            // The sort order column for the returned Cursor, used by the AlphabetIndexer
            SORT_ORDER,
    };

    // The query column numbers which map to each value in the projection
    int ID = 0;
    int LOOKUP_KEY = 1;
    int DISPLAY_NAME = 2;
}
