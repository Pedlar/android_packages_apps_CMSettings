package com.cyanogenmod.settings.lists;

import java.util.ArrayList;

public abstract class MasterLists {
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_SWITCH = 2;

    public static class List {
        public int titleResId;
        public int summaryResId;
        public String intent;
        public int type; /* 0 = Category Header, 1 = Normal, 2 = Switch */

        public List(int arg1, int arg2, String arg3, int arg4) {
            titleResId = arg1;
            summaryResId = arg2;
            intent = arg3;
            type = arg4;
        }
    }

    public abstract ArrayList<List> getList();    
}
