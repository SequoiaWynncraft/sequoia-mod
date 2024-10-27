package dev.lotnest.sequoia.feature.features.guildraidtracker;

import java.util.List;

public record GuildRaid(GuildRaidType type, List<String> players, int seasonalRating) {}
