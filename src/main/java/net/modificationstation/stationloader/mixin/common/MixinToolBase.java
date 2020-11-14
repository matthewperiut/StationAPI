package net.modificationstation.stationloader.mixin.common;

import net.minecraft.block.BlockBase;
import net.minecraft.item.tool.ToolBase;
import net.minecraft.item.tool.ToolMaterial;
import net.modificationstation.stationloader.api.common.event.item.tool.EffectiveBlocksProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ToolBase.class)
public class MixinToolBase {

    @Shadow private BlockBase[] effectiveBlocksBase;

    @Shadow protected ToolMaterial toolMaterial;

    @Inject(method = "<init>(IILnet/minecraft/item/tool/ToolMaterial;[Lnet/minecraft/block/BlockBase;)V", at = @At("RETURN"))
    private void getEffectiveBlocks(int id, int j, ToolMaterial arg, BlockBase[] effectiveBlocks, CallbackInfo ci) {
        List<BlockBase> list = new ArrayList<>(Arrays.asList(effectiveBlocksBase));
        EffectiveBlocksProvider.EVENT.getInvoker().getEffectiveBlocks((ToolBase) (Object) this, toolMaterial, list);
        effectiveBlocksBase = list.toArray(new BlockBase[0]);
    }
}