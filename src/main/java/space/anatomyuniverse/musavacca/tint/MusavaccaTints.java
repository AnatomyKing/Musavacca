package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.data.models.newgen.Tints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

//? if >=1.21.4 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

public final class MusavaccaTints {
    public static final int NO_TINT = -1;
    public static final int RANDOM_TINT = -2;
    public static final int DEFAULT_TINT = 0xD5CD49;

    public static final String HEX_COLOR_KEY = "hex_color";
    public static final String MULTI_HEX_COLOR_KEY = "multi_hex_color";

    private static final int RGB_MASK = 0xFFFFFF;
    private static final int RGB_BOUND = 0x1000000;
    private static final int MULTI_TINT_STRIDE = 100;

    private static final Hex HEX = new Hex(-1);

    private MusavaccaTints() {}

    public interface HexSource {
        int getHexColor();

        default boolean hasHexColor() {
            return hasTint(getHexColor());
        }
    }

    public interface MultiHexSource {
        int getHexColor(int index);
    }

    public record Hex(int multiIndex) implements Tints.Tint {
        public Hex {
            if (multiIndex < -1) {
                throw new IllegalArgumentException("multiIndex must be >= -1");
            }
        }

        public Hex multi(int index) {
            return new Hex(requireMultiIndex(index));
        }

        @Override
        public int tintIndex() {
            return tintOffset(multiIndex);
        }

        @Override
        public int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return opaqueRgb(sourceColor(state, level, pos, multiIndex));
        }

        @Override
        public int itemColor(ItemStack stack) {
            return opaqueRgb(itemSourceColor(stack, multiIndex));
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return multiIndex >= 0 ? ItemTint.multi(multiIndex) : ItemTint.INSTANCE;
        }

