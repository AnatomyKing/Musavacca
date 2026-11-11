package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.BananaPearlDustBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;

public record VocoDustRoute(List<BlockPos> nodes) {
    public static final int MAX_DUST = 512;

    public VocoDustRoute {
        nodes = List.copyOf(nodes);
    }

    public static VocoDustRoute find(ServerLevel level, BlockPos source) {
        Direction facing = poweredFacing(VocoPostBlock.refreshPowered(level, source));
        if (facing == null) return null;
        ArrayList<BlockPos> nodes = new ArrayList<>();
        nodes.add(source.immutable());
        BlockPos previous = source;
        BlockPos current = source.relative(facing);
        while (nodes.size() <= MAX_DUST) {
            BlockPos next = nextAfter(level, current, previous);
            if (next == null) return null;
            nodes.add(current);
            if (level.getBlockState(next).getBlock() instanceof VocoPostBlock) {
                Direction incoming = poweredFacing(VocoPostBlock.refreshPowered(level, next));
                if (next.equals(source) || incoming == null || !next.relative(incoming).equals(current)) return null;
                nodes.add(next);
                return new VocoDustRoute(nodes);
            }
            previous = current;
            current = next;
        }
        return null;
    }

    private static Direction poweredFacing(BlockState state) {
        return state.getBlock() instanceof VocoPostBlock && state.getValue(VocoPostBlock.LIT) && state.getValue(VocoPostBlock.POWERED)
                ? state.getValue(VocoPostBlock.POWERED_FACING) : null;
    }

    private static BlockPos nextAfter(ServerLevel level, BlockPos current, BlockPos previous) {
        if (!(level.getBlockState(current).getBlock() instanceof BananaPearlDustBlock)) return null;
        BlockPos next = null;
        boolean connectsBack = false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = BananaPearlDustBlock.connected(level, current, direction);
            if (neighbor == null) continue;
            if (neighbor.equals(previous)) connectsBack = true;
            else if (next == null) next = neighbor;
            else return null;
        }
        return connectsBack ? next : null;
    }

    public boolean endpointsReady(ServerLevel level) {
        return attachedTo(level, 0, 1) && attachedTo(level, nodes.size() - 1, nodes.size() - 2);
    }

    private boolean attachedTo(ServerLevel level, int postIndex, int dustIndex) {
        BlockPos post = nodes.get(postIndex);
        Direction facing = poweredFacing(level.getBlockState(post));
        return facing != null && post.relative(facing).equals(nodes.get(dustIndex));
    }

    public boolean intactAt(ServerLevel level, int index) {
        return nodes.get(index + 1).equals(nextAfter(level, nodes.get(index), nodes.get(index - 1)));
    }
}
