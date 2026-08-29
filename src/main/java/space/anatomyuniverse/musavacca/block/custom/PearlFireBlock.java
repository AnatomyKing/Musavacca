package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
//? if >=1.21.5
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if <1.21.2 {
/*import net.minecraft.world.level.LevelAccessor;
*///?} else {
import net.minecraft.world.level.ScheduledTickAccess;
//?}
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;

public class PearlFireBlock extends FireBlock implements EntityBlock {
    public PearlFireBlock(Properties properties) {
        super(properties);
    }

    public BlockState getPlacementState(BlockGetter level, BlockPos pos) {
        return this.getStateForPlacement(level, pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlFireBlockEntity(pos, state);
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    //? if >=1.21.5 {
    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier effectApplier
    ) {
        if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
            tryStampCleanSimCard(level, pos, itemEntity);
        }

        super.entityInside(state, level, pos, entity, effectApplier);
    }
    //?} else {
    /*@Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
            tryStampCleanSimCard(level, pos, itemEntity);
        }

        super.entityInside(state, level, pos, entity);
    }
    *///?}

    private static void tryStampCleanSimCard(Level level, BlockPos firePos, ItemEntity itemEntity) {
        ItemStack oldStack = itemEntity.getItem();

        if (!(oldStack.getItem() instanceof SimCardItem)) {
            return;
        }

        if (oldStack.get(ModDataComponents.HEX_COLOR.get()) != null) {
            return;
        }

        int fireHex = getPearlFireHex(level, firePos);
        if (fireHex == PearlFireBlockEntity.UNSET_HEX_COLOR) {
            return;
        }

        ItemStack stampedStack = oldStack.copy();
        SimCardItem.setStoredHex(stampedStack, fireHex);

        itemEntity.setItem(stampedStack);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) {
            return;
        }

        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex == null) {
            return;
        }

        setPlacedPearlFireHex(level, pos, savedHex);
    }

    //? if <1.21.2 {
    /*@Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        return this.canSurvive(state, level, pos)
                ? this.getPearlStateWithAge(level, pos, state.getValue(AGE))
                : Blocks.AIR.defaultBlockState();
    }
    *///?} else {
    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        return this.canSurvive(state, level, pos)
                ? this.getPearlStateWithAge(level, pos, state.getValue(AGE))
                : Blocks.AIR.defaultBlockState();
    }
    //?}

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, getPearlFireTickDelay(level.random));

        if (!shouldRunPearlFireTick(level, pos)) {
            return;
        }

        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
            return;
        }

        int sourceHex = getPearlFireHex(level, pos);

        BlockState belowState = level.getBlockState(pos.below());
        boolean eternalSource = belowState.isFireSource(level, pos.below(), Direction.UP);
        int age = state.getValue(AGE);

        if (!eternalSource && level.isRaining() && this.isNearRain(level, pos)
                && random.nextFloat() < 0.2F + (float) age * 0.03F) {
            level.removeBlock(pos, false);
            return;
        }

        int newAge = Math.min(15, age + random.nextInt(3) / 2);
        if (age != newAge) {
            state = state.setValue(AGE, newAge);
            level.setBlock(pos, state, 260);
            age = newAge;
        }

        if (!eternalSource) {
            if (!this.isValidPearlFireLocation(level, pos)) {
                BlockPos belowPos = pos.below();
                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP) || age > 3) {
                    level.removeBlock(pos, false);
                }
                return;
            }

            if (age == 15 && random.nextInt(4) == 0 && !this.canCatchFire(level, pos.below(), Direction.UP)) {
                level.removeBlock(pos, false);
                return;
            }
        }

        boolean burnoutBiome = level.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
        int biomeModifier = burnoutBiome ? -50 : 0;

        this.checkPearlBurnOut(level, pos.east(), 300 + biomeModifier, random, age, Direction.WEST, sourceHex);
        this.checkPearlBurnOut(level, pos.west(), 300 + biomeModifier, random, age, Direction.EAST, sourceHex);
        this.checkPearlBurnOut(level, pos.below(), 250 + biomeModifier, random, age, Direction.UP, sourceHex);
        this.checkPearlBurnOut(level, pos.above(), 250 + biomeModifier, random, age, Direction.DOWN, sourceHex);
        this.checkPearlBurnOut(level, pos.north(), 300 + biomeModifier, random, age, Direction.SOUTH, sourceHex);
        this.checkPearlBurnOut(level, pos.south(), 300 + biomeModifier, random, age, Direction.NORTH, sourceHex);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int dy = -1; dy <= 4; ++dy) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    int chanceDivisor = 100;
                    if (dy > 1) {
                        chanceDivisor += (dy - 1) * 100;
                    }

                    mutable.setWithOffset(pos, dx, dy, dz);
                    int igniteOdds = this.getPearlIgniteOdds(level, mutable);

                    if (igniteOdds <= 0) {
                        continue;
                    }

                    int spreadChance = (igniteOdds + 40 + level.getDifficulty().getId() * 7) / (age + 30);
                    if (burnoutBiome) {
                        spreadChance /= 2;
                    }

                    if (spreadChance <= 0
                            || random.nextInt(chanceDivisor) > spreadChance
                            || (level.isRaining() && this.isNearRain(level, mutable))) {
                        continue;
                    }

                    int spreadAge = Math.min(15, age + random.nextInt(5) / 4);
                    level.setBlock(mutable, this.getPearlStateWithAge(level, mutable, spreadAge), 3);

                    if (sourceHex != PearlFireBlockEntity.UNSET_HEX_COLOR) {
                        setPlacedPearlFireHex(level, mutable, sourceHex);
                    }
                }
            }
        }
    }

    private static boolean shouldRunPearlFireTick(ServerLevel level, BlockPos pos) {
        //? if >=1.21.5 {
        return level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)
                && (level.getGameRules().getBoolean(GameRules.RULE_ALLOWFIRETICKAWAYFROMPLAYERS)
                || level.anyPlayerCloseEnoughForSpawning(pos));
        //?}
        //? if <1.21.5
        //return level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK);
    }

    private static int getPearlFireHex(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PearlFireBlockEntity pearlFireBe && pearlFireBe.hasHexColor()) {
            return pearlFireBe.getHexColor();
        }

        return PearlFireBlockEntity.UNSET_HEX_COLOR;
    }

    private static void setPlacedPearlFireHex(Level level, BlockPos pos, int hexColor) {
        if (level.isClientSide()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PearlFireBlockEntity pearlFireBe) {
            pearlFireBe.setHexColor(hexColor);
        }
    }

    private BlockState getPearlStateWithAge(LevelReader level, BlockPos pos, int age) {
        BlockState state = this.getPlacementState(level, pos);
        return state.is(this) ? state.setValue(AGE, age) : state;
    }

    private boolean isValidPearlFireLocation(BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canCatchFire(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    private int getPearlIgniteOdds(LevelReader level, BlockPos pos) {
        if (!level.isEmptyBlock(pos)) {
            return 0;
        }

        int max = 0;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            max = Math.max(
                    neighborState.getFireSpreadSpeed(level, neighborPos, direction.getOpposite()),
                    max
            );
        }

        return max;
    }

    private void checkPearlBurnOut(
            Level level,
            BlockPos pos,
            int chance,
            RandomSource random,
            int age,
            Direction face,
            int sourceHex
    ) {
        int flammability = level.getBlockState(pos).getFlammability(level, pos, face);

        if (random.nextInt(chance) >= flammability) {
            return;
        }

        BlockState targetState = level.getBlockState(pos);
        targetState.onCaughtFire(level, pos, face, null);

        if (random.nextInt(age + 10) < 5 && !level.isRainingAt(pos)) {
            int newAge = Math.min(age + random.nextInt(5) / 4, 15);
            level.setBlock(pos, this.getPearlStateWithAge(level, pos, newAge), 3);

            if (sourceHex != PearlFireBlockEntity.UNSET_HEX_COLOR) {
                setPlacedPearlFireHex(level, pos, sourceHex);
            }
        } else {
            level.removeBlock(pos, false);
        }
    }

    private static int getPearlFireTickDelay(RandomSource random) {
        return 30 + random.nextInt(10);
    }

    public static void bootStrap() {
        PearlFireBlock fire = ModBlocks.PEARL_FIRE.get();

        fire.setFlammable(ModBlocks.MUSAVACCA_PLANKS.get(), 5, 20);
        fire.setFlammable(ModBlocks.MUSAVACCA_STEM.get(), 5, 5);
        fire.setFlammable(ModBlocks.STRIPPED_MUSAVACCA_STEM.get(), 5, 5);
        fire.setFlammable(ModBlocks.MUSAVACCA_LEAVES.get(), 30, 60);
    }
}


