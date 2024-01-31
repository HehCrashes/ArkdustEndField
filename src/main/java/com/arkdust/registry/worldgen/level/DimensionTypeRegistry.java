package com.arkdust.registry.worldgen.level;

import com.arkdust.Arkdust;
import com.arkdust.worldgen.dimension.SarconDimension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DimensionTypeRegistry {
    public static final DeferredRegister<DimensionType> REGISTER = DeferredRegister.create(Registries.DIMENSION_TYPE, Arkdust.MODID);

    public static final DeferredHolder<DimensionType,DimensionType> SARCON = REGISTER.register("resource/sarcon_type",()->SarconDimension.TYPE_INSTANCE);

}
