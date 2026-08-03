package app.morphe.extension.tiktok.settings.preference;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorInt;

public final class SettingsUi {
    public static final @ColorInt int TIKTOK_PINK = Color.argb(255, 254, 44, 85);
    public static final @ColorInt int TIKTOK_CYAN = Color.argb(255, 37, 244, 239);
    public static final @ColorInt int TIKTOK_RED = Color.argb(255, 255, 0, 80);

    public static final @ColorInt int ACCENT = TIKTOK_PINK;

    public static final @ColorInt int DARK_BG = Color.argb(255, 17, 17, 17);
    public static final @ColorInt int DARK_SURFACE = Color.argb(255, 28, 28, 30);
    public static final @ColorInt int DARK_SURFACE_LIFTED = Color.argb(255, 38, 38, 42);
    public static final @ColorInt int DARK_BORDER = Color.argb(255, 48, 48, 52);
    public static final @ColorInt int DARK_DIVIDER = Color.argb(255, 38, 38, 42);
    public static final @ColorInt int DARK_TEXT_PRIMARY = Color.argb(255, 255, 255, 255);
    public static final @ColorInt int DARK_TEXT_SECONDARY = Color.argb(255, 168, 168, 176);
    public static final @ColorInt int DARK_TEXT_DISABLED = Color.argb(255, 99, 99, 102);

    public static final @ColorInt int LIGHT_BG = Color.argb(255, 242, 242, 247);
    public static final @ColorInt int LIGHT_SURFACE = Color.WHITE;
    public static final @ColorInt int LIGHT_SURFACE_LIFTED = Color.argb(255, 250, 250, 252);
    public static final @ColorInt int LIGHT_BORDER = Color.argb(255, 229, 229, 234);
    public static final @ColorInt int LIGHT_DIVIDER = Color.argb(255, 235, 235, 240);
    public static final @ColorInt int LIGHT_TEXT_PRIMARY = Color.argb(255, 17, 17, 17);
    public static final @ColorInt int LIGHT_TEXT_SECONDARY = Color.argb(255, 99, 99, 102);
    public static final @ColorInt int LIGHT_TEXT_DISABLED = Color.argb(255, 174, 174, 178);

    private SettingsUi() {
    }

    public static boolean isDarkMode() {
        return app.morphe.extension.shared.Utils.isDarkModeEnabled();
    }

    public static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static @ColorInt int background() {
        return isDarkMode() ? DARK_BG : LIGHT_BG;
    }

    public static @ColorInt int surface() {
        return isDarkMode() ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static @ColorInt int liftedSurface() {
        return isDarkMode() ? DARK_SURFACE_LIFTED : LIGHT_SURFACE_LIFTED;
    }

    public static @ColorInt int border() {
        return isDarkMode() ? DARK_BORDER : LIGHT_BORDER;
    }

    public static @ColorInt int divider() {
        return isDarkMode() ? DARK_DIVIDER : LIGHT_DIVIDER;
    }

    public static @ColorInt int textPrimary() {
        return isDarkMode() ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
    }

    public static @ColorInt int textSecondary() {
        return isDarkMode() ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static @ColorInt int textDisabled() {
        return isDarkMode() ? DARK_TEXT_DISABLED : LIGHT_TEXT_DISABLED;
    }

    public static void styleTitleAndSummary(View view) {
        TextView title = view.findViewById(android.R.id.title);
        if (title != null) {
            title.setTextColor(textPrimary());
            title.setTextSize(16);
        }

        TextView summary = view.findViewById(android.R.id.summary);
        if (summary != null) {
            summary.setTextColor(textSecondary());
            summary.setTextSize(13);
        }
    }

    public static void styleCategory(View view) {
        TextView title = view.findViewById(android.R.id.title);
        if (title != null) {
            title.setTextColor(TIKTOK_PINK);
            title.setTextSize(14);
            title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    public static void styleSwitch(Switch switchWidget) {
        if (switchWidget == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_enabled},
                    new int[]{}
            };
            int[] colors = new int[]{TIKTOK_PINK, textDisabled(), textSecondary()};
            switchWidget.setThumbTintList(new android.content.res.ColorStateList(states, colors));
            int[][] trackStates = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{}
            };
            int[] trackColors = new int[]{
                    Color.argb(255, 254, 100, 120),
                    isDarkMode() ? Color.argb(255, 60, 60, 67) : Color.argb(255, 229, 229, 234)
            };
            switchWidget.setTrackTintList(new android.content.res.ColorStateList(trackStates, trackColors));
        }
    }

    public static TextView text(Context context, String value, float sizeSp, int color, int style) {
        TextView textView = new TextView(context);
        textView.setText(value);
        textView.setTextColor(color);
        textView.setTextSize(sizeSp);
        textView.setTypeface(textView.getTypeface(), style);
        return textView;
    }

    public static GradientDrawable roundedSurface(Context context, int radiusDp, boolean lifted) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(lifted ? liftedSurface() : surface());
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable borderedSurface(Context context, int radiusDp, boolean lifted) {
        GradientDrawable drawable = roundedSurface(context, radiusDp, lifted);
        drawable.setStroke(Math.max(1, dp(context, 1)), border());
        return drawable;
    }

    public static GradientDrawable cardBackground(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(surface());
        drawable.setCornerRadius(dp(context, 12));
        return drawable;
    }

    public static GradientDrawable categoryHeaderBackground(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.argb(isDarkMode() ? 20 : 15, 254, 44, 85));
        drawable.setCornerRadius(dp(context, 8));
        return drawable;
    }

    public static void styleDialog(android.app.Dialog dialog) {
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View decorView = window.getDecorView();
            if (decorView != null) {
                decorView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        if (dialog instanceof android.app.AlertDialog) {
            android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialog;
            View content = alertDialog.findViewById(android.R.id.content);
            if (content != null) {
                content.setBackgroundColor(Color.TRANSPARENT);
            }
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE), true);
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_NEGATIVE), false);
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_NEUTRAL), false);
        }
    }

    public static void styleFramedDialog(android.app.Dialog dialog) {
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(borderedSurface(dialog.getContext(), 16, true));
        }

        if (dialog instanceof android.app.AlertDialog) {
            android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialog;
            View content = alertDialog.findViewById(android.R.id.content);
            if (content != null) {
                content.setBackgroundColor(Color.TRANSPARENT);
            }
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE), true);
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_NEGATIVE), false);
            styleActionButton(alertDialog.getButton(android.content.DialogInterface.BUTTON_NEUTRAL), false);
        }
    }

    public static void styleActionButton(android.widget.Button button, boolean primary) {
        if (button == null) {
            return;
        }
        button.setTextColor(primary ? TIKTOK_PINK : textSecondary());
        button.setAllCaps(true);
        button.setTypeface(button.getTypeface(), primary ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    public static void styleTextAction(TextView button, boolean primary) {
        button.setTextColor(primary ? TIKTOK_PINK : textSecondary());
        button.setTypeface(button.getTypeface(), primary ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    public static void styleEditText(android.widget.EditText editText) {
        editText.setTextColor(textPrimary());
        editText.setHintTextColor(textSecondary());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintList(new android.content.res.ColorStateList(
                    new int[][]{new int[]{}},
                    new int[]{TIKTOK_PINK}
            ));
        }
    }

    public static void styleCheckBox(CompoundButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_enabled},
                    new int[]{}
            };
            int[] colors = new int[]{TIKTOK_PINK, textDisabled(), textSecondary()};
            button.setButtonTintList(new android.content.res.ColorStateList(states, colors));
        }
    }
}
