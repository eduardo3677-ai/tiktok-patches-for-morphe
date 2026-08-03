package app.morphe.extension.tiktok.settings.preference.categories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;

import app.morphe.extension.tiktok.settings.Settings;
import app.morphe.extension.tiktok.settings.SettingsStatus;
import app.morphe.extension.tiktok.settings.preference.SettingsUi;

@SuppressWarnings("deprecation")
public class VideoQualityPreferenceCategory extends ConditionalPreferenceCategory {

    private static final String[] QUALITY_KEYS = {"auto", "lowest", "low", "medium", "high", "ultra"};
    private static final String[] QUALITY_NAMES = {"Auto", "Lowest", "Low", "Medium", "High", "Ultra"};

    public VideoQualityPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle("Video quality");
    }

    @Override
    public boolean getSettingsStatus() {
        return SettingsStatus.videoQualityEnabled;
    }

    @Override
    public void addPreferences(Context context) {
        Preference qualityPref = new Preference(context);
        qualityPref.setTitle("Video quality");
        qualityPref.setSummary(getQualityName(Settings.VIDEO_QUALITY.get()));
        qualityPref.setOnPreferenceClickListener(pref -> {
            showQualityDialog(context, qualityPref);
            return true;
        });
        addPreference(qualityPref);
    }

    private void showQualityDialog(Context context, Preference qualityPref) {
        String current = Settings.VIDEO_QUALITY.get();
        int selectedIndex = 0;
        for (int i = 0; i < QUALITY_KEYS.length; i++) {
            if (QUALITY_KEYS[i].equals(current)) {
                selectedIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select video quality");
        builder.setSingleChoiceItems(QUALITY_NAMES, selectedIndex, (dialog, which) -> {
            Settings.VIDEO_QUALITY.save(QUALITY_KEYS[which]);
            qualityPref.setSummary(QUALITY_NAMES[which]);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        SettingsUi.styleFramedDialog(dialog);
    }

    private String getQualityName(String key) {
        for (int i = 0; i < QUALITY_KEYS.length; i++) {
            if (QUALITY_KEYS[i].equals(key)) {
                return QUALITY_NAMES[i];
            }
        }
        return "Auto";
    }
}
