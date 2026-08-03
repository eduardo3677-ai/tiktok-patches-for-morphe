package app.morphe.extension.tiktok.ghostmode;

import android.util.Log;

import app.morphe.extension.tiktok.settings.Settings;
import app.morphe.extension.tiktok.settings.SettingsStatus;

public class GhostMode {

    private static final String TAG = "MorpheGhostMode";

    public static void forceOfflinePresence() {
        if (!isGhostModeActive()) return;
        Log.i(TAG, "Ghost mode active: forcing offline presence");
    }

    public static boolean shouldBlockStoryView() {
        return isGhostModeActive();
    }

    public static boolean shouldBlockProfileView() {
        return isGhostModeActive();
    }

    public static boolean shouldBlockTypingStatus() {
        return isGhostModeActive();
    }

    public static boolean shouldBlockPresence() {
        return isGhostModeActive();
    }

    private static boolean isGhostModeActive() {
        return SettingsStatus.ghostModeEnabled && Settings.GHOST_MODE.get();
    }
}
