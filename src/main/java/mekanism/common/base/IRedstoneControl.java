package mekanism.common.base;

import javax.annotation.Nonnull;
import mekanism.api.IIncrementalEnum;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import net.minecraft.util.text.ITextComponent;

public interface IRedstoneControl {

    /**
     * Gets the RedstoneControl type from this block.
     *
     * @return this block's RedstoneControl type
     */
    RedstoneControl getControlType();

    /**
     * Sets this block's RedstoneControl type to a new value.
     *
     * @param type - RedstoneControl type to set
     */
    void setControlType(RedstoneControl type);

    /**
     * If the block is getting powered or not by redstone (indirectly).
     *
     * @return if the block is getting powered indirectly
     */
    boolean isPowered();

    /**
     * If the block was getting powered or not by redstone, last tick. Used for PULSE mode.
     */
    boolean wasPowered();

    /**
     * If the machine can be pulsed.
     */
    boolean canPulse();

    enum RedstoneControl implements IIncrementalEnum<RedstoneControl>, IHasTextComponent {
        DISABLED(MekanismLang.REDSTONE_CONTROL_DISABLED),
        HIGH(MekanismLang.REDSTONE_CONTROL_HIGH),
        LOW(MekanismLang.REDSTONE_CONTROL_LOW),
        PULSE(MekanismLang.REDSTONE_CONTROL_PULSE);

        private static final RedstoneControl[] MODES = values();
        private final ILangEntry langEntry;

        RedstoneControl(ILangEntry langEntry) {
            this.langEntry = langEntry;
        }

        @Override
        public ITextComponent getTextComponent() {
            return langEntry.translate();
        }

        @Nonnull
        @Override
        public RedstoneControl byIndex(int index) {
            return byIndexStatic(index);
        }

        public static RedstoneControl byIndexStatic(int index) {
            //TODO: Is it more efficient to check if index is negative and then just do the normal mod way?
            return MODES[Math.floorMod(index, MODES.length)];
        }
    }
}