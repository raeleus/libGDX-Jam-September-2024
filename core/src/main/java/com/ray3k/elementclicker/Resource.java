package com.ray3k.elementclicker;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Resource {
    public String name;
    public long quantity;
    public TextButton textButton;

    public Resource(String name) {
        this.name = name;
    }

    public Resource(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
