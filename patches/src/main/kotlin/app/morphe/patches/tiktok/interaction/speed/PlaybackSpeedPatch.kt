/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/tiktok/interaction/speed/PlaybackSpeedPatch.kt
 */
package app.morphe.patches.tiktok.interaction.speed

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.shared.GetEnterFromFingerprint
import app.morphe.patches.tiktok.shared.OnRenderFirstFrameFingerprint
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val playbackSpeedPatch = bytecodePatch(
    name = "Playback speed",
    description = "Enables playback-speed controls for all videos and remembers the selected speed between videos.",
    default = true,
) {
    dependsOn(sharedExtensionPatch)

    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        fun resolveSetPlaybackSpeedMethod(): String {
            val matches = mutableListOf<String>()
            classDefForEach { classDef ->
                for (method in classDef.methods) {
                    if (method.returnType != "V") continue
                    val params = method.parameterTypes
                    if (!params.contains("Lcom/ss/android/ugc/aweme/feed/model/Aweme;")) continue
                    if (!params.contains("F")) continue
                    if (method.name.startsWith("<init>")) continue

                    val awemeIdx = params.indexOfFirst { it == "Lcom/ss/android/ugc/aweme/feed/model/Aweme;" }
                    val floatIdx = params.indexOfFirst { it == "F" }
                    if (floatIdx <= awemeIdx) continue

                    val hasLongParam = params.any { it == "J" }
                    if (hasLongParam) continue

                    matches += buildString {
                        append(method.definingClass)
                        append("->")
                        append(method.name)
                        append("(")
                        params.forEach { append(it) }
                        append(")")
                        append(method.returnType)
                    }
                }
            }

            return matches.singleOrNull()
                ?: matches.firstOrNull { it.contains("LJJ(") && it.contains("Aweme;F") }
                ?: throw PatchException("Playback speed: expected one set-speed method, found ${matches.size}: $matches")
        }

        val setPlaybackSpeedMethod = resolveSetPlaybackSpeedMethod()

        GetSpeedFingerprint.method.apply {
            val injectIndex = indexOfFirstInstructionOrThrow { getReference<MethodReference>()?.returnType == "F" } + 2
            val register = getInstruction<OneRegisterInstruction>(injectIndex - 1).registerA

            addInstruction(
                injectIndex,
                "invoke-static { v$register }, " +
                    "Lapp/morphe/extension/tiktok/speed/PlaybackSpeedPatch;->rememberPlaybackSpeed(F)V",
            )
        }

        OnRenderFirstFrameFingerprint.method.addInstructions(
            0,
            """
                invoke-static { p0 }, Lapp/morphe/extension/tiktok/speed/PlaybackSpeedPatch;->applyPlaybackSpeed(Ljava/lang/Object;)V
            """,
        )

        // Kept in Morphe: supported on 46.2.15.
    }
}

