package com.rempler.stoneutilities;

import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneConfig;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.Random;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mod(StoneUtilities.MODID)
public class StoneUtilities {
    public static final String MODID = "stoneutilities";
    public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);
    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return StoneItems.STONE_CRAFTING_TABLE.get().getDefaultInstance();
        }
    };

    public StoneUtilities() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StoneConfig.COMMON_CONFIG);
        StoneConfig.loadConfig(StoneConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
        StoneBlocks.init(FMLJavaModLoadingContext.get().getModEventBus());
        StoneItems.init(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::registerClient);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::blockBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::onBlockBreak);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class EventHandler {
        private EventHandler() {}

        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if (!event.getWorld().isClientSide() && !event.getPlayer().isCreative() && (event.getState().getBlock() == Blocks.STONE
                            || event.getState().getBlock() == Blocks.COBBLESTONE)) {
                ItemStack held = event.getPlayer().getMainHandItem();
                if (!(event.getPlayer() instanceof FakePlayer) && held.isEmpty()) {
                    int j = new Random().nextInt(StoneConfig.getMaxShingleDrops());
                    ItemStack stack = StoneItems.STONE_SHARD.get().getDefaultInstance();
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
        }
    }
}
