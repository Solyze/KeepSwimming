package net.solyze.keepswimming.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.TranslatableTextContent;
import net.solyze.keepswimming.KeepSwimming;
import net.solyze.keepswimming.config.KeepSwimmingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Unique
    private static final Map<Class<? extends Screen>, Function<KeepSwimmingConfig, Boolean>> SCREEN_OPTIONS = Map.ofEntries(
            // Chat
            Map.entry(ChatScreen.class, KeepSwimmingConfig::isChat),

            // Inventory
            Map.entry(InventoryScreen.class, KeepSwimmingConfig::isInventory),
            Map.entry(CreativeInventoryScreen.class, KeepSwimmingConfig::isInventory),

            // Pause
            Map.entry(GameMenuScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(OptionsScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(GameOptionsScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(PackScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(ExperimentalWarningScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(ReportScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(DialogScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(AdvancementsScreen.class, KeepSwimmingConfig::isPause),

            // Other screens
            Map.entry(AnvilScreen.class, KeepSwimmingConfig::isAnvil),
            Map.entry(BeaconScreen.class, KeepSwimmingConfig::isBeacon),
            Map.entry(BlastFurnaceScreen.class, KeepSwimmingConfig::isBlastFurnace),
            Map.entry(BookScreen.class, KeepSwimmingConfig::isBook),
            Map.entry(BrewingStandScreen.class, KeepSwimmingConfig::isBrewing),
            Map.entry(CartographyTableScreen.class, KeepSwimmingConfig::isCartography),
            Map.entry(CommandBlockScreen.class, KeepSwimmingConfig::isCommandBlock),
            Map.entry(MinecartCommandBlockScreen.class, KeepSwimmingConfig::isCommandBlock),
            Map.entry(CrafterScreen.class, KeepSwimmingConfig::isCrafter),
            Map.entry(CraftingScreen.class, KeepSwimmingConfig::isCrafting),
            Map.entry(EnchantmentScreen.class, KeepSwimmingConfig::isEnchanting),
            Map.entry(FurnaceScreen.class, KeepSwimmingConfig::isFurnace),
            Map.entry(GrindstoneScreen.class, KeepSwimmingConfig::isGrindstone),
            Map.entry(HangingSignEditScreen.class, KeepSwimmingConfig::isSign),
            Map.entry(SignEditScreen.class, KeepSwimmingConfig::isSign),
            Map.entry(ShulkerBoxScreen.class, KeepSwimmingConfig::isShulkerBox),
            Map.entry(SmithingScreen.class, KeepSwimmingConfig::isSmithing),
            Map.entry(SmokerScreen.class, KeepSwimmingConfig::isSmoker),
            Map.entry(StonecutterScreen.class, KeepSwimmingConfig::isStonecutter),
            Map.entry(StructureBlockScreen.class, KeepSwimmingConfig::isStructureBlock),
            Map.entry(TestBlockScreen.class, KeepSwimmingConfig::isTestBlock),
            Map.entry(TestInstanceBlockScreen.class, KeepSwimmingConfig::isTestBlock),
            Map.entry(GenericContainerScreen.class, config -> {
                GenericContainerScreen screen = (GenericContainerScreen) MinecraftClient.getInstance().currentScreen;

                if (screen != null && screen.getTitle().getContent() instanceof TranslatableTextContent translatable) {
                    if (config.isChest() && translatable.getKey().equals("container.chest")) return true;
                    return config.isBarrel() && translatable.getKey().equals("container.barrel");
                }

                return false;
            }),
            Map.entry(Generic3x3ContainerScreen.class, config -> {
                Generic3x3ContainerScreen screen = (Generic3x3ContainerScreen) MinecraftClient.getInstance().currentScreen;

                if (screen != null && screen.getTitle().getContent() instanceof TranslatableTextContent translatable) {
                    if (config.isDropper() && translatable.getKey().equals("container.dropper")) return true;
                    return config.isDispenser() && translatable.getKey().equals("container.dispenser");
                }

                return false;
            })
    );

    @Unique
    private static final Map<ScreenHandlerType<?>, Function<KeepSwimmingConfig, Boolean>> SCREEN_HANDLER_OPTIONS = Map.ofEntries(
            Map.entry(ScreenHandlerType.MERCHANT, KeepSwimmingConfig::isMerchants)
    );

    @Shadow public Input input;

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/input/Input;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void tickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isInSingleplayer() || !player.isTouchingWater()) return;

        Optional<Object> optional = KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class);
        if (optional.isEmpty()) return;
        KeepSwimmingConfig config = (KeepSwimmingConfig) optional.get();
        if (!config.isMasterToggle()) return;

        if (config.isAlways()) {
            this.input.jump();
            return;
        }

        Screen screen = client.currentScreen;
        if (screen != null) {
            if (player.getAbilities().allowFlying && !config.isEvenFlying()) return;

            for (Map.Entry<Class<? extends Screen>, Function<KeepSwimmingConfig, Boolean>> entry : SCREEN_OPTIONS.entrySet()) {
                if (entry.getKey().isInstance(screen) && entry.getValue().apply(config)) {
                    this.input.jump();
                    break;
                }
            }
        }

        ScreenHandler screenHandler = player.currentScreenHandler;
        if (screenHandler != null) {
            try {
                ScreenHandlerType<?> type = screenHandler.getType();

                if (type != null) {
                    for (Map.Entry<ScreenHandlerType<?>, Function<KeepSwimmingConfig, Boolean>> entry : SCREEN_HANDLER_OPTIONS.entrySet()) {
                        if (type == entry.getKey() && entry.getValue().apply(config)) {
                            this.input.jump();
                        }
                    }
                }
            } catch (UnsupportedOperationException ignored) {
                // Minecraft won't let you call getType() if the type is null.
            }
        }
    }
}