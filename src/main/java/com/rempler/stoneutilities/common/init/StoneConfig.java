package com.rempler.stoneutilities.common.init;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class StoneConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue breakSpeed;
    private static final ForgeConfigSpec.IntValue maxShardDrops;
    private static final ForgeConfigSpec.IntValue shearDurability;
    private static final ForgeConfigSpec.IntValue helmetDurability;
    private static final ForgeConfigSpec.IntValue chestplateDurability;
    private static final ForgeConfigSpec.IntValue leggingsDurability;
    private static final ForgeConfigSpec.IntValue bootsDurability;

    static {
        breakSpeed = COMMON_BUILDER.comment("Max break speed on stone (Default: 5D)")
                .defineInRange("breakSpeed", 5D, 0D, Double.MAX_VALUE);
        maxShardDrops = COMMON_BUILDER.comment("max random drops (Default: 3)")
                .defineInRange("maxShardDrops", 3, 1, Integer.MAX_VALUE);
        shearDurability = COMMON_BUILDER.comment("durability of stone shear (Default: 82)")
                .defineInRange("shearDurability", 82, 1, Integer.MAX_VALUE);

        COMMON_BUILDER.push("armor");
        helmetDurability = COMMON_BUILDER.comment("helmet durability (Default: 82)")
                .defineInRange("helmetDurability", 82, 1, Integer.MAX_VALUE);
        chestplateDurability = COMMON_BUILDER.comment("chestplate durability (Default: 142)")
                .defineInRange("chestplateDurability", 142, 1, Integer.MAX_VALUE);
        leggingsDurability = COMMON_BUILDER.comment("leggings durability (Default: 122)")
                .defineInRange("leggingsDurability", 122, 1, Integer.MAX_VALUE);
        bootsDurability = COMMON_BUILDER.comment("boots durability (Default: 92)")
                .defineInRange("bootsDurability", 92, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static double getBreakSpeed() { return breakSpeed.get(); }
    public static int getMaxShingleDrops() { return maxShardDrops.get(); }
    public static int getShearDurability() { return shearDurability.get(); }
    public static int getHelmetDurability() { return helmetDurability.get(); }
    public static int getChestplateDurability() { return chestplateDurability.get(); }
    public static int getLeggingsDurability() { return leggingsDurability.get(); }
    public static int getBootsDurability() { return bootsDurability.get(); }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

}
