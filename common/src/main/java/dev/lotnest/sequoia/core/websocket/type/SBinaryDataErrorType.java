/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.type;

public enum SBinaryDataErrorType {
    INVALID,
    BAD_HASH,
    BAD_LENGTH,
    BAD_OFFSET,
    BAD_SEQUENCE,
    BAD_TOTAL_HASH,
    BAD_TRANSFER_ID,
    BAD_TRANSFER_TYPE
}
