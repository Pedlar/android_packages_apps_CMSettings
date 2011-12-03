package com.cyanogenmod.settings.switches;

import android.provider.Settings;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cyanogenmod.settings.switches.SwitchWidget;

public class PowerWidgetEnabler extends SwitchWidget {
    public PowerWidgetEnabler(Context context, Switch switch_) {
        mContext = context;
        mSwitch = switch_;
    }

    public void resume() {
        mSwitch.setOnCheckedChangeListener(this);
    }

    public void pause() {
        mSwitch.setOnCheckedChangeListener(null);
    }

    public void setState(Switch switch_) {
        int isEnabled = Settings.System.getInt(mContext.getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET, 1);
	mSwitch.setChecked(isEnabled == 1 ? true : false);
        return;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Do nothing if called as a result of a state machine event
        if (mStateMachineEvent) {
            return;
        }
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET,
                    isChecked ? 1 : 0);
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
