package dev.lotnest.sequoia.neoforge;

import com.wynntils.core.WynntilsMod;
import com.wynntils.mc.event.TitleScreenInitEvent;
import dev.lotnest.sequoia.SequoiaMod;
import java.io.File;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(SequoiaMod.MOD_ID)
public class SequoiaModNeoForge {
    public SequoiaModNeoForge() {
        Path path = ModLoadingContext.get()
                .getActiveContainer()
                .getModInfo()
                .getOwningFile()
                .getFile()
                .getFilePath();

        File modFile = new File(path.toUri());

        WynntilsMod.init(
                WynntilsMod.ModLoader.FORGE,
                ModLoadingContext.get()
                        .getActiveContainer()
                        .getModInfo()
                        .getVersion()
                        .toString(),
                !FMLEnvironment.production,
                modFile);

        WynntilsMod.registerEventListener(this);
    }

    // This is slightly hacky to do this, but it works
    @SubscribeEvent
    public void onClientLoad(TitleScreenInitEvent.Pre event) {
        // Enable stencil support
        Minecraft.getInstance().getMainRenderTarget().enableStencil();

        WynntilsMod.unregisterEventListener(this);
    }
}