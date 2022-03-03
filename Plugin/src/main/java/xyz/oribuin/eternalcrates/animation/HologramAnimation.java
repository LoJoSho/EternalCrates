package xyz.oribuin.eternalcrates.animation;

import xyz.oribuin.orilibrary.util.StringPlaceholders;

public abstract class HologramAnimation extends Animation {

    private StringPlaceholders placeholders;

    public HologramAnimation(String name, String author) {
        super(name, AnimationType.HOLOGRAM, author, true);
        this.placeholders = new StringPlaceholders();
    }

    public StringPlaceholders getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(StringPlaceholders placeholders) {
        this.placeholders = placeholders;
    }

}
