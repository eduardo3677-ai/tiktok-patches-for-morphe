package app.morphe.extension.tiktok.settings.preference.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;

import app.morphe.extension.tiktok.settings.preference.SettingsUi;

@SuppressWarnings("deprecation")
public abstract class ConditionalPreferenceCategory extends PreferenceCategory {
    public ConditionalPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context);

        if (getSettingsStatus()) {
            screen.addPreference(this);
            addPreferences(context);
        }
    }

    public abstract boolean getSettingsStatus();

    public abstract void addPreferences(Context context);

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        view.setPadding(
                SettingsUi.dp(getContext(), 20),
                SettingsUi.dp(getContext(), 16),
                SettingsUi.dp(getContext(), 20),
                SettingsUi.dp(getContext(), 6)
        );

        TextView title = view.findViewById(android.R.id.title);
        if (title != null) {
            title.setTextColor(SettingsUi.ACCENT);
            title.setTextSize(13);
            title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
            title.setAllCaps(true);
            title.setLetterSpacing(0.08f);
        }
    }
}
