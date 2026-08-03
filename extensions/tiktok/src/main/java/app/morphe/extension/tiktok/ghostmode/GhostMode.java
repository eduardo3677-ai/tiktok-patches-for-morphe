package app.morphe.extension.tiktok.ghostmode;

import android.util.Log;

import app.morphe.extension.tiktok.settings.SettingsStatus;
import app.morphe.extension.shared.Utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.HttpURLConnection;

public class GhostMode {

    private static final String TAG = "MorpheGhostMode";
    private static final String BLOCK_HOST = "127.0.0.1";

    public static void forceOfflinePresence() {
        if (!SettingsStatus.ghostModeEnabled) return;
        Log.i(TAG, "Ghost mode active: forcing offline presence");
    }

    public static boolean shouldBlockStoryView() {
        return SettingsStatus.ghostModeEnabled;
    }

    public static boolean shouldBlockProfileView() {
        return SettingsStatus.ghostModeEnabled;
    }

    public static boolean shouldBlockTypingStatus() {
        return SettingsStatus.ghostModeEnabled;
    }
}
