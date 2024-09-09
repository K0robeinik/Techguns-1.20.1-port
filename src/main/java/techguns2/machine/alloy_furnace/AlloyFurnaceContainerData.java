package techguns2.machine.alloy_furnace;

import org.jetbrains.annotations.Nullable;

import techguns2.machine.AbstractMachineContainerData;

public interface AlloyFurnaceContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable
    AlloyFurnaceBlockEntity getBlockEntity();
}
