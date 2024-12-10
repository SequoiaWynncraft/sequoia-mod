package dev.lotnest.sequoia.wynn.player;

import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Player {
    private String username;
    private boolean online;
    private String server;
    private String activeCharacter;
    private String nickname;
    private String uuid;
    private String rank;
    private String rankBadge;
    private LegacyRankColour legacyRankColour;
    private String shortenedRank;
    private String supportRank;
    private boolean veteran;
    private String firstJoin;
    private String lastJoin;
    private float playtime;
    private Guild guild;
    private GlobalData globalData;
    private Integer forumLink;
    private Map<String, Integer> ranking;
    private Map<String, Integer> previousRanking;
    private boolean publicProfile;

    public static class LegacyRankColour {
        private String main;
        private String sub;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }

    public static class Guild {
        private String name;
        private String prefix;
        private String rank;
        private String rankStars;

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

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRankStars() {
            return rankStars;
        }

        public void setRankStars(String rankStars) {
            this.rankStars = rankStars;
        }
    }

    public static class GlobalData {
        private int wars;
        private int totalLevels;
        private int killedMobs;
        private int chestsFound;
        private Dungeons dungeons;
        private Raids raids;
        private int completedQuests;
        private PvP pvp;

        public int getWars() {
            return wars;
        }

        public void setWars(int wars) {
            this.wars = wars;
        }

        public int getTotalLevels() {
            return totalLevels;
        }

        public void setTotalLevels(int totalLevels) {
            this.totalLevels = totalLevels;
        }

        public int getKilledMobs() {
            return killedMobs;
        }

        public void setKilledMobs(int killedMobs) {
            this.killedMobs = killedMobs;
        }

        public int getChestsFound() {
            return chestsFound;
        }

        public void setChestsFound(int chestsFound) {
            this.chestsFound = chestsFound;
        }

        public Dungeons getDungeons() {
            return dungeons;
        }

        public void setDungeons(Dungeons dungeons) {
            this.dungeons = dungeons;
        }

        public Raids getRaids() {
            return raids;
        }

        public void setRaids(Raids raids) {
            this.raids = raids;
        }

        public int getCompletedQuests() {
            return completedQuests;
        }

        public void setCompletedQuests(int completedQuests) {
            this.completedQuests = completedQuests;
        }

        public PvP getPvp() {
            return pvp;
        }

        public void setPvp(PvP pvp) {
            this.pvp = pvp;
        }

        public static class Dungeons {
            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }
        }

        public static class Raids {
            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }

            public MutableComponent toPrettyMessage() {
                MutableComponent result = Component.empty();

                if (list == null || list.isEmpty()) {
                    return result.append(
                            Component.translatable("sequoia.command.playerRaids.playerHasNoRaidsCompleted"));
                }

                list.forEach((raidName, count) -> result.append(
                                Component.literal(raidName).withStyle(ChatFormatting.DARK_GREEN))
                        .append(Component.literal(": ").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(count + " times").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("\n")));

                if (!result.getSiblings().isEmpty()) {
                    result.getSiblings().removeLast();
                }

                return result;
            }
        }

        public static class PvP {
            private int kills;
            private int deaths;

            public int getKills() {
                return kills;
            }

            public void setKills(int kills) {
                this.kills = kills;
            }

            public int getDeaths() {
                return deaths;
            }

            public void setDeaths(int deaths) {
                this.deaths = deaths;
            }
        }
    }

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

    public String getActiveCharacter() {
        return activeCharacter;
    }

    public void setActiveCharacter(String activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRankBadge() {
        return rankBadge;
    }

    public void setRankBadge(String rankBadge) {
        this.rankBadge = rankBadge;
    }

    public LegacyRankColour getLegacyRankColour() {
        return legacyRankColour;
    }

    public void setLegacyRankColour(LegacyRankColour legacyRankColour) {
        this.legacyRankColour = legacyRankColour;
    }

    public String getShortenedRank() {
        return shortenedRank;
    }

    public void setShortenedRank(String shortenedRank) {
        this.shortenedRank = shortenedRank;
    }

    public String getSupportRank() {
        return supportRank;
    }

    public void setSupportRank(String supportRank) {
        this.supportRank = supportRank;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public String getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(String firstJoin) {
        this.firstJoin = firstJoin;
    }

    public String getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(String lastJoin) {
        this.lastJoin = lastJoin;
    }

    public float getPlaytime() {
        return playtime;
    }

    public void setPlaytime(float playtime) {
        this.playtime = playtime;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public GlobalData getGlobalData() {
        return globalData;
    }

    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

    public Integer getForumLink() {
        return forumLink;
    }

    public void setForumLink(Integer forumLink) {
        this.forumLink = forumLink;
    }

    public Map<String, Integer> getRanking() {
        return ranking;
    }

    public void setRanking(Map<String, Integer> ranking) {
        this.ranking = ranking;
    }

    public Map<String, Integer> getPreviousRanking() {
        return previousRanking;
    }

    public void setPreviousRanking(Map<String, Integer> previousRanking) {
        this.previousRanking = previousRanking;
    }

    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }
}
