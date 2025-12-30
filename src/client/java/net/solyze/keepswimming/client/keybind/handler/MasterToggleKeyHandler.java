package net.solyze.keepswimming.client.keybind.handler;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    public void onWasPressed(MinecraftClient client) {
        KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class).ifPresent(object -> {
            KeepSwimmingConfig config = (KeepSwimmingConfig) object;
            boolean toggled = !config.isMasterToggle();
            config.setMasterToggle(toggled);

            if (client.player != null) {
                client.player.sendMessage(
                        Text.literal(KeepSwimming.MOD_DISPLAY).formatted(Formatting.AQUA)
                                .append(Text.literal(" » ").formatted(Formatting.DARK_GRAY)
                                .append(Text.literal(toggled ? "Enabled" : "Disabled").formatted(toggled ?
                                        Formatting.GREEN : Formatting.RED
                                )))
                        , true);
            }
        });
    }
}