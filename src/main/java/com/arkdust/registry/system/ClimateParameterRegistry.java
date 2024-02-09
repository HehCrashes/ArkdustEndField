package com.arkdust.registry.system;

import com.arkdust.Arkdust;
import com.arkdust.registry.regtype.ResourceKeyRegistry;
import com.arkdust.system.weather.ClimateParameter;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClimateParameterRegistry {
    public static final DeferredRegister<ClimateParameter> REGISTER = DeferredRegister.create(ResourceKeyRegistry.CLIMATE_PARAMETER, Arkdust.MODID);

    public static final DeferredHolder<ClimateParameter,ClimateParameter> HUMIDITY = REGISTER.register("humidity",()->new ClimateParameter(40,5,0.5F,0.8F,0.2F));
}
