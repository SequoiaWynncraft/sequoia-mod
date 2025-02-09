/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.war;

import dev.lotnest.sequoia.models.War.WarPartyModel;
import java.util.Map;

public record GuildWarParty(int hash, String partyLeader, String territory, Map<String, WarPartyModel.Role> members) {}
