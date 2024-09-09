package techguns2.networking;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import techguns2.Techguns;
import techguns2.machine.AbstractMachineBlockEntity;
import techguns2.machine.AbstractMachineContainerData;
import techguns2.machine.AbstractMachineMenu;
import techguns2.machine.ammo_press.AmmoPressBlockEntity;
import techguns2.machine.ammo_press.AmmoPressContainerData;
import techguns2.machine.ammo_press.AmmoPressMenu;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryBlockEntity;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryContainerData;
import techguns2.machine.chemical_laboratory.ChemicalLaboratoryMenu;
import techguns2.machine.metal_press.MetalPressBlockEntity;
import techguns2.machine.metal_press.MetalPressContainerData;
import techguns2.machine.metal_press.MetalPressMenu;
import techguns2.machine.reaction_chamber.ReactionChamberControllerBlockEntity;
import techguns2.machine.reaction_chamber.ReactionChamberMenu;
import techguns2.networking.packets.ChangeAmmoPressTemplatePacket;
import techguns2.networking.packets.ChangeMetalPressStackSplitModePacket;
import techguns2.networking.packets.ChangeReactionFluidLevelPacket;
import techguns2.networking.packets.ChangeReactionIntensityPacket;
import techguns2.networking.packets.ChangeRedstoneModePacket;
import techguns2.networking.packets.ChangeSecurityModePacket;
import techguns2.networking.packets.DumpFluidTankPacket;
import techguns2.networking.packets.ProjectileImpactFXPacket;
import techguns2.networking.packets.SetChemicalLaboratoryDrainModePacket;
import techguns2.util.FluidStackContainer;

public final class TechgunsPacketHandler
{