        @Override
        public Tints.ItemTintType itemTintType() {
            return ITEM_TINT_TYPE;
        }
        //?}
    }

    public record PearlFire(
            PearlFireTintProfiles.Profile profile,
            int multiIndex,
            int physicalLayer
    ) implements Tints.Tint {
        public PearlFire {
            Objects.requireNonNull(profile, "profile");
            if (multiIndex < -1) {
                throw new IllegalArgumentException("multiIndex must be >= -1");
            }
            if (physicalLayer < -1) {
                throw new IllegalArgumentException("physicalLayer must be >= -1");
            }
            if (physicalLayer >= 0 && !profile.supports(physicalLayer)) {
                throw new IllegalArgumentException("physicalLayer is outside the profile");
            }
        }

        public PearlFire multi(int index) {
            return new PearlFire(profile, requireMultiIndex(index), physicalLayer);
        }

        @Override
        public int tintIndex() {
            return tintOffset(multiIndex) + Math.max(physicalLayer, 0);
        }

        @Override
        public List<Tints.GeneratedLayer> generatedLayers() {
            if (physicalLayer >= 0) {
                return List.of(new Tints.GeneratedLayer(physicalLayer, tintIndex()));
            }

            List<Tints.GeneratedLayer> layers = new ArrayList<>(profile.layerCount());
            int offset = tintOffset(multiIndex);

            for (int layer = 0; layer < profile.layerCount(); layer++) {
                layers.add(new Tints.GeneratedLayer(layer, offset + layer));
            }

            return List.copyOf(layers);
        }

        @Override
        public Tints.Tint physicalLayer(Tints.GeneratedLayer layer) {
            return new PearlFire(profile, multiIndex, layer.sourceLayer());
        }

        @Override
        public int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return blockColor(sourceColor(state, level, pos, multiIndex), tintIndex);
        }

        public int blockColor(int color, int tintIndex) {
            int layer = tintIndex - tintOffset(multiIndex);
            return hasTint(color) && profile.supports(layer)
                    ? PearlFireTintSource.blockTint(color, layer, profile)
                    : NO_TINT;
        }

        @Override
        public int itemColor(ItemStack stack) {
            int layer = Math.max(physicalLayer, 0);
            int color = itemSourceColor(stack, multiIndex);

            return hasTint(color) && profile.supports(layer)
                    ? PearlFireTintSource.profileTint(color, layer, profile)
                    : NO_TINT;
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return ItemTint.pearlFire(profile, Math.max(physicalLayer, 0), multiIndex);
        }

        @Override
        public Tints.ItemTintType itemTintType() {
            return ITEM_TINT_TYPE;
        }
        //?}
    }

    public static Hex hexColor() {
        return HEX;
    }

    public static PearlFire pearlFire(PearlFireTintProfiles.Profile profile) {
        return new PearlFire(profile, -1, -1);
    }

    public static boolean hasTint(int color) {
        return color != NO_TINT && color != RANDOM_TINT;
    }

    public static int rgb(int color) {
        return color & RGB_MASK;
    }

    public static int resolve(int color) {
        if (color == NO_TINT) {
            return NO_TINT;
        }
        if (color == RANDOM_TINT) {
            return randomRgb();
        }
        return rgb(color);
    }

    public static int stored(int color) {
        return color == NO_TINT || color == RANDOM_TINT
                ? NO_TINT
                : rgb(color);
    }

    public static int orDefault(int color) {
        return color == NO_TINT ? DEFAULT_TINT : resolve(color);
    }

    public static int randomRgb() {
        return ThreadLocalRandom.current().nextInt(RGB_BOUND);
    }

    public static int opaqueRgb(int color) {
        return hasTint(color) ? 0xFF000000 | rgb(color) : NO_TINT;
    }

    public static int multiColor(List<Integer> colors, int index) {
        if (colors == null || index < 0 || index >= colors.size()) {
            return NO_TINT;
        }

        Integer color = colors.get(index);
        return color == null ? NO_TINT : stored(color);
    }

    public static String formatHex(int color) {
        if (color == NO_TINT) {
            return "NO_TINT";
        }
        if (color == RANDOM_TINT) {
            return "RANDOM_TINT";
        }
        return String.format("#%06X", rgb(color));
    }

    public static int blockEntityHexColor(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return NO_TINT;
        }

        if (blockEntity instanceof HexSource source) {
            return source.hasHexColor()
                    ? stored(source.getHexColor())
                    : NO_TINT;
        }

        Integer color = blockEntity.collectComponents().get(ModDataComponents.HEX_COLOR.get());
        return color == null ? NO_TINT : stored(color);
    }

    private static int sourceColor(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int multiIndex
    ) {
        if (level == null || pos == null) {
            return NO_TINT;
        }

        BlockPos sourcePos = state.getBlock() instanceof MusavaccaPortalDoorBlock
                ? MusavaccaPortalDoorBlock.lowerDoorPos(state, pos)
                : pos;

        BlockEntity blockEntity = level.getBlockEntity(sourcePos);
        if (blockEntity == null) {
            return NO_TINT;
        }

        if (multiIndex >= 0) {
            if (blockEntity instanceof MultiHexSource source) {
                return stored(source.getHexColor(multiIndex));
            }

            DataComponentMap components = blockEntity.collectComponents();
            return multiColor(components.get(ModDataComponents.MULTI_HEX_COLOR.get()), multiIndex);
        }

        return blockEntityHexColor(blockEntity);
    }

    private static int itemSourceColor(ItemStack stack, int multiIndex) {
        if (stack == null || stack.isEmpty()) {
            return NO_TINT;
        }

        if (multiIndex >= 0) {
            return multiColor(stack.get(ModDataComponents.MULTI_HEX_COLOR.get()), multiIndex);
        }

        Integer color = stack.get(ModDataComponents.HEX_COLOR.get());
        return color == null ? NO_TINT : stored(color);
    }

    private static int tintOffset(int multiIndex) {
        return multiIndex >= 0 ? multiIndex * MULTI_TINT_STRIDE : 0;
    }

    private static int requireMultiIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("multi index must be >= 0");
        }
        return index;
    }

    //? if >=1.21.4 {
    private static final Tints.ItemTintType ITEM_TINT_TYPE =
            new Tints.ItemTintType(
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, HEX_COLOR_KEY),
                    ItemTint.MAP_CODEC
            );

    public static final class ItemTint implements ItemTintSource {
        public static final ItemTint INSTANCE = new ItemTint(null, 0, -1);

        private static final Codec<PearlFireTintProfiles.Profile> PROFILE_CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.FLOAT.fieldOf("core_to_tail_lightness")
                                .forGetter(PearlFireTintProfiles.Profile::coreToTailLightness),
                        Codec.FLOAT.fieldOf("color_jumpiness")
                                .forGetter(PearlFireTintProfiles.Profile::colorJumpiness),
                        Codec.FLOAT.fieldOf("color_amount_take_over")
                                .forGetter(PearlFireTintProfiles.Profile::colorAmountTakeOver),
                        Codec.FLOAT.fieldOf("vibrancy_darkening")
                                .forGetter(PearlFireTintProfiles.Profile::vibrancyDarkening),
                        Codec.FLOAT.fieldOf("layer_contrast")
                                .forGetter(PearlFireTintProfiles.Profile::layerContrast),
                        Codec.FLOAT.listOf().fieldOf("gray_factors")
                                .forGetter(profile -> boxed(profile.grayFactors()))
                ).apply(instance, ItemTint::decodeProfile));

        public static final MapCodec<ItemTint> MAP_CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        PROFILE_CODEC.optionalFieldOf("profile")
                                .forGetter(source -> Optional.ofNullable(source.profile)),
                        Codec.INT.optionalFieldOf("layer", 0)
                                .forGetter(source -> source.layer),
                        Codec.INT.optionalFieldOf("multi_index", -1)
                                .forGetter(source -> source.multiIndex)
                ).apply(instance, ItemTint::decode));

        private final PearlFireTintProfiles.Profile profile;
        private final int layer;
        private final int multiIndex;

        private ItemTint(PearlFireTintProfiles.Profile profile, int layer, int multiIndex) {
            if (layer < 0) {
                throw new IllegalArgumentException("layer must be >= 0");
            }
            if (multiIndex < -1) {
                throw new IllegalArgumentException("multiIndex must be >= -1");
            }
            if (profile != null && !profile.supports(layer)) {
                throw new IllegalArgumentException("layer is outside the profile");
            }

            this.profile = profile;
            this.layer = layer;
            this.multiIndex = multiIndex;
        }

        static ItemTint multi(int index) {
            return new ItemTint(null, 0, requireMultiIndex(index));
        }

        static ItemTint pearlFire(PearlFireTintProfiles.Profile profile, int layer, int multiIndex) {
            return new ItemTint(Objects.requireNonNull(profile, "profile"), layer, multiIndex);
        }

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            int color = itemSourceColor(stack, multiIndex);
            if (!hasTint(color)) {
                return NO_TINT;
            }

            return profile == null
                    ? opaqueRgb(color)
                    : PearlFireTintSource.profileTint(color, layer, profile);
        }

        @Override
        public MapCodec<ItemTint> type() {
            return MAP_CODEC;
        }

        private static ItemTint decode(
                Optional<PearlFireTintProfiles.Profile> profile,
                int layer,
                int multiIndex
        ) {
            return profile.isPresent()
                    ? pearlFire(profile.get(), layer, multiIndex)
                    : multiIndex >= 0 ? multi(multiIndex) : INSTANCE;
        }

        private static PearlFireTintProfiles.Profile decodeProfile(
                float coreToTailLightness,
                float colorJumpiness,
                float colorAmountTakeOver,
                float vibrancyDarkening,
                float layerContrast,
                List<Float> grayFactors
        ) {
            if (grayFactors == null || grayFactors.isEmpty()) {
                throw new IllegalArgumentException("gray_factors must not be empty");
            }

            float[] grays = new float[grayFactors.size()];
            for (int i = 0; i < grayFactors.size(); i++) {
                grays[i] = grayFactors.get(i);
            }

            return new PearlFireTintProfiles.Profile(
                    new PearlFireTintProfiles.Settings(
                            coreToTailLightness,
                            colorJumpiness,
                            colorAmountTakeOver,
                            vibrancyDarkening,
                            layerContrast
                    ),
                    grays
            );
        }

        private static List<Float> boxed(float[] values) {
            List<Float> result = new ArrayList<>(values.length);
            for (float value : values) {
                result.add(value);
            }
            return List.copyOf(result);
        }
    }
    //?}
}
