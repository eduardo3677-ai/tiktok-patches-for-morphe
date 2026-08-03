package app.morphe.extension.tiktok.speed;

import android.os.Handler;
import android.os.Looper;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.tiktok.settings.Settings;

public final class PlaybackSpeedPatch {
    private static volatile float rememberedSpeed = 1.0f;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private PlaybackSpeedPatch() {}

    public static void rememberPlaybackSpeed(float speed) {
        rememberedSpeed = speed;
        try {
            Settings.REMEMBERED_SPEED.save(speed);
        } catch (Throwable ignored) {}
    }

    public static float getPlaybackSpeed() {
        try {
            float persisted = Settings.REMEMBERED_SPEED.get();
            return persisted != 0.0f ? persisted : rememberedSpeed;
        } catch (Throwable ignored) {
            return rememberedSpeed;
        }
    }

    @SuppressWarnings("unused")
    public static void applyPlaybackSpeed(final Object panel, final Object enterFrom) {
        mainHandler.post(() -> {
            try {
                float speed = getPlaybackSpeed();
                Class<?> panelClass = panel.getClass();
                Class<?> awemeClass = Class.forName("com.ss.android.ugc.aweme.feed.model.Aweme");
                java.lang.reflect.Method getAweme = panelClass.getMethod("LJIIIIZZ");
                Object aweme = getAweme.invoke(panel);
                if (aweme == null) return;

                setPlaybackSpeedViaReflection(enterFrom, aweme, speed);
            } catch (Throwable e) {
                Logger.printDebug(() -> "applyPlaybackSpeed failed: " + e.getMessage());
            }
        });
    }

    private static void setPlaybackSpeedViaReflection(Object enterFrom, Object aweme, float speed) {
        try {
            for (java.lang.reflect.Method m : Class.forName("X.1EyL").getDeclaredMethods()) {
                if (m.getName().equals("LJJ") && m.getParameterTypes().length >= 4) {
                    Class<?>[] params = m.getParameterTypes();
                    boolean hasAweme = false;
                    boolean hasFloat = false;
                    for (Class<?> p : params) {
                        if (p.getName().contains("Aweme")) hasAweme = true;
                        if (p == float.class) hasFloat = true;
                    }
                    if (!hasAweme || !hasFloat) continue;

                    Object[] args = new Object[params.length];
                    for (int i = 0; i < params.length; i++) {
                        if (params[i] == float.class) args[i] = speed;
                        else if (params[i].getName().contains("Aweme")) args[i] = aweme;
                        else if (params[i] == String.class) args[i] = enterFrom != null ? enterFrom.toString() : "";
                        else if (params[i] == boolean.class) args[i] = false;
                        else if (params[i] == Boolean.class) args[i] = Boolean.FALSE;
                        else args[i] = null;
                    }
                    m.setAccessible(true);
                    m.invoke(null, args);
                    return;
                }
            }
        } catch (Throwable e) {
            Logger.printDebug(() -> "setPlaybackSpeedViaReflection failed: " + e.getMessage());
        }
    }
}

