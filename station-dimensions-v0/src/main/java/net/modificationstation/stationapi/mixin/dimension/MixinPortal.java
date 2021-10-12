package net.modificationstation.stationapi.mixin.dimension;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Portal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.block.TeleportationManager;
import net.modificationstation.stationapi.api.entity.HasTeleportationManager;
import net.modificationstation.stationapi.api.util.SideUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Portal.class)
public class MixinPortal implements TeleportationManager {

    @Unique
    @Override
    public void switchDimension(PlayerBase player) {
        //noinspection deprecation
        Object game = FabricLoader.getInstance().getGameInstance();
        //noinspection Convert2MethodRef
        SideUtils.run(() -> ((Minecraft) game).switchDimension(), () -> ((MinecraftServer) game).serverPlayerConnectionManager.sendToOppositeDimension((ServerPlayer) player));
    }

    @Inject(
            method = "onEntityCollision(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/EntityBase;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityBase;method_1388()V"
            )
    )
    private void onEntityCollision(Level level, int x, int y, int z, EntityBase entityBase, CallbackInfo ci) {
        if (entityBase instanceof HasTeleportationManager)
            ((HasTeleportationManager) entityBase).setTeleportationManager(this);
    }
}