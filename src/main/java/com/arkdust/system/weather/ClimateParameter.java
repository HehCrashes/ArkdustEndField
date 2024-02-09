package com.arkdust.system.weather;

import com.arkdust.registry.regtype.ResourceKeyRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.joml.Math;

/**
 * ClimateParameter气候参数
 * 气候参数是Arkdust添加的天气系统中的元单位，用于决定天气的生成。
 * 气候参数需要以下内容：默认值，默认变化率，范围
 * 气候参数执行的行为包括：在每个气候参数周期根据变化率改变值，并修正变化率
 *
 * 气候突变机会系数 mutationChanceCoe 决定了气候突变的概率。在abs(range)=range时，机会为0.2；range=0时，机会为0.8。在这一基础上乘以突变系数将会得到突变几率。
 * 气候突变强度系数 mutationStrengthCoe 决定了气候突变的强度。
 */
public record ClimateParameter(float range, float defaultValue, float defaultRate, float mutationChanceCoe, float mutationStrengthCoe) {
    public ClimateParameter(float range, float defaultValue, float defaultRate,@Range(from = 0L,to = 1L) float mutation) {
        this(range,defaultValue,defaultRate,mutation,mutation);
    }

    public static class ActivatedState implements INBTSerializable<CompoundTag> {
        private ClimateParameter climate;
        public float value;
        public float rate;
        public float offset;//offset参数用于在获取actuallyValue时进行偏移计算，便于在不同的环境下匹配同一种weather

        public ActivatedState(ClimateParameter climate, float defaultValue, float defaultRate, float offset){
            this.climate = climate;
            this.value = climate.defaultValue;
            this.rate = climate.defaultRate;
            this.offset = offset;
        }

        public ActivatedState(ClimateParameter climate, float defaultValue, float defaultRate){
            this(climate,defaultValue,defaultRate,0);
        }

        public ActivatedState(ClimateParameter climate){
            this(climate, climate.defaultValue, climate.defaultRate);
        }

        public ActivatedState(CompoundTag tag){
            deserializeNBT(tag);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("climate",ResourceKeyRegistry.CLIMATE_PARAMETER.getKey(climate).toString());
            tag.putFloat("value",value);
            tag.putFloat("rate",rate);
            tag.putFloat("offset",offset);
            return tag;
        }

        ClimateParameter getClimate(){
            return climate;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.climate = ResourceKeyRegistry.CLIMATE_PARAMETER.get(new ResourceLocation(tag.getString("climate")));
            this.value = tag.getFloat("value");
            this.rate = tag.getFloat("rate");
            this.offset = tag.getFloat("offset");
        }

        public void climateTick(@Nullable RandomSource random){
            random = random == null ? RandomSource.create() : random;
            this.value = Math.clamp(-climate.range,climate.range,value + rate);
            //初阶速率变换 向0方向偏折0.2至-0.1
            rate -= value * (random.nextFloat() * 0.15F + 0.15F);

            //速率突变
            if(random.nextFloat() > (0.2F + 0.6F * java.lang.Math.abs(value / climate.range)) * climate.mutationChanceCoe){
                rate += (random.nextBoolean() ? 1 : -1) * climate.range * ( 0.2F + 0.1F * random.nextFloat()) * climate.mutationStrengthCoe;
            }

            rate = Math.clamp(0.4F*climate.range,-0.4F*climate.range,rate);
        }

        public float getActuallyValue(){
            return value + offset;
        }
    }
}
