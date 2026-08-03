package app.morphe.extension.tiktok.settings.preference.categories;

import android.content.Context;
import android.preference.PreferenceScreen;

import app.morphe.extension.tiktok.settings.Settings;
import app.morphe.extension.tiktok.settings.SettingsStatus;
import app.morphe.extension.tiktok.settings.preference.TogglePreference;

@SuppressWarnings("deprecation")
public final class GhostModePreferenceCategory extends ConditionalPreferenceCategory {
    public GhostModePreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle("Ghost mode");
    }

    @Override
    public boolean getSettingsStatus() {
        return SettingsStatus.ghostModeEnabled;
    }

    @Override
    public void addPreferences(Context context) {
        if (SettingsStatus.ghostModeEnabled) {
            addPreference(new TogglePreference(
                    context,
                    "Ghost mode",
                    "Block story view notifications, profile view notifications, typing indicators, and appear offline to other users.",
                    Settings.GHOST_MODE
            ));
        }
    }
}
