package techguns2.machine.ammo_press;

import org.jetbrains.annotations.Nullable;

import techguns2.machine.AbstractMachineContainerData;

public interface AmmoPressContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable
    AmmoPressBlockEntity getBlockEntity();
    
    AmmoPressTemplate getTemplate();
    
    void setTemplate(AmmoPressTemplate template);
}
