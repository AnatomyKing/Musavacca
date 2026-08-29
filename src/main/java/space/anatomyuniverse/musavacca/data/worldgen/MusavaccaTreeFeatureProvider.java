package space.anatomyuniverse.musavacca.data.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
//? if <1.21.5
//import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.worldgen.ModConfiguredFeatures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class MusavaccaTreeFeatureProvider implements DataProvider {

    private static final String INPUT_FILE_NAME = "musavaccatree.nbt";

    private static final Path SHARED_TEMPLATE_PATH = Path.of(
            "src", "main", "resources",
            "data", MusaCore.MOD_ID,
            "structure", INPUT_FILE_NAME
    );

    private static final Path SHARED_TEMPLATE_PATH_PLURAL = Path.of(
            "src", "main", "resources",
            "data", MusaCore.MOD_ID,
            "structures", INPUT_FILE_NAME
    );

    private static final Path VERSION_TEMPLATE_PATH = Path.of(
            "versions", MusaCore.MINECRAFT,
            "src", "main", "resources",
            "data", MusaCore.MOD_ID,
            "structure", INPUT_FILE_NAME
    );

    private static final Path VERSION_TEMPLATE_PATH_PLURAL = Path.of(
            "versions", MusaCore.MINECRAFT,
            "src", "main", "resources",
            "data", MusaCore.MOD_ID,
            "structures", INPUT_FILE_NAME
    );

    private final PackOutput.PathProvider configuredFeaturePathProvider;

    public MusavaccaTreeFeatureProvider(PackOutput output) {
        this.configuredFeaturePathProvider =
                output.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/configured_feature");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        try {
            Path inputPath = findInputNbt();
            MusaCore.LOGGER.info("Using Musavacca tree template NBT: {}", toFriendlyProjectPath(inputPath));

            JsonObject configuredFeature = createConfiguredFeatureJson(inputPath);

            Path outputPath = this.configuredFeaturePathProvider.json(ModConfiguredFeatures.MUSAVACCA_TREE.location());
            return DataProvider.saveStable(output, configuredFeature, outputPath);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public String getName() {
        return "Musavacca Tree Configured Feature From NBT";
    }

    private static Path findInputNbt() throws IOException {
        List<Path> checkedPaths = new ArrayList<>();
        Path start = Path.of("").toAbsolutePath().normalize();

        for (Path base = start; base != null; base = base.getParent()) {
            addCandidate(checkedPaths, base.resolve(SHARED_TEMPLATE_PATH));
            addCandidate(checkedPaths, base.resolve(SHARED_TEMPLATE_PATH_PLURAL));
            addCandidate(checkedPaths, base.resolve(VERSION_TEMPLATE_PATH));
            addCandidate(checkedPaths, base.resolve(VERSION_TEMPLATE_PATH_PLURAL));
        }

        for (Path candidate : checkedPaths) {
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        StringBuilder message = new StringBuilder();

        message.append("Could not find ").append(INPUT_FILE_NAME).append(".\n\n");
        message.append("Expected project-relative path:\n");
        message.append("  ").append(SHARED_TEMPLATE_PATH).append("\n\n");
        message.append("Datagen working directory was:\n");
        message.append("  ").append(start).append("\n\n");
        message.append("Checked project-relative candidates:\n");

        for (Path checkedPath : checkedPaths) {
            message.append("  - ").append(toFriendlyProjectPath(checkedPath)).append("\n");
        }

        throw new IOException(message.toString());
    }

    private static void addCandidate(List<Path> checkedPaths, Path path) {
        Path normalized = path.toAbsolutePath().normalize();

        if (!checkedPaths.contains(normalized)) {
            checkedPaths.add(normalized);
        }
    }

    private static String toFriendlyProjectPath(Path path) {
        Path normalizedPath = path.toAbsolutePath().normalize();
        Path start = Path.of("").toAbsolutePath().normalize();

        for (Path base = start; base != null; base = base.getParent()) {
            Path normalizedBase = base.toAbsolutePath().normalize();

            if (normalizedPath.startsWith(normalizedBase)) {
                try {
                    return normalizedBase.relativize(normalizedPath).toString();
                } catch (IllegalArgumentException ignored) {
                    // Different roots/drives. Fall through to absolute path fallback.
                }
            }
        }

        return normalizedPath.toString();
    }

    private static JsonObject createConfiguredFeatureJson(Path inputPath) throws IOException {
        CompoundTag root = NbtIo.readCompressed(inputPath, NbtAccounter.unlimitedHeap());

        ListTag size = getRequiredList(root, "size");
        ListTag palette = getRequiredList(root, "palette");
        ListTag blocks = getRequiredList(root, "blocks");

        if (size.size() != 3) {
            throw new IOException("Invalid structure NBT: expected 'size' list with 3 ints.");
        }

        int sizeX = getRequiredInt(size, 0, "size[0]");
        int sizeZ = getRequiredInt(size, 2, "size[2]");

        int anchorX = sizeX / 2;
        int anchorY = 0;
        int anchorZ = sizeZ / 2;

        JsonObject configuredFeature = new JsonObject();
        configuredFeature.addProperty(
                "type",
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_template_tree").toString()
        );

        List<JsonObject> outputBlockList = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag blockTag = getRequiredCompound(blocks, i, "blocks[" + i + "]");

            ListTag pos = getRequiredList(blockTag, "pos");

            if (pos.size() != 3) {
                throw new IOException("Invalid structure NBT: expected 'pos' list with 3 ints at blocks[" + i + "].");
            }

            int stateIndex = getRequiredInt(blockTag, "state", "blocks[" + i + "].state");

            if (stateIndex < 0 || stateIndex >= palette.size()) {
                throw new IOException(
                        "Invalid structure NBT: state index " + stateIndex
                                + " is outside palette size " + palette.size()
                                + " at blocks[" + i + "]."
                );
            }

            CompoundTag stateTag = getRequiredCompound(palette, stateIndex, "palette[" + stateIndex + "]");
            String blockName = getRequiredString(stateTag, "Name", "palette[" + stateIndex + "].Name");

            if ("minecraft:air".equals(blockName)) {
                continue;
            }

            int x = getRequiredInt(pos, 0, "blocks[" + i + "].pos[0]") - anchorX;
            int y = getRequiredInt(pos, 1, "blocks[" + i + "].pos[1]") - anchorY;
            int z = getRequiredInt(pos, 2, "blocks[" + i + "].pos[2]") - anchorZ;

            JsonObject outputBlock = new JsonObject();

            JsonArray offset = new JsonArray();
            offset.add(x);
            offset.add(y);
            offset.add(z);

            outputBlock.add("offset", offset);
            outputBlock.add("state", blockStateToJson(stateTag));

            outputBlockList.add(outputBlock);
        }

        outputBlockList.sort(Comparator.comparing(MusavaccaTreeFeatureProvider::sortKey));

        JsonArray outputBlocks = new JsonArray();

        for (JsonObject outputBlock : outputBlockList) {
            outputBlocks.add(outputBlock);
        }

        JsonObject config = new JsonObject();
        config.add("blocks", outputBlocks);

        configuredFeature.add("config", config);

        return configuredFeature;
    }

    private static JsonObject blockStateToJson(CompoundTag stateTag) throws IOException {
        JsonObject json = new JsonObject();

        String blockName = getRequiredString(stateTag, "Name", "palette entry Name");
        json.addProperty("Name", blockName);

        CompoundTag propertiesTag = getOptionalCompound(stateTag, "Properties");

        if (propertiesTag != null) {
            JsonObject properties = new JsonObject();

            tagKeys(propertiesTag).stream()
                    .sorted()
                    .forEach(key -> {
                        String value = getRequiredPropertyString(propertiesTag, key);

                        properties.addProperty(key, value);
                    });

            json.add("Properties", properties);
        }

        return json;
    }

    private static String sortKey(JsonObject element) {
        JsonArray offset = element.getAsJsonArray("offset");

        int x = offset.get(0).getAsInt();
        int y = offset.get(1).getAsInt();
        int z = offset.get(2).getAsInt();

        return String.format("%04d_%04d_%04d", y + 2048, x + 2048, z + 2048);
    }

    private static ListTag getRequiredList(CompoundTag tag, String key) throws IOException {
        //? if >=1.21.5 {
        return tag.getList(key).orElseThrow(() ->
                new IOException("Invalid structure NBT: missing list tag '" + key + "'.")
        );
        //?} else {
        /*if (!tag.contains(key, Tag.TAG_LIST)) {
            throw new IOException("Invalid structure NBT: missing list tag '" + key + "'.");
        }

        return (ListTag) tag.get(key);
        *///?}
    }

    private static int getRequiredInt(ListTag list, int index, String path) throws IOException {
        //? if >=1.21.5 {
        return list.getInt(index).orElseThrow(() ->
                new IOException("Invalid structure NBT: missing int at " + path + ".")
        );
        //?} else {
        /*if (index < 0 || index >= list.size() || list.get(index).getId() != Tag.TAG_INT) {
            throw new IOException("Invalid structure NBT: missing int at " + path + ".");
        }

        return list.getInt(index);
        *///?}
    }

    private static int getRequiredInt(CompoundTag tag, String key, String path) throws IOException {
        //? if >=1.21.5 {
        return tag.getInt(key).orElseThrow(() ->
                new IOException("Invalid structure NBT: missing int tag at " + path + ".")
        );
        //?} else {
        /*if (!tag.contains(key, Tag.TAG_INT)) {
            throw new IOException("Invalid structure NBT: missing int tag at " + path + ".");
        }

        return tag.getInt(key);
        *///?}
    }

    private static CompoundTag getRequiredCompound(ListTag list, int index, String path) throws IOException {
        //? if >=1.21.5 {
        return list.getCompound(index).orElseThrow(() ->
                new IOException("Invalid structure NBT: missing compound tag at " + path + ".")
        );
        //?} else {
        /*if (index < 0 || index >= list.size() || list.get(index).getId() != Tag.TAG_COMPOUND) {
            throw new IOException("Invalid structure NBT: missing compound tag at " + path + ".");
        }

        return list.getCompound(index);
        *///?}
    }

    private static String getRequiredString(CompoundTag tag, String key, String path) throws IOException {
        //? if >=1.21.5 {
        return tag.getString(key).orElseThrow(() ->
                new IOException("Invalid structure NBT: missing string tag at " + path + ".")
        );
        //?} else {
        /*if (!tag.contains(key, Tag.TAG_STRING)) {
            throw new IOException("Invalid structure NBT: missing string tag at " + path + ".");
        }

        return tag.getString(key);
        *///?}
    }

    private static CompoundTag getOptionalCompound(CompoundTag tag, String key) {
        //? if >=1.21.5 {
        return tag.getCompound(key).orElse(null);
         //?} else {
        /*return tag.contains(key, Tag.TAG_COMPOUND) ? tag.getCompound(key) : null;
        *///?}
    }

    private static String getRequiredPropertyString(CompoundTag tag, String key) {
        //? if >=1.21.5 {
        return tag.getString(key).orElseThrow(() ->
                new IllegalStateException("Expected string property value for property '" + key + "'.")
        );
        //?} else {
        /*if (!tag.contains(key, Tag.TAG_STRING)) {
            throw new IllegalStateException("Expected string property value for property '" + key + "'.");
        }

        return tag.getString(key);
        *///?}
    }

    private static Set<String> tagKeys(CompoundTag tag) {
        //? if >=1.21.5 {
        return tag.keySet();
         //?} else {
        /*return tag.getAllKeys();
        *///?}
    }
}
