package net.solyze.keepswimming.config;

import lombok.Getter;
import lombok.Setter;
import net.solyze.keepswimming.KeepSwimming;

@Setter
@Getter
@ConfigInfo(name = KeepSwimming.MOD_ID + "/config")
public class KeepSwimmingConfig {

    private boolean masterToggle = true;
    private boolean evenFlying = false;
    private boolean always = false;

    private boolean inventory = true;
    private boolean chat = true;
    private boolean pause = true;
    private boolean anvil = true;
    private boolean beacon = true;
    private boolean blastFurnace = true;
    private boolean book = true;
    private boolean brewing = true;
    private boolean cartography = true;
    private boolean commandBlock = true;
    private boolean crafter = true;
    private boolean crafting = true;
    private boolean enchanting = true;
    private boolean furnace = true;
    private boolean grindstone = true;
    private boolean sign = true;
    private boolean hopper = true;
    private boolean jigsaw = true;
    private boolean lectern = true;
    private boolean loom = true;
    private boolean shulkerBox = true;
    private boolean smithing = true;
    private boolean smoker = true;
    private boolean stonecutter = true;
    private boolean structureBlock = true;
    private boolean testBlock = true;
    private boolean chest = true;
    private boolean barrel = true;
    private boolean dropper = true;
    private boolean dispenser = true;
    private boolean merchants = true;

}