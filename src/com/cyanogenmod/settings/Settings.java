package com.cyanogenmod.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.cyanogenmod.settings.R;

import com.cyanogenmod.settings.switches.PowerWidgetEnabler;

public class Settings extends PreferenceActivity {
	
    private static final String LOG_TAG = "Settings";
    private static final String META_DATA_KEY_HEADER_ID =
        "com.cyanogenmod.settings.TOP_LEVEL_HEADER_ID";
    private static final String META_DATA_KEY_FRAGMENT_CLASS =
        "com.cyanogenmod.settings.FRAGMENT_CLASS";
    private static final String META_DATA_KEY_PARENT_TITLE =
        "com.cyanogenmod.settings.PARENT_FRAGMENT_TITLE";
    private static final String META_DATA_KEY_PARENT_FRAGMENT_CLASS =
        "com.cyanogenmod.settings.PARENT_FRAGMENT_CLASS";

    private static final String EXTRA_CLEAR_UI_OPTIONS = "settings:remove_ui_options";

    private static final String SAVE_KEY_CURRENT_HEADER = "com.cyanogenmod.settings.CURRENT_HEADER";
    private static final String SAVE_KEY_PARENT_HEADER = "com.cyanogenmod.settings.PARENT_HEADER";

    private String mFragmentClass;
    private int mTopLevelHeaderId;
    private Header mFirstHeader;
    private Header mCurrentHeader;
    private Header mParentHeader;
    private boolean mInLocalHeaderSwitch;


    protected HashMap<Integer, Integer> mHeaderIndexMap = new HashMap<Integer, Integer>();
    private List<Header> mHeaders;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
            if (getIntent().getBooleanExtra(EXTRA_CLEAR_UI_OPTIONS, false)) {
            getWindow().setUiOptions(0);
        }

        getMetaData();
        mInLocalHeaderSwitch = true;
        super.onCreate(savedInstanceState);
        mInLocalHeaderSwitch = false;

