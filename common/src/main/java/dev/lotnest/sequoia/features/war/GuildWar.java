/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.war;

import dev.lotnest.sequoia.models.war.WarModel;

public record GuildWar(int hash, String territory, WarModel.Difficulty difficulty, boolean hasParty) {}
