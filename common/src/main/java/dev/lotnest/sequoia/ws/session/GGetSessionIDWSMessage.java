package dev.lotnest.sequoia.ws.session;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GGetSessionIDWSMessage extends WSMessage {
    public GGetSessionIDWSMessage(Data data) {
        super(WSMessageType.GGetSessionID.getValue(), data);
    }

    public Data getData() {
        return (Data) super.getData();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
    }

    public record Data(String username, String uuid, String clientHash, String timestamp) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            GGetSessionIDWSMessage.Data data = (GGetSessionIDWSMessage.Data) o;

            return new EqualsBuilder()
                    .append(username, data.username)
                    .append(uuid, data.uuid)
                    .append(clientHash, data.clientHash)
                    .append(timestamp, data.timestamp)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(username)
                    .append(uuid)
                    .append(clientHash)
                    .append(timestamp)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("username", username)
                    .append("uuid", uuid)
                    .append("clientHash", clientHash)
                    .append("timestamp", timestamp)
                    .toString();
        }
    }
}
