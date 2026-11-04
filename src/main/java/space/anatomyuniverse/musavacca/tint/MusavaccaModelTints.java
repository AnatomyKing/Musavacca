package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
*///?} else {
import net.minecraft.world.level.BlockAndTintGetter;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlFireBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlPortalBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.data.models.newgen.Tints;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//? if >=1.21.4 {
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import space.anatomyuniverse.musavacca.MusaCore;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

public final class MusavaccaModelTints {
    private static final Hex HEX = new Hex(-1);
    private MusavaccaModelTints() {}
    public record Hex(int multiIndex) implements Tints.Tint {
        public Hex {
            if (multiIndex < -1) throw new IllegalArgumentException("multiIndex must be >= -1");
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
            return MusavaccaTints.opaqueRgb(sourceColor(state, level, pos, multiIndex));
        }

        @Override
        public int itemColor(ItemStack stack) {
            return MusavaccaTints.opaqueRgb(itemSourceColor(stack, multiIndex));
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

    public record PearlFire(PearlFireTintProfiles.Profile profile, int multiIndex, int physicalLayer) implements Tints.Tint {
        public PearlFire {
            Objects.requireNonNull(profile, "profile");
            if (multiIndex < -1) throw new IllegalArgumentException("multiIndex must be >= -1");
            if (physicalLayer < -1) throw new IllegalArgumentException("physicalLayer must be >= -1");
            if (physicalLayer >= 0 && !profile.supports(physicalLayer)) throw new IllegalArgumentException("physicalLayer is outside the profile");
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
            if (physicalLayer >= 0) return List.of(new Tints.GeneratedLayer(physicalLayer, tintIndex()));
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
            int color = sourceColor(state, level, pos, multiIndex);
            return PearlFireTintSource.profileTint(color, tintIndex - tintOffset(multiIndex), profile);
        }

        @Override
        public int itemColor(ItemStack stack) {
            return PearlFireTintSource.profileTint(itemSourceColor(stack, multiIndex), Math.max(physicalLayer, 0), profile);
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

    private static int sourceColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int multiIndex) {
        if (level == null || pos == null) return MusavaccaTints.NO_TINT;
        BlockPos sourcePos = state.getBlock() instanceof MusavaccaPortalDoorBlock ? MusavaccaPortalDoorBlock.lowerDoorPos(state, pos) : pos;
        BlockEntity blockEntity = level.getBlockEntity(sourcePos);
        if (multiIndex >= 0) {
            if (blockEntity == null) return MusavaccaTints.NO_TINT;
            if (blockEntity instanceof MusavaccaTints.MultiHexSource source) return MusavaccaTints.stored(source.getHexColor(multiIndex));
            DataComponentMap components = blockEntity.collectComponents();
            return MusavaccaTints.multiColor(components.get(ModDataComponents.MULTI_HEX_COLOR.get()), multiIndex);
        }
        int color = MusavaccaTints.blockEntityHexColor(blockEntity);
        if (!(state.getBlock() instanceof PearlFireBlock) && !(state.getBlock() instanceof PearlPortalBlock)) return color;
        Minecraft minecraft = Minecraft.getInstance();
        Level world = minecraft == null ? null : minecraft.level;
        if (world == null) return color;
        if (MusavaccaTints.hasTint(color)) {
            //? if >=1.21.11 {
            /*PearlPlacementColorMemory.remember(world, sourcePos, color);
            *///?}
            return color;
        }
        Integer remembered = PearlPlacementColorMemory.get(world, sourcePos);
        return remembered == null ? color : remembered;
    }

    private static int itemSourceColor(ItemStack stack, int multiIndex) {
        if (stack == null || stack.isEmpty()) return MusavaccaTints.NO_TINT;
        if (multiIndex >= 0) return MusavaccaTints.multiColor(stack.get(ModDataComponents.MULTI_HEX_COLOR.get()), multiIndex);
        Integer color = stack.get(ModDataComponents.HEX_COLOR.get());
        return color == null ? MusavaccaTints.NO_TINT : MusavaccaTints.stored(color);
    }

    private static int tintOffset(int multiIndex) {
        return multiIndex >= 0 ? multiIndex * 100 : 0;
    }

    private static int requireMultiIndex(int index) {
        if (index < 0) throw new IllegalArgumentException("multi index must be >= 0");
        return index;
    }

    //? if >=1.21.4 {
    private static final Tints.ItemTintType ITEM_TINT_TYPE =
            //? if >=1.21.11 {
            /*new Tints.ItemTintType(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, MusavaccaTints.HEX_COLOR_KEY), ItemTint.MAP_CODEC);
            *///?} else {
            new Tints.ItemTintType(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, MusavaccaTints.HEX_COLOR_KEY), ItemTint.MAP_CODEC);
            //?}
    public record ItemTint(PearlFireTintProfiles.Profile profile, int layer, int multiIndex) implements ItemTintSource {
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
                                .forGetter(ItemTint::grayFactors)
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
        public ItemTint {
            if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
            if (multiIndex < -1) throw new IllegalArgumentException("multiIndex must be >= -1");
            if (profile != null && !profile.supports(layer)) throw new IllegalArgumentException("layer is outside the profile");
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
            return profile == null ? MusavaccaTints.opaqueRgb(color) : PearlFireTintSource.profileTint(color, layer, profile);
        }

        @Override
        public MapCodec<ItemTint> type() {
            return MAP_CODEC;
        }

        private static ItemTint decode(Optional<PearlFireTintProfiles.Profile> profile, int layer, int multiIndex) {
            return profile.isPresent() ? pearlFire(profile.get(), layer, multiIndex) : multiIndex >= 0 ? multi(multiIndex) : INSTANCE;
        }

        private static PearlFireTintProfiles.Profile decodeProfile(float coreToTailLightness, float colorJumpiness, float colorAmountTakeOver,
                float vibrancyDarkening, float layerContrast, List<Float> grayFactors) {
            if (grayFactors == null || grayFactors.isEmpty()) throw new IllegalArgumentException("gray_factors must not be empty");
            float[] grays = new float[grayFactors.size()];
            for (int i = 0; i < grayFactors.size(); i++) {
                grays[i] = grayFactors.get(i);
            }
            return new PearlFireTintProfiles.Profile(
                    new PearlFireTintProfiles.Settings(coreToTailLightness, colorJumpiness, colorAmountTakeOver, vibrancyDarkening, layerContrast), grays);
        }

        private static List<Float> grayFactors(PearlFireTintProfiles.Profile profile) {
            List<Float> result = new ArrayList<>(profile.layerCount());
            for (int layer = 0; layer < profile.layerCount(); layer++) {
                result.add(profile.grayFactor(layer));
            }
            return result;
        }
    }
    //?}
}
