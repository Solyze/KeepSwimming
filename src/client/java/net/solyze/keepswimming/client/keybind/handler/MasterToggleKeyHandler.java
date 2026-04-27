package net.solyze.keepswimming.client.keybind.handler;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.solyze.keepswimming.KeepSwimming;
import net.solyze.keepswimming.client.keybind.KeyHandler;
import net.solyze.keepswimming.config.KeepSwimmingConfig;
import org.lwjgl.glfw.GLFW;

@Slf4j
public class MasterToggleKeyHandler extends KeyHandler {

    public MasterToggleKeyHandler() {
        super("master-toggle", "tools", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onWasPressed(Minecraft client) {
        if (!client.isSingleplayer()) return;

        KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class).ifPresent(object -> {
            KeepSwimmingConfig config = (KeepSwimmingConfig) object;
            boolean toggled = !config.isMasterToggle();
            config.setMasterToggle(toggled);

            if (client.player != null) {
                client.player.sendOverlayMessage(
                        Component.literal(KeepSwimming.MOD_DISPLAY).withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(" » ").withStyle(ChatFormatting.DARK_GRAY)
                                .append(Component.literal(toggled ? "Enabled" : "Disabled").withStyle(toggled ?
                                        ChatFormatting.GREEN : ChatFormatting.RED
                                )))
                        );
            }
        });
    }
}