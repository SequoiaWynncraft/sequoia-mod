package dev.lotnest.sequoia.mixins;

import dev.lotnest.sequoia.manager.managers.CrashReportManager;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    @Inject(
            method = "getDetails(Ljava/lang/StringBuilder;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/SystemReport;appendToCrashReportString(Ljava/lang/StringBuilder;)V"))
    private void addSequoiaDetails(StringBuilder builder, CallbackInfo ci) {
        CrashReportCategory sequoiaCrashDetails = CrashReportManager.generateDetails();
        sequoiaCrashDetails.getDetails(builder);
        builder.append("\n\n");
    }
}
