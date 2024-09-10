package techguns2.machine;

import org.jetbrains.annotations.Nullable;

public interface AbstractMultiBlockMachineContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable 
    AbstractMultiBlockMachineControllerBlockEntity<?, ?> getBlockEntity();
    
    boolean isFormed();
}
