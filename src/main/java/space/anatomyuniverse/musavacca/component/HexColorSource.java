package space.anatomyuniverse.musavacca.component;

import java.util.Map;

public interface HexColorSource {
    Map<String, Integer> getHexColors();

    default Integer getHexColor(String slot) {
        return HexColorComponent.getSlot(getHexColors(), slot);
    }

    default int getHexColorOrUnset(String slot) {
        Integer color = getHexColor(slot);
        return color == null ? HexColorComponent.UNSET : color;
    }
}
