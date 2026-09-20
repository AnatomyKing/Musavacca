//? if <1.21.2 {
/*package space.anatomyuniverse.musavacca.compat.gleam;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.thatmaidenjaden.gleam.client.lighting.GleamLight;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;

public final class GleamHexLight {
    private static final int BLACKLIGHT = 0x000000;

    private static final float RADIUS = 8.0F;
    private static final float INTENSITY = 0.55F;
    private static final float COLOR_SCALE = 1.0F / 255.0F;

    private GleamHexLight() {}

    public static boolean handles(BlockState state) {
        Block block = state.getBlock();

        return block == ModBlocks.PEARL_FIRE.get()
                || block == ModBlocks.PEARL_PORTAL.get();
    }

    public static GleamLight create(
            BlockGetter level,
            int x,
            int y,
            int z
    ) {
        BlockEntity blockEntity =
                level.getBlockEntity(new BlockPos(x, y, z));

        if (!(blockEntity instanceof HexSource source)
                || !source.hasHexColor()) {
            return null;
        }

        int color = MusavaccaTints.rgb(source.getHexColor());
        boolean blacklight = color == BLACKLIGHT;

        return GleamLight.create(
                x + 0.5F,
                y + 0.5F,
                z + 0.5F,
                ((color >> 16) & 0xFF) * COLOR_SCALE,
                ((color >> 8) & 0xFF) * COLOR_SCALE,
                (color & 0xFF) * COLOR_SCALE,
                RADIUS,
                INTENSITY,
                blacklight,
                true
        );
    }
}
*///?}