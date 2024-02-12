package com.arkdust.item;

import com.arkdust.system.world.WorldStateGui;
import com.arkdust.system.world.weather.WeatherSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**TestItem是一个用于测试的物品 内容随时可能变化。
 * */
public class TestItem extends Item {
    public TestItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide){
            Minecraft.getInstance().setScreen(new WorldStateGui());
        }

        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    private void climateTest(Level level,Player player){
        if(!level.isClientSide()){
            WeatherSavedData data = WeatherSavedData.getInstance((ServerLevel) level);
            if(data != null){
                player.sendSystemMessage(Component.literal(data.save(new CompoundTag()).toString()));
                data.climateParaTick((ServerLevel) level);
            }
        }
    }
}
