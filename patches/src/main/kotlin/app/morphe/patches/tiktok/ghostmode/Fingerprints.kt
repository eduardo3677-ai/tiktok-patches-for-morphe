package app.morphe.patches.tiktok.ghostmode

import app.morphe.patcher.Fingerprint

internal object StoryApiReportViewedFingerprint : Fingerprint(
    custom = { method, classDef ->
        classDef.endsWith("/StoryApi;") &&
            (method.name == "reportStoryViewed" || method.name == "reportUserInteraction" || method.name == "reportStoryReveal")
    },
)

internal object ProfileViewerApiReportViewFingerprint : Fingerprint(
    custom = { method, classDef ->
        classDef.endsWith("/ProfileViewerApiService;") &&
            method.name == "reportView"
    },
)

internal object TypingStatusSenderFingerprint : Fingerprint(
    custom = { method, classDef ->
        classDef.endsWith("/TypingStatusSenderTimer;") &&
            method.parameterTypes.size == 1 &&
            method.parameterTypes[0] == "Ljava/lang/String;" &&
            method.returnType == "V"
    },
)

internal object ActivityStatusInitFingerprint : Fingerprint(
    custom = { method, classDef ->
        classDef.endsWith("/IMActiveStatusImpl;") &&
            method.name == "LJIILL"
    },
)
