package dev.lotnest.sequoia.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

public class PvPEvent extends Event {
    public static final class Tagged extends PvPEvent {
        private final Player tagger;
        private final long tagTimeMillis = System.currentTimeMillis();

        public Tagged() {
            this(null);
        }

        public Tagged(Player tagger) {
            this.tagger = tagger;
        }

        public Player getTagger() {
            return tagger;
        }
    }

    public static final class Untagged extends PvPEvent {
        private final Player tagger;
        private final long untagTimeMillis = System.currentTimeMillis();

        public Untagged() {
            this(null);
        }

        public Untagged(Player tagger) {
            this.tagger = tagger;
        }

        public Player getTagger() {
            return tagger;
        }
    }
}
