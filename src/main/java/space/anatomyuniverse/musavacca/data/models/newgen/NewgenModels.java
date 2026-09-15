package space.anatomyuniverse.musavacca.data.models.newgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Model geometry and texture bindings, shared by every supported Minecraft version. */
final class NewgenModels {
    private final NewgenOutput output;
    private final Map<Key, ResourceLocation> resolved = new HashMap<>();
    private final Map<RootYKey, ResourceLocation> rootYModels = new HashMap<>();

    private record Key(Models.Source source, Tints.ModelKey tint) {}
    private record RootYKey(Block owner, ResourceLocation parent, float yDegrees) {}

    NewgenModels(NewgenOutput output) { this.output = output; }

    ResourceLocation resolve(Models.Source source, Tints.Tint tint) {
        // Authored models already own their elements, textures and tint indices.
        if (source instanceof Models.Existing existing) return existing.model();
        Key key = new Key(source, Tints.modelKey(tint));
        return resolved.computeIfAbsent(key, ignored -> {
            if (source instanceof Models.Generated cube) return cube(cube, tint);
            if (source instanceof PortalModels.Pane pane) return portal(pane, tint);
            if (source instanceof CrossModels.Generated cross) {
                Tints.GeneratedLayer layer = Tints.singleGeneratedLayer(tint, "CrossBlocks");
                if (layer.tintIndex() > 0) {
                    throw new IllegalStateException("The generated cross template requires tint index 0; use an authored model for offsets.");
                }
                return parent(cross.model(), tint.tinted() ? "tinted_cross" : "cross",
                        Map.of("cross", cross.textures().texture()));
            }
            throw new IllegalArgumentException("Unsupported model source: " + source);
        });
    }

    ResourceLocation rootRotateY(Block owner, ResourceLocation parent, float yDegrees) {
        if (!Float.isFinite(yDegrees)) {
            throw new IllegalArgumentException("Root Y rotation must be finite: " + yDegrees);
        }

        if (Float.compare(yDegrees, 0.0F) == 0) {
            return parent;
        }

        RootYKey key = new RootYKey(
                java.util.Objects.requireNonNull(owner, "owner"),
                java.util.Objects.requireNonNull(parent, "parent"),
                yDegrees
        );

        return rootYModels.computeIfAbsent(key, ignored -> {
            ResourceLocation id = rootTransformId(owner, parent, yDegrees);

            JsonObject root = new JsonObject();
            root.addProperty("parent", parent.toString());

            JsonObject transform = new JsonObject();
            transform.addProperty("origin", "center");

            JsonObject rotation = new JsonObject();
            rotation.addProperty("y", yDegrees);

            transform.add("rotation", rotation);
            root.add("transform", transform);

            return output.model(id, root);
        });
    }

    private static ResourceLocation rootTransformId(
            Block owner,
            ResourceLocation parent,
            float yDegrees
    ) {
        String parentKey = parent.getNamespace() + "_"
                + parent.getPath().replace('/', '_');

        return ModelLocations.blockModel(
                owner,
                "_decoration/" + parentKey + "_rot_" + degreeKey(yDegrees)
        );
    }

    private static String degreeKey(float degrees) {
        String value = BigDecimal.valueOf(degrees)
                .stripTrailingZeros()
                .toPlainString();

        if (value.startsWith("-")) {
            return "neg_" + value.substring(1).replace('.', '_');
        }

        return "pos_" + value.replace('.', '_');
    }

    private ResourceLocation cube(Models.Generated generated, Tints.Tint tint) {
        Textures.Set t = generated.textures();
        JsonObject root = parentJson("minecraft:block/block", Map.of(
                "particle", t.particle(), "bottom", t.bottom(), "top", t.top(),
                "north", t.north(), "south", t.south(), "west", t.west(), "east", t.east()));
        JsonObject element = element(0, 0, 0, 16, 16, 16);
        JsonObject faces = new JsonObject();
        int tintIndex = Tints.singleGeneratedLayer(tint, "SimpleBlocks").tintIndex();
        for (String direction : List.of("down", "up", "north", "south", "west", "east")) {
            JsonObject face = new JsonObject();
            face.add("uv", vector(0, 0, 16, 16));
            face.addProperty("texture", "#" + switch (direction) { case "down" -> "bottom"; case "up" -> "top"; default -> direction; });
            face.addProperty("cullface", direction);
            if (tintIndex >= 0) face.addProperty("tintindex", tintIndex);
            faces.add(direction, face);
        }
        element.add("faces", faces);
        JsonArray elements = new JsonArray();
        elements.add(element);
        root.add("elements", elements);
        return output.model(generated.model(), root);
    }

