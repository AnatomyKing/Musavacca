package space.anatomyuniverse.musavacca.data.models.newgen;

public final class ModelTransforms {
    private ModelTransforms() {}

    public static int quarterTurn(int degrees) {
        int value = Math.floorMod(degrees, 360);

        if (value != 0 && value != 90 && value != 180 && value != 270) {
            throw new IllegalArgumentException(
                    "Only 0/90/180/270 rotations are supported: " + degrees
            );
        }

        return value;
    }

    public static int combine(int first, int second) {
        return quarterTurn(first + second);
    }

    public static int combine(int first, int second, int third) {
        return quarterTurn(first + second + third);
    }
}
