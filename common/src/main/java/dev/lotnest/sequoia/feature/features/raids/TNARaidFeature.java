package dev.lotnest.sequoia.feature.features.raids;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.PlayerUtils;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TNARaidFeature extends Feature {
    private static final Pattern SHADOWLING_KILLED = Pattern.compile("A Shadowling has been killed! \\[\\d+/3]");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.TNARaidFeature.showShadowlingKilledTitle()) {
            Matcher shadowlingKilledMatcher = SHADOWLING_KILLED.matcher(unfomattedMessage);
            if (shadowlingKilledMatcher.matches()) {
                event.setCanceled(true);
                PlayerUtils.sendTitle(
                        Component.literal("Shadowling killed!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal(shadowlingKilledMatcher.group(1) + "/3")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
