package net.modificationstation.stationapi.api.client.registry;

import net.minecraft.item.ItemBase;
import net.modificationstation.stationapi.api.client.model.item.ItemModelPredicateProvider;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.math.MathHelper;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import static net.modificationstation.stationapi.api.StationAPI.MODID;

public final class ItemModelPredicateProviderRegistry extends Registry<ItemModelPredicateProvider> {

    public static final ItemModelPredicateProviderRegistry INSTANCE = new ItemModelPredicateProviderRegistry();

    private static final Identifier DAMAGED_ID = Identifier.of("damaged");
    private static final Identifier DAMAGE_ID = Identifier.of("damage");
    private static final Identifier META_ID = Identifier.of("meta");
    private static final ItemModelPredicateProvider DAMAGED_PROVIDER = (itemInstance, clientWorld, livingEntity, seed) -> itemInstance.isDamaged() ? 1.0F : 0.0F;
    private static final ItemModelPredicateProvider DAMAGE_PROVIDER = (itemInstance, clientWorld, livingEntity, seed) -> MathHelper.clamp((float)itemInstance.getDamage() / (float)itemInstance.getDurability(), 0.0F, 1.0F);
    private static final ItemModelPredicateProvider META_PROVIDER = (itemInstance, clientWorld, livingEntity, seed) -> MathHelper.clamp((float)itemInstance.getDamage(), 0, 65535);
    private final Map<ItemBase, Map<Identifier, ItemModelPredicateProvider>> ITEM_SPECIFIC = new IdentityHashMap<>();

    private ItemModelPredicateProviderRegistry() {
        super(Identifier.of(MODID, "item_model_predicate_providers"));
    }

    public ItemModelPredicateProvider get(ItemBase item, Identifier identifier) {
        if (item.getDurability() > 0) {
            if (identifier == DAMAGE_ID)
                return DAMAGE_PROVIDER;

            if (identifier == DAMAGED_ID)
                return DAMAGED_PROVIDER;
        }
        if (item.usesMeta()) {
            if (identifier == META_ID)
                return META_PROVIDER;
        }

        Optional<ItemModelPredicateProvider> modelPredicateProvider = get(identifier);
        if (modelPredicateProvider.isPresent())
            return modelPredicateProvider.get();
        else {
            Map<Identifier, ItemModelPredicateProvider> map = ITEM_SPECIFIC.get(item);
            return map == null ? null : map.get(identifier);
        }
    }

    public void register(ItemBase item, Identifier id, ItemModelPredicateProvider provider) {
        ITEM_SPECIFIC.computeIfAbsent(item, itemx -> new IdentityHashMap<>()).put(id, provider);
    }
}