    private ResourceLocation portal(PortalModels.Pane pane, Tints.Tint tint) {
        List<Tints.GeneratedLayer> layers = Tints.generatedLayers(tint);
        PortalTextures.Set t = pane.textures();
        Map<String, ResourceLocation> textures = new java.util.LinkedHashMap<>();
        textures.put("particle", t.particle() != null ? t.particle() : t.source(tint, 0));
        JsonArray elements = new JsonArray();
        List<String> directions = switch (pane.axis()) {
            case X -> List.of("north", "south");
            case Z -> List.of("east", "west");
            case Y -> List.of("up", "down");
        };
        for (Tints.GeneratedLayer layer : layers) {
            String key = "portal_" + layer.sourceLayer();
            textures.put(key, t.source(tint, layer.sourceLayer()));
            JsonObject element = switch (pane.axis()) {
                case X -> element(0, 0, 6, 16, 16, 10);
                case Z -> element(6, 0, 0, 10, 16, 16);
                case Y -> element(0, 6, 0, 16, 10, 16);
            };
            JsonObject faces = new JsonObject();
            for (String direction : directions) {
                JsonObject face = new JsonObject();
                face.add("uv", vector(0, 0, 16, 16));
                face.addProperty("texture", "#" + key);
                if (layer.tintIndex() >= 0) face.addProperty("tintindex", layer.tintIndex());
                faces.add(direction, face);
            }
            element.add("faces", faces);
            elements.add(element);
        }
        JsonObject root = parentJson("minecraft:block/block", textures);
        root.add("elements", elements);
        return output.model(pane.model(), root);
    }

    ResourceLocation flatItem(Item item, List<ResourceLocation> textures) {
        if (textures.isEmpty() || textures.size() > 5) {
            throw new IllegalArgumentException("item/generated supports 1–5 texture layers; supply an existing item model for more layers.");
        }
        Map<String, ResourceLocation> mapping = new java.util.LinkedHashMap<>();
        for (int i = 0; i < textures.size(); i++) mapping.put("layer" + i, textures.get(i));
        return output.model(ModelLocations.itemModel(item), parentJson("minecraft:item/generated", mapping));
    }

    SlabModels slab(SlabBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var t = e.textures();
        var textures = Map.of("side", t.side(), "bottom", t.bottom(), "top", t.top());
        return SlabModels.full(template(e.block(), "", "slab", textures),
                template(e.block(), "_top", "slab_top", textures),
                template(e.block(), "_double", "cube_bottom_top", textures));
    }

    StairModels stair(StairBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var t = e.textures();
        var textures = Map.of("side", t.side(), "bottom", t.bottom(), "top", t.top());
        return StairModels.full(template(e.block(), "", "stairs", textures),
                template(e.block(), "_inner", "inner_stairs", textures),
                template(e.block(), "_outer", "outer_stairs", textures));
    }

    FenceModels fence(FenceBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("texture", e.textures().texture());
        return FenceModels.full(template(e.block(), "_post", "fence_post", textures),
                template(e.block(), "_side", "fence_side", textures),
                template(e.block(), "_inventory", "fence_inventory", textures));
    }

    FenceGateModels fenceGate(FenceGateBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("texture", e.textures().texture());
        return FenceGateModels.full(template(e.block(), "", "template_fence_gate", textures),
                template(e.block(), "_open", "template_fence_gate_open", textures),
                template(e.block(), "_wall", "template_fence_gate_wall", textures),
                template(e.block(), "_wall_open", "template_fence_gate_wall_open", textures));
    }

