package app.morphe.patches.tiktok.videoquality;

import app.morphe.patcher.Fingerprint

internal object VideoGetBitRateFingerprint : Fingerprint(
    custom = { method, classDef ->
        classDef.endsWith("/Video;") &&
            method.name == "getBitRate" &&
            method.returnType == "Ljava/util/List;" &&
            method.parameterTypes.isEmpty()
    },
)
