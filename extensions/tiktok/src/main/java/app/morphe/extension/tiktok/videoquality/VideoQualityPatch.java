package app.morphe.extension.tiktok.videoquality;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.morphe.extension.tiktok.settings.Settings;

public class VideoQualityPatch {

    private static final String TAG = "MorpheVideoQuality";

    @SuppressWarnings("unused")
    public static List<Object> filterBitRates(Object video) {
        try {
            java.lang.reflect.Method getBitRate = video.getClass().getMethod("getBitRate");
            List<Object> bitRates = (List<Object>) getBitRate.invoke(video);

            if (bitRates == null || bitRates.isEmpty()) {
                return bitRates;
            }

            String desiredQuality = Settings.VIDEO_QUALITY.get();
            if (desiredQuality == null || desiredQuality.equals("auto") || desiredQuality.isEmpty()) {
                return bitRates;
            }

            Object bestMatch = null;
            int bestMatchScore = -1;

            for (Object bitRate : bitRates) {
                String gearName = (String) bitRate.getClass().getField("gearName").get(bitRate);
                int qualityType = bitRate.getClass().getField("qualityType").getInt(bitRate);

                int score = scoreQuality(gearName, qualityType, desiredQuality);
                if (score > bestMatchScore) {
                    bestMatchScore = score;
                    bestMatch = bitRate;
                }
            }

            if (bestMatch == null) {
                return bitRates;
            }

            List<Object> filtered = new ArrayList<>();
            filtered.add(bestMatch);
            return filtered;
        } catch (Throwable e) {
            Log.e(TAG, "filterBitRates failed", e);
            return null;
        }
    }

    private static int scoreQuality(String gearName, int qualityType, String desired) {
        if (gearName == null) return 0;

        if (desired.equals("lowest")) {
            switch (qualityType) {
                case 0: return 100;
                case 1: return 80;
                case 2: return 60;
                case 3: return 40;
                case 4: return 20;
                default: return 0;
            }
        }

        if (desired.equals("highest")) {
            switch (qualityType) {
                case 4: return 100;
                case 3: return 80;
                case 2: return 60;
                case 1: return 40;
                case 0: return 20;
                default: return 0;
            }
        }

        if (desired.equals("low")) {
            if (gearName.contains("low") || gearName.contains("sm")) return 100;
            if (gearName.contains("540") || gearName.contains("480")) return 80;
            if (gearName.contains("720")) return 60;
            if (gearName.contains("1080")) return 40;
            return 0;
        }

        if (desired.equals("medium")) {
            if (gearName.contains("540")) return 100;
            if (gearName.contains("720")) return 80;
            if (gearName.contains("480")) return 60;
            if (gearName.contains("1080")) return 40;
            return 0;
        }

        if (desired.equals("high")) {
            if (gearName.contains("720")) return 100;
            if (gearName.contains("1080")) return 80;
            if (gearName.contains("540")) return 60;
            if (gearName.contains("480")) return 40;
            return 0;
        }

        if (desired.equals("ultra")) {
            if (gearName.contains("1080")) return 100;
            if (gearName.contains("2k") || gearName.contains("1440")) return 80;
            if (gearName.contains("720")) return 60;
            return 0;
        }

        return 0;
    }
}
