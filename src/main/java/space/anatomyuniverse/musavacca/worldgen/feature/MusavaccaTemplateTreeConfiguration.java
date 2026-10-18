package space.anatomyuniverse.musavacca.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
//? if <26.3 {

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
//?}

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MusavaccaTemplateTreeConfiguration(
        List<TemplateBlock> blocks
)

//? if <26.3 {
implements FeatureConfiguration
//?}
{

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
            //? if >=1.21.11 {
            /*Identifier Name,
            *///?} else {
            ResourceLocation Name,
            //?}
            Map<String, String> Properties
    ) {
        public static final Codec<TemplateBlockState> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        //? if >=1.21.11 {
                        /*Identifier.CODEC.fieldOf("Name").forGetter(TemplateBlockState::Name),
                        *///?} else {
                        ResourceLocation.CODEC.fieldOf("Name").forGetter(TemplateBlockState::Name),
                        //?}
                        Codec.unboundedMap(Codec.STRING, Codec.STRING)
                                .optionalFieldOf("Properties", Map.of())
                                .forGetter(TemplateBlockState::Properties)
                ).apply(instance, TemplateBlockState::new));

        public BlockState resolve() {
            //? if >=1.21.2 {
            Block block = BuiltInRegistries.BLOCK.getValue(this.Name);
            //?} else {
            /*Block block = BuiltInRegistries.BLOCK.get(this.Name);
            *///?}

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
