package net.modificationstation.stationapi.impl.worldgen;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.class_153;
import net.modificationstation.stationapi.api.worldgen.biome.ClimateBiomeProvider;

import java.util.Collection;
import java.util.Set;

public class OverworldBiomeProviderImpl extends ClimateBiomeProvider {
    private static final OverworldBiomeProviderImpl INSTANCE = new OverworldBiomeProviderImpl();

    private OverworldBiomeProviderImpl() {
    }

    @Override
    public class_153 getBiome(int x, int z, float temperature, float wetness) {
        class_153 biome = getBiome(temperature, wetness);
        return biome == null ? class_153.method_786(temperature, wetness) : biome;
    }

    @Override
    protected class_153 getBiome(float temperature, float wetness) {
        class_153 biome = super.getBiome(temperature, wetness);
        return biome == null ? class_153.method_786(temperature, wetness) : biome;
    }
    
    @Override
    public Collection<class_153> getBiomes() {
        Collection<class_153> biomes = super.getBiomes();
        biomes.add(class_153.field_875);
        biomes.add(class_153.field_876);
        biomes.add(class_153.field_877);
        biomes.add(class_153.field_878);
        biomes.add(class_153.field_879);
        biomes.add(class_153.field_880);
        biomes.add(class_153.field_881);
        biomes.add(class_153.field_882);
        biomes.add(class_153.field_883);
        biomes.add(class_153.field_884);
        biomes.add(class_153.field_885);
        return biomes;
    }

    public static OverworldBiomeProviderImpl getInstance() {
        return INSTANCE;
    }
}