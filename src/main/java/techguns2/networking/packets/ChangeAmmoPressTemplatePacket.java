package techguns2.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import techguns2.machine.ammo_press.AmmoPressTemplate;
import techguns2.machine.ammo_press.AmmoPressTemplates;

public final class ChangeAmmoPressTemplatePacket
{
    public final AmmoPressTemplate template;
    
    public ChangeAmmoPressTemplatePacket(AmmoPressTemplate template)
    {
        this.template = template;
    }

    public final void write(FriendlyByteBuf buf)
    {
        buf.writeInt(this.template.getIndex());
    }
    
    public static ChangeAmmoPressTemplatePacket read(FriendlyByteBuf buf)
    {
        AmmoPressTemplate template = AmmoPressTemplates.getFromIndex(buf.readInt());
        return new ChangeAmmoPressTemplatePacket(template);
    }
}
