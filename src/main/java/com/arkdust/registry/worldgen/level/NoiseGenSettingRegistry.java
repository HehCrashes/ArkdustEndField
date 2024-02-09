package com.arkdust.registry.worldgen.level;

import com.arkdust.Arkdust;
import com.arkdust.worldgen.dimension.SarconDimension;
import com.arkdust.worldgen.noiserouter.Router;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class NoiseGenSettingRegistry {
    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context){
        context.register(SarconDimension.SETTING,new NoiseGeneratorSettings(NoiseSettings.create(0,160,2,2), Blocks.STONE.defaultBlockState(),Blocks.WATER.defaultBlockState(),
//                Router.overworldWithoutCaves(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)),
                NoiseRouterData.overworld(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE),false,false),
                new SarconDimension.SurfaceSource(),//TODO SurfaceRule required
                List.of(),1,false,false,false,true));
    }

}
