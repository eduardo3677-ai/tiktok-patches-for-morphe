/*
 * Copyright 2026 icysymmetra/tiktok-patches-for-morphe contributors
 * https://github.com/icysymmetra/tiktok-patches-for-morphe
 */
package app.morphe.patches.tiktok.promobanners

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.misc.settings.SettingsStatusLoadFingerprint
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

private const val FEATURE_CONTROLS_CLASS_DESCRIPTOR = "Lapp/morphe/extension/tiktok/featurecontrols/FeatureControls;"

private object TouchPointPendantParserFingerprint : Fingerprint(
    returnType = "V",
    parameters = listOf("Ljava/util/HashMap;", "Ljava/util/List;", "Z"),
    custom = { method, _ ->
        val referencedTypes = method.implementation?.instructions
            ?.mapNotNull { it.getReference<TypeReference>()?.type }
            ?.toSet()
            ?: emptySet()
        referencedTypes.any { it.endsWith("/touchpoint/api/model/NormalPendant;") || it.endsWith("/touchpoint/api/model/l;") } &&
            referencedTypes.any { it.endsWith("/TimerPendant;") } &&
            referencedTypes.any { it.endsWith("/PendantViewModel;") || it.endsWith("/notify/l;") }
    },
)

@Suppress("unused")
val hideFloatingPromotionsPatch = bytecodePatch(
    name = "Hide floating promotions",
    description = "Removes floating promotional badges, coin icons, and timer banners from the Home feed.",
    default = true,
) {
    dependsOn(sharedExtensionPatch)
    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        SettingsStatusLoadFingerprint.method.addInstruction(
            0,
            "invoke-static {}, Lapp/morphe/extension/tiktok/settings/SettingsStatus;->enablePromotionalBanners()V",
        )

        TouchPointPendantParserFingerprint.methodOrNull?.let { method ->
            val parseIndex = method.indexOfFirstInstructionOrThrow {
                getReference<MethodReference>()?.let { reference ->
                    reference.parameterTypes == listOf("Ljava/lang/String;", "Ljava/lang/Class;") &&
                        reference.returnType == "Ljava/lang/Object;"
                } == true
            }
            val resultRegister = method.getInstruction<OneRegisterInstruction>(parseIndex + 1).registerA
            method.addInstructions(
                parseIndex + 2,
                """
                    invoke-static {v$resultRegister}, $FEATURE_CONTROLS_CLASS_DESCRIPTOR->filterNormalPendant(Ljava/lang/Object;)Ljava/lang/Object;
                    move-result-object v$resultRegister
                """,
            )
        }
    }
}
