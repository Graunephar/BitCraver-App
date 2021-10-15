package com.graunephar.bitcraver.relay;

import android.app.Activity;
import android.util.Log;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YRelay;

/**
 * The relay strategy implemented for controlling a yocto relay.
 * This is the only place in the code where you are allowed to use any libraries from Yocto.
 */
public class YoctoRelayStrategyImpl implements RelayStrategy {

    private static final String TAG = YoctoRelayStrategyImpl.class.getSimpleName();
    private final Activity mActivity;

    private YRelay usbRelay;
    private long maxActivatedTimeInMS;

    private enum RelayStates {
        STATE_A,
        STATE_B_TEMPORARY,
        STATE_B_PERMANENT,
    }

    public YoctoRelayStrategyImpl(long maxActivatedTimeInMS, Activity activity) {
        this.maxActivatedTimeInMS = maxActivatedTimeInMS;
        this.mActivity = activity;
        init();
    }

    private void init() {
        try {
            YAPI.EnableUSBHost(mActivity); // use singleton from welcomebobapp
            YAPI.RegisterHub("usb");
            /** FirstRelay() returns the first relay currently online */
            usbRelay = YRelay.FirstRelay();
            /** Does this work, when more relays are online? */
            /** The relay will close by itself after x milliseconds */
            //wakeRelay();
        } catch (YAPI_Exception e) {
        }
    }

    @Override
    public void requestInit() {
        init();

    }

    @Override
    public void activateRelayShort() {
        /** Sets the relay to State_B (1)
         * Makes the relay open */
        changestate(RelayStates.STATE_B_TEMPORARY);
    }

    @Override
    public void deactivateRelay() {
        /** Sets the relay to State_A (0)
         * Makes the relay close */
        changestate(RelayStates.STATE_A);
    }

    private void wakeRelay() throws YAPI_Exception {
        if (usbRelay != null) {
            usbRelay.setMaxTimeOnStateB(10);
            usbRelay.setState(YRelay.STATE_B);
        }
    }

    void changestate(RelayStates state) {

        if (usbRelay == null) init();

        if (usbRelay != null) {
            try {
                int result = 0; // 0 = sucess in yapi
                switch (state) {
                    case STATE_A:
                        result = usbRelay.setState(YRelay.STATE_A);
                        break;
                    case STATE_B_TEMPORARY:
                        usbRelay.setMaxTimeOnStateB(maxActivatedTimeInMS);
                        result = usbRelay.setState(YRelay.STATE_B);
                        break;
                    case STATE_B_PERMANENT:
                        usbRelay.setMaxTimeOnStateB(0);
                        result = usbRelay.setState(YRelay.STATE_B);
                        break;
                }

                if (result != YAPI.SUCCESS) {
                }
            } catch (YAPI_Exception e) {
            }
        }
    }

    @Override
    public boolean isRelayResponding() {
        if (usbRelay == null) { // If this ever happens it will be really bad and we can't say that the relay works, however it should never happen
            Log.d(TAG, "USBRELAY ER NULLL!!!!!!!");
            return false;
        }
        if (usbRelay.isOnline()) {
            Log.d(TAG, "USBRelay er online");
            return true;
        } else {
            Log.d(TAG, "USBRELAY ER OFFLINE!!!!!!!");
            return false;
        }
    }

    @Override
    public void activateRelayUntilToldToSwitchBack() {
        Log.d(TAG, "::activateRelayUntilToldToSwitchBack " + usbRelay);
        changestate(RelayStates.STATE_B_PERMANENT);
    }

}
