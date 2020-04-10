/*package mekanism.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.actions.IAction;
import com.blamejared.crafttweaker.impl.commands.CTCommands;
import java.util.LinkedList;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.common.integration.crafttweaker.commands.GasesCommand;
import mekanism.common.integration.crafttweaker.commands.InfuseTypesCommand;
import mekanism.common.integration.crafttweaker.commands.MekRecipesCommand;

//TODO: Check all for null cases if unable to find the gas
public class CrafttweakerIntegration {

    public static final List<IAction> LATE_REMOVALS = new LinkedList<>();
    public static final List<IAction> LATE_ADDITIONS = new LinkedList<>();

    /**
     * Apply after (machine)recipes have been applied, but before the FMLLoadCompleteEvent is fired. Preferably in (post)init.
     * <p>
     * Applying to early causes remove to malfunction as no recipes have been registered. Applying to late causes JEI to not pickup the changes.
     *
    public static void applyRecipeChanges() {
        //Remove before addition, so recipes can be overwritten
        applyChanges(LATE_REMOVALS);
        applyChanges(LATE_ADDITIONS);
    }

    private static void applyChanges(List<IAction> actions) {
        actions.forEach(action -> {
            try {
                CraftTweakerAPI.apply(action);
            } catch (Exception e) {
                Mekanism.logger.error("CT action failed", e);
                CraftTweakerAPI.logError(Mekanism.MOD_NAME + " CT action failed", e);
            }
        });
    }

    public static void registerCommands() {
        CTCommands.registerCommand(new GasesCommand());
        CTCommands.registerCommand(new InfuseTypesCommand());
        CTCommands.registerCommand(new MekRecipesCommand());
    }
}*/