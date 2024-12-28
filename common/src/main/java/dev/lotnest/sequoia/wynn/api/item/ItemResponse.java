package dev.lotnest.sequoia.wynn.api.item;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public record ItemResponse(
        @SerializedName("internalName") String internalName,
        @SerializedName("type") String type,
        @SerializedName("subType") String subType,
        @SerializedName("icon") Icon icon,
        @SerializedName("identifier") Boolean identifier,
        @SerializedName("allow_craftsman") Boolean allowCraftsman,
        @SerializedName("armourMaterial") String armourMaterial,
        @SerializedName("attackSpeed") String attackSpeed,
        @SerializedName("averageDps") Integer averageDPS,
        @SerializedName("gatheringSpeed") Integer gatheringSpeed,
        @SerializedName("tier") String tier,
        @SerializedName("rarity") String rarity,
        @SerializedName("consumableOnlyIDs") ConsumableOnlyIDs consumableOnlyIDs,
        @SerializedName("ingredientPositionModifiers") IngredientPositionModifiers ingredientPositionModifiers,
        @SerializedName("itemOnlyIDs") ItemOnlyIDs itemOnlyIDs,
        @SerializedName("majorIds") Map<String, String> majorIds,
        @SerializedName("craftable") List<String> craftable,
        @SerializedName("powderSlots") Integer powderSlots,
        @SerializedName("lore") String lore,
        @SerializedName("dropRestriction") String dropRestriction,
        @SerializedName("restriction") String restriction,
        @SerializedName("raidReward") Boolean raidReward,
        @SerializedName("dropMeta") DropMeta dropMeta,
        @SerializedName("base") Base base,
        @SerializedName("requirements") Requirements requirements,
        @SerializedName("identifications") Map<String, Identification> identifications) {
    public record Icon(@SerializedName("value") Object value, @SerializedName("format") String format) {}

    public record ConsumableOnlyIDs(
            @SerializedName("duration") Integer duration, @SerializedName("charges") Integer charges) {}

    public record IngredientPositionModifiers(
            @SerializedName("left") Integer left,
            @SerializedName("right") Integer right,
            @SerializedName("above") Integer above,
            @SerializedName("under") Integer under,
            @SerializedName("touching") Integer touching,
            @SerializedName("not_touching") Integer notTouching) {}

    public record ItemOnlyIDs(
            @SerializedName("durability_modifier") Integer durabilityModifier,
            @SerializedName("strength_requirement") Integer strengthRequirement,
            @SerializedName("dexterity_requirement") Integer dexterityRequirement,
            @SerializedName("intelligence_requirement") Integer intelligenceRequirement,
            @SerializedName("defence_requirement") Integer defenceRequirement,
            @SerializedName("agility_requirement") Integer agilityRequirement) {}

    public record DropMeta(
            @SerializedName("coordinates") List<Integer> coordinates,
            @SerializedName("name") String name,
            @SerializedName("type") String type) {}

    public record Base(@SerializedName("baseDamage") BaseDamage baseDamage) {
        public record BaseDamage(
                @SerializedName("min") Integer min,
                @SerializedName("max") Integer max,
                @SerializedName("raw") Integer raw) {}
    }

    public record Requirements(
            @SerializedName("level") Integer level,
            @SerializedName("levelRange") LevelRange levelRange,
            @SerializedName("strength") Integer strength,
            @SerializedName("dexterity") Integer dexterity,
            @SerializedName("intelligence") Integer intelligence,
            @SerializedName("defence") Integer defence,
            @SerializedName("agility") Integer agility,
            @SerializedName("quest") String quest,
            @SerializedName("classRequirement") String classRequirement,
            @SerializedName("skills") List<String> skills) {
        public record LevelRange(@SerializedName("min") Integer min, @SerializedName("max") Integer max) {}
    }

    public record Identification(
            @SerializedName("raw") Integer rawIdentification,
            @SerializedName("min") Integer min,
            @SerializedName("max") Integer max) {}
}
