package com.majesttyx.cobblemonrandomizer.starter;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.advancement.CobblemonCriteria;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.starter.StarterHandler;
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes;
import com.cobblemon.mod.common.config.starter.StarterCategory;
import com.cobblemon.mod.common.net.messages.client.starter.OpenStarterUIPacket;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules;
import com.majesttyx.cobblemonrandomizer.CobblemonRandomizer;
import com.majesttyx.cobblemonrandomizer.config.CobblemonRandomizerConfig;
import kotlin.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomizedStarterHandler implements StarterHandler {
    private static final String CATEGORY_NAME = "randomized_nuzlocke";
    private static final String CATEGORY_DISPLAY_NAME = "Randomized Nuzlocke";

    private static final String ROOT_TAG = "CobblemonRandomizer";
    private static final String STARTER_OPTIONS_TAG = "StarterOptions";

    private final StarterHandler fallback;

    public RandomizedStarterHandler(StarterHandler fallback) {
        this.fallback = fallback;
    }

    @Override
    public List<StarterCategory> getStarterList(ServerPlayer player) {
        List<String> starterIds = getOrCreateStarterIds(player);
        List<PokemonProperties> properties = new ArrayList<>();

        for (String starterId : starterIds) {
            Species species = resolveSpecies(starterId);
            if (species == null) {
                CobblemonRandomizer.LOGGER.warn(
                        "Skipping unresolved randomized starter species id {} for player {}.",
                        starterId,
                        player.getGameProfile().getName()
                );
                continue;
            }

            properties.add(PokemonProperties.Companion.parse(toPokemonPropertiesString(species)));
        }

        if (properties.isEmpty()) {
            CobblemonRandomizer.LOGGER.warn(
                    "Randomized starter list was empty for player {}; falling back to Cobblemon starters.",
                    player.getGameProfile().getName()
            );
            return fallback.getStarterList(player);
        }

        return List.of(new StarterCategory(CATEGORY_NAME, CATEGORY_DISPLAY_NAME, properties));
    }

    @Override
    public void handleJoin(ServerPlayer player) {
        fallback.handleJoin(player);
    }

    @Override
    public void requestStarterChoice(ServerPlayer player) {
        var playerData = Cobblemon.INSTANCE.getPlayerDataManager().getGenericData(player);

        if (playerData.getStarterSelected()) {
            fallback.requestStarterChoice(player);
            return;
        }

        if (playerData.getStarterLocked()) {
            fallback.requestStarterChoice(player);
            return;
        }

        OpenStarterUIPacket packet = new OpenStarterUIPacket(getStarterList(player));
        packet.sendToPlayer(player);

        playerData.setStarterPrompted(true);
        Cobblemon.INSTANCE.getPlayerDataManager().saveSingle(
                playerData,
                PlayerInstancedDataStoreTypes.INSTANCE.getGENERAL()
        );
    }

    @Override
    public void chooseStarter(ServerPlayer player, String categoryName, int index) {
        if (!CATEGORY_NAME.equals(categoryName)) {
            fallback.chooseStarter(player, categoryName, index);
            return;
        }

        var playerData = Cobblemon.INSTANCE.getPlayerDataManager().getGenericData(player);

        if (playerData.getStarterSelected() || playerData.getStarterLocked()) {
            fallback.chooseStarter(player, categoryName, index);
            return;
        }

        List<StarterCategory> categories = getStarterList(player);
        StarterCategory category = categories.stream()
                .filter(candidate -> CATEGORY_NAME.equals(candidate.getName()))
                .findFirst()
                .orElse(null);

        if (category == null) {
            return;
        }

        if (index < 0 || index >= category.getPokemon().size()) {
            return;
        }

        PokemonProperties properties = category.getPokemon().get(index);
        Pokemon pokemon = properties.create(player);

        applyConfiguredLevel(pokemon);
        applyConfiguredIvs(pokemon);

        CobblemonEvents.STARTER_CHOSEN.postThen(
                new StarterChosenEvent(player, properties, pokemon),
                event -> {
                    CobblemonRandomizer.LOGGER.info(
                            "Randomized starter choice was cancelled for player {}.",
                            player.getGameProfile().getName()
                    );

                    return Unit.INSTANCE;
                },
                event -> {
                    Pokemon selectedPokemon = event.getPokemon();

                    Cobblemon.INSTANCE.getStorage().getParty(player).add(selectedPokemon);

                    playerData.setStarterSelected(true);
                    playerData.setStarterUUID(selectedPokemon.getUuid());

                    if (player.level().getGameRules().getBoolean(CobblemonGameRules.SHINY_STARTERS)) {
                        selectedPokemon.setShiny(true);
                    }

                    CobblemonCriteria.PICK_STARTER.trigger(player, selectedPokemon);
                    Cobblemon.INSTANCE.getPlayerDataManager().saveSingle(
                            playerData,
                            PlayerInstancedDataStoreTypes.INSTANCE.getGENERAL()
                    );
                    playerData.sendToPlayer(player);

                    CobblemonRandomizer.LOGGER.info(
                            "Player {} chose randomized starter {}.",
                            player.getGameProfile().getName(),
                            selectedPokemon.getSpecies().getName()
                    );

                    return Unit.INSTANCE;
                }
        );
    }

    private static List<String> getOrCreateStarterIds(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag root = persistentData.getCompound(ROOT_TAG);

        List<String> existing = readStarterIds(root);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<String> generated = RandomStarterGenerator.generateStarterSpeciesIds(
                player.getUUID(),
                CobblemonRandomizerConfig.starterSelectionSize()
        );

        writeStarterIds(root, generated);
        persistentData.put(ROOT_TAG, root);

        CobblemonRandomizer.LOGGER.info(
                "Generated {} randomized starter options for player {}: {}",
                generated.size(),
                player.getGameProfile().getName(),
                generated
        );

        return generated;
    }

    private static List<String> readStarterIds(CompoundTag root) {
        if (!root.contains(STARTER_OPTIONS_TAG)) {
            return List.of();
        }

        ListTag listTag = root.getList(STARTER_OPTIONS_TAG, 8);
        List<String> values = new ArrayList<>();

        for (int i = 0; i < listTag.size(); i++) {
            String value = listTag.getString(i);
            if (value != null && !value.isBlank()) {
                values.add(value);
            }
        }

        return values;
    }

    private static void writeStarterIds(CompoundTag root, List<String> values) {
        ListTag listTag = new ListTag();

        for (String value : values) {
            listTag.add(StringTag.valueOf(value));
        }

        root.put(STARTER_OPTIONS_TAG, listTag);
    }

    private static Species resolveSpecies(String rawId) {
        ResourceLocation id = ResourceLocation.tryParse(rawId);
        if (id == null) {
            return null;
        }

        return PokemonSpecies.INSTANCE.getByIdentifier(id);
    }

    private static String toPokemonPropertiesString(Species species) {
        return "species=\"" + species.getResourceIdentifier() + "\" level=" + CobblemonRandomizerConfig.starterLevel();
    }

    private static void applyConfiguredLevel(Pokemon pokemon) {
        pokemon.setLevel(CobblemonRandomizerConfig.starterLevel());
    }

    private static void applyConfiguredIvs(Pokemon pokemon) {
        int min = clamp(CobblemonRandomizerConfig.starterIvMin(), 0, IVs.MAX_VALUE);
        int max = clamp(CobblemonRandomizerConfig.starterIvMax(), 0, IVs.MAX_VALUE);

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        pokemon.setIV(Stats.HP, rollInclusive(min, max));
        pokemon.setIV(Stats.ATTACK, rollInclusive(min, max));
        pokemon.setIV(Stats.DEFENCE, rollInclusive(min, max));
        pokemon.setIV(Stats.SPECIAL_ATTACK, rollInclusive(min, max));
        pokemon.setIV(Stats.SPECIAL_DEFENCE, rollInclusive(min, max));
        pokemon.setIV(Stats.SPEED, rollInclusive(min, max));
    }

    private static int rollInclusive(int min, int max) {
        if (min == max) {
            return min;
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}