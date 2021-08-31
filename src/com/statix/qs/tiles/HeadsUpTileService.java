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

import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/** Quick settings tile: Heads up **/
public class HeadsUpTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateState();
    }

    private void updateState() {
        boolean enabled = getEnabled();
        getQsTile().setContentDescription(enabled ?
                getString(R.string.accessibility_quick_settings_heads_up_on) :
                getString(R.string.accessibility_quick_settings_heads_up_off));
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        setEnabled(!getEnabled());
        updateState();
    }

    private boolean getEnabled() {
        return Settings.Global.getInt(getContentResolver(),
                       Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED, 0) == 1;
    }

    private void setEnabled(boolean enabled) {
        Settings.Global.putInt(getContentResolver(),
                Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
                enabled ? 1 : 0);
    }
}
