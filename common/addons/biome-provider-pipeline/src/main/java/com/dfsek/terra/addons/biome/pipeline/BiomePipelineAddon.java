/*
 * Copyright (c) 2020-2024 Polyhedral Development
 *
 * The Terra Core Addons are licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in this module's root directory.
 */

package com.dfsek.terra.addons.biome.pipeline;

import com.dfsek.tectonic.api.config.template.object.ObjectTemplate;

import java.util.function.Supplier;

import com.dfsek.terra.addons.biome.pipeline.api.Source;
import com.dfsek.terra.addons.biome.pipeline.api.Stage;
import com.dfsek.terra.addons.biome.pipeline.api.biome.PipelineBiome;
import com.dfsek.terra.addons.biome.pipeline.config.BiomePipelineTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.PipelineBiomeLoader;
import com.dfsek.terra.addons.biome.pipeline.config.source.SamplerSourceTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.expander.ExpanderStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.BorderListStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.BorderStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.ReplaceListStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.ReplaceStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.SmoothStageTemplate;
import com.dfsek.terra.addons.manifest.api.AddonInitializer;
import com.dfsek.terra.api.Platform;
import com.dfsek.terra.api.addon.BaseAddon;
import com.dfsek.terra.api.event.events.config.pack.ConfigPackPostLoadEvent;
import com.dfsek.terra.api.event.events.config.pack.ConfigPackPreLoadEvent;
import com.dfsek.terra.api.event.functional.FunctionalEventHandler;
import com.dfsek.terra.api.inject.annotations.Inject;
import com.dfsek.terra.api.registry.CheckedRegistry;
import com.dfsek.terra.api.registry.Registry;
import com.dfsek.terra.api.util.reflection.TypeKey;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;


public class BiomePipelineAddon implements AddonInitializer {

    public static final TypeKey<Supplier<ObjectTemplate<Source>>> SOURCE_REGISTRY_KEY = new TypeKey<>() {
    };

    public static final TypeKey<Supplier<ObjectTemplate<Stage>>> STAGE_REGISTRY_KEY = new TypeKey<>() {
    };
    public static final TypeKey<Supplier<ObjectTemplate<BiomeProvider>>> PROVIDER_REGISTRY_KEY = new TypeKey<>() {
    };
    @Inject
    private Platform platform;

    @Inject
    private BaseAddon addon;

    @Override
    public void initialize() {
        platform.getEventManager()
            .getHandler(FunctionalEventHandler.class)
            .register(addon, ConfigPackPreLoadEvent.class)
            .then(event -> {
                CheckedRegistry<Supplier<ObjectTemplate<BiomeProvider>>> providerRegistry = event.getPack().getOrCreateRegistry(
                    PROVIDER_REGISTRY_KEY);
                providerRegistry.register(addon.key("PIPELINE"), BiomePipelineTemplate::new);
            })
            .then(event -> {
                CheckedRegistry<Supplier<ObjectTemplate<Source>>> sourceRegistry = event.getPack().getOrCreateRegistry(
                    SOURCE_REGISTRY_KEY);
                sourceRegistry.register(addon.key("SAMPLER"), SamplerSourceTemplate::new);
            })
            .then(event -> {
                CheckedRegistry<Supplier<ObjectTemplate<Stage>>> stageRegistry = event.getPack().getOrCreateRegistry(
                    STAGE_REGISTRY_KEY);
                stageRegistry.register(addon.key("FRACTAL_EXPAND"), ExpanderStageTemplate::new);
                stageRegistry.register(addon.key("SMOOTH"), SmoothStageTemplate::new);
                stageRegistry.register(addon.key("REPLACE"), ReplaceStageTemplate::new);
                stageRegistry.register(addon.key("REPLACE_LIST"), ReplaceListStageTemplate::new);
                stageRegistry.register(addon.key("BORDER"), BorderStageTemplate::new);
                stageRegistry.register(addon.key("BORDER_LIST"), BorderListStageTemplate::new);
            })
            .failThrough();
        platform.getEventManager()
            .getHandler(FunctionalEventHandler.class)
            .register(addon, ConfigPackPostLoadEvent.class)
            .then(event -> {
                Registry<Biome> biomeRegistry = event.getPack().getRegistry(Biome.class);
                event.getPack().applyLoader(PipelineBiome.class, new PipelineBiomeLoader(biomeRegistry));
            });
    }
}
