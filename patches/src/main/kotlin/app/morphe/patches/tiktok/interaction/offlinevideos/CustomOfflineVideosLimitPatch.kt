package app.morphe.patches.tiktok.interaction.offlinevideos

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import app.morphe.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val CUSTOM_OFFLINE_VIDEO_LIMIT = 500
private const val CUSTOM_OFFLINE_VIDEO_LIMIT_SMALI = "0x1f4"
private const val CUSTOM_OFFLINE_VIDEOS_HELPER =
    "Lapp/morphe/extension/tiktok/offline/CustomOfflineVideosLimitPatch;"

@Suppress("unused")
val customOfflineVideosLimitPatch = bytecodePatch(
    name = "Custom offline videos limit",
    description = "Adds a custom entry to TikTok's offline videos menu with a configurable limit of up to 500 videos.",
    default = true,
) {
    compatibleWith(*AppCompatibilities.tiktok46215())

    execute {
        OfflineModeSheetOptionsFingerprint.methodOrNull?.apply {
            try {
                val freezeListIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.INVOKE_STATIC &&
                        getReference<MethodReference>()?.let { reference ->
                            reference.parameterTypes == listOf("[Ljava/lang/Object;") &&
                                reference.returnType == "Ljava/util/List;"
                        } == true
                }
                val moveResultIndex = indexOfFirstInstructionOrThrow(freezeListIndex + 1) {
                    opcode == Opcode.MOVE_RESULT_OBJECT
                }
                val optionsRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA

                addInstructions(
                    moveResultIndex + 1,
                    """
                        invoke-static {v$optionsRegister}, $CUSTOM_OFFLINE_VIDEOS_HELPER->getOfflineVideoOptions(Ljava/util/List;)Ljava/util/List;
                        move-result-object v$optionsRegister
                    """,
                )
            } catch (e: Exception) {
                // Pattern not found in this version — skip.
            }
        }

        OfflineModeListConstructorFingerprint.methodOrNull?.apply {
            try {
                val capFieldWriteIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.IPUT &&
                        getReference<FieldReference>()?.let { field ->
                            field.definingClass.endsWith("/OfflineModeListVM;") &&
                                field.type == "I"
                        } == true
                }
                val capRegister = getInstruction<Instruction22c>(capFieldWriteIndex).registerA
                val capLiteralIndex = indexOfFirstInstructionReversedOrThrow(capFieldWriteIndex - 1) {
                    this is NarrowLiteralInstruction &&
                        this is OneRegisterInstruction &&
                        registerA == capRegister
                }

                replaceInstruction(capLiteralIndex, "const/16 v$capRegister, $CUSTOM_OFFLINE_VIDEO_LIMIT_SMALI")
            } catch (e: Exception) {
                // Pattern not found in this version — skip.
            }
        }

        OfflineModeOptionConfigFingerprint.methodOrNull?.apply {
            fun postProcessOptionsField() {
                val fieldWriteIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.SPUT_OBJECT &&
                        getReference<FieldReference>()?.type == "Ljava/util/List;"
                }
                val moveResultIndex = indexOfFirstInstructionReversedOrThrow(fieldWriteIndex - 1) {
                    opcode == Opcode.MOVE_RESULT_OBJECT
                }
                val optionsRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA

                addInstructions(
                    moveResultIndex + 1,
                    """
                        invoke-static {v$optionsRegister}, $CUSTOM_OFFLINE_VIDEOS_HELPER->getOfflineVideoOptions(Ljava/util/List;)Ljava/util/List;
                        move-result-object v$optionsRegister
                    """,
                )
            }

            try { postProcessOptionsField() } catch (e: Exception) {}
        }

        OfflineModeOptionEnumFingerprint.methodOrNull?.apply {
            try {
                val customEnumSizeLiteralIndex = indexOfFirstInstructionOrThrow {
                    this is NarrowLiteralInstruction &&
                        this is OneRegisterInstruction &&
                        registerA == 15 &&
                        narrowLiteral == CUSTOM_OFFLINE_VIDEO_LIMIT
                }
                val customEnumConstructorIndex = indexOfFirstInstructionOrThrow(customEnumSizeLiteralIndex + 1) {
                    (opcode == Opcode.INVOKE_DIRECT || opcode == Opcode.INVOKE_DIRECT_RANGE) &&
                        getReference<MethodReference>()?.let { reference ->
                            reference.definingClass.endsWith("/OfflineModeSheetPageAssem;") &&
                                reference.name == "<init>" &&
                                reference.returnType == "V" &&
                                reference.parameterTypes.size == 5
                        } == true
                }
                addInstructions(
                    customEnumConstructorIndex,
                    """
                        invoke-static {v13}, $CUSTOM_OFFLINE_VIDEOS_HELPER->getCustomOfflineVideoLimitOrOriginal(I)I
                        move-result v13
                        invoke-static {v14}, $CUSTOM_OFFLINE_VIDEOS_HELPER->getCustomOfflineVideoMinutesOrOriginal(I)I
                        move-result v14
                        invoke-static {v15}, $CUSTOM_OFFLINE_VIDEOS_HELPER->getCustomOfflineVideoSizeMbOrOriginal(I)I
                        move-result v15
                    """,
                )
            } catch (e: Exception) {
                // Enum constructor pattern not found in this version — skip.
            }
        }
    }
}
