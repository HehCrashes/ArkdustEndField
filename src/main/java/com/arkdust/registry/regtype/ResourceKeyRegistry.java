package com.arkdust.registry.regtype;

import com.arkdust.Arkdust;
import com.arkdust.system.weather.ClimateParameter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ResourceKeyRegistry {
    public static final Registry<ClimateParameter> CLIMATE_PARAMETER = new RegistryBuilder<>(Keys.CLIMATE_PARAMETER).sync(true).create();


    public static class Keys{
        public static final ResourceKey<Registry<ClimateParameter>> CLIMATE_PARAMETER = create("climate_parameter");


        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(Arkdust.MODID,name));
        }
    }
}
