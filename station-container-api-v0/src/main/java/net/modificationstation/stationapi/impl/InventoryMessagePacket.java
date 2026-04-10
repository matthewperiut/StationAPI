package net.modificationstation.stationapi.impl;

import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class InventoryMessagePacket extends MessagePacket {
    public Inventory inventory;

    /**
     * Default Message constructor.
     *
     * @param identifier the Message's identifier.
     */
    public InventoryMessagePacket(Identifier identifier) {
        super(identifier);
    }
}
