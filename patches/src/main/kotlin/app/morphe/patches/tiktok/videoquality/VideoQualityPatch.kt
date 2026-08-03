package app.morphe.patches.tiktok.videoquality

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.misc.settings.SettingsStatusLoadFingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/tiktok/videoquality/VideoQualityPatch;"

@Suppress("unused")
val videoQualityPatch = bytecodePatch(
    name = "Video quality",
    description = "Forces a selected video quality level for all videos in the feed.",
    default = true,
) {
    dependsOn(sharedExtensionPatch)

    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        SettingsStatusLoadFingerprint.method.addInstruction(
            0,
            "invoke-static {}, Lapp/morphe/extension/tiktok/settings/SettingsStatus;->enableVideoQuality()V",
        )

        VideoGetBitRateFingerprint.method.addInstructions(
            0,
            """
                invoke-static {p0}, $EXTENSION_CLASS_DESCRIPTOR->filterBitRates(Lcom/ss/android/ugc/aweme/feed/model/Video;)Ljava/util/List;
                move-result-object v0
                return-object v0
            """,
        )
    }
}
