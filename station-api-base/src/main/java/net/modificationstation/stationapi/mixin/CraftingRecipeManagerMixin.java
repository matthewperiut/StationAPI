package net.modificationstation.stationapi.mixin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.recipe.CraftingRecipe;
import net.modificationstation.stationapi.impl.recipe.CraftingRecipeComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.recipe.CraftingRecipeManager")
public class CraftingRecipeManagerMixin {
    @Shadow
    private List recipes;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V"))
    void resort(List<CraftingRecipe> list, Comparator<? super CraftingRecipe> c) {
        // Skip the buggy sort and use our fixed comparator instead
        Collections.sort(this.recipes, new CraftingRecipeComparator());
    }
}
