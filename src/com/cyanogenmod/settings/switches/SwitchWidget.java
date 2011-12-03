package com.cyanogenmod.settings.switches;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.cyanogenmod.settings.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class SwitchWidget implements CompoundButton.OnCheckedChangeListener  {
    public static Context mContext;
    public Switch mSwitch;
    public AtomicBoolean mConnected = new AtomicBoolean(false);

    public boolean mStateMachineEvent;

/*    public SwitchWidget(Context context, Switch switch_) {
        super(context, switch_);
        mContext = context;
        mSwitch = switch_;
    }
*/
    public SwitchWidget() { 
    }
    public void resume() {
        mSwitch.setOnCheckedChangeListener(this);
    }

    public void pause() {
        mSwitch.setOnCheckedChangeListener(null);
    }

    public void setSwitch(Switch switch_) {
        /* Stub! */
	if (mSwitch == switch_) return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(this);

        setState(switch_);
    }

    public void setState(Switch switch_) {
        /* Stub */
        return;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /* Stub! */
        return;
    }

    private void setSwitchChecked(boolean checked) {
        if (checked != mSwitch.isChecked()) {
            mStateMachineEvent = true;
            mSwitch.setChecked(checked);
            mStateMachineEvent = false;
        }
    }
}
