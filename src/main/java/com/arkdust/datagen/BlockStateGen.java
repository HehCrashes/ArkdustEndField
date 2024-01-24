package com.arkdust.datagen;

import com.arkdust.Arkdust;
import com.arkdust.helper.ListAndMapHelper;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockStateGen extends BlockStateProvider {
    private static final Map<StateType, List<DeferredBlock<Block>>> MAP = new HashMap<>();
    public static void add(DeferredBlock<Block> block,StateType type){
        if(type != null) ListAndMapHelper.tryAddElementToMapList(MAP,type,block);
    }

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Arkdust.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        MAP.forEach((type, deferredBlocks) -> deferredBlocks.forEach(block -> {
            switch (type){
                case CUBE_ALL -> simpleBlockWithItem(block.get(),cubeAll(block.get()));
            }
        }));
    }

    public enum StateType{//TODO
        NONE,
        CUBE_ALL
    }
}
