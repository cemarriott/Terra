package com.dfsek.terra.addons.feature.locator.locators;

import com.dfsek.terra.api.TerraPlugin;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.structure.feature.Locator;
import com.dfsek.terra.api.util.ConstantRange;
import com.dfsek.terra.api.util.Range;
import com.dfsek.terra.api.world.Column;

import java.util.Collections;
import java.util.List;

public class SurfaceLocator implements Locator {
    private final Range search;

    private final BlockState air;

    public SurfaceLocator(Range search, TerraPlugin main) {
        this.search = search;
        this.air = main.getWorldHandle().air();
    }

    @Override
    public List<Integer> getSuitableCoordinates(Column column) {
        for(int y : search) {
            if(column.getBlock(y).matches(air) && !column.getBlock(y-1).matches(air)) {
                return Collections.singletonList(y);
            }
        }
        return Collections.emptyList();
    }
}
