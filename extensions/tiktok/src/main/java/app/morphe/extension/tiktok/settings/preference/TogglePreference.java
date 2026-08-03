package app.morphe.extension.tiktok.settings.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.Switch;

import app.morphe.extension.shared.settings.BooleanSetting;
import app.morphe.extension.tiktok.Utils;

@SuppressWarnings("deprecation")
public class TogglePreference extends SwitchPreference {

    public TogglePreference(Context context, String title, String summary, BooleanSetting setting) {
        super(context);
        setTitle(title);
        setSummary(summary);
        setKey(setting.key);
        setChecked(setting.get());
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        view.setBackground(SettingsUi.roundedSurface(getContext(), 0, false));
        view.setMinimumHeight(SettingsUi.dp(getContext(), 64));
        view.setPadding(
                SettingsUi.dp(getContext(), 20),
                SettingsUi.dp(getContext(), 14),
                SettingsUi.dp(getContext(), 16),
                SettingsUi.dp(getContext(), 14)
        );

        Utils.setTitleAndSummaryColor(view);

        Switch switchWidget = view.findViewById(android.R.id.switch_widget);
        if (switchWidget != null) {
            SettingsUi.styleSwitch(switchWidget);
        }
    }
}
