package net.modificationstation.stationloader.api.common.event.container.slot;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationloader.api.common.event.Event;
import net.modificationstation.stationloader.api.common.factory.EventFactory;

public interface ItemUsedInCrafting {

    Event<ItemUsedInCrafting> EVENT = EventFactory.INSTANCE.newEvent(ItemUsedInCrafting.class, listeners ->
            (player, craftingMatrix, itemOrdinal, itemUsed, itemCrafted) -> {
                for (ItemUsedInCrafting event : listeners)
                    event.onItemUsedInCrafting(player, craftingMatrix, itemOrdinal, itemUsed, itemCrafted);
            });

    void onItemUsedInCrafting(PlayerBase player, InventoryBase craftingMatrix, int itemOrdinal, ItemInstance itemUsed, ItemInstance itemCrafted);
}