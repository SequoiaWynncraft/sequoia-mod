/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixin.accessors;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Options.class})
public interface OptionsAccessor {
    @Accessor("keyMappings")
    @Mutable
    void setKeyBindMixins(KeyMapping[] var1);
}
