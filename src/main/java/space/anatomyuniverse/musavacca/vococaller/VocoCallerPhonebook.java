// file: src/main/java/space/anatomyuniverse/musavacca/vococaller/VocoCallerPhonebook.java
package space.anatomyuniverse.musavacca.vococaller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record VocoCallerPhonebook(List<Integer> recent, List<Integer> saved) {
    public static final int ROW_COUNT = 13;
    public static final int EMPTY = -1;
    public static final VocoCallerPhonebook EMPTY_PHONEBOOK =
            new VocoCallerPhonebook(List.of(), List.of());

    public static final Codec<VocoCallerPhonebook> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.listOf()
                            .optionalFieldOf("recent", List.of())
                            .forGetter(VocoCallerPhonebook::recent),
                    Codec.INT.listOf()
                            .optionalFieldOf("saved", List.of())
                            .forGetter(VocoCallerPhonebook::saved)
            ).apply(instance, VocoCallerPhonebook::new));

    /*
     * Compact ItemStack network codec.
     *
     * 0            = empty row
     * 1..0x1000000 = hex address + 1
     *
     * Empty rows therefore cost one byte and normal addresses use only the
     * VarInt bytes they actually need. This is used when Minecraft syncs a
     * SIM ItemStack (including a SIM nested inside the Banana Phone bundle).
     */
    public static final StreamCodec<ByteBuf, VocoCallerPhonebook> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public VocoCallerPhonebook decode(ByteBuf buffer) {
                    int[] recent = readAddresses(buffer);
                    int[] saved = readAddresses(buffer);
                    return VocoCallerPhonebook.of(recent, saved);
                }

                @Override
                public void encode(
                        ByteBuf buffer,
                        VocoCallerPhonebook phonebook
                ) {
                    writeAddresses(buffer, phonebook.recent);
                    writeAddresses(buffer, phonebook.saved);
                }
            };

    public VocoCallerPhonebook {
        recent = normalize(recent);
        saved = normalize(saved);
    }

    public static VocoCallerPhonebook of(int[] recent, int[] saved) {
        return new VocoCallerPhonebook(
                toList(recent),
                toList(saved)
        );
    }

    public int[] recentArray() {
        return toArray(this.recent);
    }

    public int[] savedArray() {
        return toArray(this.saved);
    }

    private static int[] readAddresses(ByteBuf buffer) {
        int[] result = new int[ROW_COUNT];

        for (int row = 0; row < ROW_COUNT; row++) {
            int encoded = ByteBufCodecs.VAR_INT.decode(buffer);
            result[row] = encoded == 0
                    ? EMPTY
                    : (encoded - 1) & 0xFFFFFF;
        }

        return result;
    }

    private static void writeAddresses(
            ByteBuf buffer,
            List<Integer> values
    ) {
        for (int row = 0; row < ROW_COUNT; row++) {
            int value = valueAt(values, row);

            ByteBufCodecs.VAR_INT.encode(
                    buffer,
                    value < 0
                            ? 0
                            : (value & 0xFFFFFF) + 1
            );
        }
    }

    private static List<Integer> normalize(List<Integer> values) {
        ArrayList<Integer> result = new ArrayList<>(ROW_COUNT);

        for (int row = 0; row < ROW_COUNT; row++) {
            result.add(valueAt(values, row));
        }

        return List.copyOf(result);
    }

    private static List<Integer> toList(int[] values) {
        ArrayList<Integer> result = new ArrayList<>(ROW_COUNT);

        for (int row = 0; row < ROW_COUNT; row++) {
            result.add(valueAt(values, row));
        }

        return result;
    }

    private static int[] toArray(List<Integer> values) {
        int[] result = new int[ROW_COUNT];

        for (int row = 0; row < ROW_COUNT; row++) {
            result[row] = valueAt(values, row);
        }

        return result;
    }

    private static int valueAt(List<Integer> values, int row) {
        int value = values != null && row < values.size()
                ? values.get(row)
                : EMPTY;

        return value < 0
                ? EMPTY
                : value & 0xFFFFFF;
    }

    private static int valueAt(int[] values, int row) {
        int value = values != null && row < values.length
                ? values[row]
                : EMPTY;

        return value < 0
                ? EMPTY
                : value & 0xFFFFFF;
    }
}
