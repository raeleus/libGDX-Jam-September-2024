package com.ray3k.elementclicker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.raeleus.gamejoltapi.GameJoltApi;
import com.ray3k.stripe.PopTable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private Skin skin;
    private ScreenViewport viewport;
    private Stage stage;
    private Table root;
    private DragAndDrop dragAndDrop;
    private VerticalGroup demandGroup;
    private TextButton goldButton;

    public static long gold;
    public static long goldHistory;
    public static int demandTimer = 0;
    public static final int DEMAND_DELAY = 10;
    public static final int DEMANDS_LIMIT = 5;
    public static final Array<Long> milestones = new Array<>(new Long[]{2000000L, 1000000L, 500000L, 100000L, 10000L, 1000L});
    public static final Array<String> unlocks = new Array<>(new String[]{"fire", "earth", "water", "air"});
    public static Preferences prefs;
    private static GameJoltApi gj;
    private static String gameID = "927301";
    private static String key = "8bb5cf570abcbf7cf7cb1905107bd15a";

    @Override
    public void create() {
        var music = Gdx.audio.newMusic(Gdx.files.internal("bg.mp3"));
        music.setLooping(true);
        music.play();
        gj = new GameJoltApi();
        skin = new Skin(Gdx.files.internal("skin.json"));
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        dragAndDrop = new DragAndDrop();
        prefs = Gdx.app.getPreferences("com.ray3k.elementclicker");

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.defaults().expandX().left();
        var elementsGroup = new VerticalGroup();
        elementsGroup.wrap();
        elementsGroup.columnAlign(Align.top);
        root.add(elementsGroup).growY();

        Resources.initResources();

        for (int i = 0; i < Resources.resources.size; i++) {
            var resource = Resources.resources.get(i);
            createElementButton(resource.name, elementsGroup);
        }

        enableElements("earth", "air", "fire", "water");

        demandGroup = new VerticalGroup();
        demandGroup.wrap();
        root.add(demandGroup).growY();

        root.row();
        var image = new Image(skin, "divider-10");
        root.add(image).colspan(2).growX();

        root.row();
        var shopGroup = new VerticalGroup();
        shopGroup.wrap();
        shopGroup.columnAlign(Align.top);
        root.add(shopGroup).colspan(2).growY();

        for (int i = 0; i < Resources.autoClickers.size; i++) {
            var autoClicker = Resources.autoClickers.get(i);
            createShopButton(autoClicker.name, shopGroup, autoClicker);
        }

        root.row();
        var statsGroup = new HorizontalGroup();
        statsGroup.wrap();
        root.add(statsGroup).colspan(2).growX();

        goldButton = new TextButton("", skin);
        goldButton.setTouchable(Touchable.disabled);
        goldButton.setName("coin");
        statsGroup.addActor(goldButton);

        var textButton = new TextButton("UPLOAD SCORE", skin);
        statsGroup.addActor(textButton);
        onChange(textButton, () -> {
            playSound("Blip_Select2.mp3");
            showScoreDialog();
        });

        updateEverything();
        stage.addAction(forever(delay(1f, run(this::gameStep))));
    }

    private TextButton createElementButton(String element, VerticalGroup group) {
        var resource = Resources.get(element);
        var clickable = element.equals("fire") || element.equals("earth") || element.equals("air") || element.equals("water");
        var textButton = new TextButton("", skin, clickable ? "default" : "notclickable");
        textButton.setUserObject(resource);
        updateElementText(textButton, resource);
        textButton.setName(element);
        textButton.setVisible(false);
        group.addActor(textButton);
        resource.textButton = textButton;

        onChange(textButton, () -> {
            if (!clickable) return;
            elementSound(element);
            resource.quantity++;
            updateElementText(textButton, resource);
        });

        dragAndDrop.addSource(new Source(textButton) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (resource.quantity <= 0) return null;

                var payload = new Payload();

                payload.setObject(textButton);

                var label = new Label(element, skin);
                payload.setDragActor(label);
                return payload;
            }
        });

        dragAndDrop.addTarget(new Target(textButton) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                var payloadTextButton = (TextButton) payload.getObject();
                var payloadResource = (Resource) payloadTextButton.getUserObject();

                if (resource == payloadResource && resource.quantity <= 1) return;
                if (resource.quantity <= 0 || payloadResource.quantity <= 0) return;

                var result = Resources.combine(resource, payloadResource);
                if (result != null) {
                    elementSound(result);
                    if (!unlocks.contains(result, false)) unlocks.add(result);
                    resource.quantity--;
                    payloadResource.quantity--;
                    updateElementText(textButton, resource);
                    updateElementText(payloadTextButton, payloadResource);

                    var textButton = enableElement(result);
                    var resultResource = Resources.get(result);
                    resultResource.quantity++;
                    updateElementText(textButton, resultResource);
                }
            }
        });
        return textButton;
    }

    private void updateElementText(TextButton textButton, Resource resource) {
        textButton.setText(resource.name + ": " + resource.quantity);
    }

    private void updateAutoClickerText(TextButton textButton, AutoClicker autoClicker) {
        textButton.setText(autoClicker.name + " x" + autoClicker.rate + " cost: " + autoClicker.cost);
    }

    private TextButton enableElement(String element) {
        var textButton = (TextButton) root.findActor(element);
        textButton.setVisible(true);
        return textButton;
    }

    private void enableElements(String ...elements) {
        for (var element : elements) enableElement(element);
    }

    private TextButton createShopButton(String name, VerticalGroup group, AutoClicker autoClicker) {
        var textButton = new TextButton("", skin);
        textButton.setUserObject(autoClicker);
        updateAutoClickerText(textButton, autoClicker);
        textButton.setName(name);
        textButton.setVisible(true);
        group.addActor(textButton);
        autoClicker.textButton = textButton;

        onChange(textButton, () -> {
            if (gold >= autoClicker.cost) {
                playSound("Pickup_Coin.mp3");
                autoClicker.rate++;
                gold -= autoClicker.cost;
                autoClicker.cost *= 1.5;
                updateEverything();
            }
        });

        return textButton;
    }

    private TextButton createDemandButton() {
        var quantity = 1;
        var resourceName = "fire";
        var price = 100;
        var level = 6 - milestones.size;

        switch (level) {
            case 0:
                quantity = MathUtils.random(5, 10);
                resourceName = (new Array<>(new String[] {"fire", "water", "earth", "air"}).random());
                price = 10;
                break;
            case 1:
                quantity = MathUtils.random(10, 20);
                resourceName = (new Array<>(new String[] {"steam", "rain", "mud", "smoke", "lava", "dust", "ice", "plasma", "tornado", "stone"}).random());
                price = 20;
                break;
            case 2:
                quantity = MathUtils.random(20, 30);
                resourceName = (new Array<>(new String[] {"sleet", "brimstone", "explosion", "metal", "plant", "wood", "coal", "electricity", "life"}).random());
                price = 100;
                break;
            case 3:
                quantity = MathUtils.random(30, 40);
                resourceName = (new Array<>(new String[] {"bullet", "raygun", "paper", "money", "poison", "boulder", "alloy", "forest"}).random());
                price = 200;
                break;
            case 4:
                quantity = MathUtils.random(40, 80);
                resourceName = (new Array<>(new String[] {"gas", "robot", "wildfire", "farm", "mine", "capitalism", "airplane", "predator"}).random());
                price = 400;
                break;
            case 5:
                quantity = MathUtils.random(100, 1000);
                resourceName = (new Array<>(new String[] {"human", "murder", "war", "apocalypse", "baby", "military industrial complex", "franchise", "nuke"}).random());
                price = 1000;
                break;
        }

        price = quantity * price;
        var resource = Resources.get(resourceName);

        var demand = new Demand();
        demand.quantity = quantity;
        demand.resource = resource;
        demand.reward = price;
        Resources.demands.add(demand);

        var textButton = new TextButton("Buyer: " + quantity + " " + resourceName + " for " + price + " gp", skin);
        demandGroup.addActor(textButton);
        onChange(textButton, () -> {
            if (demand.resource.quantity >= demand.quantity) {
                playSound("Pickup_Coin.mp3");
                textButton.remove();
                Resources.demands.removeValue(demand, true);
                demand.click();
            }
        });

        return textButton;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    public void gameStep() {
        for (int i = 0; i < Resources.autoClickers.size; i++) {
            var autoClicker = Resources.autoClickers.get(i);
            autoClicker.click();
        }

        updateEverything();

        demandTimer--;
        if (demandTimer <= 0) {
            demandTimer = DEMAND_DELAY;
            if (Resources.demands.size < DEMANDS_LIMIT) createDemandButton();
        }
    }

    private void updateEverything() {
        var level = 6 - milestones.size;

        for (int i = 0; i < Resources.resources.size; i++) {
            var resource = Resources.resources.get(i);
            updateElementText(resource.textButton, resource);
        }

        for (int i = 0; i < Resources.autoClickers.size; i++) {
            var autoClicker = Resources.autoClickers.get(i);
            updateAutoClickerText(autoClicker.textButton, autoClicker);
            autoClicker.textButton.setVisible(level >= autoClicker.unlockLevel && unlocks.contains(autoClicker.resultResource.name, false));
        }

        if (milestones.size > 0) goldButton.setText("Coin: " + gold + " Level: " + level + " Progress: " + goldHistory + "/" + milestones.peek());
        else goldButton.setText("Coin: " + gold + " Level: 6 (MAX) Total: " + goldHistory);
    }

    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }

    public void showScoreDialog() {
        PopTable popTable = new PopTable(skin.get(WindowStyle.class));

        var label = new Label("Please enter your name", skin);
        popTable.add(label);

        popTable.row();
        var textField = new TextField("", skin);
        popTable.add(textField).width(400);

        popTable.row();
        var table = new Table();
        popTable.add(table);

        table.defaults().uniformX().fillX();
        var textButton = new TextButton("OK", skin);
        table.add(textButton);
        onChange(textButton, () -> {
            playSound("Blip_Select2.mp3");
            submitScore(textField.getText(), goldHistory);
            popTable.hide();
        });

        textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        onChange(textButton, () -> {
            playSound("Blip_Select2.mp3");
            popTable.hide();
        });

        popTable.show(stage);
        stage.setKeyboardFocus(textField);
    }

    public void submitScore(String name, long score) {
        gj.addGuestScore(gameID, key, name, score);
    }

    public static void playSound(String soundName) {
        var sound = Gdx.audio.newSound(Gdx.files.internal(soundName));
        sound.play();
    }

    public static void elementSound(String element) {
        switch (element) {
            case "fire":
                playSound("Randomize2.mp3");
                break;
            case "water":
                playSound("Hit_Hurt21.mp3");
                break;
            case "earth":
                playSound("Hit_Hurt14.mp3");
                break;
            case "air":
                playSound("Jump3.mp3");
                break;
            case "steam":
                playSound("Jump5.mp3");
                break;
            case "rain":
                playSound("Mutation2.mp3");
                break;
            case "mud":
                playSound("Explosion2.mp3");
                break;
            case "smoke":
                playSound("Laser_Shoot4.mp3");
                break;
            case "lava":
                playSound("Randomize3.mp3");
                break;
            case "dust":
                playSound("Randomize4.mp3");
                break;
            case "ice":
                playSound("Randomize8.mp3");
                break;
            case "plasma":
                playSound("Laser_Shoot23.mp3");
                break;
            case "tornado":
                playSound("Explosion2.mp3");
                break;
            case "stone":
                playSound("Explosion6.mp3");
                break;
            case "sleet":
                playSound("Explosion12.mp3");
                break;
            case "brimstone":
                playSound("Explosion10.mp3");
                break;
            case "explosion":
                playSound("Explosion6.mp3");
                break;
            case "metal":
                playSound("Hit_Hurt28.mp3");
                break;
            case "bullet":
                playSound("Laser_Shoot4.mp3");
                break;
            case "raygun":
                playSound("Laser_Shoot6.mp3");
                break;
            case "plant":
                playSound("Jump5.mp3");
                break;
            case "wood":
                playSound("Powerup2.mp3");
                break;
            case "paper":
                playSound("Powerup3.mp3");
                break;
            case "money":
                playSound("Powerup4.mp3");
                break;
            case "coal":
                playSound("Explosion6.mp3");
                break;
            case "poison":
                playSound("Hit_Hurt28.mp3");
                break;
            case "gas":
                playSound("Hit_Hurt14.mp3");
                break;
            case "boulder":
                playSound("Hit_Hurt25.mp3");
                break;
            case "alloy":
                playSound("Laser_Shoot23.mp3");
                break;
            case "electricity":
                playSound("Pickup_Coin9.mp3");
                break;
            case "robot":
                playSound("Blip_Select3.mp3");
                break;
            case "life":
                playSound("Powerup3.mp3");
                break;
            case "forest":
                playSound("Powerup4.mp3");
                break;
            case "wildfire":
                playSound("Randomize2.mp3");
                break;
            case "human":
                playSound("Jump5.mp3");
                break;
            case "murder":
                playSound("Laser_Shoot23.mp3");
                break;
            case "war":
                playSound("Mutation2.mp3");
                break;
            case "apocalypse":
                playSound("Mutation1.mp3");
                break;
            case "baby":
                playSound("Blip_Select2.mp3");
                break;
            case "farm":
                playSound("Hit_Hurt25.mp3");
                break;
            case "mine":
                playSound("Powerup.mp3");
                break;
            case "capitalism":
                playSound("Randomize2.mp3");
                break;
            case "airplane":
                playSound("Randomize2.mp3");
                break;
            case "predator":
                playSound("Randomize4.mp3");
                break;
            case "military industrial complex":
                playSound("Randomize8.mp3");
                break;
            case "franchise":
                playSound("Pickup_Coin.mp3");
                break;
            case "nuke":
                playSound("Randomize3.mp3");
                break;
            default:
                playSound("Blip_Select.mp3");
                break;
        }
    }
}
