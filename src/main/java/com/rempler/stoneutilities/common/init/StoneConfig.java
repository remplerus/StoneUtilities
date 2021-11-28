package com.rempler.stoneutilities.common.init;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class StoneConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue breakSpeed;
    private static final ForgeConfigSpec.IntValue maxShardDrops;

    static {
        breakSpeed = COMMON_BUILDER.comment("Max break speed on stone (Default: 5D)")
                .defineInRange("breakSpeed", 5D, 0D, Double.MAX_VALUE);
        maxShardDrops = COMMON_BUILDER.comment("max random drops (Default: 3)")
                .defineInRange("maxShardDrops", 3, 1, Integer.MAX_VALUE);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static double getBreakSpeed() { return breakSpeed.get(); }
    public static int getMaxShingleDrops() { return maxShardDrops.get(); }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}
