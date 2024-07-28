package dev.lotnest.sequoia.forge;

import com.wynntils.core.WynntilsMod;
import com.wynntils.mc.event.TitleScreenInitEvent;
import dev.lotnest.sequoia.SequoiaMod;
import java.io.File;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SequoiaMod.MOD_ID)
public class SequoiaModForge {
    public SequoiaModForge() {
        Path path = ModLoadingContext.get()
                .getActiveContainer()
                .getModInfo()
                .getOwningFile()
                .getFile()
                .getFilePath();

        File modFile = new File(path.toUri());

        SequoiaMod.init(
                SequoiaMod.ModLoader.FORGE,
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
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientLoad(TitleScreenInitEvent.Pre event) {
        // Enable stencil support
        Minecraft.getInstance().getMainRenderTarget().enableStencil();

        WynntilsMod.unregisterEventListener(this);
    }
}
