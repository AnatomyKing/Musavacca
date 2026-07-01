package space.anatomyuniverse.musavacca.component;

public interface MutableHexColorSource extends HexColorSource {
    boolean setHexColorSlot(String slot, int hexColor);

    boolean clearHexColorSlot(String slot);
}
