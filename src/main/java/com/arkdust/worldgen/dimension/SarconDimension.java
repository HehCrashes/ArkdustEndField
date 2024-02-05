package com.arkdust.worldgen.dimension;

import com.arkdust.Arkdust;
import com.arkdust.registry.worldgen.level.BiomeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SarconDimension{
    public static final ResourceKey<LevelStem> STEM = ResourceKey.create(Registries.LEVEL_STEM,new ResourceLocation(Arkdust.MODID,"sarcon"));
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(Registries.DIMENSION,new ResourceLocation(Arkdust.MODID,"sarcon"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,new ResourceLocation(Arkdust.MODID,"sarcon_type"));
    public static final ResourceKey<NoiseGeneratorSettings> SETTING = ResourceKey.create(Registries.NOISE_SETTINGS,new ResourceLocation(Arkdust.MODID,"sarcon"));

    public static class Source extends BiomeSource{
        public static final Logger LOGGER = LogManager.getLogger(Arkdust.getLogName("worldgen.dimension.biome.sarcon"));

        public static final Codec<Source> CODEC = RecordCodecBuilder.create(obj->obj.group(
                Biome.CODEC.listOf().fieldOf("biomes").forGetter(ins->ins.BIOMES)
        ).apply(obj,obj.stable(Source::new)));

        private final List<Holder<Biome>> BIOMES;
        private final Map<ResourceKey<Biome>,Holder<Biome>> BIOME_MAP;

        public Source(HolderGetter<Biome> getter){
            this.BIOME_MAP = new HashMap<>();
            BiomeRegistry.Sarcon.BIOMES.forEach(obj->BIOME_MAP.put(obj,getter.getOrThrow(obj)));
            this.BIOMES = BIOME_MAP.values().stream().toList();

        }

        public Source(List<Holder<Biome>> biomes){
            BIOMES = biomes;
            BIOME_MAP = new HashMap<>();
            for(Holder<Biome> holder : biomes){
                if(holder.unwrapKey().isEmpty()) LOGGER.warn("Biome holder({})'s element is not exist",holder);
                BIOME_MAP.put(holder.unwrapKey().get(),holder);
            }
        }

        @Override
        protected Codec<? extends BiomeSource> codec() {
            return CODEC;
        }

        @Override
        protected Stream<Holder<Biome>> collectPossibleBiomes() {
            return BIOMES.stream();
        }

        @Override
        public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {//TODO
            return switch (getBiomeTypeMark(pX,pY)){
                case 0 -> BIOME_MAP.get(BiomeRegistry.Sarcon.DESERT);
                case 1 -> BIOME_MAP.get(BiomeRegistry.Sarcon.DEGRADED_GRASSLAND);
                case 2 -> BIOME_MAP.get(BiomeRegistry.Sarcon.PLATEAU);
                case 3 -> BIOME_MAP.get(BiomeRegistry.Sarcon.PLATEAU);
                case 4 -> BIOME_MAP.get(BiomeRegistry.Sarcon.SPARSE_RAIN_FOREST);
                case 5 -> BIOME_MAP.get(BiomeRegistry.Sarcon.RAIN_FOREST);
                default -> throw new IllegalArgumentException("Can't get any valuable biome mark index in Arkdust Sarcon Level Biome Source");
            };
        }

        /*0:沙漠
        * 1:退化草原
        * 2:山崖
        * 3:高山草甸
        * 4:稀疏雨林
        * 5:雨林*/
        public static int getBiomeTypeMark(int pX, int pY) {
            final int k = -1200;
            long y = (long) pY << 2 + 2;
            long x = (long) pX << 2 + 2;
            float distance = (y + 2 * x - k) / 2.236F;
            double origin = Math.sqrt(x * x + y * y);
            if (distance <= Math.min(1200 + origin / 20, 2400)) {
                return 0;
            } else if (distance <= -40) {
                return 1;
            } else if(distance <= 0) {
                return 2;
            } else if(distance <=  Math.min(1800 + origin / 40,2000)){
                return 3;
            } else if (distance <= Math.min(4000 + origin / 30,5000)) {
                return 4;
            }else {
                return 5;
            }
        }


    }

    //TODO 高度计算出错:x = 0.5+16n出现半悬空半消失   z = 0.5+16n + 8 位置下陷 其它位置最顶层缺失   z = 8.5+16n 出现整条顶层缺失
    //TODO 底层高度缺失:from height-16 to +32
    //TODO 地形噪音增高算法区间出错
    public static class Generator extends NoiseBasedChunkGenerator {

        public static final Codec<Generator> CODEC = RecordCodecBuilder.create(
                p_255585_ -> p_255585_.group(
                                BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                                NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
                        )
                        .apply(p_255585_, p_255585_.stable(Generator::new))
        );

        @Override
        protected Codec<? extends ChunkGenerator> codec() {
            return CODEC;
        }

        public static int getHeight(int x, int y) {
            // 定义一个常量k，表示直线y = -2x + k的截距
            final int k = -1200;
            // 定义一个变量height，用于存储高度值
            int height = 0;
            // 判断坐标(x, y)是否在直线y = -2x + k上
            if (y == -2 * x + k) {
                // 如果在直线上，高度值为344
                height = 343;
            } else {
                // 计算坐标(x, y)到直线y = -2x + k的距离
                float distance = Math.abs(y + 2 * x - k) / 2.236F;
                // 计算坐标(x, y)到原点(0, 0)的距离
                double origin = Math.sqrt(x * x + y * y);
                if (y < -2 * x + k) {//沙漠半区高度计算
                    double m = Math.min(1200 + origin / 20, 2400);
                    if (distance < 40) {//断崖区高度计算
                        height = 344 - (int) (distance * 3.2F);
                    } else if (distance < m) {//退化草原区高度计算
                        height = 216 - (int) (24 * distance / origin);
                    } else {//沙漠区高度计算
                        height = 192;
                    }
                } else {//雨林半区高度计算
                    // 计算n的值，高山草甸边界
                    double n = Math.min(1800 + origin / 40,2000);
                    // 计算p的值，稀疏雨林边界
                    double p = Math.min(4000 + origin / 30,5000);
                    // 判断距离是否小于n
                    if (distance < n) {//高山草甸高度计算
                        height = 344 - (int) (30 * distance / n);
                    } else if (distance < p) {//稀疏雨林高度计算
                        height = 314 - (int) (50 * (distance - n) / p);
                    } else {//密林高度计算
                        height = 264 - (int) (40 * (distance - p) / 4000);
                    }
                }
            }
            // 返回高度值
            return height;
        }

        public Generator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settingsHolder) {
            super(pBiomeSource,settingsHolder);
        }

        @Override
        public int getGenDepth() {
            return 432;
        }

        @Override
        public int getSeaLevel() {
            return 0;
        }

        @Override
        public int getMinY() {
            return 200;
        }

        @Override
        public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
            return 0;
        }



        @Override
        protected OptionalInt iterateNoiseColumn(LevelHeightAccessor pLevel, RandomState pRandom, int pX, int pZ, @Nullable MutableObject<NoiseColumn> pColumn, @Nullable Predicate<BlockState> pStoppingState) {
            //-16 +32
            OptionalInt opt = super.iterateNoiseColumn(pLevel, pRandom, pX, pZ, pColumn, pStoppingState);
            if(opt.isPresent()){
                return OptionalInt.of(opt.getAsInt() + getHeight(pX,pZ));
            }
            return opt;

//            return OptionalInt.of(getHeight(pX,pZ));
        }

        @Override
        public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
            return super.fillFromNoise(pExecutor, pBlender, pRandom, pStructureManager, pChunk);
        }

        @Override
        public ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunk, int minCellY, int cellCountY) {
            NoiseChunk noiseChunk = chunk.getOrCreateNoiseChunk(access -> this.createNoiseChunk(access, structureManager, blender, random));
            Heightmap oceanFloorHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
            Heightmap worldSurfaceHeightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
            ChunkPos chunkPos = chunk.getPos();
            int chunkMinX = chunkPos.getMinBlockX();
            int chunkMinZ = chunkPos.getMinBlockZ();
            Aquifer aquifer = noiseChunk.aquifer();
            noiseChunk.initializeForFirstCellX();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int cellWidth = noiseChunk.cellWidth();
            int cellHeight = noiseChunk.cellHeight();
            int cellCountX = 16 / cellWidth;
            int cellCountZ = 16 / cellWidth;

            for(int cellX = 0; cellX < cellCountX; ++cellX) {
                noiseChunk.advanceCellX(cellX);

                for(int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                    int sectionIndex = chunk.getSectionsCount() - 1;
                    LevelChunkSection levelChunkSection = chunk.getSection(sectionIndex);

                    for(int cellY = cellCountY - 1; cellY >= 0; --cellY) {
                        noiseChunk.selectCellYZ(cellY, cellZ);

                        for(int y = cellHeight - 1; y >= 0; --y) {

                            for(int x = 0; x < cellWidth; ++x) {
                                int blockX = chunkMinX + cellX * cellWidth + x;
                                int xInChunk = blockX & 15;
                                double xRatio = (double)x / (double)cellWidth;
                                noiseChunk.updateForX(blockX, xRatio);

                                for(int z = 0; z < cellWidth; ++z) {
                                    int blockZ = chunkMinZ + cellZ * cellWidth + z;
                                    int zInChunk = blockZ & 15;
                                    double zRatio = (double)z / (double)cellWidth;
                                    noiseChunk.updateForZ(blockZ, zRatio);

                                    int blockY = (minCellY + cellY) * cellHeight + y + getHeight(blockX,blockZ) - 16;
//                                    int blockY = (minCellY + cellY) * cellHeight + y;
//                                    int blockY = getHeight(blockX,blockZ) - 16;
                                    if(blockY < 0) continue;
                                    int yInChunk = blockY & 15;
                                    int sectionIndexInChunk = chunk.getSectionIndex(blockY);
                                    if (sectionIndex != sectionIndexInChunk) {
                                        sectionIndex = sectionIndexInChunk;
                                        levelChunkSection = chunk.getSection(sectionIndex);
                                    }

                                    double yRatio = (double)y / (double)cellHeight;
                                    noiseChunk.updateForY(blockY, yRatio);
//                                    int blockYRelative = blockY + getHeight(blockX,blockZ);

                                    BlockState blockState = noiseChunk.getInterpolatedState();
                                    if (blockState == null) {
                                        blockState = this.generatorSettings().value().defaultBlock();
                                    }
                                    if (!blockState.isAir() && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
//                                        levelChunkSection.setBlockState(xInChunk, yInChunk, zInChunk, blockState, false);
                                        oceanFloorHeightmap.update(xInChunk, blockY, zInChunk, blockState);
                                        worldSurfaceHeightmap.update(xInChunk, blockY, zInChunk, blockState);
                                        if (aquifer.shouldScheduleFluidUpdate() && !blockState.getFluidState().isEmpty()) {
                                            mutableBlockPos.set(blockX, blockY, blockZ);
                                            chunk.markPosForPostprocessing(mutableBlockPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                noiseChunk.swapSlices();
            }

            noiseChunk.stopInterpolation();
            return chunk;
        }
    }

    public static class SurfaceSource implements SurfaceRules.RuleSource{
        public static final Codec<SurfaceSource> CODEC = Codec.unit(new SurfaceSource());
        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return new KeyDispatchDataCodec<>(CODEC);
        }

        @Override
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            return new SurfaceRule(context);
        }
    }
    public record SurfaceRule(SurfaceRules.Context context) implements SurfaceRules.SurfaceRule{
        @Nullable
        @Override
        public BlockState tryApply(int pX, int pY, int pZ) {
//            Holder<Biome> biome = context.biome.get();
            return Blocks.STONE.defaultBlockState();
        }
    }
}
