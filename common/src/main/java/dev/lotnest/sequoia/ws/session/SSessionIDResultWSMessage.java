package dev.lotnest.sequoia.ws.session;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SSessionIDResultWSMessage extends WSMessage {
    public SSessionIDResultWSMessage(Data data) {
        super(WSMessageType.SSessionIDResult.getValue(), data);
    }

    public Data getData() {
        return (Data) super.getData();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
    }

    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            SSessionIDResultWSMessage.Data data = (SSessionIDResultWSMessage.Data) o;

            return new EqualsBuilder()
                    .append(error, data.error)
                    .append(result, data.result)
                    .append(expireAt, data.expireAt)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(error)
                    .append(result)
                    .append(expireAt)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("error", error)
                    .append("result", result)
                    .append("expireAt", expireAt)
                    .toString();
        }
    }
}