    PressurePlateModels pressurePlate(PressurePlateBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("texture", e.textures().texture());
        return PressurePlateModels.full(template(e.block(), "", "pressure_plate_up", textures),
                template(e.block(), "_down", "pressure_plate_down", textures));
    }

    ButtonModels button(ButtonBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("texture", e.textures().texture());
        return ButtonModels.full(template(e.block(), "", "button", textures),
                template(e.block(), "_pressed", "button_pressed", textures),
                template(e.block(), "_inventory", "button_inventory", textures));
    }

    WallModels wall(WallBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("wall", e.textures().texture());
        return WallModels.full(template(e.block(), "_post", "template_wall_post", textures),
                template(e.block(), "_side", "template_wall_side", textures),
                template(e.block(), "_side_tall", "template_wall_side_tall", textures),
                template(e.block(), "_inventory", "wall_inventory", textures));
    }

    TrapdoorModels trapdoor(TrapdoorBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("texture", e.textures().texture());
        return TrapdoorModels.full(template(e.block(), "_bottom", "template_trapdoor_bottom", textures),
                template(e.block(), "_top", "template_trapdoor_top", textures),
                template(e.block(), "_open", "template_trapdoor_open", textures));
    }

    DoorModels door(DoorBlocks.Entry e) {
        if (e.baseMode() == BaseModelMode.EXISTING) return e.baseModels();
        var textures = Map.of("bottom", e.textures().bottom(), "top", e.textures().top());
        return DoorModels.full(template(e.block(), "_bottom_left", "door_bottom_left", textures),
                template(e.block(), "_bottom_left_open", "door_bottom_left_open", textures),
                template(e.block(), "_bottom_right", "door_bottom_right", textures),
                template(e.block(), "_bottom_right_open", "door_bottom_right_open", textures),
                template(e.block(), "_top_left", "door_top_left", textures),
                template(e.block(), "_top_left_open", "door_top_left_open", textures),
                template(e.block(), "_top_right", "door_top_right", textures),
                template(e.block(), "_top_right_open", "door_top_right_open", textures));
    }

    private String template(Block block, String suffix, String parent, Map<String, ResourceLocation> textures) {
        return parent(ModelLocations.blockModel(block, suffix), parent, textures).toString();
    }

    private ResourceLocation parent(ResourceLocation id, String parent, Map<String, ResourceLocation> textures) {
        return output.model(id, parentJson("minecraft:block/" + parent, textures));
    }

