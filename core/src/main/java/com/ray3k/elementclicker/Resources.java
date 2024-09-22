package com.ray3k.elementclicker;

import com.badlogic.gdx.utils.Array;

public class Resources {
    public static Array<Resource> resources;
    public static Array<AutoClicker> autoClickers;
    public static Array<Demand> demands;

    public static void initResources() {
        resources = new Array<>();
        autoClickers = new Array<>();
        demands = new Array<>();

        resources.add(new Resource("fire"));
        resources.add(new Resource("water"));
        resources.add(new Resource("earth"));
        resources.add(new Resource("air"));
        resources.add(new Resource("steam"));//water + fire
        resources.add(new Resource("rain"));//water + air
        resources.add(new Resource("mud"));//water + earth
        resources.add(new Resource("smoke"));//fire + air
        resources.add(new Resource("lava"));//fire + earth
        resources.add(new Resource("dust"));//air + earth
        resources.add(new Resource("ice"));//water + water
        resources.add(new Resource("plasma"));//fire + fire
        resources.add(new Resource("tornado"));//air + air
        resources.add(new Resource("stone"));//earth + earth
        resources.add(new Resource("sleet"));//ice + rain
        resources.add(new Resource("brimstone"));//lava + dust
        resources.add(new Resource("explosion"));//fire + smoke
        resources.add(new Resource("metal"));//stone + fire
        resources.add(new Resource("bullet"));//metal + smoke
        resources.add(new Resource("raygun"));//plasma + bullet
        resources.add(new Resource("plant"));//dust + water
        resources.add(new Resource("wood"));//earth + plant
        resources.add(new Resource("paper"));//wood + metal
        resources.add(new Resource("money"));//paper + paper
        resources.add(new Resource("coal"));//fire + wood
        resources.add(new Resource("poison"));//plant + plant
        resources.add(new Resource("gas"));//poison + air
        resources.add(new Resource("boulder"));//stone + stone
        resources.add(new Resource("alloy"));//metal + metal
        resources.add(new Resource("electricity"));//steam + fire
        resources.add(new Resource("robot"));//electricity + metal
        resources.add(new Resource("life"));//electricity + water
        resources.add(new Resource("forest"));//wood + wood
        resources.add(new Resource("wildfire"));//forest + fire
        resources.add(new Resource("human"));//life + earth
        resources.add(new Resource("murder"));//human + bullet
        resources.add(new Resource("war"));//murder + earth
        resources.add(new Resource("apocalypse"));//war + war
        resources.add(new Resource("baby"));//human + human
        resources.add(new Resource("farm"));//plant + rain
        resources.add(new Resource("mine"));//coal + coal
        resources.add(new Resource("capitalism"));//money + money
        resources.add(new Resource("airplane"));//alloy + air
        resources.add(new Resource("predator"));//forest + raygun
        resources.add(new Resource("military industrial complex"));//capitalism + war
        resources.add(new Resource("franchise"));//predator + capitalism
        resources.add(new Resource("nuke"));//airplane + explosion

        createAutoClicker(null, null, "fire", 50, 0);
        createAutoClicker(null, null, "water", 50, 0);
        createAutoClicker(null, null, "earth", 50, 0);
        createAutoClicker(null, null, "air", 50, 0);
        createAutoClicker("water", "fire", "steam", 200, 1);
        createAutoClicker("water", "air", "rain", 200, 1);
        createAutoClicker("water", "earth", "mud", 200, 1);
        createAutoClicker("air", "fire", "smoke", 200, 1);
        createAutoClicker("earth", "fire", "lava", 200, 1);
        createAutoClicker("air", "earth", "dust", 200, 1);
        createAutoClicker("water", "water", "ice", 200, 1);
        createAutoClicker("fire", "fire", "plasma", 200, 1);
        createAutoClicker("air", "air", "tornado", 200, 1);
        createAutoClicker("earth", "earth", "stone", 200, 1);
        createAutoClicker("ice", "rain", "sleet", 300, 2);
        createAutoClicker("lava", "dust", "brimstone", 300, 2);
        createAutoClicker("smoke", "fire", "explosion", 300, 2);
        createAutoClicker("stone", "fire", "metal", 300, 2);
        createAutoClicker("metal", "smoke", "bullet", 400, 3);
        createAutoClicker("plasma", "bullet", "raygun", 400, 3);
        createAutoClicker("dust", "water", "plant", 300, 2);
        createAutoClicker("earth", "plant", "wood", 300, 2);
        createAutoClicker("wood", "metal", "paper", 400, 3);
        createAutoClicker("paper", "paper", "money", 400, 3);
        createAutoClicker("wood", "fire", "coal", 300, 2);
        createAutoClicker("plant", "plant", "poison", 400, 3);
        createAutoClicker("poison", "air", "gas", 500, 4);
        createAutoClicker("stone", "stone", "boulder", 400, 3);
        createAutoClicker("metal", "metal", "alloy", 400, 3);
        createAutoClicker("steam", "fire", "electricity", 300, 2);
        createAutoClicker("electricity", "metal", "robot", 500, 4);
        createAutoClicker("electricity", "water", "life", 300, 2);
        createAutoClicker("wood", "wood", "forest", 400, 3);
        createAutoClicker("forest", "fire", "wildfire", 500, 4);
        createAutoClicker("life", "earth", "human", 600, 4);
        createAutoClicker("human", "bullet", "murder", 700, 5);
        createAutoClicker("murder", "earth", "war", 800, 5);
        createAutoClicker("war", "war", "apocalypse", 900, 5);
        createAutoClicker("human", "human", "baby", 600, 4);
        createAutoClicker("plant", "rain", "farm", 600, 4);
        createAutoClicker("coal", "coal", "mine", 600, 4);
        createAutoClicker("money", "money", "capitalism", 600, 4);
        createAutoClicker("alloy", "air", "airplane", 600, 4);
        createAutoClicker("forest", "raygun", "predator", 600, 4);
        createAutoClicker("capitalism", "war", "military industrial complex", 1000, 5);
        createAutoClicker("predator", "capitalism", "franchise", 1000, 5);
        createAutoClicker("airplane", "explosion", "nuke", 1000, 5);
    }

