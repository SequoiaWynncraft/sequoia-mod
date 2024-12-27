package dev.lotnest.sequoia.feature.features.lootpool;

import com.google.gson.annotations.SerializedName;
import com.wynntils.models.items.WynnItem;

public record LootPoolItem(@SerializedName("wynn_item") WynnItem wynnItem) {}