        if (!onIsHidingHeaders() && onIsMultiPane()) {
            highlightHeader();
            setTitle("CM Settings");
        }
        if (savedInstanceState != null) {
            mCurrentHeader = savedInstanceState.getParcelable(SAVE_KEY_CURRENT_HEADER);
            mParentHeader = savedInstanceState.getParcelable(SAVE_KEY_PARENT_HEADER);
        }
        if (savedInstanceState != null && mCurrentHeader != null) {
            showBreadCrumbs(mCurrentHeader.title, null);
        }
        if (mParentHeader != null) {
            setParentTitle(mParentHeader.title, null, new OnClickListener() {
                public void onClick(View v) {
                    switchToParent(mParentHeader.fragment);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentHeader != null) {
            outState.putParcelable(SAVE_KEY_CURRENT_HEADER, mCurrentHeader);
        }
        if (mParentHeader != null) {
            outState.putParcelable(SAVE_KEY_PARENT_HEADER, mParentHeader);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ListAdapter listAdapter = getListAdapter();
        if (listAdapter instanceof HeaderAdapter) {
            ((HeaderAdapter) listAdapter).resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ListAdapter listAdapter = getListAdapter();
        if (listAdapter instanceof HeaderAdapter) {
            ((HeaderAdapter) listAdapter).pause();
        }
    }
    
    @Override
    public Header onGetInitialHeader() {
        String fragmentClass = getStartingFragmentClass(super.getIntent());
        if (fragmentClass != null) {
            Header header = new Header();
            header.fragment = fragmentClass;
            header.title = getTitle();
            header.fragmentArguments = getIntent().getExtras();
            mCurrentHeader = header;
            return header;
        }

        return mFirstHeader;
    }

    @Override
    public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args,
            int titleRes, int shortTitleRes) {
        Intent intent = super.onBuildStartFragmentIntent(fragmentName, args,
                titleRes, shortTitleRes);

        intent.setClass(this, SubSettings.class);
        return intent;
    }
    
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.main, target);
        updateHeaderList(target);
        mHeaders = target;
    }
    
    private void updateHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;
            
            if (target.get(i) == header) {
                if (mFirstHeader == null &&
                        HeaderAdapter.getHeaderType(header) != HeaderAdapter.HEADER_TYPE_CATEGORY) {
                    mFirstHeader = header;
                }
                mHeaderIndexMap.put(id, i);
                i++;
            }
        }
    }

    private void getMetaData() {
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(),
                    PackageManager.GET_META_DATA);
            if (ai == null || ai.metaData == null) return;
            mTopLevelHeaderId = ai.metaData.getInt(META_DATA_KEY_HEADER_ID);
            mFragmentClass = ai.metaData.getString(META_DATA_KEY_FRAGMENT_CLASS);
            
            final int parentHeaderTitleRes = ai.metaData.getInt(META_DATA_KEY_PARENT_TITLE);
            String parentFragmentClass = ai.metaData.getString(META_DATA_KEY_PARENT_FRAGMENT_CLASS);
            if (parentFragmentClass != null) {
                mParentHeader = new Header();
                mParentHeader.fragment = parentFragmentClass;
                if (parentHeaderTitleRes != 0) {
                    mParentHeader.title = getResources().getString(parentHeaderTitleRes);
                }
            }
        } catch (NameNotFoundException nnfe) {
            // No recovery
        }
    }

    private void switchToParent(String className) {
        final ComponentName cn = new ComponentName(this, className);
        try {
            final PackageManager pm = getPackageManager();
            final ActivityInfo parentInfo = pm.getActivityInfo(cn, PackageManager.GET_META_DATA);

            if (parentInfo != null && parentInfo.metaData != null) {
                String fragmentClass = parentInfo.metaData.getString(META_DATA_KEY_FRAGMENT_CLASS);
                CharSequence fragmentTitle = parentInfo.loadLabel(pm);
                Header parentHeader = new Header();
                parentHeader.fragment = fragmentClass;
                parentHeader.title = fragmentTitle;
                mCurrentHeader = parentHeader;

                switchToHeaderLocal(parentHeader);
                highlightHeader();

                mParentHeader = new Header();
                mParentHeader.fragment
                        = parentInfo.metaData.getString(META_DATA_KEY_PARENT_FRAGMENT_CLASS);
                mParentHeader.title = parentInfo.metaData.getString(META_DATA_KEY_PARENT_TITLE);
            }
        } catch (NameNotFoundException nnfe) {
        }
    }

    protected String getStartingFragmentClass(Intent intent) {
        if (mFragmentClass != null) return mFragmentClass;

        String intentClass = intent.getComponent().getClassName();
        if (intentClass.equals(getClass().getName())) return null;

        return intentClass;
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0
                && mFirstHeader != null && !onIsHidingHeaders() && onIsMultiPane()) {
            switchToHeaderLocal(mFirstHeader);
        }
    }

    private void highlightHeader() {
        if (mTopLevelHeaderId != 0) {
            Integer index = mHeaderIndexMap.get(mTopLevelHeaderId);
            if (index != null) {
                getListView().setItemChecked(index, true);
                getListView().smoothScrollToPosition(index);
            }
        }
    }

    private void switchToHeaderLocal(Header header) {
        mInLocalHeaderSwitch = true;
        switchToHeader(header);
        mInLocalHeaderSwitch = false;
    }

    @Override
    public void switchToHeader(Header header) {
        if (!mInLocalHeaderSwitch) {
            mCurrentHeader = null;
            mParentHeader = null;
        }
        super.switchToHeader(header);
    }


    @Override
    public Intent getIntent() {
        Intent superIntent = super.getIntent();
        String startingFragment = getStartingFragmentClass(superIntent);
        if (startingFragment != null && !onIsMultiPane()) {
            Intent modIntent = new Intent(superIntent);
            modIntent.putExtra(EXTRA_SHOW_FRAGMENT, startingFragment);
            Bundle args = superIntent.getExtras();
            if (args != null) {
                args = new Bundle(args);
            } else {
                args = new Bundle();
            }
            args.putParcelable("intent", superIntent);
            modIntent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, superIntent.getExtras());
            return modIntent;
        }
        return superIntent;
    }

    private static class HeaderAdapter extends ArrayAdapter<Header> {
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

        static int getHeaderType(Header header) {
            if (header.fragment == null && header.intent == null) {
                return HEADER_TYPE_CATEGORY;
            } else if (header.id == R.id.power_widget_settings) {
                return HEADER_TYPE_SWITCH;
            } else {
                return HEADER_TYPE_NORMAL;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Header header = getItem(position);
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

        public HeaderAdapter(Context context, List<Header> objects) {
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
            Header header = getItem(position);
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
                    holder.title.setText(header.getTitle(getContext().getResources()));
                    break;

                case HEADER_TYPE_SWITCH:
                    if (header.id == R.id.power_widget_settings) {
                        mWidgetEnabler.setSwitch(holder.switch_);
                    }
                    //$FALL-THROUGH$
                case HEADER_TYPE_NORMAL:
                    holder.icon.setImageResource(header.iconRes);
                    holder.title.setText(header.getTitle(getContext().getResources()));
                    CharSequence summary = header.getSummary(getContext().getResources());
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
    
    @Override
    public void setListAdapter(ListAdapter adapter) {
        if (mHeaders == null) {
            mHeaders = new ArrayList<Header>();
            for (int i = 0; i < adapter.getCount(); i++) {
                mHeaders.add((Header) adapter.getItem(i));
            }
        }
        super.setListAdapter(new HeaderAdapter(this, mHeaders));
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        CharSequence title = pref.getTitle();
        startPreferencePanel(pref.getFragment(), pref.getExtras(), 0, title, null, 0);
        return true;
    }

    public static class PowerWidgetActivity extends Settings { /* */ }
    public static class PowerWidgetChooserActivity extends Settings { /* */ }
    public static class PowerWidgetOrderActivity extends Settings { /* */ }
    public static class BackLightActivity extends Settings { /* */ }
}
