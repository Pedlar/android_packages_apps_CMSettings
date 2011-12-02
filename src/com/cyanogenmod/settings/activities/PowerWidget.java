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

package com.cyanogenmod.settings.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.internal.telephony.Phone;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.ListPreferenceMultiSelect;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cyanogenmod.settings.utils.PowerWidgetUtil;
import com.cyanogenmod.settings.widgets.TouchInterceptor;
import com.cyanogenmod.settings.activities.ColorPickerDialog;
import com.cyanogenmod.settings.R;

public class PowerWidget extends PreferenceFragment 
	implements OnPreferenceChangeListener {
    private static final String UI_EXP_WIDGET = "expanded_widget";

    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";

    private static final String UI_EXP_WIDGET_HIDE_INDICATOR = "expanded_hide_indicator";

    private static final String UI_EXP_WIDGET_HIDE_SCROLLBAR = "expanded_hide_scrollbar";

    private static final String UI_EXP_WIDGET_HAPTIC_FEEDBACK = "expanded_haptic_feedback";

    private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";

    private static final String UI_EXP_WIDGET_PICKER = "widget_picker";

    private static final String UI_EXP_WIDGET_ORDER = "widget_order";

    private CheckBoxPreference mPowerWidget;

    private CheckBoxPreference mPowerWidgetHideOnChange;

    private CheckBoxPreference mPowerWidgetIndicatorHide;

    private CheckBoxPreference mPowerWidgetHideScrollBar;

    private ListPreference mPowerWidgetHapticFeedback;

    private Preference mPowerWidgetColor;

    private PreferenceScreen mPowerPicker;

    private PreferenceScreen mPowerOrder;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ui_power_widget_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Expanded View Power Widget */
        mPowerWidget = (CheckBoxPreference) prefSet.findPreference(UI_EXP_WIDGET);
        mPowerWidgetHideOnChange = (CheckBoxPreference) prefSet
                .findPreference(UI_EXP_WIDGET_HIDE_ONCHANGE);
        mPowerWidgetHideScrollBar = (CheckBoxPreference) prefSet
                .findPreference(UI_EXP_WIDGET_HIDE_SCROLLBAR);
        mPowerWidgetIndicatorHide = (CheckBoxPreference) prefSet
                .findPreference(UI_EXP_WIDGET_HIDE_INDICATOR);
        mPowerWidgetHapticFeedback = (ListPreference) prefSet
                .findPreference(UI_EXP_WIDGET_HAPTIC_FEEDBACK);
        mPowerWidgetHapticFeedback.setOnPreferenceChangeListener(this);

        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
        mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);

        mPowerWidget.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 1) == 1));
        mPowerWidgetHideOnChange.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.EXPANDED_HIDE_ONCHANGE, 0) == 1));
        mPowerWidgetHideScrollBar.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.EXPANDED_HIDE_SCROLLBAR, 0) == 1));
        mPowerWidgetIndicatorHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.EXPANDED_HIDE_INDICATOR, 0) == 1));
        mPowerWidgetHapticFeedback.setValue(Integer.toString(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.EXPANDED_HAPTIC_FEEDBACK, 2)));

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPowerWidgetHapticFeedback) {
            int intValue = Integer.parseInt((String)newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_HAPTIC_FEEDBACK, intValue);
            return true;
        }
        return false;
    }



    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mPowerWidget) {
            value = mPowerWidget.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET,
                    value ? 1 : 0);
            return true;
        }
        if (preference == mPowerWidgetHideOnChange) {
            value = mPowerWidgetHideOnChange.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_HIDE_ONCHANGE,
                    value ? 1 : 0);
            return true;
        }
        if (preference == mPowerWidgetHideScrollBar) {
            value = mPowerWidgetHideScrollBar.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_HIDE_SCROLLBAR,
                    value ? 1 : 0);
            return true;
        }
        if (preference == mPowerWidgetIndicatorHide) {
            value = mPowerWidgetIndicatorHide.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_HIDE_INDICATOR,
                    value ? 1 : 0);
            return true;
        }
        if (preference == mPowerWidgetColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity().getApplicationContext(), mWidgetColorListener,
                    readWidgetColor());
            cp.show();
            return true;
        }
        return false;
    }

    private int readWidgetColor() {
        try {
            return Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR);
        } catch (SettingNotFoundException e) {
            return -16777216;
        }
    }

    ColorPickerDialog.OnColorChangedListener mWidgetColorListener = new ColorPickerDialog.OnColorChangedListener() {
        public void colorChanged(int color) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR, color);
        }

        public void colorUpdate(int color) {
        }
    };
    
    public static class PowerWidgetChooser extends PreferenceFragment 
                 implements OnPreferenceChangeListener {

    	public PowerWidgetChooser() {}
    	
        private static final String TAG = "PowerWidgetActivity";

        private static final String BUTTONS_CATEGORY = "pref_buttons";
        private static final String SELECT_BUTTON_KEY_PREFIX = "pref_button_";

        private static final String EXP_BRIGHTNESS_MODE = "pref_brightness_mode";
        private static final String EXP_NETWORK_MODE = "pref_network_mode";
        private static final String EXP_SCREENTIMEOUT_MODE = "pref_screentimeout_mode";
        private static final String EXP_RING_MODE = "pref_ring_mode";
        private static final String EXP_FLASH_MODE = "pref_flash_mode";

        private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

        ListPreferenceMultiSelect mBrightnessMode;
        ListPreference mNetworkMode;
        ListPreference mScreentimeoutMode;
        ListPreferenceMultiSelect mRingMode;
        ListPreference mFlashMode;
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.power_widget);

            PreferenceScreen prefSet = getPreferenceScreen();

            if(getActivity().getApplicationContext() == null) {
            	return;
            }
            
            mBrightnessMode = (ListPreferenceMultiSelect) prefSet.findPreference(EXP_BRIGHTNESS_MODE);
            mBrightnessMode.setValue(Settings.System.getString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE));
            mBrightnessMode.setOnPreferenceChangeListener(this);
            mNetworkMode = (ListPreference) prefSet.findPreference(EXP_NETWORK_MODE);
            mNetworkMode.setOnPreferenceChangeListener(this);
            mScreentimeoutMode = (ListPreference) prefSet.findPreference(EXP_SCREENTIMEOUT_MODE);
            mScreentimeoutMode.setOnPreferenceChangeListener(this);
            mRingMode = (ListPreferenceMultiSelect) prefSet.findPreference(EXP_RING_MODE);
            mRingMode.setValue(Settings.System.getString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_RING_MODE));
            mRingMode.setOnPreferenceChangeListener(this);
            mFlashMode = (ListPreference) prefSet.findPreference(EXP_FLASH_MODE);
            mFlashMode.setOnPreferenceChangeListener(this);

            PreferenceCategory prefButtons = (PreferenceCategory) prefSet.findPreference(BUTTONS_CATEGORY);

            // empty our preference category and set it to order as added
            prefButtons.removeAll();
            prefButtons.setOrderingAsAdded(false);

            // emtpy our checkbox map
            mCheckBoxPrefs.clear();

            // get our list of buttons
            ArrayList<String> buttonList = PowerWidgetUtil.getButtonListFromString(PowerWidgetUtil.getCurrentButtons(getActivity().getApplicationContext()));

            // Don't show WiMAX option if not supported
           /*
            boolean isWimaxEnabled = WimaxHelper.isWimaxSupported(this);
            if (!isWimaxEnabled) {
                PowerWidgetUtil.BUTTONS.remove(PowerWidgetUtil.BUTTON_WIMAX);
            }
            */

            // fill that checkbox map!
            for(PowerWidgetUtil.ButtonInfo button : PowerWidgetUtil.BUTTONS.values()) {
                // create a checkbox
                CheckBoxPreference cb = new CheckBoxPreference(getActivity().getApplicationContext());

                // set a dynamic key based on button id
                cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

                // set vanity info
                cb.setTitle(button.getTitleResId());

                // set our checked state
                if(buttonList.contains(button.getId())) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }

                // add to our prefs set
                mCheckBoxPrefs.put(cb, button.getId());

                // specific checks for availability on some platforms
                if (PowerWidgetUtil.BUTTON_FLASHLIGHT.equals(button.getId()) &&
                        !getResources().getBoolean(R.bool.has_led_flash)) { // disable flashlight if it's not supported
                    cb.setEnabled(false);
                    mFlashMode.setEnabled(false);
                } else if (PowerWidgetUtil.BUTTON_NETWORKMODE.equals(button.getId())) {
                    // some phones run on networks not supported by this button, so disable it
                    int network_state = -99;

                    try {
                        network_state = Settings.Secure.getInt(getActivity().getApplicationContext().getContentResolver(),
                                Settings.Secure.PREFERRED_NETWORK_MODE);
                    } catch(Settings.SettingNotFoundException e) {
                        Log.e(TAG, "Unable to retrieve PREFERRED_NETWORK_MODE", e);
                    }

                    switch(network_state) {
                        // list of supported network modes
                        case Phone.NT_MODE_WCDMA_PREF:
                        case Phone.NT_MODE_WCDMA_ONLY:
                        case Phone.NT_MODE_GSM_UMTS:
                        case Phone.NT_MODE_GSM_ONLY:
                            break;
                        default:
                            cb.setEnabled(false);
                            break;
                    }
                }
                /*else if (PowerWidgetUtil.BUTTON_WIMAX.equals(button.getId())) {
                    if (!isWimaxEnabled) {
                        cb.setEnabled(false);
                    }
                }*/

                // add to the category
                prefButtons.addPreference(cb);
            }
        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            // we only modify the button list if it was one of our checks that was clicked
            boolean buttonWasModified = false;
            ArrayList<String> buttonList = new ArrayList<String>();
            for(Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
                if(entry.getKey().isChecked()) {
                    buttonList.add(entry.getValue());
                }

                if(preference == entry.getKey()) {
                    buttonWasModified = true;
                }
            }

            if(buttonWasModified) {
                // now we do some wizardry and reset the button list
                PowerWidgetUtil.saveCurrentButtons(getActivity().getApplicationContext(), PowerWidgetUtil.mergeInNewButtonString(
                        PowerWidgetUtil.getCurrentButtons(getActivity().getApplicationContext()), PowerWidgetUtil.getButtonStringFromList(buttonList)));
                return true;
            }

            return false;
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(preference == mBrightnessMode) {
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE, (String) newValue);
            } else if(preference == mNetworkMode) {
                int value = Integer.valueOf((String)newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_NETWORK_MODE, value);
            } else if(preference == mScreentimeoutMode) {
                int value = Integer.valueOf((String)newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_SCREENTIMEOUT_MODE, value);
            } else if(preference == mRingMode) {
                Settings.System.putString(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_RING_MODE, (String) newValue);
            } else if(preference == mFlashMode) {
                int value = Integer.valueOf((String)newValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), Settings.System.EXPANDED_FLASH_MODE, value);
            }
            return true;
        }
    }


    public static class PowerWidgetOrder extends ListFragment
    {
        private static final String TAG = "PowerWidgetOrderActivity";

        private ListView mButtonList;
        private ButtonAdapter mButtonAdapter;
        View mContentView = null;
        Context mContext;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.order_power_widget_buttons_activity, null);
            return mContentView;
        }

        /** Called when the activity is first created. */
        //@Override
        //public void onCreate(Bundle icicle)
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContext = getActivity().getApplicationContext();
            
            mButtonList = getListView();
            ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
            mButtonAdapter = new ButtonAdapter(mContext);
            setListAdapter(mButtonAdapter);
        }

        @Override
        public void onDestroy() {
            ((TouchInterceptor) mButtonList).setDropListener(null);
            setListAdapter(null);
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            // reload our buttons and invalidate the views for redraw
            mButtonAdapter.reloadButtons();
            mButtonList.invalidateViews();
        }

        private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
                public void drop(int from, int to) {
                    // get the current button list
                    ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                            PowerWidgetUtil.getCurrentButtons(mContext));

                    // move the button
                    if(from < buttons.size()) {
                        String button = buttons.remove(from);

                        if(to <= buttons.size()) {
                            buttons.add(to, button);

                            // save our buttons
                            PowerWidgetUtil.saveCurrentButtons(mContext,
                                    PowerWidgetUtil.getButtonStringFromList(buttons));

                            // tell our adapter/listview to reload
                            mButtonAdapter.reloadButtons();
                            mButtonList.invalidateViews();
                        }
                    }
                }
            };

        private class ButtonAdapter extends BaseAdapter {
            private Context mContext;
            private Resources mSystemUIResources = null;
            private LayoutInflater mInflater;
            private ArrayList<PowerWidgetUtil.ButtonInfo> mButtons;

            public ButtonAdapter(Context c) {
                mContext = c;
                mInflater = LayoutInflater.from(mContext);

                PackageManager pm = mContext.getPackageManager();
                if(pm != null) {
                    try {
                        mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                    } catch(Exception e) {
                        mSystemUIResources = null;
                        Log.e(TAG, "Could not load SystemUI resources", e);
                    }
                }

                reloadButtons();
            }

            public void reloadButtons() {
                ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                        PowerWidgetUtil.getCurrentButtons(mContext));

                mButtons = new ArrayList<PowerWidgetUtil.ButtonInfo>();
                for(String button : buttons) {
                    if(PowerWidgetUtil.BUTTONS.containsKey(button)) {
                        mButtons.add(PowerWidgetUtil.BUTTONS.get(button));
                    }
                }
            }

            public int getCount() {
                return mButtons.size();
            }

            public Object getItem(int position) {
                return mButtons.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                final View v;
                if(convertView == null) {
                    v = mInflater.inflate(R.layout.order_power_widget_button_list_item, null);
                } else {
                    v = convertView;
                }

                PowerWidgetUtil.ButtonInfo button = mButtons.get(position);

                final TextView name = (TextView)v.findViewById(R.id.name);
                final ImageView icon = (ImageView)v.findViewById(R.id.icon);

                name.setText(button.getTitleResId());

                // assume no icon first
                icon.setVisibility(View.GONE);

                // attempt to load the icon for this button
                if(mSystemUIResources != null) {
                    int resId = mSystemUIResources.getIdentifier(button.getIcon(), null, null);
                    if(resId > 0) {
                        try {
                            Drawable d = mSystemUIResources.getDrawable(resId);
                            icon.setVisibility(View.VISIBLE);
                            icon.setImageDrawable(d);
                        } catch(Exception e) {
                            Log.e(TAG, "Error retrieving icon drawable", e);
                        }
                    }
                }

                return v;
            }
        }
    }
    
}
