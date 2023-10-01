package net.modificationstation.stationapi.impl.registry;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.resource.DataResourceReloaderRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.registry.ServerDynamicRegistryType;
import net.modificationstation.stationapi.api.tag.TagManagerLoader;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
@EventListener(phase = StationAPI.INTERNAL_PHASE)
public final class TagReloaderImpl {
    @EventListener
    private static void registerTagLoader(DataResourceReloaderRegisterEvent event) {
        event.resourceManager.registerReloader(new TagManagerLoader(ServerDynamicRegistryType.createCombinedDynamicRegistries().getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE)));
    }
}
