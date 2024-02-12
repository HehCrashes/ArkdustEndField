package com.arkdust.registry;

import com.arkdust.Arkdust;
import com.arkdust.datagen.BlockTagGen;
import com.arkdust.helper.ListAndMapHelper;
import com.arkdust.item.TestItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    public static void bootstrap(IEventBus bus) {
        ITEMS_DEFAULT.register(bus);
    }

    public static final DeferredRegister.Items ITEMS_DEFAULT = DeferredRegister.createItems(Arkdust.MODID);

    public static final DeferredItem<Item> TEST_ITEM = ITEMS_DEFAULT.register("test_item",TestItem::new);
    public static final DeferredItem<BlockItem> SPIRIT_STONE_UNACTIVATED = ITEMS_DEFAULT.registerSimpleBlockItem(BlockRegistry.SPIRIT_STONE_UNACTIVATED,new Item.Properties().fireResistant());
    public static final DeferredItem<BlockItem> SPIRIT_STONE_ACTIVATED = ITEMS_DEFAULT.registerSimpleBlockItem(BlockRegistry.SPIRIT_STONE_ACTIVATED,new Item.Properties().fireResistant());
    public static final DeferredItem<BlockItem> SPIRIT_STONE = ITEMS_DEFAULT.registerSimpleBlockItem(BlockRegistry.SPIRIT_STONE,new Item.Properties().fireResistant());
    public static final DeferredItem<BlockItem> SPIRIT_STELA = ITEMS_DEFAULT.registerSimpleBlockItem(BlockRegistry.SPIRIT_STELA,new Item.Properties().fireResistant());
//    public static final DeferredItem<BlockItem> SPIRIT_PORTAL = ITEMS_DEFAULT.registerSimpleBlockItem(BlockRegistry.SPIRIT_PORTAL,new Item.Properties().fireResistant());

    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        List<DeferredHolder<Item, ? extends Item>> list = new ArrayList<>();
        list.addAll(ITEMS_DEFAULT.getEntries());
        return list;
    }


    public static class Rar{
        public static final Rarity NULL = Rarity.create("ard_null", ChatFormatting.DARK_GRAY);
        public static final Rarity COMMON = Rarity.create("ard_common", ChatFormatting.WHITE);
        public static final Rarity SPECIAL = Rarity.create("ard_special", ChatFormatting.DARK_GREEN);
        public static final Rarity VALUABLE = Rarity.create("ard_valuable", ChatFormatting.BLUE);
        public static final Rarity TREASURE = Rarity.create("ard_treasure", ChatFormatting.DARK_PURPLE);
        public static final Rarity RARE = Rarity.create("ard_rare", ChatFormatting.YELLOW);
        public static final Rarity EPIC = Rarity.create("ard_epic", ChatFormatting.GOLD);
        public static final Rarity END = Rarity.create("ard_end", ChatFormatting.RED);
        public static final Rarity INFINITY = Rarity.create("ard_infinity", ChatFormatting.BLACK);
    }
}
