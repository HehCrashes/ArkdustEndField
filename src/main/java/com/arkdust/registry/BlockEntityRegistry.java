package com.arkdust.registry;

import com.arkdust.Arkdust;
import com.arkdust.blockentity.portal.SpiritPortalBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Arkdust.MODID);

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SpiritPortalBlockEntity>> SPIRIT_PORTAL =
            REGISTER.register("spirit_portal",()->BlockEntityType.Builder.of(SpiritPortalBlockEntity::new,BlockRegistry.SPIRIT_PORTAL.get()).build(null));
}
