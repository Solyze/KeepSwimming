package net.solyze.keepswimming.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.solyze.keepswimming.KeepSwimming;
import net.solyze.keepswimming.client.util.KeepSwimmingOptionData;
import net.solyze.keepswimming.client.keybind.KeyHandler;
import net.solyze.keepswimming.client.keybind.handler.MasterToggleKeyHandler;
import net.solyze.keepswimming.config.KeepSwimmingConfig;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class KeepSwimmingClient implements ClientModInitializer {

    private static final List<KeepSwimmingOptionData> OPTION_DATA = List.of(
            new KeepSwimmingOptionData("always", "Always", "Always keep swimming.",
                    KeepSwimmingConfig::isAlways, KeepSwimmingConfig::setAlways),
            new KeepSwimmingOptionData("inventory", "Inventory", "Keep swimming whilst your inventory is open.",
                    KeepSwimmingConfig::isInventory, KeepSwimmingConfig::setInventory),
            new KeepSwimmingOptionData("chat", "Chat", "Keep swimming whilst your chat is open.",
                    KeepSwimmingConfig::isChat, KeepSwimmingConfig::setChat),
            new KeepSwimmingOptionData("evenflying", "Even Flying", "Keep swimming, even when allowed to fly.",
                    KeepSwimmingConfig::isEvenFlying, KeepSwimmingConfig::setEvenFlying),
            new KeepSwimmingOptionData("pause", "Pause", "Keep swimming whilst pause/sub screens are open.",
                    KeepSwimmingConfig::isPause, KeepSwimmingConfig::setPause),
            new KeepSwimmingOptionData("chest", "Chest", "Keep swimming whilst the chest GUIs are open.",
                    KeepSwimmingConfig::isChest, KeepSwimmingConfig::setChest),
            new KeepSwimmingOptionData("barrel", "Barrel", "Keep swimming whilst the barrel GUIs are open.",
                    KeepSwimmingConfig::isBarrel, KeepSwimmingConfig::setBarrel),
            new KeepSwimmingOptionData("dropper", "Dropper", "Keep swimming whilst the dropper GUI is open.",
                    KeepSwimmingConfig::isDropper, KeepSwimmingConfig::setDropper),
            new KeepSwimmingOptionData("dispenser", "Dispenser", "Keep swimming whilst the dispenser GUI is open.",
                    KeepSwimmingConfig::isDispenser, KeepSwimmingConfig::setDispenser),
            new KeepSwimmingOptionData("anvil", "Anvil", "Keep swimming whilst the anvil GUI is open.",
                    KeepSwimmingConfig::isAnvil, KeepSwimmingConfig::setAnvil),
            new KeepSwimmingOptionData("beacon", "Beacon", "Keep swimming whilst the beacon GUI is open.",
                    KeepSwimmingConfig::isBeacon, KeepSwimmingConfig::setBeacon),
            new KeepSwimmingOptionData("blastfurnace", "Blast Furnace", "Keep swimming whilst the blast furnace GUI is open.",
                    KeepSwimmingConfig::isBlastFurnace, KeepSwimmingConfig::setBlastFurnace),
            new KeepSwimmingOptionData("book", "Book", "Keep swimming whilst book GUIs are open.",
                    KeepSwimmingConfig::isBook, KeepSwimmingConfig::setBook),
            new KeepSwimmingOptionData("brewing", "Brewing", "Keep swimming whilst the brewing GUI is open.",
                    KeepSwimmingConfig::isBrewing, KeepSwimmingConfig::setBrewing),
            new KeepSwimmingOptionData("cartography", "Cartography", "Keep swimming whilst the cartography GUI is open.",
                    KeepSwimmingConfig::isCartography, KeepSwimmingConfig::setCartography),
            new KeepSwimmingOptionData("commandblock", "Command Block", "Keep swimming whilst command block GUIs are open.",
                    KeepSwimmingConfig::isCommandBlock, KeepSwimmingConfig::setCommandBlock),
            new KeepSwimmingOptionData("crafter", "Crafter", "Keep swimming whilst the crafter GUI is open.",
                    KeepSwimmingConfig::isCrafter, KeepSwimmingConfig::setCrafter),
            new KeepSwimmingOptionData("crafting", "Crafting", "Keep swimming whilst the crafting GUI is open.",
                    KeepSwimmingConfig::isCrafting, KeepSwimmingConfig::setCrafting),
            new KeepSwimmingOptionData("enchanting", "Enchanting", "Keep swimming whilst the enchanting GUI is open.",
                    KeepSwimmingConfig::isEnchanting, KeepSwimmingConfig::setEnchanting),
            new KeepSwimmingOptionData("furnace", "Furnace", "Keep swimming whilst the furnace GUI is open.",
                    KeepSwimmingConfig::isFurnace, KeepSwimmingConfig::setFurnace),
            new KeepSwimmingOptionData("grindstone", "Grindstone", "Keep swimming whilst the grindstone GUI is open.",
                    KeepSwimmingConfig::isGrindstone, KeepSwimmingConfig::setGrindstone),
            new KeepSwimmingOptionData("sign", "Sign", "Keep swimming whilst sign GUIs are open.",
                    KeepSwimmingConfig::isSign, KeepSwimmingConfig::setSign),
            new KeepSwimmingOptionData("hopper", "Hopper", "Keep swimming whilst hopper GUIs are open.",
                    KeepSwimmingConfig::isHopper, KeepSwimmingConfig::setHopper),
            new KeepSwimmingOptionData("jigsaw", "Jigsaw", "Keep swimming whilst the jigsaw GUI is open.",
                    KeepSwimmingConfig::isJigsaw, KeepSwimmingConfig::setJigsaw),
            new KeepSwimmingOptionData("lectern", "Lectern", "Keep swimming whilst the lectern GUI is open.",
                    KeepSwimmingConfig::isLectern, KeepSwimmingConfig::setLectern),
            new KeepSwimmingOptionData("loom", "Loom", "Keep swimming whilst the loom GUI is open.",
                    KeepSwimmingConfig::isLoom, KeepSwimmingConfig::setLoom),
            new KeepSwimmingOptionData("shulkerbox", "Shulker Box", "Keep swimming whilst the shulker box GUI is open.",
                    KeepSwimmingConfig::isShulkerBox, KeepSwimmingConfig::setShulkerBox),
            new KeepSwimmingOptionData("smithing", "Smithing", "Keep swimming whilst the smithing GUI is open.",
                    KeepSwimmingConfig::isSmithing, KeepSwimmingConfig::setSmithing),
            new KeepSwimmingOptionData("smoker", "Smoker", "Keep swimming whilst the smoker GUI is open.",
                    KeepSwimmingConfig::isSmoker, KeepSwimmingConfig::setSmoker),
            new KeepSwimmingOptionData("stonecutter", "Stonecutter", "Keep swimming whilst the stonecutter GUI is open.",
                    KeepSwimmingConfig::isStonecutter, KeepSwimmingConfig::setStonecutter),
            new KeepSwimmingOptionData("structureblock", "Structure Block", "Keep swimming whilst the structure block GUIs are open.",
                    KeepSwimmingConfig::isStructureBlock, KeepSwimmingConfig::setStructureBlock),
            new KeepSwimmingOptionData("testblock", "Test Block", "Keep swimming whilst the test block GUIs are open.",
                    KeepSwimmingConfig::isTestBlock, KeepSwimmingConfig::setTestBlock),
            new KeepSwimmingOptionData("merchants", "Merchants", "Keep swimming whilst the merchant GUIs are open.",
                    KeepSwimmingConfig::isMerchants, KeepSwimmingConfig::setMerchants)
    );

    private final List<KeyHandler> keyBindHandlers = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        this.registerKeyBindHandler(new MasterToggleKeyHandler());
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);

        LiteralArgumentBuilder<FabricClientCommandSource> command = literal("keepswimming").executes(ctx -> {
            if (checkMultiplayer(ctx)) return 1;
            showHelp(ctx);
            return 1;
        });

        for (KeepSwimmingOptionData option : OPTION_DATA) {
            command = command.then(literal(option.key()).executes(ctx -> {
                if (checkMultiplayer(ctx)) return 1;
                toggleOption(option, ctx);
                return 1;
            }));
        }

        LiteralArgumentBuilder<FabricClientCommandSource> finalCommand = command;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(finalCommand));
    }

    private void toggleOption(KeepSwimmingOptionData option, CommandContext<FabricClientCommandSource> ctx) {
        KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class).ifPresent(object -> {
            KeepSwimmingConfig config = (KeepSwimmingConfig) object;
            boolean toggled = !option.getter().apply(config);
            option.setter().accept(config, toggled);
            ctx.getSource().getPlayer().sendMessage(getOptionToggleText(option, toggled), true);

            KeepSwimming.INSTANCE.saveConfig(KeepSwimmingConfig.class);
        });
    }

    private void showHelp(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("Using the command with an option below will toggle said option.")
                .formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.empty(), false);

        for (KeepSwimmingOptionData option : OPTION_DATA) {
            player.sendMessage(getOptionText(option), false);
        }

        player.sendMessage(Text.empty(), false);
        String joined = String.join(" | ", OPTION_DATA.stream().map(KeepSwimmingOptionData::key).toList());
        player.sendMessage(Text.literal("Usage: /keepswimming <" + joined + ">").formatted(Formatting.RED), false);
        player.sendMessage(Text.empty(), false);
        return;
    }

    private MutableText getOptionText(KeepSwimmingOptionData option) {
        MutableText prefix = Text.literal("▎ ").formatted(Formatting.DARK_GRAY);
        MutableText name = Text.literal(option.key()).formatted(Formatting.YELLOW);
        MutableText sep = Text.literal(" - ").formatted(Formatting.DARK_GRAY);
        MutableText desc = Text.literal(option.description()).formatted(Formatting.WHITE);
        return prefix.append(name).append(sep).append(desc);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.options == null) return;
        for (KeyHandler keyHandler : this.keyBindHandlers) keyHandler.preCheckPress(client);
        for (KeyHandler keyHandler : this.keyBindHandlers) if (keyHandler.getKeyBinding().wasPressed())
            keyHandler.onWasPressed(client);
    }

    private void registerKeyBindHandler(KeyHandler keyHandler) {
        KeyBindingHelper.registerKeyBinding(keyHandler.getKeyBinding());
        keyHandler.onInitializeClient();
        this.keyBindHandlers.add(keyHandler);
    }

    private boolean checkMultiplayer(CommandContext<FabricClientCommandSource> ctx) {
        boolean multiplayer = !MinecraftClient.getInstance().isInSingleplayer();

        if (multiplayer) {
            MutableText name = Text.literal(KeepSwimming.MOD_DISPLAY).formatted(Formatting.AQUA);
            MutableText sep = Text.literal(" » ").formatted(Formatting.DARK_GRAY);
            MutableText err = Text.literal("This mod cannot be used on Multiplayer servers.").formatted(Formatting.RED);

            ctx.getSource().getPlayer().sendMessage(name.append(sep).append(err), true);
        }

        return multiplayer;
    }

    private static MutableText getOptionToggleText(KeepSwimmingOptionData option, boolean toggled) {
        return getOptionToggleText(option.key(), toggled);
    }

    private static MutableText getOptionToggleText(String optionName, boolean toggled) {
        MutableText name = Text.literal(KeepSwimming.MOD_DISPLAY).formatted(Formatting.AQUA);
        MutableText bracket = Text.literal(" (").formatted(Formatting.DARK_GRAY);
        MutableText opt = Text.literal(optionName).formatted(Formatting.DARK_AQUA);
        MutableText sep = Text.literal(") » ").formatted(Formatting.DARK_GRAY);
        MutableText toggle = Text.literal(toggled ? "Enabled" : "Disabled").formatted(toggled ?
                Formatting.GREEN : Formatting.RED
        );
        return name.append(bracket).append(opt).append(sep.append(toggle));
    }
}