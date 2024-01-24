package com.arkdust.registry;

import com.arkdust.Arkdust;
import com.arkdust.blocks.levelblocks.SpiritStoneBlocks;
import com.arkdust.datagen.BlockStateGen;
import com.arkdust.datagen.BlockTagGen;
import com.arkdust.datagen.LootTableGen;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockRegistry{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Arkdust.MODID);

    public static final DeferredBlock<Block> SPIRIT_STONE_ACTIVATED = Builder.create("spirit_stone_activated",BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i)->8)).breakLev(3).state(BlockStateGen.StateType.CUBE_ALL).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.SELF).build();
    public static final DeferredBlock<Block> SPIRIT_STONE_UNACTIVATED = Builder.create("spirit_stone_unactivated", SpiritStoneBlocks.Unactivated::new).breakLev(4).state(BlockStateGen.StateType.CUBE_ALL).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.other(SPIRIT_STONE_ACTIVATED)).build();
    public static final DeferredBlock<Block> SPIRIT_STONE = Builder.create("spirit_stone",BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i)->2)).breakLev(3).state(BlockStateGen.StateType.CUBE_ALL).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.SELF).build();


    private static class Builder {
        private final DeferredBlock<Block> obj;
        private int breakLevel = 0;
        private BlockTagGen.ToolType toolType = null;
        private BlockStateGen.StateType stateType = null;
        private LootTableGen.ILootFunc lootFunc = null;
        private Builder(DeferredBlock<Block> obj){
            this.obj = obj;
        }

        protected static Builder create(DeferredBlock<Block> obj){
            return new Builder(obj);
        }

        protected static Builder create(String name , Supplier<? extends Block> supplier){
            return new Builder(BLOCKS.register(name,supplier));
        }

        protected static Builder create(String name , BlockBehaviour.Properties properties){
            return new Builder(BLOCKS.registerSimpleBlock(name,properties));
        }

        protected Builder breakLev(int level){
            breakLevel = level;
            return this;
        }

        protected Builder tool(BlockTagGen.ToolType type){
            this.toolType = type;
            return this;
        }

        protected Builder state(BlockStateGen.StateType type){
            this.stateType = type;
            return this;
        }

        protected Builder loot(LootTableGen.ILootFunc func){
            this.lootFunc = func;
            return this;
        }

        protected DeferredBlock<Block> build(){
            BlockTagGen.add(obj,toolType,breakLevel);
            BlockStateGen.add(obj,stateType);
            LootTableGen.BlockLoot.add(obj, lootFunc);
            return obj;
        }
    }
}
