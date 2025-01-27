/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.wynn.api.item;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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

    public Component toComponent() {
        MutableComponent result = Component.empty();

        if (type != null) {
            result = result.append(Component.literal("Type: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(type).withStyle(ChatFormatting.YELLOW));
        }

        if (subType != null) {
            result = result.append(Component.literal("\nSubtype: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(subType).withStyle(ChatFormatting.YELLOW));
        }

        if (identifier != null) {
            result = result.append(Component.literal("\nIdentifier: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(identifier.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (allowCraftsman != null) {
            result = result.append(Component.literal("\nAllow Craftsman: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(allowCraftsman.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (armourMaterial != null) {
            result = result.append(Component.literal("\nArmour Material: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(armourMaterial).withStyle(ChatFormatting.YELLOW));
        }

        if (attackSpeed != null) {
            result = result.append(Component.literal("\nAttack Speed: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(attackSpeed).withStyle(ChatFormatting.YELLOW));
        }

        if (averageDPS != null) {
            result = result.append(Component.literal("\nAverage DPS: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(averageDPS.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (gatheringSpeed != null) {
            result = result.append(Component.literal("\nGathering Speed: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(gatheringSpeed.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (tier != null) {
            result = result.append(Component.literal("\nTier: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(tier).withStyle(ChatFormatting.YELLOW));
        }

        if (rarity != null) {
            result = result.append(Component.literal("\nRarity: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(rarity).withStyle(ChatFormatting.YELLOW));
        }

        if (consumableOnlyIDs != null) {
            result = result.append(Component.literal("\nConsumable Only IDs: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(consumableOnlyIDs.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (ingredientPositionModifiers != null) {
            result = result.append(Component.literal("\nIngredient Position Modifiers: ")
                            .withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(ingredientPositionModifiers.toString())
                            .withStyle(ChatFormatting.YELLOW));
        }

        if (itemOnlyIDs != null) {
            result = result.append(Component.literal("\nItem Only IDs: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(itemOnlyIDs.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (majorIds != null) {
            result = result.append(Component.literal("\nMajor IDs: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(majorIds.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (craftable != null) {
            result = result.append(Component.literal("\nCraftable: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(craftable.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (powderSlots != null) {
            result = result.append(Component.literal("\nPowder Slots: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(powderSlots.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (lore != null) {
            result = result.append(Component.literal("\nLore: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(lore).withStyle(ChatFormatting.YELLOW));
        }

        if (dropRestriction != null) {
            result = result.append(Component.literal("\nDrop Restriction: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(dropRestriction).withStyle(ChatFormatting.YELLOW));
        }

        if (restriction != null) {
            result = result.append(Component.literal("\nRestriction: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(restriction).withStyle(ChatFormatting.YELLOW));
        }

        if (raidReward != null) {
            result = result.append(Component.literal("\nRaid Reward: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(raidReward.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (dropMeta != null) {
            result = result.append(Component.literal("\nDrop Meta: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(dropMeta.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (base != null) {
            result = result.append(Component.literal("\nBase: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(base.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (requirements != null) {
            result = result.append(Component.literal("\nRequirements: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(requirements.toString()).withStyle(ChatFormatting.YELLOW));
        }

        if (identifications != null) {
            result = result.append(Component.literal("\nIdentifications: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(identifications.toString()).withStyle(ChatFormatting.YELLOW));
        }

        return result;
    }
}
