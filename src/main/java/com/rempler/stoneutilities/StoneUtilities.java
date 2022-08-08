package com.rempler.stoneutilities;

import com.rempler.stoneutilities.common.blocks.hopper.client.StoneHopperScreen;
import com.rempler.stoneutilities.common.init.StoneBlockEntites;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneConfig;
import com.rempler.stoneutilities.common.init.StoneContainers;
import com.rempler.stoneutilities.common.init.StoneEntities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Mod(StoneUtilities.MODID)
public class StoneUtilities {
    public static final String MODID = "stoneutilities";
    public static TagKey<Item> STONE_RODS = ItemTags.create(new ResourceLocation("forge", "rods/stone"));
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return StoneItems.STONE_CRAFTING_TABLE.get().getDefaultInstance();
        }
    };

    public StoneUtilities() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StoneConfig.COMMON_CONFIG);
        StoneConfig.loadConfig(StoneConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
        StoneBlocks.init(eventBus);
        StoneItems.init(eventBus);
        StoneBlockEntites.init(eventBus);
        StoneEntities.init(eventBus);
        StoneContainers.init(eventBus);
        eventBus.addListener(EventHandler::registerClient);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::blockBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::onBlockBreak);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        private EventHandler() {}

        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if (!event.getWorld().isClientSide() && !event.getPlayer().isCreative() && (event.getState().getBlock() == Blocks.STONE
                            || event.getState().getBlock() == Blocks.COBBLESTONE)) {
                ItemStack held = event.getPlayer().getMainHandItem();
                if (!(event.getPlayer() instanceof FakePlayer) && held.isEmpty()) {
                    int j = new Random().nextInt(StoneConfig.getMaxShingleDrops());
                    ItemStack stack = StoneItems.STONE_SHARD.get().getDefaultInstance();
                    if (ModList.get().isLoaded("exnihilosequentia")) {
                        ResourceLocation pebble = new ResourceLocation("exnihilosequentia", "pebble_stone");
                        if (ForgeRegistries.ITEMS.containsKey(pebble)) {
                            stack = new ItemStack(ForgeRegistries.ITEMS.getValue(pebble));
                        }
                    }
                    for (int i = 0; i <= j; i++) {
                        event.getWorld().addFreshEntity(new ItemEntity((Level) event.getWorld(), event.getPos().getX() + .5, event.getPos().getY() + .5, event.getPos().getZ() + .5, stack));
                    }
                }
            }
        }

        public static void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
            if (event.getState().getBlock() == Blocks.STONE || event.getState().getBlock() == Blocks.COBBLESTONE) {
                double speed = StoneConfig.getBreakSpeed();
                if (event.getPlayer().getMainHandItem().isEmpty()) {
                    event.setNewSpeed((float) speed);
                }
            }
        }

        public static void registerClient(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(StoneBlocks.STONE_LADDER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(StoneBlocks.STONE_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(StoneBlocks.WALL_STONE_TORCH.get(), RenderType.cutout());
            MenuScreens.register(StoneContainers.STONE_HOPPER.get(), StoneHopperScreen::new);
            EntityRenderers.register(StoneEntities.STONE_HOPPER_MINECART.get(), (context) ->
                    new MinecartRenderer<>(context, ModelLayers.MINECART));
        }

        public static void handleStoneHopperMinecartSpawn(Entity entity) {
			if(entity instanceof AbstractMinecart) {
                Minecraft.getInstance().getSoundManager().play(new MinecartSoundInstance((AbstractMinecart) entity));
            }
        }
    }
}
