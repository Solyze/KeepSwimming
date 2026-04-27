package net.solyze.keepswimming.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.solyze.keepswimming.KeepSwimming;

@Getter
public abstract class KeyHandler {

    private final String id, category;
    private final int keyCode;
    private final KeyMapping keyBinding;

    public KeyHandler(String id, String category, int keyCode) {
        this.id = id;
        this.category = category;
        this.keyCode = keyCode;

        this.keyBinding = new KeyMapping(
                "key." + KeepSwimming.MOD_ID + "." + id,
                InputConstants.Type.KEYSYM,
                keyCode,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(KeepSwimming.MOD_ID, category))
        );
    }

    public abstract void onWasPressed(Minecraft client);
    public void preCheckPress(Minecraft client) {}
    public void onInitializeClient() {}
}
