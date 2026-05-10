package com.majesttyx.cobblemonrandomizer.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CobblemonRandomizerConfig {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue STARTER_SELECTION_SIZE;
    private static final ModConfigSpec.IntValue STARTER_LEVEL;

    private static final ModConfigSpec.BooleanValue ALLOW_BABY_POKEMON;
    private static final ModConfigSpec.BooleanValue ALLOW_SINGLE_STAGE_POKEMON;

    private static final ModConfigSpec.BooleanValue ALLOW_LEGENDARIES;
    private static final ModConfigSpec.BooleanValue ALLOW_MYTHICALS;
    private static final ModConfigSpec.BooleanValue ALLOW_ULTRA_BEASTS;
    private static final ModConfigSpec.BooleanValue ALLOW_PARADOX_POKEMON;

    private static final ModConfigSpec.IntValue STARTER_IV_MIN;
    private static final ModConfigSpec.IntValue STARTER_IV_MAX;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("starter_randomizer");

        STARTER_SELECTION_SIZE = builder
                .comment("Number of randomized starter choices shown to each player. The intended Nuzlocke value is 3.")
                .defineInRange("starterSelectionSize", 3, 1, 6);

        STARTER_LEVEL = builder
                .comment("Level applied to the chosen randomized starter. Cobblemon's default starter level is 10.")
                .defineInRange("starterLevel", 10, 1, 100);

        ALLOW_BABY_POKEMON = builder
                .comment("Whether baby Pokemon such as Pichu, Riolu, Togepi, Munchlax, etc. can appear as randomized starters.")
                .define("allowBabyPokemon", true);

        ALLOW_SINGLE_STAGE_POKEMON = builder
                .comment("Whether single-stage Pokemon such as Absol, Lapras, Heracross, etc. can appear as randomized starters.")
                .define("allowSingleStagePokemon", true);

        builder.pop();

        builder.push("restricted_categories");

        ALLOW_LEGENDARIES = builder
                .comment("Whether Pokemon with a legendary-style label can appear.")
                .define("allowLegendaries", false);

        ALLOW_MYTHICALS = builder
                .comment("Whether Pokemon with a mythical-style label can appear.")
                .define("allowMythicals", false);

        ALLOW_ULTRA_BEASTS = builder
                .comment("Whether Pokemon with an ultra-beast-style label can appear.")
                .define("allowUltraBeasts", false);

        ALLOW_PARADOX_POKEMON = builder
                .comment("Whether Pokemon with a paradox-style label can appear.")
                .define("allowParadoxPokemon", false);

        builder.pop();

        builder.push("starter_ivs");

        STARTER_IV_MIN = builder
                .comment("Minimum IV rolled per stat for the chosen starter. Default 0 keeps normal Cobblemon-style full-range IVs.")
                .defineInRange("starterIvMin", 0, 0, 31);

        STARTER_IV_MAX = builder
                .comment("Maximum IV rolled per stat for the chosen starter. Default 31 keeps normal Cobblemon-style full-range IVs.")
                .defineInRange("starterIvMax", 31, 0, 31);

        builder.pop();

        SPEC = builder.build();
    }

    private CobblemonRandomizerConfig() {
    }

    public static int starterSelectionSize() {
        return STARTER_SELECTION_SIZE.get();
    }

    public static int starterLevel() {
        return STARTER_LEVEL.get();
    }

    public static boolean allowBabyPokemon() {
        return ALLOW_BABY_POKEMON.get();
    }

    public static boolean allowSingleStagePokemon() {
        return ALLOW_SINGLE_STAGE_POKEMON.get();
    }

    public static boolean allowLegendaries() {
        return ALLOW_LEGENDARIES.get();
    }

    public static boolean allowMythicals() {
        return ALLOW_MYTHICALS.get();
    }

    public static boolean allowUltraBeasts() {
        return ALLOW_ULTRA_BEASTS.get();
    }

    public static boolean allowParadoxPokemon() {
        return ALLOW_PARADOX_POKEMON.get();
    }

    public static int starterIvMin() {
        return STARTER_IV_MIN.get();
    }

    public static int starterIvMax() {
        return STARTER_IV_MAX.get();
    }
}