/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.istateopcodes;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.core.ws.message.IStateOpCode;
import dev.lotnest.sequoia.core.ws.type.IStateOpCodeType;

public class LocationServiceDataIStateOpCode extends IStateOpCode {
    public LocationServiceDataIStateOpCode(Data data) {
        super(IStateOpCodeType.LOCATION_SERVICE.getValue(), GSON.toJsonTree(data));
    }

    public record Data(int x, int z, String server) {}
}
