package com.arkdust.worldgen.dimension;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Predicate;

public class DimensionPre {
    public static abstract class YRelativeDimensionGenerator extends NoiseBasedChunkGenerator{

        public abstract int offsetY(int x,int z);

        public YRelativeDimensionGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> setting) {
            super(biomeSource, setting);
        }

        protected OptionalInt iterateNoiseColumn(LevelHeightAccessor pLevel, RandomState pRandom, int pX, int pZ, @Nullable MutableObject<NoiseColumn> pColumn, @Nullable Predicate<BlockState> pStoppingState) {
            OptionalInt opt = super.iterateNoiseColumn(pLevel, pRandom, pX, pZ, pColumn, pStoppingState);
            if(opt.isPresent()){
                return OptionalInt.of(opt.getAsInt() + offsetY(pX, pZ));
            }
            return opt;
        }

        public ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunk, int minCellY, int cellCountY) {
            NoiseChunk noiseChunk = chunk.getOrCreateNoiseChunk(access -> this.createNoiseChunk(access, structureManager, blender, random));//TODO
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

                    //由下到上的一个竖直区块内的子区块(16^3)
                    LevelChunkSection levelChunkSection = chunk.getSection(sectionIndex);

                    for(int cellY = cellCountY - 1; cellY >= 0; --cellY) {
                        noiseChunk.selectCellYZ(cellY, cellZ);

                        for(int y = cellHeight - 1; y >= 0; --y) {
                            int basicY = (minCellY + cellY) * cellHeight + y;//here is the basic y


                            //在每个噪音细胞内进行处理
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


                                    int blockY = offsetY(blockX,blockZ) + basicY;//here comes the offset count
                                    int yInChunk = blockY & 15;
                                    int sectionIndexInChunk = chunk.getSectionIndex(blockY);
                                    if (sectionIndex != sectionIndexInChunk) {
                                        sectionIndex = sectionIndexInChunk;
                                        levelChunkSection = chunk.getSection(sectionIndex);
                                    }
                                    double yRatio = (double)y / (double)cellHeight;
                                    noiseChunk.updateForY(blockY, yRatio);
                                    //获取应当使用的方块状态
                                    BlockState blockState = noiseChunk.getInterpolatedState();
                                    if (blockState == null) {
                                        blockState = this.generatorSettings().value().defaultBlock();
                                    }
                                    //判断方块放置
                                    if (blockState.is(Blocks.AIR) && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
                                        levelChunkSection.setBlockState(xInChunk, yInChunk, zInChunk, blockState, false);
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

        @Override
        public NoiseChunk createNoiseChunk(ChunkAccess pChunk, StructureManager pStructureManager, Blender pBlender, RandomState pRandom) {
            return super.createNoiseChunk(pChunk, pStructureManager, pBlender, pRandom);
        }
    }
}
