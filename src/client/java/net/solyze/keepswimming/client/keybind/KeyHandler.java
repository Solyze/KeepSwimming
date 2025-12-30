package net.solyze.keepswimming.client.keybind;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.solyze.keepswimming.KeepSwimming;

@Getter
public abstract class KeyHandler {

    private final String id, category;
    private final int keyCode;
    private final KeyBinding keyBinding;

    public KeyHandler(String id, String category, int keyCode) {
        this.id = id;
        this.category = category;
        this.keyCode = keyCode;

        this.keyBinding = new KeyBinding(
                "key." + KeepSwimming.MOD_ID + "." + id,
                InputUtil.Type.KEYSYM,
                keyCode,
                KeyBinding.Category.create(Identifier.of(KeepSwimming.MOD_ID, category))
        );
    }

    public abstract void onWasPressed(MinecraftClient client);
    public void preCheckPress(MinecraftClient client) {}
    public void onInitializeClient() {}
}
