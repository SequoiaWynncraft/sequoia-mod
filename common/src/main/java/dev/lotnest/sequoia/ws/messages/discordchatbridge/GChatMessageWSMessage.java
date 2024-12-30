package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.GChatMessage.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return GSON.fromJson(getData(), GChatMessageWSMessage.Data.class);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
    }

    public record Data(String username, String nickname, String message, String timestamp, String clientName) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return new EqualsBuilder()
                    .append(username(), data.username())
                    .append(nickname(), data.nickname())
                    .append(message(), data.message())
                    .append(timestamp(), data.timestamp())
                    .append(clientName(), data.clientName())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(username())
                    .append(nickname())
                    .append(message())
                    .append(timestamp())
                    .append(clientName())
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("username", username)
                    .append("nickname", nickname)
                    .append("message", message)
                    .append("timestamp", timestamp)
                    .append("clientName", clientName)
                    .toString();
        }
    }
}
