package com.arkdust;

import com.arkdust.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Arkdust.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {

    //仅用于生成数据包 不在正常游戏过程中触发
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(),new BlockStateGen(output,fileHelper));
        generator.addProvider(event.includeClient(),new ItemModelGen(output,fileHelper));

        generator.addProvider(event.includeClient(),LootTableGen.create(output));
        BlockTagGen blockTagGen = generator.addProvider(event.includeServer(),new BlockTagGen(output,lookup,fileHelper));
        generator.addProvider(event.includeServer(),new ItemTagGen(output,lookup,blockTagGen.contentsGetter(),fileHelper));
    }

}
