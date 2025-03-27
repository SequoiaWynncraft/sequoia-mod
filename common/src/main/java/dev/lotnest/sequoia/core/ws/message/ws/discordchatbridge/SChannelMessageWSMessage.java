/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.S_CHANNEL_MESSAGE.getValue(), GSON.toJsonTree(data));
    }

    public Data getSChannelMessageData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String username,
            String nickname,
            @SerializedName("display_name") String displayName,
            @SerializedName("sequoia_roles") String[] sequoiaRoles,
            String message,
            OffsetDateTime timestamp) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return username.equals(data.username)
                    && nickname.equals(data.nickname)
                    && displayName.equals(data.displayName)
                    && Arrays.equals(sequoiaRoles, data.sequoiaRoles)
                    && message.equals(data.message)
                    && timestamp.equals(data.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, nickname, displayName, Arrays.hashCode(sequoiaRoles), message, timestamp);
        }

        @Override
        public String toString() {
            return "Data{" + "username='"
                    + username + '\'' + ", nickname='"
                    + nickname + '\'' + ", displayName='"
                    + displayName + '\'' + ", sequoiaRoles="
                    + Arrays.toString(sequoiaRoles) + ", message='"
                    + message + '\'' + ", timestamp="
                    + timestamp + '}';
        }
    }
}
