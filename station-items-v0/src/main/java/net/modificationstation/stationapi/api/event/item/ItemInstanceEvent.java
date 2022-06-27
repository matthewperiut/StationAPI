package net.modificationstation.stationapi.api.event.item;

import lombok.experimental.SuperBuilder;
import net.mine_diver.unsafeevents.Event;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;

@SuperBuilder
public abstract class ItemInstanceEvent extends Event {

    public final ItemInstance itemInstance;

    @SuperBuilder
    public static class Crafted extends ItemInstanceEvent {

        public final Level level;
        public final PlayerBase player;

        @Override
        protected int getEventID() {
            return ID;
        }

        public static final int ID = NEXT_ID.incrementAndGet();
    }
}
