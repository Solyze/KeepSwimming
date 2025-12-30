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
            Map.entry(TestInstanceBlockScreen.class, KeepSwimmingConfig::isTestBlock)
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
        if (!player.isTouchingWater()) return;

        Optional<Object> optional = KeepSwimming.INSTANCE.getConfig(KeepSwimmingConfig.class);
        Screen screen = MinecraftClient.getInstance().currentScreen;

        if (screen != null && optional.isPresent()) {
            KeepSwimmingConfig config = (KeepSwimmingConfig) optional.get();

            if (!config.isMasterToggle()) return;

            if (config.isAlways()) {
                this.input.jump();
                return;
            }

            if (player.getAbilities().allowFlying && !config.isEvenFlying()) return;

            for (Map.Entry<Class<? extends Screen>, Function<KeepSwimmingConfig, Boolean>> entry : SCREEN_OPTIONS.entrySet()) {
                if (entry.getKey().isInstance(screen) && entry.getValue().apply(config)) {
                    this.input.jump();
                    break;
                }
            }
        }
    }
}