/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixin;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.mc.SharedPanoramaRenderer;
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
        "I have a damage build that relies on heals",
        "16LE to be in next SEQ slander",
        "@Defender BLAK incident",
        "Drastically improved server performance",
        "Waiting for players 1/3",
        "Crazy? I was crazy once",
        "Salted, you owe me 2 dungeon bombs",
        "I am the night",
        "I am the vengeance",
        "4 snakes, 1 meteor",
        "Increasing to 12stx next year",
        "can we honestly e-war?",
        "Did you dodge your aura today?",
        "Spellforged, the #1 Minecraft MMORPG",
        "Bottom text",
        "Sleep = Downtime",
        "SEQueue",
        "seqwawa",
        "llamadile rewarded 1024 Emeralds to llamadile",
        "Who is Blud?",
        "October 14th 2022",
        "Please do not the cat",
        "Lots of Nests!",
        "Network Protocol Error",
        "Vibrant Network Protocol Error for the next 60 challenges",
        "Pan",
        "can someone lend me 17stx",
        "[QZZ] has taken control of Bloody Trail!",
        "[GsW] has taken control of Entrance to Bucie!",
        "I broke into Lotnest's code to say mewo - Ninja",
        "\"tyfr\""
    };

    @Shadow
    @Mutable
    private SplashRenderer splash;

    @Unique
    private final Random random = new Random();

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void renderPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!SequoiaMod.CONFIG.renderSequoiaPanorama()) {
            return;
        }

        SharedPanoramaRenderer.INSTANCE.render(
                guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 1.0F, partialTick);
        ci.cancel();
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void addSequoiaSplashes(CallbackInfo ci) {
        if (!SequoiaMod.CONFIG.renderSequoiaSplashes()) {
            return;
        }

        splash = new SplashRenderer(SPLASHES[random.nextInt(SPLASHES.length)]);
    }
}
