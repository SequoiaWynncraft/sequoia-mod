/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.feature.features.guildraidtracker;

import java.util.List;
import java.util.UUID;

public record GuildRaid(
        RaidType type, List<String> players, UUID reporterID, int aspects, int emeralds, int xp, int sr) {}
