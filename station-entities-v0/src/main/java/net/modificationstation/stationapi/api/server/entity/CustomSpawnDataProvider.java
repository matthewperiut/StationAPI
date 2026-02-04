package net.modificationstation.stationapi.api.server.entity;

import net.minecraft.network.packet.Packet;

import java.io.IOException;

public interface CustomSpawnDataProvider {

    Packet getSpawnData() throws IOException;
}
