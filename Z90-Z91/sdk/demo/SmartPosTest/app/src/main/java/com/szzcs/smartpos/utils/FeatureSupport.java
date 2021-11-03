package com.szzcs.smartpos.utils;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.szzcs.smartpos.BuildConfig;
import com.szzcs.smartpos.R;
import com.zcs.sdk.util.PowerHelper;

/**
 * Created by yyzz on 20.2.27.
 */
public class FeatureSupport {


    public static void optimize(PreferenceFragment preferenceFragment) {
        int[] Z91_NOT_SUPPORT = {
                R.string.key_whole_engine_test,
                //R.string.key_update_firmware,
                R.string.key_pinPad,
                //R.string.key_led,
                //R.string.key_fingerprint,
                R.string.key_beep
        };

        int[] DEBUG_SUPPORT = {
                R.string.key_power_on,
                R.string.key_power_off,
                R.string.key_init,
                R.string.key_pinPad
        };
        if (!BuildConfig.DEBUG) {
            for (int i : DEBUG_SUPPORT) {
                Log.e("optimize: ", preferenceFragment + "");
                Log.e("optimize: ", preferenceFragment.getClass().getName());
                deletePref(preferenceFragment, i);
            }
        }
        if (PowerHelper.isZ91()) {
            for (int i : Z91_NOT_SUPPORT) {
                deletePref(preferenceFragment, i);
            }
        }
    }

    private static void deletePref(PreferenceFragment preferenceFragment, int i) {
        try {
            Preference pref = preferenceFragment.findPreference(preferenceFragment.getString(i));
            if (pref != null) {
                preferenceFragment.getPreferenceScreen().removePreference(pref);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
