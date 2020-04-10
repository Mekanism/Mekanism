package mekanism.common.item.gear;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.item.IItemHUDProvider;
import mekanism.common.item.IModeItem;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemElectricBow extends BowItem implements IModeItem, IItemHUDProvider {

    //TODO: Config max energy, damage, etc
    private static final FloatingLong MAX_ENERGY = FloatingLong.createConst(120_000);
    private static final FloatingLong FIRE_ENERGY = FloatingLong.createConst(1_200);
    private static final FloatingLong NORMAL_ENERGY = FloatingLong.createConst(120);

    public ItemElectricBow(Properties properties) {
        super(properties.setNoRepair());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
        tooltip.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull ItemStack stack, @Nonnull World world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            //Vanilla diff - Get the energy container, because if something went wrong and we don't have one then we can exit early
            IEnergyContainer energyContainer = null;
            boolean fireState = getFireState(stack);
            if (!player.isCreative()) {
                energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                FloatingLong energyNeeded = fireState ? FIRE_ENERGY : NORMAL_ENERGY;
                if (energyContainer == null || energyContainer.extract(energyNeeded, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyNeeded)) {
                    return;
                }
            }
            boolean infinity = player.isCreative() || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack ammo = player.findAmmo(stack);
            int charge = ForgeEventFactory.onArrowLoose(stack, world, player, getUseDuration(stack) - timeLeft, !ammo.isEmpty() || infinity);
            if (charge < 0) {
                return;
            }
            if (!ammo.isEmpty() || infinity) {
                float velocity = getArrowVelocity(charge);
                if (velocity < 0.1) {
                    return;
                }
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }
                boolean noConsume = player.isCreative() || (ammo.getItem() instanceof ArrowItem && ((ArrowItem) ammo.getItem()).isInfinite(ammo, stack, player));
                if (!world.isRemote) {
                    ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                    AbstractArrowEntity arrowEntity = arrowitem.createArrow(world, ammo, player);
                    arrowEntity = customeArrow(arrowEntity);
                    arrowEntity.shoot(player, player.rotationPitch, player.rotationYaw, 0, velocity * 3, 1);
                    if (velocity == 1) {
                        arrowEntity.setIsCritical(true);
                    }
                    int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                    if (power > 0) {
                        arrowEntity.setDamage(arrowEntity.getDamage() + (double) power * 0.5D + 0.5D);
                    }
                    int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                    if (punch > 0) {
                        arrowEntity.setKnockbackStrength(punch);
                    }
                    //Vanilla diff - set it on fire if the bow's mode is set to fire, even if there is no flame enchantment
                    if (fireState || EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                        arrowEntity.setFire(100);
                    }
                    //Vanilla diff - Instead of damaging the item we remove energy from it
                    if (!player.isCreative() && energyContainer != null) {
                        energyContainer.extract(fireState ? FIRE_ENERGY : NORMAL_ENERGY, Action.EXECUTE, AutomationType.MANUAL);
                    }
                    if (noConsume || player.isCreative() && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                        arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }
                    world.addEntity(arrowEntity);
                }
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1,
                      1.0F / (random.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                if (!noConsume && !player.isCreative()) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.inventory.deleteStack(ammo);
                    }
                }
                player.addStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public void setFireState(ItemStack stack, boolean state) {
        ItemDataUtils.setBoolean(stack, NBTConstants.MODE, state);
    }

    public boolean getFireState(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, NBTConstants.MODE);
    }

    @Override
    public void addHUDStrings(List<ITextComponent> list, ItemStack stack, EquipmentSlotType slotType) {
        list.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getDurabilityForDisplay(stack);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
        if (isInGroup(group)) {
            items.add(StorageUtils.getFilledEnergyVariant(new ItemStack(this), MAX_ENERGY));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
        // Internal is used by the "null" side, which is what will get used for most items
        return new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(() -> MAX_ENERGY, BasicEnergyContainer.notExternal, BasicEnergyContainer.alwaysTrue));
    }

    @Override
    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
        if (Math.abs(shift) % 2 == 1) {
            //We are changing by an odd amount, so toggle the mode
            boolean newState = !getFireState(stack);
            setFireState(stack, newState);
            if (displayChangeMessage) {
                player.sendMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
                      MekanismLang.FIRE_MODE.translateColored(EnumColor.GRAY, OnOff.of(newState, true))));
            }
        }
    }

    @Nonnull
    @Override
    public ITextComponent getScrollTextComponent(@Nonnull ItemStack stack) {
        return MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack), true));
    }
}