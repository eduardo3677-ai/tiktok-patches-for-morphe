package app.morphe.patches.tiktok.ghostmode

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.misc.settings.SettingsStatusLoadFingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.util.returnEarly

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/tiktok/ghostmode/GhostMode;"

@Suppress("unused")
val ghostModePatch = bytecodePatch(
    name = "Ghost mode",
    description = "Blocks story view notifications, profile view notifications, typing indicators, and forces offline presence status.",
    default = true,
) {
    dependsOn(sharedExtensionPatch)

    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        SettingsStatusLoadFingerprint.method.addInstruction(
            0,
            "invoke-static {}, Lapp/morphe/extension/tiktok/settings/SettingsStatus;->enableGhostMode()V",
        )

        StoryApiReportViewedFingerprint.methodOrNull?.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return-object v0
            """,
        )

        ProfileViewerApiReportViewFingerprint.methodOrNull?.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return-object v0
            """,
        )

        TypingStatusSenderFingerprint.methodOrNull?.returnEarly()

        ActivityStatusInitFingerprint.methodOrNull?.addInstructions(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->forceOfflinePresence()V
            """,
        )
    }
}
