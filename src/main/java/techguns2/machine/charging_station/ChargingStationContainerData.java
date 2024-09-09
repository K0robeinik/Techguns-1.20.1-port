package techguns2.machine.charging_station;

import org.jetbrains.annotations.Nullable;

import techguns2.machine.AbstractMachineContainerData;

public interface ChargingStationContainerData extends AbstractMachineContainerData
{
    @Override
    @Nullable 
    ChargingStationBlockEntity getBlockEntity();
    
    int getOperationTotalEnergyNeeded();
    int getOperationEnergyRemaining();
}
