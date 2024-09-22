package com.ray3k.elementclicker;

import com.badlogic.gdx.utils.Array;

public class Demand {
    public Resource resource;
    public long quantity;
    public long reward;

    public void click() {
        resource.quantity -= quantity;
        Core.gold += reward;
        Core.goldHistory += reward;
        if (Core.goldHistory > Core.milestones.peek()) Core.milestones.pop();
    }
}
