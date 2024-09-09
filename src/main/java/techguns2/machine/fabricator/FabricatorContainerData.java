package techguns2.machine.fabricator;

import org.jetbrains.annotations.NotNull;

import techguns2.machine.AbstractMultiBlockMachineContainerData;

public interface FabricatorContainerData extends AbstractMultiBlockMachineContainerData
{
    @Override
    @NotNull
    FabricatorControllerBlockEntity getBlockEntity();
}
