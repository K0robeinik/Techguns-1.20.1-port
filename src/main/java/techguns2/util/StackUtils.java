package techguns2.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class StackUtils
{
    private StackUtils()
    { }
    
    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || b.isEmpty())
            return true;
        else
            return ItemHandlerHelper.canItemStacksStack(a, b);
    }
    
    public static boolean canFluidStacksStack(@NotNull FluidStack a, @NotNull FluidStack b)
    {
        if (a.isEmpty() || b.isEmpty())
            return true;
        else if (!a.getFluid().isSame(b.getFluid()) || a.hasTag() != b.hasTag())
            return false;
        else
            return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }
}
