/*
 * Thanks to lyyako for the original implementation and help with this patch.
 *
 * TikTok 46.2.15 adaptation:
 * https://github.com/icysymmetra/tiktok-patches-for-morphe
 */
package app.morphe.patches.tiktok.interaction.publishdate

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patches.tiktok.misc.extension.sharedExtensionPatch
import app.morphe.patches.tiktok.misc.settings.SettingsStatusLoadFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/tiktok/publishdate/AlwaysShowPublishDatePatch;"

@Suppress("unused")
val alwaysShowPublishDatePatch = bytecodePatch(
    name = "Always show publish date",
    description = "Always shows the publish date in video author information. Thanks to lyyako for the original implementation.",
    default = true,
) {
    dependsOn(sharedExtensionPatch)

    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        SettingsStatusLoadFingerprint.method.addInstruction(
            0,
            "invoke-static {}, " +
                "Lapp/morphe/extension/tiktok/settings/SettingsStatus;->enableAlwaysShowPublishDate()V",
        )

        VideoAuthorInfoStateFingerprint.method.showPostTimeForMainFeeds()
    }
}

private fun app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.showPostTimeForMainFeeds() {
    val instructions = implementation!!.instructions

    val gateCallIndices = instructions.withIndex()
        .filter { (index, instruction) ->
            index < instructions.size - 1 &&
            instruction.isStaticStringBooleanCall() &&
            getInstruction(index + 1).opcode == Opcode.MOVE_RESULT
        }
        .map { it.index }

    check(gateCallIndices.isNotEmpty()) {
        "Could not find video author post-time visibility gates"
    }

    gateCallIndices.asReversed().forEach { index ->
        val resultRegister = getInstruction<OneRegisterInstruction>(index + 1).registerA
        addInstructions(
            index + 2,
            """
                invoke-static/range {v$resultRegister .. v$resultRegister}, $EXTENSION_CLASS_DESCRIPTOR->showPostTimeForMainFeeds(Z)Z
                move-result v$resultRegister
            """,
        )
    }
}

private fun Any.isStaticStringBooleanCall(): Boolean {
    if ((this as? com.android.tools.smali.dexlib2.iface.instruction.Instruction)?.opcode != Opcode.INVOKE_STATIC) {
        return false
    }

    val reference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return false
    return reference.returnType == "Z" &&
        reference.parameterTypes == listOf("Ljava/lang/String;")
}
