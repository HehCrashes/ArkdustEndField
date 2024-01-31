package com.arkdust.worldgen.dimension;

import com.arkdust.Arkdust;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class SarconDimension{
    public static final ResourceKey<LevelStem> STEM = ResourceKey.create(Registries.LEVEL_STEM,new ResourceLocation(Arkdust.MODID,"resource/sarcon"));
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(Registries.DIMENSION,new ResourceLocation(Arkdust.MODID,"resource/sarcon"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,new ResourceLocation(Arkdust.MODID,"resource/sarcon_type"));

    public static final DimensionType TYPE_INSTANCE = new DimensionType(OptionalLong.empty(),true,false,false,false,0,true,false,
            0,464,464, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS,0,new DimensionType.MonsterSettings(true,true, UniformInt.of(0, 6),9));

//    public static class Generator extends ChunkGenerator {
//
//        // 定义一个方法，传入两个int参数x和y，返回一个int参数height
//        public static int getHeight(int x, int y) {
//            // 定义一个常量k，表示直线y = -2x + k的截距
//            final int k = -1200;
//            // 定义一个变量height，用于存储高度值
//            int height = 0;
//            // 判断坐标(x, y)是否在直线y = -2x + k上
//            if (y == -2 * x + k) {
//                // 如果在直线上，高度值为344
//                height = 344;
//            } else if (y < -2 * x + k) {
//                // 如果在直线的左侧，高度值递减的速度呈先快后慢的趋势
//                // 计算坐标(x, y)到直线y = -2x + k的距离
//                double distance = Math.abs(y + 2 * x - k) / 2.236F;
//                // 计算坐标(x, y)到原点(0, 0)的距离
//                double origin = Math.sqrt(x * x + y * y);
//                // 计算m的值，m是距离直线约1200格时高度值为172的位置
//                double m = 1200 + origin / 20;
//                // 判断距离是否小于40
//                if (distance < 40) {
//                    // 如果小于40，高度值为344减去距离的3倍
//                    height = 344 - (int) (distance * 3);
//                } else if (distance < m) {
//                    // 如果大于等于40且小于m，高度值为172减去距离减去40的0.1倍
//                    height = 172 - (int) ((distance - 40) * 0.1);
//                } else {
//                    // 如果大于等于m，高度值为172减去m减去40的0.1倍再减去距离减去m的0.05倍
//                    height = 172 - (int) ((m - 40) * 0.1) - (int) ((distance - m) * 0.05);
//                }
//            } else {
//                // 如果在直线的右侧，高度值的递减速度呈较平缓的先小后大再小的趋势
//                // 计算坐标(x, y)到直线y = -2x + k的距离
//                double distance = Math.abs(y + 2 * x - k) / 2.236F;
//                // 计算坐标(x, y)到原点(0, 0)的距离
//                double origin = Math.sqrt(x * x + y * y);
//                // 计算n的值，n是距离直线约1800格时高度值下降30的位置
//                double n = 1800 + origin / 400;
//                // 计算p的值，p是距离直线约4000格时高度值再下降50的位置
//                double p = 4000 + origin / 100;
//                // 判断距离是否小于n
//                if (distance < n) {
//                    // 如果小于n，高度值为344减去距离的0.0167倍
//                    height = 344 - (int) (distance * 0.0167);
//                } else if (distance < p) {
//                    // 如果大于等于n且小于p，高度值为314减去距离减去n的0.0125倍
//                    height = 314 - (int) ((distance - n) * 0.0125);
//                } else {
//                    // 如果大于等于p，高度值为264减去距离减去p的0.01倍
//                    height = 264 - (int) ((distance - p) * 0.01);
//                }
//            }
//            // 返回高度值
//            return height;
//        }
//
//        @Override
//        public CompletableFuture<ChunkAccess> createBiomes(Executor pExecutor, RandomState pRandomState, Blender pBlender, StructureManager pStructureManager, ChunkAccess pChunk) {
//            return super.createBiomes(pExecutor, pRandomState, pBlender, pStructureManager, pChunk);
//        }
//
//        public Generator(BiomeSource pBiomeSource) {
//            super(pBiomeSource);
//        }
//
//        @Override
//        protected Codec<? extends ChunkGenerator> codec() {
//            return ;
//        }
//
//        @Override
//        public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {
//
//        }
//
//        @Override
//        public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {
//
//        }
//
//        @Override
//        public void spawnOriginalMobs(WorldGenRegion pLevel) {
//
//        }
//
//        @Override
//        public int getGenDepth() {
//            return 400;
//        }
//
//        @Override
//        public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
//            return null;
//        }
//
//        @Override
//        public int getSeaLevel() {
//            return 0;
//        }
//
//        @Override
//        public int getMinY() {
//            return 200;
//        }
//
//        @Override
//        public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
//            return 0;
//        }
//
//        @Override
//        public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pHeight, RandomState pRandom) {
//            return null;
//        }
//
//        @Override
//        public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {
//
//        }
//    }
}