    private TechgunsPacketHandler()
    { }
    
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Techguns.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::contentEquals,
            PROTOCOL_VERSION::contentEquals);
    
    private static boolean _initialized;
    
    @Deprecated
    public static void init()
    {
        if (_initialized)
            return;
        
        int packetIndex = 0;
        
        INSTANCE.registerMessage(packetIndex++, ChangeSecurityModePacket.class, ChangeSecurityModePacket::write, ChangeSecurityModePacket::read, TechgunsPacketHandler::onChangeSecurityModePacket);
        INSTANCE.registerMessage(packetIndex++, ChangeRedstoneModePacket.class, ChangeRedstoneModePacket::write, ChangeRedstoneModePacket::read, TechgunsPacketHandler::onChangeRedstoneModePacket);
        INSTANCE.registerMessage(packetIndex++, ChangeAmmoPressTemplatePacket.class, ChangeAmmoPressTemplatePacket::write, ChangeAmmoPressTemplatePacket::read, TechgunsPacketHandler::onChangeAmmoPressTemplatePacket);
        INSTANCE.registerMessage(packetIndex++, ChangeMetalPressStackSplitModePacket.class, ChangeMetalPressStackSplitModePacket::write, ChangeMetalPressStackSplitModePacket::read, TechgunsPacketHandler::onChangeMetalPressStackSplitModePacket);
        INSTANCE.registerMessage(packetIndex++, DumpFluidTankPacket.class, DumpFluidTankPacket::write, DumpFluidTankPacket::read, TechgunsPacketHandler::onDumpFluidTankPacket);
        INSTANCE.registerMessage(packetIndex++, SetChemicalLaboratoryDrainModePacket.class, SetChemicalLaboratoryDrainModePacket::write, SetChemicalLaboratoryDrainModePacket::read, TechgunsPacketHandler::onSetChemicalLaboratoryDrainModePacket);
        INSTANCE.registerMessage(packetIndex++, ChangeReactionFluidLevelPacket.class, ChangeReactionFluidLevelPacket::write, ChangeReactionFluidLevelPacket::read, TechgunsPacketHandler::onChangeReactionFluidLevelPacket);
        INSTANCE.registerMessage(packetIndex++, ChangeReactionIntensityPacket.class, ChangeReactionIntensityPacket::write, ChangeReactionIntensityPacket::read, TechgunsPacketHandler::onChangeReactionIntensityPacket);
        INSTANCE.registerMessage(packetIndex++, ProjectileImpactFXPacket.class, ProjectileImpactFXPacket::write, ProjectileImpactFXPacket::read, TechgunsPacketHandler::onProjectileImpactFXPacket);
        
        _initialized = true;
    }
    
    private static void onChangeRedstoneModePacket(ChangeRedstoneModePacket changeRedstoneModePacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof AbstractMachineMenu machineMenu))
                return;
            
            AbstractMachineContainerData containerData = machineMenu.getData();
            AbstractMachineBlockEntity blockEntity = containerData.getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            blockEntity.setRestoneBehaviour(changeRedstoneModePacket.newRedstoneMode);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onChangeSecurityModePacket(ChangeSecurityModePacket changeSecurityModePacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof AbstractMachineMenu machineMenu))
                return;
            
            AbstractMachineContainerData containerData = machineMenu.getData();
            AbstractMachineBlockEntity blockEntity = containerData.getBlockEntity();
            
            if (blockEntity == null)
                return;
            
            if (!blockEntity.isOwnedBy(player))
                return;
            
            blockEntity.setSecurityMode(changeSecurityModePacket.newSecurityMode);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onChangeAmmoPressTemplatePacket(ChangeAmmoPressTemplatePacket changeAmmoPressTemplatePacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof AmmoPressMenu ammoPressMenu))
                return;
            
            AmmoPressContainerData containerData = ammoPressMenu.getData();
            AmmoPressBlockEntity blockEntity = containerData.getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            blockEntity.setTemplate(changeAmmoPressTemplatePacket.template);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onChangeMetalPressStackSplitModePacket(ChangeMetalPressStackSplitModePacket changeMetalPressStackSplitModePacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof MetalPressMenu metalPressMenu))
                return;
            
            MetalPressContainerData containerData = metalPressMenu.getData();
            MetalPressBlockEntity blockEntity = containerData.getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            blockEntity.setSplitStack(changeMetalPressStackSplitModePacket.enabled);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onDumpFluidTankPacket(DumpFluidTankPacket dumpFluidTankPacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof AbstractMachineMenu machineMenu))
                return;
            
            AbstractMachineBlockEntity blockEntity = machineMenu.getData().getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player) || 
                    !(blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElse(null) instanceof FluidStackContainer fluidStackContainer))
                return;
            
            fluidStackContainer.setStackInSlot(dumpFluidTankPacket.slotIndex, FluidStack.EMPTY);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onSetChemicalLaboratoryDrainModePacket(SetChemicalLaboratoryDrainModePacket setChemicalLaboratoryDrainModePacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof ChemicalLaboratoryMenu chemicalLaboratoryMenu))
                return;
            
            ChemicalLaboratoryContainerData containerData = chemicalLaboratoryMenu.getData();
            ChemicalLaboratoryBlockEntity blockEntity = containerData.getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            if (setChemicalLaboratoryDrainModePacket.fromOutput)
                blockEntity.setDrainFromOutput();
            else
                blockEntity.setDrainFromInput();
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onChangeReactionFluidLevelPacket(ChangeReactionFluidLevelPacket changeReactionFluidLevelPacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof ReactionChamberMenu machineMenu))
                return;
            
            ReactionChamberControllerBlockEntity blockEntity = machineMenu.getData().getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            blockEntity.setFluidLevel(changeReactionFluidLevelPacket.fluidLevel);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onChangeReactionIntensityPacket(ChangeReactionIntensityPacket changeReactionIntensityPacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            
            if (!(player.containerMenu instanceof ReactionChamberMenu machineMenu))
                return;
            
            ReactionChamberControllerBlockEntity blockEntity = machineMenu.getData().getBlockEntity();
            
            if (blockEntity == null || !blockEntity.canPlayerAccess(player))
                return;
            
            blockEntity.setIntensity(changeReactionIntensityPacket.intensity);
            blockEntity.notifyUpdate();
            
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static void onProjectileImpactFXPacket(ProjectileImpactFXPacket projectileImpactFXPacket, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            ServerLevel world = serverPlayer.serverLevel();
            
            if (projectileImpactFXPacket.incendiary)
            {
                TGFX.createFX(
                        "Impact_IncendiaryBullet",
                        world,
                        projectileImpactFXPacket.locationX,
                        projectileImpactFXPacket.locationY,
                        projectileImpactFXPacket.locationZ,
                        0.0D,
                        0.0D,
                        0.0D,
                        projectileImpactFXPacket.pitch,
                        projectileImpactFXPacket.yaw);
            }
            
            // TODO: FINISH TGFX and Packet
            //       Reference: techguns.packets.PacketGunImpactFX.PacketHandler.handle
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static final class TGFX
    {
        private static void createFX(
                String name,
                ServerLevel world,
                double x,
                double y,
                double z,
                double unknownParam0,
                double unknownParam1,
                double unknownParam2,
                float pitch,
                float yaw)
        {
            
        }
    }

}
