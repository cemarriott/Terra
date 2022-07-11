package com.dfsek.terra.mod.util;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.mod.CommonPlatform;
import com.dfsek.terra.mod.config.ProtoPlatformBiome;
import com.dfsek.terra.mod.config.VanillaBiomeProperties;
import com.dfsek.terra.mod.mixin.access.VillagerTypeAccessor;


public class BiomeUtil {
    private static final Logger logger = LoggerFactory.getLogger(BiomeUtil.class);
    
    public static void registerBiomes() {
        logger.info("Registering biomes...");
        CommonPlatform.get().getConfigRegistry().forEach(pack -> { // Register all Terra biomes.
            pack.getCheckedRegistry(Biome.class)
                .forEach((id, biome) -> registerBiome(biome, pack, id));
        });
        logger.info("Terra biomes registered.");
    }
    
    protected static RegistryKey<net.minecraft.world.biome.Biome> registerBiome(Identifier identifier,
                                                                                net.minecraft.world.biome.Biome biome) {
        BuiltinRegistries.add(BuiltinRegistries.BIOME,
                              MinecraftUtil.registerKey(identifier)
                                           .getValue(),
                              biome);
        return getBiomeKey(identifier);
    }
    
    public static RegistryKey<net.minecraft.world.biome.Biome> getBiomeKey(Identifier identifier) {
        return BuiltinRegistries.BIOME.getKey(BuiltinRegistries.BIOME.get(identifier)).orElseThrow();
    }
    
    /**
     * Clones a Vanilla biome and injects Terra data to create a Terra-vanilla biome delegate.
     *
     * @param biome The Terra BiomeBuilder.
     * @param pack  The ConfigPack this biome belongs to.
     */
    protected static void registerBiome(Biome biome, ConfigPack pack,
                                        com.dfsek.terra.api.registry.key.RegistryKey id) {
        VanillaBiomeProperties vanillaBiomeProperties = biome.getContext().get(VanillaBiomeProperties.class);
        
        net.minecraft.world.biome.Biome minecraftBiome = MinecraftUtil.createBiome(vanillaBiomeProperties);
        
        Identifier identifier = new Identifier("terra", MinecraftUtil.createBiomeID(pack, id));
        
        biome.setPlatformBiome(new ProtoPlatformBiome(identifier, registerBiome(identifier, minecraftBiome)));
        
        Map villagerMap = VillagerTypeAccessor.getBiomeTypeToIdMap();
        
        villagerMap.put(RegistryKey.of(Registry.BIOME_KEY, identifier),
                        Objects.requireNonNullElse(vanillaBiomeProperties.getVillagerType(), VillagerType.PLAINS));
        
        for(Identifier tag : vanillaBiomeProperties.getTags()) {
            MinecraftUtil.TERRA_BIOME_TAG_MAP.getOrDefault(TagKey.of(Registry.BIOME_KEY, tag), new ArrayList<>()).add(identifier);
        }
    }
}
