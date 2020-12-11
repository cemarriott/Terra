package com.dfsek.terra.api.implementations.bukkit.generator;

import com.dfsek.terra.api.generic.generator.TerraChunkGenerator;
import com.dfsek.terra.api.implementations.bukkit.BukkitBiomeGrid;
import com.dfsek.terra.api.implementations.bukkit.BukkitWorld;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BukkitChunkGeneratorWrapper extends ChunkGenerator {
    private final TerraChunkGenerator delegate;

    public BukkitChunkGeneratorWrapper(TerraChunkGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
        BukkitWorld bukkitWorld = new BukkitWorld(world);

        return (ChunkData) delegate.generateChunkData(bukkitWorld, random, x, z, new BukkitBiomeGrid(biome), new BukkitChunkGenerator.BukkitChunkData(createChunkData(world))).getHandle();
    }

    public TerraChunkGenerator getDelegate() {
        return delegate;
    }
}
