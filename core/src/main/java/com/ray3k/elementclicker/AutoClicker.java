package com.ray3k.elementclicker;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class AutoClicker {
    public String name;
    public Resource resource1;
    public Resource resource2;
    public Resource resultResource;
    public long rate;
    public long cost;
    public TextButton textButton;
    public int unlockLevel;

    public AutoClicker(String name, Resource resource1, Resource resource2, Resource resultResource, int cost, int unlockLevel) {
        this.name = name;
        this.resource1 = resource1;
        this.resource2 = resource2;
        this.resultResource = resultResource;
        this.cost = cost;
        this.unlockLevel = unlockLevel;
        rate = 0;
    }

    public void click() {
        if (rate <= 0) return;

        if (resource1 != null && resource2 != null) {
            if (resource1.quantity <= 0 || resource2.quantity <= 0) return;
            resource1.quantity--;
            resource2.quantity--;
        } else {
            if (resource1 != null) {
                if (resource1.quantity <= 1) return;
                resource1.quantity--;
            }
            if (resource2 != null) {
                if (resource2.quantity <= 1) return;
                resource2.quantity--;
            }
        }
        resultResource.quantity += rate;
    }
}
