package net.solyze.keepswimming.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.reporting.ReportPlayerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.solyze.keepswimming.KeepSwimming;
import net.solyze.keepswimming.client.KeepSwimmingClient;
import net.solyze.keepswimming.config.KeepSwimmingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {

    @Unique
    private static final Map<Class<? extends Screen>, Function<KeepSwimmingConfig, Boolean>> SCREEN_OPTIONS = Map.ofEntries(
            // Chat
            Map.entry(ChatScreen.class, KeepSwimmingConfig::isChat),

            // Inventory
            Map.entry(InventoryScreen.class, KeepSwimmingConfig::isInventory),
            Map.entry(CreativeModeInventoryScreen.class, KeepSwimmingConfig::isInventory),

            // Pause
            Map.entry(PauseScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(OptionsScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(OptionsSubScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(PackSelectionScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(WarningScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(ReportPlayerScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(DialogScreen.class, KeepSwimmingConfig::isPause),
            Map.entry(AdvancementsScreen.class, KeepSwimmingConfig::isPause),

            // Other screens
            Map.entry(AnvilScreen.class, KeepSwimmingConfig::isAnvil),
            Map.entry(BeaconScreen.class, KeepSwimmingConfig::isBeacon),
            Map.entry(BlastFurnaceScreen.class, KeepSwimmingConfig::isBlastFurnace),
            Map.entry(BookEditScreen.class, KeepSwimmingConfig::isBook),
            Map.entry(BookViewScreen.class, KeepSwimmingConfig::isBook),
            Map.entry(BookSignScreen.class, KeepSwimmingConfig::isBook),
            Map.entry(BrewingStandScreen.class, KeepSwimmingConfig::isBrewing),
            Map.entry(CartographyTableScreen.class, KeepSwimmingConfig::isCartography),
            Map.entry(CommandBlockEditScreen.class, KeepSwimmingConfig::isCommandBlock),
            Map.entry(MinecartCommandBlockEditScreen.class, KeepSwimmingConfig::isCommandBlock),
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
            Map.entry(StructureBlockEditScreen.class, KeepSwimmingConfig::isStructureBlock),
            Map.entry(TestBlockEditScreen.class, KeepSwimmingConfig::isTestBlock),
            Map.entry(TestInstanceBlockEditScreen.class, KeepSwimmingConfig::isTestBlock),
            Map.entry(ContainerScreen.class, config -> {
                ContainerScreen screen = (ContainerScreen) Minecraft.getInstance().screen;

                if (screen != null && screen.getTitle().getContents() instanceof TranslatableContents translatable) {
                    if (config.isChest() && translatable.getKey().equals("container.chest")) return true;
                    return config.isBarrel() && translatable.getKey().equals("container.barrel");
                }

                return false;
            }),
            Map.entry(DispenserScreen.class, config -> {
                DispenserScreen screen = (DispenserScreen) Minecraft.getInstance().screen;

                if (screen != null && screen.getTitle().getContents() instanceof TranslatableContents translatable) {
                    if (config.isDropper() && translatable.getKey().equals("container.dropper")) return true;
                    return config.isDispenser() && translatable.getKey().equals("container.dispenser");
                }

                return false;
            })
    );

    @Unique
    private static final Map<MenuType<?>, Function<KeepSwimmingConfig, Boolean>> MENU_OPTIONS = Map.of(
            MenuType.MERCHANT, KeepSwimmingConfig::isMerchants
    );

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void keepSwimming$tickMovement(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Minecraft client = Minecraft.getInstance();

        if (!player.isInWater()) return;
        if (!client.isSingleplayer() && !KeepSwimmingClient.INSTANCE.isServerCompatible()) return;

        Optional<Object> optional = KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class);
        if (optional.isEmpty()) return;
        KeepSwimmingConfig config = (KeepSwimmingConfig) optional.get();

        if (!config.isMasterToggle()) return;

        if (config.isAlways()) {
            client.options.keyJump.setDown(true);
            return;
        }

        Screen screen = client.screen;

        if (screen != null) {
            if (player.getAbilities().flying && !config.isEvenFlying()) return;

            for (Map.Entry<Class<? extends Screen>, Function<KeepSwimmingConfig, Boolean>> entry : SCREEN_OPTIONS.entrySet()) {
                if (entry.getKey().isInstance(screen) && entry.getValue().apply(config)) {
                    client.options.keyJump.setDown(true);
                    return;
                }
            }
        }
    }
}