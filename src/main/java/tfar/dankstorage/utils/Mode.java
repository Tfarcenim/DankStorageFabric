package tfar.dankstorage.utils;

public enum Mode {
    normal(0xffffff), pickup_all(0x00ff00), filtered_pickup(0xffff00), void_pickup(0xff0000);

    public static final Mode[] modes = values();
    private final int color;

    Mode(int color) {
        this.color = color;
    }

    public float r() {
        return (color >> 16 & 0xff) / 255f;
    }

    public float g() {
        return (color >> 8 & 0xff) / 255f;
    }

    public float b() {
        return (color & 0xff) / 255f;
    }

    public Mode cycle() {
        if (ordinal() < modes.length - 1)
            return modes[ordinal() + 1];
        return normal;
    }
}