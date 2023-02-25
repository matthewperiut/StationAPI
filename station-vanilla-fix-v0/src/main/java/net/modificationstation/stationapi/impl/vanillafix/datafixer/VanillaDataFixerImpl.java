package net.modificationstation.stationapi.impl.vanillafix.datafixer;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.datafixer.DataFixers;
import net.modificationstation.stationapi.api.datafixer.TypeReferences;
import net.modificationstation.stationapi.api.event.datafixer.DataFixerRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.util.Util;
import net.modificationstation.stationapi.api.vanillafix.datadamager.damage.StationFlatteningToMcRegionChunkDamage;
import net.modificationstation.stationapi.api.vanillafix.datadamager.damage.StationFlatteningToMcRegionItemStackDamage;
import net.modificationstation.stationapi.api.vanillafix.datadamager.schema.SchemaMcRegionDamager;
import net.modificationstation.stationapi.api.vanillafix.datadamager.schema.SchemaStationFlatteningDamager;
import net.modificationstation.stationapi.api.vanillafix.datafixer.fix.McRegionToStationFlatteningChunkFix;
import net.modificationstation.stationapi.api.vanillafix.datafixer.fix.McRegionToStationFlatteningItemStackFix;
import net.modificationstation.stationapi.api.vanillafix.datafixer.schema.SchemaMcRegion;
import net.modificationstation.stationapi.api.vanillafix.datafixer.schema.SchemaStationFlattening;

import java.util.Set;
import java.util.function.Supplier;

import static net.modificationstation.stationapi.api.StationAPI.MODID;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public final class VanillaDataFixerImpl {

    public static final String STATION_ID = MODID.id("id").toString();
    public static final int CURRENT_VERSION = 69420;
    public static final int HIGHEST_VERSION = Integer.MAX_VALUE / 10;
    public static final int VANILLA_VERSION = HIGHEST_VERSION - 19132;
    public static final Supplier<DataFixer> DATA_DAMAGER = Suppliers.memoize(() -> {
        final DataFixerBuilder builder = new DataFixerBuilder(VANILLA_VERSION);
        Schema schema69420 = builder.addSchema(HIGHEST_VERSION - 69420, SchemaStationFlatteningDamager::new);
        Schema schema19132 = builder.addSchema(VANILLA_VERSION, SchemaMcRegionDamager::new);
        builder.addFixer(StationFlatteningToMcRegionChunkDamage.create(schema19132, "Station chunk damage", SchemaStationFlattening::lookupOldBlockId));
        builder.addFixer(StationFlatteningToMcRegionItemStackDamage.create(schema19132, "Station itemstack damage", SchemaStationFlattening::lookupOldItemId));
        return builder.buildOptimized(Set.of(TypeReferences.CHUNK, TypeReferences.ITEM_STACK), Util.getBootstrapExecutor());
    });

    @EventListener(numPriority = Integer.MAX_VALUE / 2 + Integer.MAX_VALUE / 4)
    private static void registerFixer(DataFixerRegisterEvent event) {
        DataFixers.registerFixer(MODID, executor -> {
            DataFixerBuilder builder = new DataFixerBuilder(CURRENT_VERSION);
            Schema schema19132 = builder.addSchema(19132, SchemaMcRegion::new);
            Schema schema69420 = builder.addSchema(69420, SchemaStationFlattening::new);
            builder.addFixer(McRegionToStationFlatteningChunkFix.create(schema69420, "Vanilla chunk fix", SchemaStationFlattening::lookupState));
            builder.addFixer(McRegionToStationFlatteningItemStackFix.create(schema69420, "Vanilla itemstack fix", SchemaStationFlattening::lookupItem));
            return builder.buildOptimized(Set.of(TypeReferences.CHUNK, TypeReferences.ITEM_STACK), executor);
        }, CURRENT_VERSION);
    }
}
