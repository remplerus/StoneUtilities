package com.rempler.stoneutilities;

import com.rempler.stoneutilities.common.blocks.hopper.client.StoneHopperScreen;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneConfig;
import com.rempler.stoneutilities.common.init.StoneContainers;
import com.rempler.stoneutilities.common.init.StoneEntities;
import com.rempler.stoneutilities.common.init.StoneItems;
import com.rempler.stoneutilities.common.init.StoneTiles;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MinecartTickableSound;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.logging.Logger;

@Mod(StoneUtilities.MODID)
public class StoneUtilities {
    public static final String MODID = "stoneutilities";
    public static final Logger LOGGER = Logger.getLogger(MODID);
    public static final ItemGroup TAB = new ItemGroup(MODID) {
        @Nonnull
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
        StoneTiles.init(eventBus);
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
                        event.getWorld().addFreshEntity(new ItemEntity((World) event.getWorld(), event.getPos().getX() + .5, event.getPos().getY() + .5, event.getPos().getZ() + .5, stack));
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
            RenderTypeLookup.setRenderLayer(StoneBlocks.STONE_LADDER.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(StoneBlocks.STONE_TORCH.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(StoneBlocks.WALL_STONE_TORCH.get(), RenderType.cutout());
            ScreenManager.register(StoneContainers.STONE_HOPPER.get(), StoneHopperScreen::new);
            RenderingRegistry.registerEntityRenderingHandler(StoneEntities.STONE_HOPPER_MINECART.get(), MinecartRenderer::new);
        }

        public static void handleStoneHopperMinecartSpawn(Entity entity) {
        if(entity instanceof AbstractMinecartEntity) {
                Minecraft.getInstance().getSoundManager().play(new MinecartTickableSound((AbstractMinecartEntity) entity));
            }
        }
    }
}