    private static void createAutoClicker(String name1, String name2, String resultName, int cost, int unlockLevel) {
        var resource1 = get(name1);
        var resource2 = get(name2);
        var result = get(resultName);

        var autoClicker = new AutoClicker(resultName + " auto clicker", resource1, resource2, result, cost, unlockLevel);
        autoClickers.add(autoClicker);
    }

    public static void add(String element, int quantity) {
        for (var resource : resources) {
            if (resource.name.equals(element)) {
                resource.quantity += quantity;
                return;
            }
        }
    }

    public static Resource get(String element) {
        for (var resource : resources) {
            if (resource.name.equals(element)) {
                return resource;
            }
        }
        return null;
    }

    public static String combine(String name1, String name2) {
        return combine(get(name1), get(name2));
    }

    private static Array<String> temp = new Array<>();

    public static String combine(Resource resource1, Resource resource2) {
        temp.clear();
        temp.add(resource1.name, resource2.name);
        if (temp.contains("water", false) && temp.contains("fire", false)) return "steam";
        else if (temp.contains("water", false) && temp.contains("air", false)) return "rain";
        else if (temp.contains("water", false) && temp.contains("earth", false)) return "mud";
        else if (temp.contains("fire", false) && temp.contains("air", false)) return "smoke";
        else if (temp.contains("fire", false) && temp.contains("earth", false)) return "lava";
        else if (temp.contains("air", false) && temp.contains("earth", false)) return "dust";
        else if (resource1.name.equals("water") && resource1 == resource2) return "ice";
        else if (resource1.name.equals("fire") && resource1 == resource2) return "plasma";
        else if (resource1.name.equals("air") && resource1 == resource2) return "tornado";
        else if (resource1.name.equals("earth") && resource1 == resource2) return "stone";
        else if (temp.contains("ice", false) && temp.contains("rain", false)) return "sleet";
        else if (temp.contains("lava", false) && temp.contains("dust", false)) return "brimstone";
        else if (temp.contains("fire", false) && temp.contains("smoke", false)) return "explosion";
        else if (temp.contains("stone", false) && temp.contains("fire", false)) return "metal";
        else if (temp.contains("metal", false) && temp.contains("smoke", false)) return "bullet";
        else if (temp.contains("plasma", false) && temp.contains("bullet", false)) return "raygun";
        else if (temp.contains("dust", false) && temp.contains("water", false)) return "plant";
        else if (temp.contains("earth", false) && temp.contains("plant", false)) return "wood";
        else if (temp.contains("wood", false) && temp.contains("metal", false)) return "paper";
        else if (temp.contains("paper", false) && temp.contains("paper", false)) return "money";
        else if (temp.contains("wood", false) && temp.contains("fire", false)) return "coal";
        else if (resource1.name.equals("plant") && resource1 == resource2) return "poison";
        else if (temp.contains("poison", false) && temp.contains("air", false)) return "gas";
        else if (resource1.name.equals("stone") && resource1 == resource2) return "boulder";
        else if (resource1.name.equals("metal") && resource1 == resource2) return "alloy";
        else if (temp.contains("steam", false) && temp.contains("fire", false)) return "electricity";
        else if (temp.contains("electricity", false) && temp.contains("metal", false)) return "robot";
        else if (temp.contains("water", false) && temp.contains("electricity", false)) return "life";
        else if (resource1.name.equals("wood") && resource1 == resource2) return "forest";
        else if (temp.contains("forest", false) && temp.contains("fire", false)) return "wildfire";
        else if (temp.contains("life", false) && temp.contains("earth", false)) return "human";
        else if (temp.contains("human", false) && temp.contains("bullet", false)) return "murder";
        else if (temp.contains("murder", false) && temp.contains("earth", false)) return "war";
        else if (resource1.name.equals("war") && resource1 == resource2) return "apocalypse";
        else if (resource1.name.equals("human") && resource1 == resource2) return "baby";
        else if (temp.contains("plant", false) && temp.contains("rain", false)) return "farm";
        else if (resource1.name.equals("coal") && resource1 == resource2) return "mine";
        else if (resource1.name.equals("money") && resource1 == resource2) return "capitalism";
        else if (temp.contains("alloy", false) && temp.contains("air", false)) return "airplane";
        else if (temp.contains("forest", false) && temp.contains("raygun", false)) return "predator";
        else if (temp.contains("capitalism", false) && temp.contains("war", false)) return "military industrial complex";
        else if (temp.contains("predator", false) && temp.contains("capitalism", false)) return "franchise";
        else if (temp.contains("airplane", false) && temp.contains("explosion", false)) return "nuke";

        return null;
    }
}
