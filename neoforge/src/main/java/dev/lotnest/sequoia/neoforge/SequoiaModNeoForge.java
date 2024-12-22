package dev.lotnest.sequoia.neoforge;

import com.wynntils.core.WynntilsMod;
import com.wynntils.mc.event.TitleScreenInitEvent;
import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(SequoiaMod.MOD_ID)
public class SequoiaModNeoForge {
    public SequoiaModNeoForge() {
        SequoiaMod.init(
                SequoiaMod.ModLoader.FORGE,
                !FMLEnvironment.production,
                ModLoadingContext.get()
                        .getActiveContainer()
                        .getModInfo()
                        .getVersion()
                        .toString());

        WynntilsMod.registerEventListener(this);
    }

    @SubscribeEvent
    public void onClientLoad(TitleScreenInitEvent.Pre event) {
        Minecraft.getInstance().getMainRenderTarget().enableStencil();

        WynntilsMod.unregisterEventListener(this);
    }
}
