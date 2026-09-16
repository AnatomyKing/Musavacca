package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class BlockFamilyValidation {
    private BlockFamilyValidation() {}

    static <T extends Comparable<T>> void requireProperty(
            Block block,
            Property<T> property,
            String family
    ) {
        if (!block.defaultBlockState().hasProperty(property)) {
            throw new IllegalStateException(
                    family + " requires property " + property.getName()
                            + " on " + ModelLocations.blockId(block)
            );
        }
    }

    static void conditions(Block block, Conditions.Match conditions) {
        for (Conditions.Term<?> term : conditions.terms()) {
            requireProperty(block, term.property(), "Condition");
            if (!term.property().getPossibleValues().contains(term.value())) {
                throw new IllegalStateException("Invalid condition " + term + " on " + ModelLocations.blockId(block));
            }
        }
    }

    static void rotation(Block block, Rotations.Spec rotations, String family) {
        if (rotations.type() == Rotations.Type.BRICKS) {
            return;
        }

        Property<?> property = rotations.property();

        if (!block.defaultBlockState().hasProperty(property)) {
            throw new IllegalStateException(
                    family + " rotation " + rotations.type()
                            + " expects property " + property
                            + " on " + ModelLocations.blockId(block)
            );
        }

        Set<Comparable<?>> mapped = new HashSet<>();

        for (Rotations.Case rotation : rotations.cases()) {
            if (rotation.value() != null) {
                mapped.add(rotation.value());
            }
        }

        for (Comparable<?> value : property.getPossibleValues()) {
            if (!mapped.contains(value)) {
                throw new IllegalStateException(
                        family + " rotation " + rotations.type()
                                + " has no mapping for " + property.getName()
                                + "=" + value
                                + " on " + ModelLocations.blockId(block)
                );
            }
        }
    }

    static void exactlyOneModelPerState(
            Block block,
            List<? extends BlockFamily.ModelRule<?, ?>> models,
            String family
    ) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            int matches = 0;

            for (BlockFamily.ModelRule<?, ?> model : models) {
                if (model.conditions().matches(state)) {
                    matches++;
                }
            }

            if (matches != 1) {
                throw new IllegalStateException(
                        family + " MODELS must match every block state exactly once, but "
                                + matches + " models match " + state
                );
            }
        }
    }
}

