package app.morphe.patches.tiktok.ghostmode

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.misc.settings.SettingsStatusLoadFingerprint

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

        // Story view: return null if ghost mode is on (blocks /tiktok/story/view/report/v1)
        StoryApiReportViewedFingerprint.methodOrNull?.addInstructionsWithLabels(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->shouldBlockStoryView()Z
                move-result v0
                if-eqz v0, :morphe_ghost_story_off
                const/4 v0, 0x0
                return-object v0
                :morphe_ghost_story_off
                nop
            """,
        )

        // Profile view: return null if ghost mode is on (blocks /tiktok/user/profile/view_record/add/v1)
        ProfileViewerApiReportViewFingerprint.methodOrNull?.addInstructionsWithLabels(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->shouldBlockProfileView()Z
                move-result v0
                if-eqz v0, :morphe_ghost_profile_off
                const/4 v0, 0x0
                return-object v0
                :morphe_ghost_profile_off
                nop
            """,
        )

        // Typing indicator: return early if ghost mode is on
        TypingStatusSenderFingerprint.methodOrNull?.addInstructionsWithLabels(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->shouldBlockTypingStatus()Z
                move-result v0
                if-eqz v0, :morphe_ghost_typing_off
                return-void
                :morphe_ghost_typing_off
                nop
            """,
        )

        // Presence: return early if ghost mode is on (prevents online status initialization)
        ActivityStatusInitFingerprint.methodOrNull?.addInstructionsWithLabels(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->shouldBlockPresence()Z
                move-result v0
                if-eqz v0, :morphe_ghost_presence_off
                return-void
                :morphe_ghost_presence_off
                nop
            """,
        )
    }
}
