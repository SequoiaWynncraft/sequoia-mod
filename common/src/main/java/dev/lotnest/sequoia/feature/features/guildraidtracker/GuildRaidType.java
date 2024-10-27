package dev.lotnest.sequoia.feature.features.guildraidtracker;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public enum GuildRaidType {
    THE_CANYON_COLOSSUS("The Canyon Colossus", "TCC"),
    THE_NAMELESS_ANOMALY("The Nameless Anomaly", "TNA"),
    ORPHIONS_NEXUS_OF_LIGHT("Orphion's Nexus of Light", "NOL"),
    NEST_OF_THE_GROOTSLANGS("Nest of the Grootslangs", "NOG");

    private static final Map<String, GuildRaidType> NAME_TO_TYPE_MAP = Maps.newHashMap();

    static {
        for (GuildRaidType type : GuildRaidType.values()) {
            NAME_TO_TYPE_MAP.put(type.getName().toLowerCase(Locale.ROOT), type);
            NAME_TO_TYPE_MAP.put(type.getShortenedName().toLowerCase(Locale.ROOT), type);
        }
    }

    private final String name;
    private final String shortenedName;

    GuildRaidType(String name, String shortenedName) {
        this.name = name;
        this.shortenedName = shortenedName;
    }

    public static GuildRaidType fromString(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return NAME_TO_TYPE_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    public String getName() {
        return name;
    }

    public String getShortenedName() {
        return shortenedName;
    }

    @Override
    public String toString() {
        return name;
    }
}
