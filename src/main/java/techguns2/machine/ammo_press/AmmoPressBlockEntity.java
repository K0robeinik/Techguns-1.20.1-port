package techguns2.machine.ammo_press;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryManager;
import techguns2.TGBlockEntityTypes;
import techguns2.TGCustomRegistries;
import techguns2.TGRecipeTypes;
import techguns2.TGSounds;
import techguns2.TGTranslations;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.MachineUpgrade;
import techguns2.machine.MachineUpgradeItem;
import techguns2.util.DirtyableResult;
import techguns2.util.ItemStackContainer;
import techguns2.util.StackUtils;

public final class AmmoPressBlockEntity extends AbstractMachineBlockEntity
{
    private final RecipeManager.CachedCheck<AmmoPressBlockEntity, IAmmoPressRecipe> _quickCheck;
    private AmmoPressTemplate _template;
    
    @Nullable
    private AmmoPressOperation _currentOperation;
    
    private final ContainerData _containerData;
    
    public AmmoPressBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TGBlockEntityTypes.AMMO_PRESS.get(), blockPos, blockState, (pThis) -> new ItemStackContainerInventory(pThis, SLOT_COUNT)
        {
            @Override
            public final boolean canPlaceItemInSlot(int slot, @NotNull ItemStack itemStack)
            {
                switch (slot)
                {
                    case SLOT_BULLET:
                    case SLOT_CASING:
                    case SLOT_POWDER:
                        return true;
                    case SLOT_UPGRADE:
                        return itemStack.getItem() instanceof MachineUpgradeItem;
                    default:
                        return false;
                }
            }
            
            @Override
            public final boolean canTakeItemFromSlot(int slot, int amount)
            {
                return slot == SLOT_RESULT;
            }
        });
        
        this._quickCheck = RecipeManager.createCheck(TGRecipeTypes.AMMO_PRESS.get());
        
        this._template = AmmoPressTemplates.DEFAULT;
        
        this._containerData = new ContainerData(this);
    }
    
    @Override
    public final String getMachineName()
    {
        return "Ammo Press";
    }
    
    @Override
    @Nullable 
    public final AmmoPressOperation getCurrentOperation()
    {
        return this._currentOperation;
    }
    
    @Override
    public final boolean hasOperation()
    {
        return this._currentOperation != null;
    }

    public final AmmoPressTemplate getTemplate()
    {
        return this._template;
    }

    public final void setTemplate(AmmoPressTemplate template)
    {
        if (template == null)
            this._template = AmmoPressTemplates.DEFAULT;
        else
            this._template = template;
    }
    
    public final ItemStack getCasingItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_CASING);
    }

    public final ItemStack getBulletItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_BULLET);
    }

    public final ItemStack getPowderItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_POWDER);
    }

    public final ItemStack getResultItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_RESULT);
    }

    public final ItemStack getUpgradeItem()
    {
        return this._itemContainer.getStackInSlot(SLOT_UPGRADE);
    }
    
    @Override
    public final int getEnergyCapacity()
    {
        return ENERGY_CAPACITY;
    }

    @Override
    protected final Component getDefaultName()
    {
        return Component.translatable(TGTranslations.Machine.AmmoPress.NAME);
    }
    
    @Override
    protected final void read(CompoundTag tag, boolean clientPacket)
    {
        super.read(tag, clientPacket);
        
        if (tag.contains("Template", Tag.TAG_STRING))
        {
            ResourceLocation templateLocation = ResourceLocation.tryParse(tag.getString("Template"));
            if (templateLocation != null)
            {
                AmmoPressTemplate template = RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.AMMO_PRESS_TEMPLATES).getValue(templateLocation);
                if (template == null)
                    template = AmmoPressTemplates.DEFAULT;
                
                this._template = template;
            }
            else
                this._template = AmmoPressTemplates.DEFAULT;
        }
        
        if (tag.contains("CurrentOperation", Tag.TAG_COMPOUND))
            this._currentOperation = new AmmoPressOperation(tag.getCompound("CurrentOperation"), clientPacket);
        else
            this._currentOperation = null;
    }
    
    @Override
    protected final void write(CompoundTag tag, boolean clientPacket)
    {
        super.write(tag, clientPacket);
        
        tag.putString("Template", RegistryManager.ACTIVE.getRegistry(TGCustomRegistries.AMMO_PRESS_TEMPLATES).getKey(this._template).toString());
        
        if (this._currentOperation != null)
        {
            CompoundTag currentOperationData = new CompoundTag();
            this._currentOperation.serialize(currentOperationData, clientPacket);
            tag.put("CurrentOperation", currentOperationData);
        }
    }

    @Override
    protected final AmmoPressMenu createMenu(int menuId, Inventory inventory)
    {
        return new AmmoPressMenu(menuId, inventory, this, this._containerData, this._ownerId);
    }
    
    @Override
    protected final boolean fetchNextOperation()
    {
        this._contentsChanged = false;
        @Nullable
        IAmmoPressRecipe nextRecipe = this._quickCheck.getRecipeFor(this, this.level).orElse(null);
        
        if (nextRecipe == null)
        {
            this._currentOperation = null;
            return false;
        }
        
        ItemStack recipeResultItem;
        
        ItemStack operationBulletItem = ItemStack.EMPTY;
        ItemStack operationCasingItem = ItemStack.EMPTY;
        ItemStack operationPowderItem = ItemStack.EMPTY;
        ItemStack operationResultItem = ItemStack.EMPTY;
        
        int energyConsumptionMultiplier = 0;
        int processAmount = 1;
        
        ItemStack upgradeItem = this.getUpgradeItem();
        if (upgradeItem.getItem() instanceof MachineUpgradeItem machineUpgradeItem)
        {
            MachineUpgrade upgrade = machineUpgradeItem.getUpgrade();
            if (upgrade instanceof MachineUpgrade.Stack)
            {
                processAmount += upgradeItem.getCount();
            }
        }
        
        // copy existing results
        ItemStackContainer resultContainer = this._itemContainer.createView(SLOT_RESULT);
        
        while (processAmount > 0)
        {
            int bulletConsumptionAmount = nextRecipe.testBulletWithConsumption(this.getBulletItem());
            int casingConsumptionAmount = nextRecipe.testCasingWithConsumption(this.getCasingItem());
            int powderConsumptionAmount = nextRecipe.testCasingWithConsumption(this.getPowderItem());
            
            if (bulletConsumptionAmount <= 0 ||
                    casingConsumptionAmount <= 0 ||
                    powderConsumptionAmount <= 0)
            {
                break;
            }
            
            recipeResultItem = nextRecipe.assemble(this, this.level.registryAccess());
            if (!StackUtils.canItemStacksStack(operationResultItem, recipeResultItem))
                break;
            
            if (!resultContainer.insertItemUnchecked(recipeResultItem, false).isEmpty())
                break;
            
            if (operationBulletItem.isEmpty())
                operationBulletItem = this.getBulletItem().copyWithCount(bulletConsumptionAmount);
            else
                operationBulletItem.grow(bulletConsumptionAmount);
            
            if (operationCasingItem.isEmpty())
                operationCasingItem = this.getCasingItem().copyWithCount(casingConsumptionAmount);
            else
                operationCasingItem.grow(casingConsumptionAmount);
            
            if (operationPowderItem.isEmpty())
                operationPowderItem = this.getPowderItem().copyWithCount(powderConsumptionAmount);
            else
                operationPowderItem.grow(powderConsumptionAmount);
            
            if (operationResultItem.isEmpty())
                operationResultItem = recipeResultItem.copy();
            else
                operationResultItem.grow(recipeResultItem.getCount());
            
            this.getBulletItem().shrink(bulletConsumptionAmount);
            this.getCasingItem().shrink(casingConsumptionAmount);
            this.getPowderItem().shrink(powderConsumptionAmount);
            
            processAmount--;
            energyConsumptionMultiplier++;
        }
        
        if (energyConsumptionMultiplier == 0)
        {
            this._currentOperation = null;
            
            return false;
        }
        
        this._currentOperation = new AmmoPressOperation(
                nextRecipe.getProcessingTime(),
                nextRecipe.getProcessingTime(),
                nextRecipe.getEnergyDrainPerTick() * energyConsumptionMultiplier,
                operationBulletItem,
                operationCasingItem,
                operationPowderItem,
                operationResultItem);
        return true;
    }
    
    @Override
    protected final void onOperationComplete()
    {
        var operation = this.getCurrentOperation();
        if (operation == null)
            return;
        
        this._itemContainer.insertItemUnchecked(SLOT_RESULT, operation.resultItem.copy(), false);
    }
    
    @Override
    protected final boolean operationClientTick()
    {
        var operation = this.getCurrentOperation();
        if (operation.isPaused)
            return false;

        this.drainEnergy(operation.energyDrainPerTick);
        operation.ticksRemaining = Math.max(0, operation.ticksRemaining - 1);
        return true;
    }
    
    @Override
    protected final DirtyableResult operationServerTick()
    {
        var operation = this.getCurrentOperation();
        boolean operationCanProcess = this.tryConsumeEnergy(operation.energyDrainPerTick);
        
        if (!operationCanProcess)
        {
            if (!operation.isPaused)
            {
                this._currentOperation.isPaused = true;
                return DirtyableResult.FAILED_DIRTY;
            }
            else
                return DirtyableResult.FAILED;
        }
        
        operation.ticksRemaining--;
        
        boolean wasUnpaused = !operation.isPaused;
        operation.isPaused = false;

        if (wasUnpaused)
            return DirtyableResult.SUCCESSFUL_DIRTY;
        else
            return DirtyableResult.SUCCESSFUL;
    }
    
    @Override
    protected final void playAmbienceSounds()
    {
        var operation = this.getCurrentOperation();
        
        int soundTick1 = Math.round((float)operation.totalTicks * 0.05f);
        int soundTick2 = Math.round((float)operation.totalTicks * 0.30f);
        
        int halfTime = Math.round((float)operation.totalTicks * 0.5f);
        
        int progress = operation.totalTicks - operation.ticksRemaining;
        
        if (progress == soundTick1 || progress == (soundTick1 + halfTime))
            this.level.playLocalSound(this.getBlockPos(), TGSounds.AMMO_PRESS_WORK1.get(), SoundSource.BLOCKS, 0.5f, 1.0f, true);
        else if (progress == soundTick2 || progress == (soundTick2 + halfTime))
            this.level.playLocalSound(this.getBlockPos(), TGSounds.AMMO_PRESS_WORK2.get(), SoundSource.BLOCKS, 0.5f, 1.0f, true);
    }

    public static final int SLOT_BULLET = 0;
    public static final int SLOT_CASING = 1;
    public static final int SLOT_POWDER = 2;
    public static final int SLOT_RESULT = 3;
    public static final int SLOT_UPGRADE = 4;
    public static final int SLOT_COUNT = 5;
    
    public static final int DATA_SLOT_TEMPLATE = AbstractMachineBlockEntity.NEXT_DATA_SLOT;
    public static final int NEXT_DATA_SLOT = DATA_SLOT_TEMPLATE + 1;
    
    private static final int ENERGY_CAPACITY = 20000;
    
    public static final void tick(Level level, BlockPos blockPos, BlockState blockState, AmmoPressBlockEntity blockEntity)
    {
        blockEntity.tick();
    }
    
    private static final class ContainerData extends AbstractMachineBlockEntity.TickedContainerData implements AmmoPressContainerData
    {
        private final AmmoPressBlockEntity _blockEntity;
        
        public ContainerData(AmmoPressBlockEntity blockEntity)
        {
            this._blockEntity = blockEntity;
        }
        
        @Override
        @NotNull
        public final AmmoPressBlockEntity getBlockEntity()
        {
            return this._blockEntity;
        }

        @Override
        public final int getOperationTotalTicks()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.totalTicks;
        }

        @Override
        public final int getOperationTicksRemaining()
        {
            var operation = this.getBlockEntity().getCurrentOperation();
            if (operation == null)
                return 0;
            else
                return operation.ticksRemaining;
        }
        
        @Override
        public final AmmoPressTemplate getTemplate()
        {
            return this.getBlockEntity().getTemplate();
        }
        
        @Override
        public final void setTemplate(AmmoPressTemplate template)
        {
            this.getBlockEntity().setTemplate(template);
        }

        @Override
        public final int get(int index)
        {
            if (index == DATA_SLOT_TEMPLATE)
                return this.getTemplate().getIndex();
            else
                return super.get(index);
        }

        @Override
        public final void set(int index, int value)
        {
            if (index == DATA_SLOT_TEMPLATE)
                this.setTemplate(AmmoPressTemplates.getFromIndex(index));
            else
                super.set(index, value);
        }

        @Override
        public final int getCount()
        {
            return NEXT_DATA_SLOT;
        }
    }
}
