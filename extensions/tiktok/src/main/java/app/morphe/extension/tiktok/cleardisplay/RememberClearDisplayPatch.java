/*
 * Forked from:
 * https://github.com/ReVanced/revanced-patches/blob/377d4e15016296b45d809697f7f69bce74badd3a/extensions/tiktok/src/main/java/app/revanced/extension/tiktok/cleardisplay/RememberClearDisplayPatch.java
 */

package app.morphe.extension.tiktok.cleardisplay;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.settings.BaseSettings;
import app.morphe.extension.tiktok.settings.Settings;

@SuppressWarnings("unused")
public class RememberClearDisplayPatch {
    private static volatile Boolean lastLoggedState;
    private static final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    public static boolean getClearDisplayState() {
        boolean state = Settings.CLEAR_DISPLAY.get();
        if (BaseSettings.DEBUG.get() && (lastLoggedState == null || lastLoggedState != state)) {
            lastLoggedState = state;
            Logger.printInfo(() -> "[Morphe ClearDisplay] get state=" + state);
        }
        return state;
    }

    public static void rememberClearDisplayState(boolean newState) {
        if (BaseSettings.DEBUG.get()) {
            boolean oldState = Settings.CLEAR_DISPLAY.get();
            Logger.printInfo(() -> "[Morphe ClearDisplay] remember state " + oldState + " -> " + newState);
        }
        Settings.CLEAR_DISPLAY.save(newState);
    }

    @SuppressWarnings("unused")
    public static void applyClearDisplayIfNeeded(final Object panel) {
        if (!getClearDisplayState()) return;

        mainHandler.post(() -> {
            try {
                java.lang.reflect.Method getEnterFrom = panel.getClass().getMethod("getEnterFrom", boolean.class);
                Object enterFrom = getEnterFrom.invoke(panel, true);

                Class<?> eventClass = Class.forName("com.ss.android.ugc.feed.platform.panel.clearmode.ClearModePanelComponent$ClearDisplayEvent");
                java.lang.reflect.Constructor<?> constructor = eventClass.getConstructor(boolean.class, int.class, String.class, String.class);
                Object event = constructor.newInstance(true, 0, "", "long_press");
                java.lang.reflect.Method post = eventClass.getMethod("post");
                post.invoke(event);
            } catch (Throwable e) {
                Logger.printDebug(() -> "applyClearDisplayIfNeeded failed: " + e.getMessage());
            }
        });
    }
}

