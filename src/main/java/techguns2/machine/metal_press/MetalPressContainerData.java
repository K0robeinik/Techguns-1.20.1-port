package techguns2.machine.metal_press;

import org.jetbrains.annotations.Nullable;

import techguns2.machine.AbstractMachineContainerData;

public interface MetalPressContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable
    MetalPressBlockEntity getBlockEntity();
    
    boolean splitItems();
    void setSplitItems(boolean splitItems);
}
