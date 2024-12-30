package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.SChannelMessage.getValue(), GSON.toJsonTree(data));
    }

    public Data getChannelMessageData() {
        return GSON.fromJson(getData(), Data.class);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
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
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return new EqualsBuilder()
                    .append(username(), data.username())
                    .append(nickname(), data.nickname())
                    .append(displayName(), data.displayName())
                    .append(sequoiaRoles(), data.sequoiaRoles())
                    .append(message(), data.message())
                    .append(timestamp(), data.timestamp())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(username())
                    .append(nickname())
                    .append(displayName())
                    .append(sequoiaRoles())
                    .append(message())
                    .append(timestamp())
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("username", username)
                    .append("nickname", nickname)
                    .append("displayName", displayName)
                    .append("sequoiaRoles", sequoiaRoles)
                    .append("message", message)
                    .append("timestamp", timestamp)
                    .toString();
        }
    }
}