    private static JsonObject parentJson(String parent, Map<String, ResourceLocation> textures) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", parent);
        JsonObject mapping = new JsonObject();
        textures.forEach((key, texture) -> mapping.addProperty(key, texture.toString()));
        root.add("textures", mapping);
        return root;
    }

    ResourceLocation fire(FireBlocks.Entry entry, String suffix, Shape shape, int frame,
                          List<Tints.GeneratedLayer> layers) {
        return output.model(ModelLocations.blockModel(entry.block(), suffix), fireJson(entry, shape, frame, layers));
    }

    enum Shape { FLOOR, SIDE, SIDE_ALT, UP, UP_ALT }

    private static JsonObject fireJson(
            FireBlocks.Entry entry,
            Shape shape,
            int frame,
            List<Tints.GeneratedLayer> layers
    ) {

        JsonObject root = new JsonObject();
        root.addProperty("ambientocclusion", false);

        JsonObject textures = new JsonObject();
        textures.addProperty(
                "particle",
                textureForLayer(entry.textures(), entry.tint(), frame, layers.get(0)).toString()
        );

        for (Tints.GeneratedLayer layer : layers) {
            textures.addProperty(
                    textureKey(layer),
                    textureForLayer(entry.textures(), entry.tint(), frame, layer).toString()
            );
        }

        root.add("textures", textures);

        JsonArray elements = new JsonArray();

        switch (shape) {
            case FLOOR -> addFloor(elements, layers);
            case SIDE -> addSide(elements, layers, false);
            case SIDE_ALT -> addSide(elements, layers, true);
            case UP -> addUp(elements, layers, false);
            case UP_ALT -> addUp(elements, layers, true);
        }

        root.add("elements", elements);
        return root;
    }

    private static void addFloor(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            elements.add(rotatedPlane(
                    layer,
                    0, 0, 8.8F,
                    16, 22.4F, 8.8F,
                    8, 8, 8,
                    "x", -22.5F,
                    "south", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    0, 0, 7.2F,
                    16, 22.4F, 7.2F,
                    8, 8, 8,
                    "x", 22.5F,
                    "north", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    8.8F, 0, 0,
                    8.8F, 22.4F, 16,
                    8, 8, 8,
                    "z", -22.5F,
                    "west", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    7.2F, 0, 0,
                    7.2F, 22.4F, 16,
                    8, 8, 8,
                    "z", 22.5F,
                    "east", 0
            ));
        }
    }

    private static void addSide(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers,
            boolean alternateUv
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            JsonObject element = element(
                    0, 0, 0.01F,
                    16, 22.4F, 0.01F
            );
            element.addProperty("shade", false);

            JsonObject faces = new JsonObject();
            faces.add("south", face(layer, alternateUv, 0));
            faces.add("north", face(layer, alternateUv, 0));
            element.add("faces", faces);

            elements.add(element);
        }
    }

    private static void addUp(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers,
            boolean alternate
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            if (!alternate) {
                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        16, 16, 8,
                        "z", 22.5F,
                        "down", 270
                ));

                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        0, 16, 8,
                        "z", -22.5F,
                        "down", 90
                ));
            } else {
                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 16,
                        "x", -22.5F,
                        "down", 180
                ));

                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 0,
                        "x", 22.5F,
                        "down", 0
                ));
            }
        }
    }

    private static JsonObject rotatedPlane(
            Tints.GeneratedLayer layer,
            float fromX,
            float fromY,
            float fromZ,
            float toX,
            float toY,
            float toZ,
            float originX,
            float originY,
            float originZ,
            String axis,
            float angle,
            String faceDirection,
            int faceRotation
    ) {
        JsonObject element = element(fromX, fromY, fromZ, toX, toY, toZ);
        element.addProperty("shade", false);

        JsonObject rotation = new JsonObject();
        rotation.add("origin", vector(originX, originY, originZ));
        rotation.addProperty("axis", axis);
        rotation.addProperty("angle", angle);
        rotation.addProperty("rescale", true);
        element.add("rotation", rotation);

        JsonObject faces = new JsonObject();
        faces.add(faceDirection, face(layer, false, faceRotation));
        element.add("faces", faces);

        return element;
    }

    private static JsonObject element(
            float fromX,
            float fromY,
            float fromZ,
            float toX,
            float toY,
            float toZ
    ) {
        JsonObject element = new JsonObject();
        element.add("from", vector(fromX, fromY, fromZ));
        element.add("to", vector(toX, toY, toZ));
        return element;
    }

    private static JsonObject face(
            Tints.GeneratedLayer layer,
            boolean alternateUv,
            int rotation
    ) {
        JsonObject face = new JsonObject();

        JsonArray uv = new JsonArray();
        uv.add(alternateUv ? 16 : 0);
        uv.add(0);
        uv.add(alternateUv ? 0 : 16);
        uv.add(16);
        face.add("uv", uv);

        face.addProperty("texture", "#" + textureKey(layer));

        if (layer.tintIndex() >= 0) {
            face.addProperty("tintindex", layer.tintIndex());
        }

        if (rotation != 0) {
            face.addProperty("rotation", rotation);
        }

        return face;
    }

    private static JsonArray vector(float... values) {
        JsonArray array = new JsonArray();
        for (float value : values) array.add(value);
        return array;
    }

    private static ResourceLocation textureForLayer(
            FireTextures.Set textures,
            Tints.Tint tint,
            int frame,
            Tints.GeneratedLayer layer
    ) {
        if (tint.kind() == Tints.Kind.PEARL_FIRE) {
            return FireTextures.layer(textures, frame, layer.sourceLayer());
        }

        return FireTextures.frame(textures, frame);
    }

    private static String textureKey(Tints.GeneratedLayer layer) {
        return "fire_" + layer.sourceLayer();
    }
}


