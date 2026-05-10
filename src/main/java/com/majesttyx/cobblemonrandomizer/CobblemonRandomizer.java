package com.majesttyx.cobblemonrandomizer;

import com.cobblemon.mod.common.Cobblemon;
import com.majesttyx.cobblemonrandomizer.config.CobblemonRandomizerConfig;
import com.majesttyx.cobblemonrandomizer.starter.RandomizedStarterHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CobblemonRandomizer.MODID)
public final class CobblemonRandomizer {
    public static final String MODID = "cobblemon_randomizer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonRandomizer(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CobblemonRandomizerConfig.SPEC);

        Cobblemon.INSTANCE.setStarterHandler(new RandomizedStarterHandler(Cobblemon.INSTANCE.getStarterHandler()));

        LOGGER.info("Cobblemon Randomizer loaded and replaced Cobblemon starter handler.");
    }
}