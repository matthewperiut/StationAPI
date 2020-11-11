package net.modificationstation.stationloader.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.inventory.InventoryBase;
import net.modificationstation.stationloader.api.client.event.gui.GuiRegister;
import net.modificationstation.stationloader.api.common.event.ModIDEvent;
import net.modificationstation.stationloader.api.common.event.packet.PacketRegister;
import net.modificationstation.stationloader.api.common.factory.GeneralFactory;
import net.modificationstation.stationloader.api.common.registry.ModIDRegistry;
import net.modificationstation.stationloader.impl.client.entity.player.PlayerHelper;
import net.modificationstation.stationloader.impl.client.gui.GuiHelper;
import net.modificationstation.stationloader.impl.client.model.CustomModelRenderer;
import net.modificationstation.stationloader.impl.client.packet.PacketHelper;
import net.modificationstation.stationloader.impl.client.texture.TextureFactory;
import net.modificationstation.stationloader.impl.client.texture.TextureRegistry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class StationLoader extends net.modificationstation.stationloader.impl.common.StationLoader {

    @Override
    public void setup() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, URISyntaxException {
        if (new Exception().getStackTrace()[1].getClassName().equals(MinecraftApplet.class.getName()))
            super.setup();
        else
            throw new IllegalAccessException("Tried running StationLoader.setup() from an unknown source!");
    }

    @Override
    public void setupAPI() {
        super.setupAPI();
        getLogger().info("Setting up client GeneralFactory...");
        GeneralFactory.INSTANCE.addFactory(net.modificationstation.stationloader.api.client.model.CustomModelRenderer.class, (args) -> new CustomModelRenderer((String) args[0], (String) args[1]));
        getLogger().info("Setting up TextureFactory...");
        net.modificationstation.stationloader.api.client.texture.TextureFactory.INSTANCE.setHandler(new TextureFactory());
        getLogger().info("Setting up TextureRegistry...");
        net.modificationstation.stationloader.api.client.texture.TextureRegistry.RUNNABLES.put("unbind", TextureRegistry::unbind);
        net.modificationstation.stationloader.api.client.texture.TextureRegistry.FUNCTIONS.put("getRegistry", TextureRegistry::getRegistry);
        net.modificationstation.stationloader.api.client.texture.TextureRegistry.SUPPLIERS.put("currentRegistry", TextureRegistry::currentRegistry);
        net.modificationstation.stationloader.api.client.texture.TextureRegistry.SUPPLIERS.put("registries", TextureRegistry::registries);
        getLogger().info("Setting up PlayerHelper...");
        net.modificationstation.stationloader.api.common.entity.player.PlayerHelper.INSTANCE.setHandler(new PlayerHelper());
        getLogger().info("Setting up PacketHelper...");
        net.modificationstation.stationloader.api.common.packet.PacketHelper.INSTANCE.setHandler(new PacketHelper());
        getLogger().info("Setting up GuiHelper...");
        net.modificationstation.stationloader.api.common.gui.GuiHelper.INSTANCE.setHandler(new GuiHelper());
        PacketRegister.EVENT.register((register, customDataPackets) -> {
            customDataPackets.put("open_gui", ((playerBase, customData) -> {
                boolean isClient = playerBase.level.isClient;
                ModIDRegistry.gui.get(customData.strings()[0]).get((short) Byte.toUnsignedInt(customData.bytes()[0])).accept(playerBase, isClient ? null : (InventoryBase) customData.objects()[0], customData);
                if (isClient)
                    playerBase.container.currentContainerId = customData.ints()[0];
            }));
            ModIDEvent<GuiRegister> event = GuiRegister.EVENT;
            GuiRegister invoker = event.getInvoker();
            String modid = event.getListenerModID(invoker);
            if (modid != null)
                ModIDRegistry.gui.put(modid, new HashMap<>());
            invoker.registerGUIs(ModIDRegistry.gui.get(modid));
        }, getData());
    }
}
