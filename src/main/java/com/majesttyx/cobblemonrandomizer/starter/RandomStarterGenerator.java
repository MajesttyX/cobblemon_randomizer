package com.majesttyx.cobblemonrandomizer.starter;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.majesttyx.cobblemonrandomizer.CobblemonRandomizer;
import com.majesttyx.cobblemonrandomizer.config.CobblemonRandomizerConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class RandomStarterGenerator {
    private static final Set<String> BABY_SPECIES = Set.of(
            "pichu",
            "cleffa",
            "igglybuff",
            "togepi",
            "tyrogue",
            "smoochum",
            "elekid",
            "magby",
            "azurill",
            "wynaut",
            "budew",
            "chingling",
            "bonsly",
            "mimejr",
            "happiny",
            "munchlax",
            "riolu",
            "mantyke",
            "toxel"
    );

    private static final Set<String> LEGENDARY_LABELS = Set.of(
            "legendary",
            "sub-legendary",
            "sub_legendary",
            "restricted_legendary",
            "restricted-legendary"
    );

    private static final Set<String> MYTHICAL_LABELS = Set.of(
            "mythical"
    );

    private static final Set<String> ULTRA_BEAST_LABELS = Set.of(
            "ultra_beast",
            "ultrabeast",
            "ultra-beast"
    );

    private static final Set<String> PARADOX_LABELS = Set.of(
            "paradox",
            "past",
            "future"
    );

    private RandomStarterGenerator() {
    }

    public static List<String> generateStarterSpeciesIds(UUID playerUuid, int count) {
        List<Species> candidates = getEligibleSpecies();

        if (candidates.isEmpty()) {
            CobblemonRandomizer.LOGGER.error("No eligible randomized starter candidates were found.");
            return List.of();
        }

        candidates.sort(Comparator.comparing(species -> species.getResourceIdentifier().toString()));

        Random random = new Random(playerUuid.getMostSignificantBits() ^ playerUuid.getLeastSignificantBits() ^ System.nanoTime());
        List<Species> shuffled = new ArrayList<>(candidates);
        java.util.Collections.shuffle(shuffled, random);

        int finalCount = Math.min(Math.max(count, 1), shuffled.size());
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < finalCount; i++) {
            selected.add(shuffled.get(i).getResourceIdentifier().toString());
        }

        return selected;
    }

    private static List<Species> getEligibleSpecies() {
        List<Species> candidates = new ArrayList<>();

        for (Species species : PokemonSpecies.INSTANCE.getImplemented()) {
            if (species == null) {
                continue;
            }

            if (!isEligibleStarterSpecies(species)) {
                continue;
            }

            candidates.add(species);
        }

        return candidates;
    }

    private static boolean isEligibleStarterSpecies(Species species) {
        if (!species.getImplemented()) {
            return false;
        }

        if (!isFirstInEvolutionLine(species)) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowSingleStagePokemon() && species.getEvolutions().isEmpty()) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowBabyPokemon() && isKnownBabySpecies(species)) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowLegendaries() && hasAnyLabel(species, LEGENDARY_LABELS)) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowMythicals() && hasAnyLabel(species, MYTHICAL_LABELS)) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowUltraBeasts() && hasAnyLabel(species, ULTRA_BEAST_LABELS)) {
            return false;
        }

        if (!CobblemonRandomizerConfig.allowParadoxPokemon() && hasAnyLabel(species, PARADOX_LABELS)) {
            return false;
        }

        return true;
    }

    private static boolean isFirstInEvolutionLine(Species species) {
        return species.getPreEvolution() == null;
    }

    private static boolean isKnownBabySpecies(Species species) {
        return BABY_SPECIES.contains(normalizedSpeciesName(species));
    }

    private static boolean hasAnyLabel(Species species, Set<String> labels) {
        for (String label : species.getLabels()) {
            String normalized = normalize(label);
            if (labels.contains(normalized)) {
                return true;
            }
        }

        return false;
    }

    private static String normalizedSpeciesName(Species species) {
        ResourceLocation id = species.getResourceIdentifier();
        return normalize(id.getPath());
    }

    private static String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT)
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
                .replace(".", "");
    }
}