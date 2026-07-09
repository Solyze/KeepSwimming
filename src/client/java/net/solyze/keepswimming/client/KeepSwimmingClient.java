package net.solyze.keepswimming.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.solyze.keepswimming.KeepSwimming;
import net.solyze.keepswimming.client.util.KeepSwimmingOptionData;
import net.solyze.keepswimming.client.keybind.KeyHandler;
import net.solyze.keepswimming.client.keybind.handler.MasterToggleKeyHandler;
import net.solyze.keepswimming.config.KeepSwimmingConfig;
import net.solyze.keepswimming.networking.HandshakePacket;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class KeepSwimmingClient implements ClientModInitializer {

    public static KeepSwimmingClient INSTANCE;

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
    @Getter private boolean serverCompatible;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        this.registerKeyBindHandler(new MasterToggleKeyHandler());
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);

        ClientPlayNetworking.registerGlobalReceiver(HandshakePacket.PACKET_ID, ((_, _) -> {
            this.serverCompatible = true;
            KeepSwimming.LOGGER.info("Handshake packet received! Now server compatible.");
        }));

        ClientPlayConnectionEvents.JOIN.register(((_, _, client) -> {
            this.serverCompatible = false;
            KeepSwimming.LOGGER.info("Scheduling handshake packet...");

            client.execute(() -> {
                KeepSwimming.LOGGER.info("Sending handshake packet...");
                ClientPlayNetworking.send(new HandshakePacket());
            });
        }));

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

        command = command.then(literal("handshakestatus").executes(ctx -> {
            ctx.getSource().getPlayer().sendSystemMessage(Component.literal(String.valueOf(this.serverCompatible))
                    .withColor(this.serverCompatible ? 0x55FF55 : 0xFF5555));
            return 1;
        }));

        LiteralArgumentBuilder<FabricClientCommandSource> finalCommand = command;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(finalCommand));
    }

    private void toggleOption(KeepSwimmingOptionData option, CommandContext<FabricClientCommandSource> ctx) {
        KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class).ifPresent(object -> {
            KeepSwimmingConfig config = (KeepSwimmingConfig) object;
            boolean toggled = !option.getter().apply(config);
            option.setter().accept(config, toggled);
            ctx.getSource().getPlayer().sendSystemMessage(getOptionToggleText(option, toggled));

            KeepSwimming.INSTANCE.saveConfig(KeepSwimmingConfig.class);
        });
    }

    private void showHelp(CommandContext<FabricClientCommandSource> ctx) {
        LocalPlayer player = ctx.getSource().getPlayer();
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("Using the command with an option below will toggle said option.")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());

        for (KeepSwimmingOptionData option : OPTION_DATA) {
            player.sendSystemMessage(getOptionText(option));
        }

        player.sendSystemMessage(Component.empty());
        String joined = String.join(" | ", OPTION_DATA.stream().map(KeepSwimmingOptionData::key).toList());
        player.sendSystemMessage(Component.literal("Usage: /keepswimming <" + joined + ">").withStyle(ChatFormatting.RED));
        player.sendSystemMessage(Component.empty());
    }

    private MutableComponent getOptionText(KeepSwimmingOptionData option) {
        MutableComponent prefix = Component.literal("▎ ").withStyle(ChatFormatting.DARK_GRAY);
        MutableComponent name = Component.literal(option.key()).withStyle(ChatFormatting.YELLOW);
        MutableComponent sep = Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY);
        MutableComponent desc = Component.literal(option.description()).withStyle(ChatFormatting.WHITE);
        return prefix.append(name).append(sep).append(desc);
    }

    private void onEndClientTick(Minecraft client) {
        for (KeyHandler keyHandler : this.keyBindHandlers) keyHandler.preCheckPress(client);
        for (KeyHandler keyHandler : this.keyBindHandlers) if (keyHandler.getKeyBinding().consumeClick())
            keyHandler.onWasPressed(client);
    }

    private void registerKeyBindHandler(KeyHandler keyHandler) {
        KeyMappingHelper.registerKeyMapping(keyHandler.getKeyBinding());
        keyHandler.onInitializeClient();
        this.keyBindHandlers.add(keyHandler);
    }

    private boolean checkMultiplayer(CommandContext<FabricClientCommandSource> ctx) {
        boolean multiplayer = !Minecraft.getInstance().isLocalServer();

        if (multiplayer) {
            MutableComponent name = Component.literal(KeepSwimming.MOD_DISPLAY).withStyle(ChatFormatting.AQUA);
            MutableComponent sep = Component.literal(" » ").withStyle(ChatFormatting.DARK_GRAY);
            MutableComponent err = Component.literal("This mod cannot be used on Multiplayer servers.").withStyle(ChatFormatting.RED);

            ctx.getSource().getPlayer().sendSystemMessage(name.append(sep).append(err));
        }

        return multiplayer;
    }

    private static MutableComponent getOptionToggleText(KeepSwimmingOptionData option, boolean toggled) {
        return getOptionToggleText(option.key(), toggled);
    }

    private static MutableComponent getOptionToggleText(String optionName, boolean toggled) {
        MutableComponent name = Component.literal(KeepSwimming.MOD_DISPLAY).withStyle(ChatFormatting.AQUA);
        MutableComponent bracket = Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY);
        MutableComponent opt = Component.literal(optionName).withStyle(ChatFormatting.DARK_AQUA);
        MutableComponent sep = Component.literal(") » ").withStyle(ChatFormatting.DARK_GRAY);
        MutableComponent toggle = Component.literal(toggled ? "Enabled" : "Disabled").withStyle(toggled ?
                ChatFormatting.GREEN : ChatFormatting.RED
        );
        return name.append(bracket).append(opt).append(sep.append(toggle));
    }
}