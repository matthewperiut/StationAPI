package net.modificationstation.stationapi.impl.client.texture;

import net.modificationstation.stationapi.api.client.model.BakedModelRenderer;
import net.modificationstation.stationapi.api.client.texture.plugin.BlockRendererPlugin;

public interface BlockRendererCustomAccessor {

    BlockRendererPlugin getStationBlockRenderer();

    BakedModelRenderer getBakedModelRenderer();
}
