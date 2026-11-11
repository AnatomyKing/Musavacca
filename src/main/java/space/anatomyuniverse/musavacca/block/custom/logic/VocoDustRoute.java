package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.BananaPearlDustBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoDustRoute {
    private VocoDustRoute() {}

    public record Node(BlockPos pos, ReceptorPosition receptor) {
        public Node {
            pos = pos.immutable();
        }

        public boolean isReceptor() {
            return receptor != null;
        }
    }

    public static int dx(ReceptorPosition receptor) {
        return receptor == ReceptorPosition.NORTH_EAST || receptor == ReceptorPosition.SOUTH_EAST ? 1 : -1;
    }

    public static int dz(ReceptorPosition receptor) {
        return receptor == ReceptorPosition.SOUTH_EAST || receptor == ReceptorPosition.SOUTH_WEST ? 1 : -1;
    }

    public static BlockPos tableDust(BlockPos pos, ReceptorPosition receptor) {
        return pos.offset(dx(receptor), 0, dz(receptor));
    }

    public static boolean isLit(ServerLevel level, Node node) {
        return node.isReceptor() && VocoReceptorLogic.isReceptorLit(level, node.pos(), node.receptor());
    }

    public static List<Node> neighbors(ServerLevel level, Node node) {
        ArrayList<Node> connected = new ArrayList<>();
        BlockPos pos = node.pos();
        BlockState state = level.getBlockState(pos);
        if (node.isReceptor()) {
            if (!isLit(level, node)) return connected;
            if (state.getBlock() instanceof VocoTableBlock) {
                BlockPos dust = tableDust(pos, node.receptor());
                if (level.getBlockState(dust).getBlock() instanceof BananaPearlDustBlock) connected.add(new Node(dust, null));
            } else if (state.getBlock() instanceof VocoPostBlock) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos dust = pos.relative(direction);
                    if (level.getBlockState(dust).getBlock() instanceof BananaPearlDustBlock) connected.add(new Node(dust, null));
                }
            }
            return connected;
        }
        if (!(state.getBlock() instanceof BananaPearlDustBlock)) return connected;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos next = BananaPearlDustBlock.connected(level, pos, direction);
            if (next == null) continue;
            BlockState adjacent = level.getBlockState(next);
            if (adjacent.getBlock() instanceof BananaPearlDustBlock) connected.add(new Node(next, null));
            else if (adjacent.getBlock() instanceof VocoPostBlock) {
                ReceptorPosition receptor = VocoPostBlock.receptorPosition(adjacent);
                Node endpoint = new Node(next, receptor);
                if (isLit(level, endpoint)) connected.add(endpoint);
            }
        }
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            BlockPos table = pos.offset(-dx(receptor), 0, -dz(receptor));
            if (!(level.getBlockState(table).getBlock() instanceof VocoTableBlock)) continue;
            Node endpoint = new Node(table, receptor);
            if (isLit(level, endpoint)) connected.add(endpoint);
        }
        return connected;
    }
}
