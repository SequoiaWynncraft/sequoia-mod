package dev.lotnest.sequoia.feature.features.guildmessagefilter;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.feature.Feature;
import java.util.regex.Matcher;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class GuildMessageFilterFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        Matcher territoryDefenseMatcher = GuildMessageFilterPatterns.TERRITORY_DEFENSE.matcher(
                event.getStyledText().getString());
        if (territoryDefenseMatcher.find()) {
            String player = territoryDefenseMatcher.group("player");
            String territory = territoryDefenseMatcher.group("territory");
            String defense = territoryDefenseMatcher.group("defense");

            McUtils.sendMessageToClient(Component.literal(
                    "[GuildMessageFilter] Player: " + player + ", Territory: " + territory + ", Defense: " + defense));
        }
    }
}
