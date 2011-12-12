package com.cyanogenmod.settings.lists;
public class InterfaceList extends zList {
    public static final String[][] mList = {
        { "Expanded Widget", "Notification Bar Widget", "com.cyanogenmod.settings.activities.PowerWidget", "", "2" },
    };

    @Override
    public String[][] getList() {

        return mList;
    }
}
