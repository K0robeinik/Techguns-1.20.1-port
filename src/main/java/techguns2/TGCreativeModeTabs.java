package techguns2;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class TGCreativeModeTabs implements TGInitializer
{
    protected TGCreativeModeTabs()
    { }

    @Override
    public final void setup(IEventBus eventBus)
    {
        REGISTER.register(eventBus);
    }

    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Techguns.MODID);
    
    public static final RegistryObject<CreativeModeTab> BASE = REGISTER.register("base", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.base"))
            .icon(() -> new ItemStack(TGItems.PISTOL_ROUNDS.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.BASE_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());
    
    public static final RegistryObject<CreativeModeTab> MACHINES = REGISTER.register("machines", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.machines"))
            .icon(() -> new ItemStack(TGBlocks.AMMO_PRESS.get().asItem()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Block> registryObject : TGBlocks.MACHINE_BLOCKS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(BASE.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> AMMO = REGISTER.register("ammo", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.ammo"))
            .icon(() -> new ItemStack(TGItems.PISTOL_MAGAZINE.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.AMMO_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(MACHINES.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> PARTS = REGISTER.register("parts", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.parts"))
            .icon(() -> new ItemStack(TGItems.STONE_BARREL.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.PART_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(AMMO.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> MATERIALS = REGISTER.register("materials", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.materials"))
            .icon(() -> new ItemStack(TGItems.OBSIDIAN_STEEL_INGOT.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.MATERIAL_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(PARTS.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> UPGRADES = REGISTER.register("upgrades", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.upgrades"))
            .icon(() -> new ItemStack(TGItems.STACK_UPGRADE.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.UPGRADE_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(MATERIALS.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> EQUIPMENT = REGISTER.register("equipment", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.equipment"))
            .icon(() -> new ItemStack(TGItems.MINING_DRILL.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.EQUIPMENT_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(UPGRADES.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> ARMOR = REGISTER.register("armor", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.armor"))
            .icon(() -> new ItemStack(TGItems.EXO_HELMET.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.ARMOR_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(EQUIPMENT.getKey())
            .build());
    
    public static final RegistryObject<CreativeModeTab> GUNS = REGISTER.register("guns", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.techguns2.guns"))
            .icon(() -> new ItemStack(TGItems.PISTOL.get()))
            .displayItems((params, output) -> {
                for (RegistryObject<? extends Item> registryObject : TGItems.GUN_ITEMS)
                {
                    output.accept(registryObject.get());
                }
            })
            .withTabsBefore(ARMOR.getKey())
            .build());
}
