package com.arkdust;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.LevelEvent;

@Mod.EventBusSubscriber(modid = Arkdust.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusConsumer {
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event){
//        event.getLevel() = null;
//        event.getLevel()
    }
}
