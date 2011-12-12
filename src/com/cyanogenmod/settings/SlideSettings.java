package com.cyanogenmod.settings;

import com.cyanogenmod.settings.R;
import com.cyanogenmod.settings.switches.PowerWidgetEnabler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
//import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.text.TextUtils;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import com.cyanogenmod.settings.lists.ApplicationList;
import com.cyanogenmod.settings.lists.DisplayList;
import com.cyanogenmod.settings.lists.InputList;
import com.cyanogenmod.settings.lists.InterfaceList;
import com.cyanogenmod.settings.lists.LockscreenList;
import com.cyanogenmod.settings.lists.PerformanceList;
import com.cyanogenmod.settings.lists.SoundList;
import com.cyanogenmod.settings.lists.SystemList;
import com.cyanogenmod.settings.lists.TabletList;
import com.cyanogenmod.settings.lists.zList;

public class SlideSettings extends Activity {

    SettingsAdapter mAdapter;
    ViewPager mPager;
    ActionBar mActionBar;
    static final int VIEWS = 2;
//    public static LinkedList listHead;

    private static final HashMap<Integer, Class<? extends zList>> LIST = new HashMap<Integer, Class<? extends zList>>();
    static {
        LIST.put(0, ApplicationList.class);
        LIST.put(1, DisplayList.class);
        LIST.put(2, InputList.class);
        LIST.put(3, InterfaceList.class);
        LIST.put(4, LockscreenList.class);
        LIST.put(5, PerformanceList.class);
        LIST.put(6, SoundList.class);
        LIST.put(7, SystemList.class);
        LIST.put(8, TabletList.class);
    }
    private static final HashMap<Integer, zList> listHead = new HashMap<Integer, zList>();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settings_view);
        createLists();
        mAdapter = new SettingsAdapter(getFragmentManager());
        mActionBar = getActionBar();
        mPager = (ViewPager) findViewById(R.id.settings_pager);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowTitleEnabled(true);

        for (String entry : mAdapter.getTabs()) {
            ActionBar.Tab tab = mActionBar.newTab();
            tab.setTabListener(new TabListener() {
                @Override
                public void onTabReselected(Tab tab, FragmentTransaction ft) {
                }
                @Override
                public void onTabSelected(Tab tab, FragmentTransaction ft) {
                    mPager.setCurrentItem(tab.getPosition());
                }
                @Override
                public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                }
            });
            tab.setText(entry);
            mActionBar.addTab(tab);
        }

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int idx) {
                mActionBar.selectTab(mActionBar.getTabAt(idx));
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void createLists() {
        for (HashMap.Entry<Integer, Class<? extends zList>> entry : LIST.entrySet()) {
            try {
                Integer key = entry.getKey();
                zList value = entry.getValue().newInstance();
                listHead.put(key, value);
            } catch (Exception e) {
            }
        }
    }

    public static class SettingsAdapter extends FragmentPagerAdapter {
        private final String[] tabs = {"Application", "Display", "Input", "Interface", "Lockscreen", 
                                  "Performace", "Sound", "System", "Tablet" };

        public SettingsAdapter(FragmentManager fm) {
            super(fm);
        }

        public String[] getTabs() {
            return tabs;
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("-" + mNum + "-");
            return v;
        }

//        private static final String[][] mHeaders = {
//            { "Expanded Widget", "Notification Bar Widget", "com.cyanogenmod.settings.activities.PowerWidget", "", "2" },
//            { "Backlight",                              "", "com.cyanogenmod.settings.activities.Backlight",   "", "1" }
//        };

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            List<Headers> aHeaders = new ArrayList<Headers>();
            String[][] mHeaders = listHead.get(mNum).getList();
            for(int i = 0; i < mHeaders.length; i++) {
                Headers header = new Headers(mHeaders[i][0], 
                                             mHeaders[i][1],
                                             mHeaders[i][2],
                                             mHeaders[i][3],
                                             mHeaders[i][4]);
                aHeaders.add(header);
            }
            setListAdapter(new HeaderAdapter(getActivity().getApplicationContext(), aHeaders));
        }
        
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            ListAdapter adapter = getListAdapter();
            Headers header = (Headers)adapter.getItem(position);
            if (!TextUtils.isEmpty(header.mFragment)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName(getActivity().getApplicationContext(), header.mFragment);
                startActivity(intent);
            }
        }

        private class Headers {
            public String mTitle;
            public String mSummary;
            public String mFragment;
            public String mIntent;
            public int mType;
            public Headers(String title, String summary, String fragment, String intent, String type) {
                mTitle = title;
                mSummary = summary;
                mFragment = fragment;
                mIntent = intent;
                if(!type.isEmpty()) {
                    mType = Integer.parseInt(type);
                } else {
                    mType = 1;
                }
            }
        }

        private static class HeaderAdapter extends ArrayAdapter<Headers> {
            static final int HEADER_TYPE_CATEGORY = 0;
            static final int HEADER_TYPE_NORMAL = 1;
            static final int HEADER_TYPE_SWITCH = 2;
            private static final int HEADER_TYPE_COUNT = HEADER_TYPE_SWITCH + 1;

            private final PowerWidgetEnabler mWidgetEnabler;

            private static class HeaderViewHolder {
                ImageView icon;
                TextView title;
                TextView summary;
                Switch switch_;
            }

            private LayoutInflater mInflater;

            static int getHeaderType(Headers header) {/*
                if (header.fragment == null && header.intent == null) {
                    return HEADER_TYPE_CATEGORY;
                } else if (header.id == R.id.power_widget_settings) {
                    return HEADER_TYPE_SWITCH;
                } else {
                    return HEADER_TYPE_NORMAL;
                }*/
                return header.mType;
            }

            @Override
            public int getItemViewType(int position) {
                Headers header = getItem(position);
                return getHeaderType(header);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false; // because of categories
            }

            @Override
            public boolean isEnabled(int position) {
                return getItemViewType(position) != HEADER_TYPE_CATEGORY;
            }

            @Override
            public int getViewTypeCount() {
                return HEADER_TYPE_COUNT;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            public HeaderAdapter(Context context, List<Headers> objects) {
                super(context, 0, objects);
                mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
                // Temp Switches provided as placeholder until the adapter replaces these with actual
                // Switches inflated from their layouts. Must be done before adapter is set in super
                /*
                mWifiEnabler = new WifiEnabler(context, new Switch(context));
                mBluetoothEnabler = new BluetoothEnabler(context, new Switch(context));
                */
                mWidgetEnabler = new PowerWidgetEnabler(context, new Switch(context));
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                HeaderViewHolder holder;
                Headers header = getItem(position);
                int headerType = getHeaderType(header);
                View view = null;

                if (convertView == null) {
                    holder = new HeaderViewHolder();
                    switch (headerType) {
                        case HEADER_TYPE_CATEGORY:
                            view = new TextView(getContext(), null,
                                android.R.attr.listSeparatorTextViewStyle);
                            holder.title = (TextView) view;
                            break;

                        case HEADER_TYPE_SWITCH:
                            view = mInflater.inflate(R.layout.preference_header_switch_item, parent,
                                false);
                            holder.icon = (ImageView) view.findViewById(R.id.icon);
                            holder.title = (TextView)
                                view.findViewById(R.id.title);
                            holder.summary = (TextView)
                                view.findViewById(R.id.summary);
                            holder.switch_ = (Switch) view.findViewById(R.id.switchWidget);
                            break;

                        case HEADER_TYPE_NORMAL:
                            view = mInflater.inflate(
                                R.layout.preference_header_item, parent,
                                    false);
                            holder.icon = (ImageView) view.findViewById(R.id.icon);
                            holder.title = (TextView) view.findViewById(R.id.pref_title);
                            holder.summary = (TextView) view.findViewById(R.id.pref_summary);
                            break;
                    }
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (HeaderViewHolder) view.getTag();
                }

                switch (headerType) {
                    case HEADER_TYPE_CATEGORY:
                        holder.title.setText(header.mTitle);
                        break;

                    case HEADER_TYPE_SWITCH:
                        if (header.mTitle.equals("Expanded Widget")) {
                             mWidgetEnabler.setSwitch(holder.switch_);
                        }
                        //$FALL-THROUGH$
                    case HEADER_TYPE_NORMAL:
                        //holder.icon.setImageResource(header.iconRes);
                        holder.title.setText(header.mTitle);
                        CharSequence summary = header.mSummary;
                        if (!TextUtils.isEmpty(summary)) {
                            holder.summary.setVisibility(View.VISIBLE);
                            holder.summary.setText(summary);
                        } else {
                            holder.summary.setVisibility(View.GONE);
                        }
                        break;
                }

                return view;
            }
        
            public void resume() {
                mWidgetEnabler.resume();
            }
        
             public void pause() {
                 mWidgetEnabler.pause();
             } 
        }

    }

}
