package com.arkdust;

import com.arkdust.blockentity.portal.SpiritPortalBlockEntity;
import com.arkdust.datagen.*;
import com.arkdust.registry.BlockEntityRegistry;
import com.arkdust.registry.regtype.ArkdustRegistry;
import com.arkdust.registry.render.RenderTypeRegistry;
import com.arkdust.registry.worldgen.level.BiomeRegistry;
import com.arkdust.registry.worldgen.level.DimensionTypeRegistry;
import com.arkdust.registry.worldgen.level.LevelStemRegistry;
import com.arkdust.registry.worldgen.level.NoiseGenSettingRegistry;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Arkdust.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.NOISE_SETTINGS, NoiseGenSettingRegistry::bootstrap)
            .add(Registries.DIMENSION_TYPE, DimensionTypeRegistry::bootstrap)
            .add(Registries.LEVEL_STEM, LevelStemRegistry::bootstrap)
//            .add(Registries.PROCESSOR_LIST, StructureProcessorListRegistry::bootstrap)
//            .add(Registries.STRUCTURE, StructureRegistry::bootstrap)
//            .add(Registries.TEMPLATE_POOL, ExtraStructureJigsawPool::bootstrap)
//            .add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap)
//            .add(Registries.STRUCTURE_SET, ExtraStructureSet::bootstrap)
//            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureRegistry::bootstrap);
//            .add(Registries.PLACED_FEATURE, PlacedFeatureRegistry::bootstrap);
            .add(Registries.BIOME, BiomeRegistry::bootstrap);

    //仅用于生成数据包 不在正常游戏过程中触发
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(),new BlockStateGen(output,fileHelper));
        generator.addProvider(event.includeClient(),new ItemModelGen(output,fileHelper));
        generator.addProvider(event.includeClient(),new ItemExplainGen(output));

        generator.addProvider(event.includeClient(),LootTableGen.create(output));
        BlockTagGen blockTagGen = generator.addProvider(event.includeServer(),new BlockTagGen(output,lookup,fileHelper));
        generator.addProvider(event.includeServer(),new ItemTagGen(output,lookup,blockTagGen.contentsGetter(),fileHelper));

        generator.addProvider(event.includeServer(),new DatapackBuiltinEntriesProvider(output,lookup,BUILDER, Collections.singleton(Arkdust.MODID)));
    }

    @SubscribeEvent
    public static void blockEntityRenderer(EntityRenderersEvent.RegisterRenderers event){
        //Block entities renderer registry
        event.registerBlockEntityRenderer(BlockEntityRegistry.SPIRIT_PORTAL.get(), SpiritPortalBlockEntity.Renderer::new);
    }

//    @SubscribeEvent
//    public static void renderTypeRegistry(RegisterNamedRenderTypesEvent event){
//        event.register(new ResourceLocation(Arkdust.MODID,"spirit_portal"),RenderType.cutout(),RenderType.entityCutout(new ResourceLocation(Arkdust.MODID,"empty")), RenderTypeRegistry.SPIRIT_PORTAL);
//    }

    @SubscribeEvent
    public static void shadersRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),new ResourceLocation(Arkdust.MODID,"spirit_portal"), DefaultVertexFormat.BLOCK),instance -> RenderTypeRegistry.SHADERINS_SPIRIT_PORTAL = instance);
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event){
        event.register(ArkdustRegistry.CLIMATE_PARAMETER);
        event.register(ArkdustRegistry.WEATHER);
        event.register(ArkdustRegistry.WEATHER_PROVIDER);
    }

}
