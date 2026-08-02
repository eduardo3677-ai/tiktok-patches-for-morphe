package app.morphe.patches.tiktok.misc.navigation

import app.morphe.patcher.Fingerprint

internal object HomeTabAbilityListFingerprint : Fingerprint(
    returnType = "Ljava/util/List;",
    parameters = listOf("Z"),
    custom = { method, classDef ->
        classDef.endsWith("/TabAbilityAssem;") &&
            method.returnType == "Ljava/util/List;" &&
            method.parameterTypes == listOf("Z")
    },
)

internal object BottomTabBuildListFingerprint : Fingerprint(
    returnType = "V",
    parameters = listOf("Ljava/util/List;"),
    custom = { method, classDef ->
        classDef.type == "LX/0tBq;" &&
            method.returnType == "V" &&
            method.parameterTypes == listOf("Ljava/util/List;")
    },
)
