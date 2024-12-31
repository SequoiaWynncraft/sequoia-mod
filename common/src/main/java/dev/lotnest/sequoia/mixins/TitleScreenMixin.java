package dev.lotnest.sequoia.mixins;

import dev.lotnest.sequoia.minecraft.SharedPanoramaRenderer;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Unique
    private static final String[] SPLASHES = {
        "Cutting Sequoia trees in Fruma since 3045",
        "SEQ RAT mod",
        "dotJJ is a catgirl",
        "Join Wynncraft today!",
        "discord.gg/seq",
        "Run TCC with Mehku",
        "NOL 0/4",
        "if (connectedToWynncraft) { lag(); } // or crash()",
        "catgirl aura dodge?",
        "lf defective Guardian in HR",
        "Mine Base Plains defense is Very High",
        "Join SEQ",
        "Current queue size - 12 players",
        "pagoroni downtime count - " + Integer.MAX_VALUE,
        "nice elder change guys",
        "/seq meow",
        "meow",
        "99% gamblers quit before hitting it big",
        "I have a damage build that relies on heals"
    };

    @Shadow
    @Mutable
    private SplashRenderer splash;

    @Unique
    private final Random random = new Random();

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void renderPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        SharedPanoramaRenderer.INSTANCE.render(
                guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 1.0F, partialTick);
        ci.cancel();
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void addSequoiaSplashes(CallbackInfo ci) {
        splash = new SplashRenderer(SPLASHES[random.nextInt(SPLASHES.length)]);
    }
}
