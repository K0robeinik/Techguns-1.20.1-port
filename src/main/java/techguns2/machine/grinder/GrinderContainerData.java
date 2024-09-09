package techguns2.machine.grinder;

import org.jetbrains.annotations.Nullable;

import techguns2.machine.AbstractMachineContainerData;

public interface GrinderContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable
    GrinderBlockEntity getBlockEntity();
}
