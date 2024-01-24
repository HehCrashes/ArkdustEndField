package com.arkdust.blocks.levelblocks;

import com.arkdust.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

public class SpiritStoneBlocks {
    public static class Unactivated extends Block{
        public Unactivated() {
            super(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i)->1));
        }

        public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
            if(CommonHooks.isCorrectToolForDrops(pState, pPlayer)){
                spawnParticles(pPlayer.level(), pPos, ParticleTypes.SOUL_FIRE_FLAME,3);
                return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
            }
            spawnParticles(pPlayer.level(), pPos, ParticleTypes.ENCHANT,2);
            return 0;
        }

        private void spawnParticles(Level world, BlockPos pos, ParticleOptions particle, int count) {
            RandomSource random = world.getRandom();
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + random.nextGaussian() * 0.6;
                double y = pos.getY() + 0.5 + random.nextGaussian() * 0.6;
                double z = pos.getZ() + 0.5 + random.nextGaussian() * 0.6;
                world.addParticle(particle, x, y, z, 0,0,0);
            }
        }
    }
}
