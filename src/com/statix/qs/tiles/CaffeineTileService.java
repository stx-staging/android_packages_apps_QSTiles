/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/** Quick settings tile: Heads up **/
public class CaffeineTileService extends TileService {

    private Handler mHandler;
    private PowerManager.WakeLock mWakeLock;
    private final Receiver mReceiver = new Receiver();

    @Override
    public void onStartListening() {
        super.onStartListening();
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mReceiver.init();
        if (mWakeLock == null) {
            mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                    PowerManager.FULL_WAKE_LOCK, "CaffeineTile");
        }
        updateState();
    }

    private void updateState() {
        getQsTile().setContentDescription(mWakeLock.isHeld() ?
                getString(R.string.accessibility_quick_settings_caffeine_on) :
                getString(R.string.accessibility_quick_settings_caffeine_off));
        getQsTile().setState(mWakeLock.isHeld() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        // toggle
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
            mReceiver.destroy();
        } else {
            mWakeLock.acquire();
        }
        updateState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mReceiver.destroy();
    }

    private final class Receiver extends BroadcastReceiver {
        public void init() {
            // Register for Intent broadcasts for...
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(this, filter, null, mHandler);
        }

        public void destroy() {
            unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // disable caffeine if user force off (power button)
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                updateState();
            }
        }
    }
}
