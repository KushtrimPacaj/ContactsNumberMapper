package com.example.android.contactslist;

import java.util.ArrayList;

/**
 * Created by kushtrimpacaj on 11/4/17.
 */

public class KContact {

    private int id;
    private int rawId;

    private String displayName;

    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private String lookupKey;

    public KContact(int id, int rawId, String displayName, ArrayList<String> phoneNumbers, String lookupKey) {
        this.id = id;
        this.rawId = rawId;
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;
        this.lookupKey = lookupKey;
    }

    public int getId() {
        return id;
    }

    public int getRawId() {
        return rawId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getLookupKey() {
        return lookupKey;
    }
}
