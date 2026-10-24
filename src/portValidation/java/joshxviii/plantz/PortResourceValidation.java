package joshxviii.plantz;

import com.google.gson.*;
import com.mojang.serialization.*;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.nio.file.*;
import java.util.*;

/** Decodes ported data without starting a client/server or loading a world.
 * Mod references use vanilla stand-ins and custom components are excluded:
 * this checks schemas and local data-reference existence, not gameplay,
 * component contents, or code-registered item/block/entity identities.
 */
public final class PortResourceValidation {
    private static int checked;
    private static final List<String> errors = new ArrayList<>();
    private static Path dataRoot;
    private static final Set<String> dataRegistries = new HashSet<>();

    public static void main(String[] args) throws Exception {
        dataRoot = Path.of(args[0]);
        SharedConstants.tryDetectVersion();
        var stderr = System.err;
        Bootstrap.bootStrap();
        System.setOut(Bootstrap.STDOUT);
        System.setErr(stderr);
        var world = VanillaRegistries.createWorldLookup();
        var reloadable = VanillaRegistries.createReloadableLookup(world);
        var vanilla = HolderLookup.Provider.create(java.util.stream.Stream.concat(world.listRegistries(), reloadable.listRegistries()).distinct());
        var worldCodecs = new LinkedHashMap<String, Codec<?>>();
        java.util.stream.Stream.concat(RegistryDataLoader.WORLD_REGISTRIES.stream(), RegistryDataLoader.DIMENSION_REGISTRIES.stream())
            .forEach(registry -> worldCodecs.put(registry.key().identifier().getPath(), registry.elementCodec()));
        dataRegistries.addAll(worldCodecs.keySet());
        RegistryDataLoader.RELOADABLE_REGISTRIES.forEach(registry -> dataRegistries.add(registry.key().identifier().getPath()));
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, new RegistryOps.RegistryInfoLookup() {
            public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> registry) {
                return vanilla.lookup(registry).map(delegate -> new HolderGetter<T>() {
                    public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                        var found = delegate.get(key);
                        if (found.isEmpty() && !key.identifier().getNamespace().equals("minecraft")) {
                            validateOwnReference(key.identifier(), registry.identifier().getPath(), "registry reference");
                            return delegate.listElements().findFirst();
                        }
                        return found;
                    }
                    public Optional<HolderSet.Named<T>> get(TagKey<T> tag) {
                        return delegate.get(tag).or(() -> Optional.of(HolderSet.emptyNamed(this, tag)));
                    }
                    public boolean canSerializeIn(HolderOwner<T> owner) { return true; }
                });
            }
        });
        try (var paths = Files.walk(Path.of(args[0]))) {
            for (var path : paths.filter(p -> p.toString().endsWith(".json")).sorted().toList()) {
                JsonElement json = JsonParser.parseString(Files.readString(path));
                String relative = Path.of(args[0]).relativize(path).toString().replace('\\', '/');
                if (relative.contains("/tags/")) {
                    validateTagReferences(Path.of(args[0]), path, relative, json, vanilla);
                    continue;
                }
                if (relative.contains("/loot_table/") || relative.contains("/predicate/")) rejectLegacyLootKeys(path, json);
                if (relative.contains("/worldgen/")) rejectLegacySpawnKeys(path, json);
                if (relative.contains("/advancement/")) validateAdvancementReferences(path, json);
                json = substituteModReferences(json, "", relative.contains("/worldgen/"));
                String dataPath = relative.substring(relative.indexOf('/') + 1);
                var worldCodec = worldCodecs.entrySet().stream().filter(entry -> dataPath.startsWith(entry.getKey() + "/")).findFirst();
                if (worldCodec.isPresent()) decode(path, worldCodec.get().getValue(), json, ops);
                else if (relative.contains("/loot_table/")) decode(path, LootTable.DIRECT_CODEC, json, ops);
                else if (relative.contains("/villager_trade/")) decode(path, VillagerTrade.CODEC, json, ops);
                else if (relative.contains("/recipe/")) decode(path, Recipe.CODEC, json, ops);
                else if (relative.contains("/predicate/")) decode(path, LootItemCondition.DIRECT_CODEC, json, ops);
                else if (relative.contains("/advancement/")) {
                    for (var entry : json.getAsJsonObject().getAsJsonObject("criteria").entrySet()) {
                        var criterion = entry.getValue().getAsJsonObject();
                        String trigger = criterion.get("trigger").getAsString();
                        var conditions = criterion.has("conditions") ? criterion.get("conditions") : new JsonObject();
                        var id = Identifier.parse(trigger);
                        if (id.getNamespace().equals("minecraft")) {
                            var type = BuiltInRegistries.TRIGGER_TYPES.getValue(id);
                            if (type == null) errors.add(path + ": unknown trigger " + trigger);
                            else decode(path, type.codec(), conditions, ops);
                        } else {
                            Codec<?> codec = switch (id.getPath()) {
                                case "raid_wave" -> new joshxviii.plantz.advancement.RaidWaveTrigger().codec();
                                case "wallnut_bowling" -> new joshxviii.plantz.advancement.WallNutBowlingTrigger().codec();
                                default -> joshxviii.plantz.advancement.SimpleCheckCriteria.Companion.getCODEC();
                            };
                            decode(path, codec, conditions, ops);
                        }
                    }
                }
            }
        }
        errors.forEach(System.err::println);
        if (!errors.isEmpty()) throw new IllegalStateException(errors.size() + " invalid resources");
        System.out.println("Validated " + checked + " resources/criteria with Minecraft 26.3 codecs, required tags, and local data-reference existence (mod values use stand-ins).");
    }

    private static void decode(Path path, Codec<?> codec, JsonElement json, RegistryOps<JsonElement> ops) {
        checked++;
        codec.parse(ops, json).error().ifPresent(error -> errors.add(path + ": " + error.message()));
    }

    private static void validateOwnReference(Identifier id, String registry, String source) {
        if (!id.getNamespace().equals("plantz") || !dataRegistries.contains(registry)) return;
        Path resource = dataRoot.resolve(id.getNamespace()).resolve(registry).resolve(id.getPath() + ".json");
        if (!Files.isRegularFile(resource)) errors.add(source + ": missing " + registry + " resource " + id);
    }

    private static void validateAdvancementReferences(Path path, JsonElement json) {
        if (json.isJsonArray()) json.getAsJsonArray().forEach(value -> validateAdvancementReferences(path, value));
        if (json.isJsonObject()) {
            for (var entry : json.getAsJsonObject().entrySet()) {
                String registry = switch (entry.getKey()) {
                    case "recipes" -> "recipe";
                    case "loot" -> "loot_table";
                    case "parent" -> "advancement";
                    default -> null;
                };
                if (registry != null) {
                    var ids = entry.getValue().isJsonArray() ? entry.getValue().getAsJsonArray() : new JsonArray();
                    if (!entry.getValue().isJsonArray()) ids.add(entry.getValue());
                    for (var id : ids) {
                        if (id.isJsonPrimitive() && id.getAsJsonPrimitive().isString() && !id.getAsString().startsWith("#")) {
                            validateOwnReference(Identifier.parse(id.getAsString()), registry, path.toString());
                        }
                    }
                }
                validateAdvancementReferences(path, entry.getValue());
            }
        }
    }

    private static void validateTagReferences(Path root, Path path, String relative, JsonElement json, HolderLookup.Provider registries) {
        String body = relative.substring(relative.indexOf("/tags/") + 6);
        String registry = registries.listRegistryKeys().map(key -> key.identifier().getPath())
            .filter(key -> body.startsWith(key + "/")).max(Comparator.comparingInt(String::length)).orElse(null);
        if (registry == null) {
            errors.add(path + ": unknown tag registry");
            return;
        }
        checked++;
        for (var entry : json.getAsJsonObject().getAsJsonArray("values")) {
            if (entry.isJsonObject() && entry.getAsJsonObject().has("required") && !entry.getAsJsonObject().get("required").getAsBoolean()) continue;
            String id = entry.isJsonObject() ? entry.getAsJsonObject().get("id").getAsString() : entry.getAsString();
            if (!id.startsWith("#")) {
                validateOwnReference(Identifier.parse(id), registry, path.toString());
                continue;
            }
            var tag = Identifier.parse(id.substring(1));
            String resource = tag.getNamespace() + "/tags/" + registry + "/" + tag.getPath() + ".json";
            if (!Files.exists(root.resolve(resource)) && PortResourceValidation.class.getResource("/data/" + resource) == null) {
                errors.add(path + ": missing required tag " + id);
            }
        }
    }

    private static void rejectLegacyLootKeys(Path path, JsonElement json) {
        if (json.isJsonArray()) json.getAsJsonArray().forEach(value -> rejectLegacyLootKeys(path, value));
        if (json.isJsonObject()) {
            for (var entry : json.getAsJsonObject().entrySet()) {
                if (Set.of("functions", "conditions", "function").contains(entry.getKey()) ||
                    (entry.getKey().equals("condition") && entry.getValue().isJsonPrimitive())) {
                    errors.add(path + ": obsolete loot key " + entry.getKey());
                }
                rejectLegacyLootKeys(path, entry.getValue());
            }
        }
    }

    private static void rejectLegacySpawnKeys(Path path, JsonElement json) {
        if (json.isJsonArray()) json.getAsJsonArray().forEach(value -> rejectLegacySpawnKeys(path, value));
        if (json.isJsonObject()) {
            for (var entry : json.getAsJsonObject().entrySet()) {
                if (Set.of("minCount", "maxCount", "spawners").contains(entry.getKey())) {
                    errors.add(path + ": obsolete spawn key " + entry.getKey());
                }
                rejectLegacySpawnKeys(path, entry.getValue());
            }
        }
    }

    private static JsonElement substituteModReferences(JsonElement input, String field, boolean worldgen) {
        if (input.isJsonArray()) {
            var output = new JsonArray();
            input.getAsJsonArray().forEach(value -> output.add(substituteModReferences(value, field, worldgen)));
            return output;
        }
        if (!input.isJsonObject()) {
            if (input.isJsonPrimitive() && input.getAsJsonPrimitive().isString()) {
                String value = input.getAsString();
                if (value.startsWith("plantz:")) {
                    if (field.equals("entity_id") || field.equals("minecraft:entity_type")) return new JsonPrimitive("minecraft:zombie");
                    if (field.equals("id") && worldgen) return new JsonPrimitive("minecraft:oak_stairs");
                    if (field.equals("id") || field.equals("name") || field.equals("items") || field.equals("item") || field.equals("ingredients") || field.length() == 1) return new JsonPrimitive("minecraft:stick");
                }
            }
            return input;
        }
        var output = new JsonObject();
        for (var entry : input.getAsJsonObject().entrySet()) {
            if (field.equals("components") && entry.getKey().startsWith("plantz:")) continue;
            String childField = field.equals("minecraft:entity_data") && entry.getKey().equals("id") ? "entity_id" : entry.getKey();
            if (entry.getKey().equals("type") && input.getAsJsonObject().has("weight")) childField = "entity_id";
            output.add(entry.getKey(), substituteModReferences(entry.getValue(), childField, worldgen));
        }
        return output;
    }
}
