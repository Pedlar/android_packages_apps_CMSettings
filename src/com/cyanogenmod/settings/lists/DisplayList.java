/*
* Copyright (C) 2011 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.cyanogenmod.settings.lists;
import com.cyanogenmod.settings.R;

import java.util.ArrayList;
public class DisplayList extends MasterLists {
    public static ArrayList<MasterLists.List> mList = null;

    public DisplayList() {
        //mList.put(new MasterLists.List(ResID, ResID, Intent, Type));
        mList = new ArrayList<MasterLists.List>();
        mList.add(new MasterLists.List(R.string.backlight_title, R.string.backlight_summary, "com.cyanogenmod.settings.activities.Backlight", TYPE_NORMAL));
    }

    public ArrayList<MasterLists.List> getList() {
        return mList;
    }

}

