package dev.lotnest.sequoia.wynn.api.guild;

import dev.lotnest.sequoia.SequoiaMod;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class GuildResponse {
    private String uuid;
    private String name;
    private String prefix;
    private int level;
    private int xpPercent;
    private int territories;
    private int wars;
    private String created;
    private Members members;
    private int online;
    private Banner banner;
    private Map<String, SeasonRank> seasonRanks;

    public static class Members {
        private int total;
        private Map<String, MemberDetails> owner;
        private Map<String, MemberDetails> chief;
        private Map<String, MemberDetails> strategist;
        private Map<String, MemberDetails> captain;
        private Map<String, MemberDetails> recruiter;
        private Map<String, MemberDetails> recruit;

        public static class MemberDetails {
            private String username;
            private boolean online;
            private String server;
            private long contributed;
            private int guildRank;
            private String joined;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public boolean isOnline() {
                return online;
            }

            public void setOnline(boolean online) {
                this.online = online;
            }

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public long getContributed() {
                return contributed;
            }

            public void setContributed(long contributed) {
                this.contributed = contributed;
            }

            public int getGuildRank() {
                return guildRank;
            }

            public void setGuildRank(int guildRank) {
                this.guildRank = guildRank;
            }

            public String getJoined() {
                return joined;
            }

            public void setJoined(String joined) {
                this.joined = joined;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                MemberDetails that = (MemberDetails) o;
                return isOnline() == that.isOnline()
                        && getContributed() == that.getContributed()
                        && getGuildRank() == that.getGuildRank()
                        && Objects.equals(getUsername(), that.getUsername())
                        && Objects.equals(getServer(), that.getServer())
                        && Objects.equals(getJoined(), that.getJoined());
            }

            @Override
            public int hashCode() {
                return Objects.hash(
                        getUsername(), isOnline(), getServer(), getContributed(), getGuildRank(), getJoined());
            }

            @Override
            public String toString() {
                return "MemberDetails{" + "username='"
                        + username + '\'' + ", online="
                        + online + ", server='"
                        + server + '\'' + ", contributed="
                        + contributed + ", guildRank="
                        + guildRank + ", joined='"
                        + joined + '\'' + '}';
            }
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public Map<String, MemberDetails> getOwner() {
            return owner;
        }

        public void setOwner(Map<String, MemberDetails> owner) {
            this.owner = owner;
        }

        public Map<String, MemberDetails> getChief() {
            return chief;
        }

        public void setChief(Map<String, MemberDetails> chief) {
            this.chief = chief;
        }

        public Map<String, MemberDetails> getStrategist() {
            return strategist;
        }

        public void setStrategist(Map<String, MemberDetails> strategist) {
            this.strategist = strategist;
        }

        public Map<String, MemberDetails> getCaptain() {
            return captain;
        }

        public void setCaptain(Map<String, MemberDetails> captain) {
            this.captain = captain;
        }

        public Map<String, MemberDetails> getRecruiter() {
            return recruiter;
        }

        public void setRecruiter(Map<String, MemberDetails> recruiter) {
            this.recruiter = recruiter;
        }

        public Map<String, MemberDetails> getRecruit() {
            return recruit;
        }

        public void setRecruit(Map<String, MemberDetails> recruit) {
            this.recruit = recruit;
        }

        public MutableComponent toPrettyMessage() {
            MutableComponent result = Component.empty();

            if (total == 0) {
                return result.append(Component.translatable("sequoia.command.onlineMembers.noGuildMembersFound"));
            }

            BiConsumer<String, Map<String, MemberDetails>> appendRank = (rankName, rankMembers) -> {
                if (!rankMembers.isEmpty()) {
                    result.append(Component.translatable(rankName).withStyle(ChatFormatting.DARK_GREEN))
                            .append(Component.literal(": ").withStyle(ChatFormatting.GREEN));

                    List<MutableComponent> members = rankMembers.entrySet().stream()
                            .map(entry -> {
                                MutableComponent usernameComponent = Component.literal(entry.getKey())
                                        .withStyle(ChatFormatting.AQUA)
                                        .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                                        HoverEvent.Action.SHOW_TEXT,
                                                        Component.translatable(
                                                                "sequoia.tooltip.clickToPrivateMessage",
                                                                entry.getKey())))
                                                .withClickEvent(new ClickEvent(
                                                        ClickEvent.Action.SUGGEST_COMMAND,
                                                        "/msg " + entry.getKey() + " ")));

                                return usernameComponent
                                        .append(Component.literal(" ").withStyle(ChatFormatting.GRAY))
                                        .append(Component.literal(
                                                        "(" + entry.getValue().getServer() + ")")
                                                .withStyle(ChatFormatting.GRAY));
                            })
                            .toList();

                    for (int i = 0; i < members.size(); i++) {
                        result.append(members.get(i));
                        if (i < members.size() - 1) {
                            result.append(Component.literal(", ").withStyle(ChatFormatting.GREEN));
                        }
                    }

                    result.append(Component.literal("\n"));
                }
            };

            appendRank.accept("wynncraft.guild.rank.owner", owner);
            appendRank.accept("wynncraft.guild.rank.chief", chief);
            appendRank.accept("wynncraft.guild.rank.strategist", strategist);
            appendRank.accept("wynncraft.guild.rank.captain", captain);
            appendRank.accept("wynncraft.guild.rank.recruiter", recruiter);
            appendRank.accept("wynncraft.guild.rank.recruit", recruit);

            if (!result.getSiblings().isEmpty()) {
                result.getSiblings().removeLast();
            }

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Members members = (Members) o;
            return getTotal() == members.getTotal()
                    && Objects.equals(getOwner(), members.getOwner())
                    && Objects.equals(getChief(), members.getChief())
                    && Objects.equals(getStrategist(), members.getStrategist())
                    && Objects.equals(getCaptain(), members.getCaptain())
                    && Objects.equals(getRecruiter(), members.getRecruiter())
                    && Objects.equals(getRecruit(), members.getRecruit());
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    getTotal(), getOwner(), getChief(), getStrategist(), getCaptain(), getRecruiter(), getRecruit());
        }

        @Override
        public String toString() {
            return "Members{" + "total="
                    + total + ", owner="
                    + owner + ", chief="
                    + chief + ", strategist="
                    + strategist + ", captain="
                    + captain + ", recruiter="
                    + recruiter + ", recruit="
                    + recruit + '}';
        }
    }

    public static class Banner {
        private String base;
        private int tier;
        private String structure;
        private List<Layer> layers;

        public static class Layer {
            private String colour;
            private String pattern;

            public String getColour() {
                return colour;
            }

            public void setColour(String colour) {
                this.colour = colour;
            }

            public String getPattern() {
                return pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                Layer layer = (Layer) o;
                return Objects.equals(getColour(), layer.getColour())
                        && Objects.equals(getPattern(), layer.getPattern());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getColour(), getPattern());
            }

            @Override
            public String toString() {
                return "Layer{" + "colour='" + colour + '\'' + ", pattern='" + pattern + '\'' + '}';
            }
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public int getTier() {
            return tier;
        }

        public void setTier(int tier) {
            this.tier = tier;
        }

        public String getStructure() {
            return structure;
        }

        public void setStructure(String structure) {
            this.structure = structure;
        }

        public List<Layer> getLayers() {
            return layers;
        }

        public void setLayers(List<Layer> layers) {
            this.layers = layers;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Banner banner = (Banner) o;
            return getTier() == banner.getTier()
                    && Objects.equals(getBase(), banner.getBase())
                    && Objects.equals(getStructure(), banner.getStructure())
                    && Objects.equals(getLayers(), banner.getLayers());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBase(), getTier(), getStructure(), getLayers());
        }

        @Override
        public String toString() {
            return "Banner{" + "base='"
                    + base + '\'' + ", tier="
                    + tier + ", structure='"
                    + structure + '\'' + ", layers="
                    + layers + '}';
        }
    }

    public static class SeasonRank {
        private int rating;
        private int finalTerritories;

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public int getFinalTerritories() {
            return finalTerritories;
        }

        public void setFinalTerritories(int finalTerritories) {
            this.finalTerritories = finalTerritories;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SeasonRank that = (SeasonRank) o;
            return getRating() == that.getRating() && getFinalTerritories() == that.getFinalTerritories();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getRating(), getFinalTerritories());
        }

        @Override
        public String toString() {
            return "SeasonRank{" + "rating=" + rating + ", finalTerritories=" + finalTerritories + '}';
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXpPercent() {
        return xpPercent;
    }

    public void setXpPercent(int xpPercent) {
        this.xpPercent = xpPercent;
    }

    public int getTerritories() {
        return territories;
    }

    public void setTerritories(int territories) {
        this.territories = territories;
    }

    public int getWars() {
        return wars;
    }

    public void setWars(int wars) {
        this.wars = wars;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Members getMembers() {
        return members;
    }

    public void setMembers(Members members) {
        this.members = members;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Map<String, SeasonRank> getSeasonRanks() {
        return seasonRanks;
    }

    public void setSeasonRanks(Map<String, SeasonRank> seasonRanks) {
        this.seasonRanks = seasonRanks;
    }

    public Members getOnlineMembers() {
        Members onlineMembers = new Members();
        onlineMembers.setOwner(members.getOwner().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setChief(members.getChief().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setStrategist(members.getStrategist().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setCaptain(members.getCaptain().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setRecruiter(members.getRecruiter().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setRecruit(members.getRecruit().entrySet().stream()
                .filter(entry -> entry.getValue().isOnline())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        onlineMembers.setTotal(onlineMembers.getOwner().size()
                + onlineMembers.getChief().size()
                + onlineMembers.getStrategist().size()
                + onlineMembers.getCaptain().size()
                + onlineMembers.getRecruiter().size()
                + onlineMembers.getRecruit().size());
        SequoiaMod.debug(name + "'s online guild members: " + onlineMembers);
        return onlineMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuildResponse guildResponse = (GuildResponse) o;
        return getLevel() == guildResponse.getLevel()
                && getXpPercent() == guildResponse.getXpPercent()
                && getTerritories() == guildResponse.getTerritories()
                && getWars() == guildResponse.getWars()
                && getOnline() == guildResponse.getOnline()
                && Objects.equals(getUuid(), guildResponse.getUuid())
                && Objects.equals(getName(), guildResponse.getName())
                && Objects.equals(getPrefix(), guildResponse.getPrefix())
                && Objects.equals(getCreated(), guildResponse.getCreated())
                && Objects.equals(getMembers(), guildResponse.getMembers())
                && Objects.equals(getBanner(), guildResponse.getBanner())
                && Objects.equals(getSeasonRanks(), guildResponse.getSeasonRanks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getUuid(),
                getName(),
                getPrefix(),
                getLevel(),
                getXpPercent(),
                getTerritories(),
                getWars(),
                getCreated(),
                getMembers(),
                getOnline(),
                getBanner(),
                getSeasonRanks());
    }

    @Override
    public String toString() {
        return "Guild{" + "uuid='"
                + uuid + '\'' + ", name='"
                + name + '\'' + ", prefix='"
                + prefix + '\'' + ", level="
                + level + ", xpPercent="
                + xpPercent + ", territories="
                + territories + ", wars="
                + wars + ", created='"
                + created + '\'' + ", members="
                + members + ", online="
                + online + ", banner="
                + banner + ", seasonRanks="
                + seasonRanks + '}';
    }
}
