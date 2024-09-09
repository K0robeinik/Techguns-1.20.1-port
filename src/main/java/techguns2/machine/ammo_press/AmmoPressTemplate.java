package techguns2.machine.ammo_press;

import java.util.Objects;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class AmmoPressTemplate
{
    private final Component _name;
    private AmmoPressTemplate _nextTemplate;
    private AmmoPressTemplate _previousTemplate;
    private ResourceLocation _id;
    private int _index;
    
    public AmmoPressTemplate(Component name)
    {
        this._name = name;
        
        this._nextTemplate = null;
        this._previousTemplate = null;
        this._id = null;
        this._index = -1;
    }

    public final Component getName()
    {
        return this._name;
    }
    
    public final ResourceLocation getId()
    {
        return this._id;
    }
    
    public final int getIndex()
    {
        return this._index;
    }
    
    public final AmmoPressTemplate getNextTemplate()
    {
        return this._nextTemplate;
    }
    
    public final AmmoPressTemplate getPreviousTemplate()
    {
        return this._previousTemplate;
    }
    
    protected final void setNextTemplate(AmmoPressTemplate nextTemplate)
    {
        this._nextTemplate = nextTemplate;
    }
    
    protected final void setPreviousTemplate(AmmoPressTemplate previousTemplate)
    {
        this._previousTemplate = previousTemplate;
    }
    
    protected final void setId(ResourceLocation id)
    {
        this._id = id;
    }
    
    protected final void setIndex(int index)
    {
        this._index = index;
    }
    
    public static final Builder builder()
    {
        return new Builder();
    }
    
    public static final class Builder
    {
        private Component _name;
        
        public final Builder withName(Component name)
        {
            this._name = name;
            return this;
        }
        
        public final Builder withTranslatableName(String name)
        {
            return this.withName(Component.translatable(name));
        }
        
        public final AmmoPressTemplate build()
        {
            Objects.requireNonNull(this._name, "Name cannot be null!");
            
            return new AmmoPressTemplate(this._name);
        }
    }
    
}
