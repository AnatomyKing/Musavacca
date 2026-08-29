package space.anatomyuniverse.musavacca.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MusavaccaTemplateTreeConfiguration(
        List<TemplateBlock> blocks
) implements FeatureConfiguration {

    public static final Codec<MusavaccaTemplateTreeConfiguration> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    TemplateBlock.CODEC.listOf().fieldOf("blocks").forGetter(MusavaccaTemplateTreeConfiguration::blocks)
            ).apply(instance, MusavaccaTemplateTreeConfiguration::new));

    public record TemplateBlock(
            BlockPos offset,
            TemplateBlockState state
    ) {
        public static final Codec<TemplateBlock> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        BlockPos.CODEC.fieldOf("offset").forGetter(TemplateBlock::offset),
                        TemplateBlockState.CODEC.fieldOf("state").forGetter(TemplateBlock::state)
                ).apply(instance, TemplateBlock::new));
    }

    public record TemplateBlockState(
            ResourceLocation Name,
            Map<String, String> Properties
    ) {
        public static final Codec<TemplateBlockState> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("Name").forGetter(TemplateBlockState::Name),
                        Codec.unboundedMap(Codec.STRING, Codec.STRING)
                                .optionalFieldOf("Properties", Map.of())
                                .forGetter(TemplateBlockState::Properties)
                ).apply(instance, TemplateBlockState::new));

        public BlockState resolve() {
            Block block =
                    //? if <1.21.2 {
                    /*BuiltInRegistries.BLOCK.get(this.Name);
                    *///?} else {
                    BuiltInRegistries.BLOCK.getValue(this.Name);
                    //?}

            if (block == null) {
                throw new IllegalStateException("Unknown block in Musavacca tree template: " + this.Name);
            }

            BlockState state = block.defaultBlockState();

            for (Map.Entry<String, String> entry : this.Properties.entrySet()) {
                state = applyProperty(state, entry.getKey(), entry.getValue());
            }

            return state;
        }

        private static BlockState applyProperty(BlockState state, String propertyName, String valueName) {
            Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);

            if (property == null) {
                throw new IllegalStateException(
                        "Block " + BuiltInRegistries.BLOCK.getKey(state.getBlock())
                                + " does not have property '" + propertyName + "'"
                );
            }

            return applyPropertyTyped(state, property, valueName);
        }

        private static <T extends Comparable<T>> BlockState applyPropertyTyped(
                BlockState state,
                Property<T> property,
                String valueName
        ) {
            Optional<T> value = property.getValue(valueName);

            if (value.isEmpty()) {
                throw new IllegalStateException(
                        "Property '" + property.getName() + "' does not allow value '" + valueName + "'"
                );
            }

            return state.setValue(property, value.get());
        }
    }
}
