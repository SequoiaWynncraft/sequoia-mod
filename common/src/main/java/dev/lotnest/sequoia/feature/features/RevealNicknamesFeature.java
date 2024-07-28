package dev.lotnest.sequoia.feature.features;

import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.ClientsideMessageEvent;
import com.wynntils.utils.type.IterationDecision;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Category(CategoryType.CHAT)
public class RevealNicknamesFeature extends Feature {
    public static final Pattern NICKNAME_REGEX = Pattern.compile("(?<nickname>.+)'s real username is (?<username>.+)");

    // TODO: Implement a config option for this
    private static boolean replaceNicknames = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ChatMessageReceivedEvent event) {
        event.setMessage(revealNickname(event.getStyledText()).getComponent());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientsideMessage(ClientsideMessageEvent event) {
        event.setMessage(revealNickname(event.getStyledText()).getComponent());
    }

    public static StyledText revealNickname(StyledText text) {
        return text.iterate((next, changes) -> {
            HoverEvent hoverEvent = next.getPartStyle().getStyle().getHoverEvent();

            if (hoverEvent != null
                    && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT
                    && next.getPartStyle().isItalic()) {
                for (StyledText nicknameText : StyledText.fromComponent(
                                hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT))
                        .split("\n")) {
                    Matcher nicknameMatcher = nicknameText.getMatcher(NICKNAME_REGEX, PartStyle.StyleType.NONE);
                    if (!nicknameMatcher.matches()) continue;

                    String username = nicknameMatcher.group("username");

                    if (replaceNicknames) changes.clear();

                    changes.add(new StyledTextPart(
                            replaceNicknames ? username : " (" + username + ")",
                            (replaceNicknames
                                            ? next.getPartStyle().getStyle()
                                            : Style.EMPTY.withColor(ChatFormatting.RED))
                                    .withItalic(false),
                            null,
                            next.getPartStyle().getStyle()));

                    return IterationDecision.CONTINUE;
                }
            }

            return IterationDecision.CONTINUE;
        });
    }
}
