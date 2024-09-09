package techguns2.ammo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;
import techguns2.TGCustomRegistries;

/**
 * A group/collection of ammunition.
 */
public final class AmmoGroup
{
    private List<UnbakedAmmoGroupEntry> _unbakedSubGroups;
    private List<UnbakedAmmoEntry> _unbakedAmmo;
    
    private List<AmmoGroup> _subGroups;
    private List<IAmmo> _ammo;
    
    private boolean _isBaked;
    
    public AmmoGroup()
    {
        this._unbakedSubGroups = new ArrayList<UnbakedAmmoGroupEntry>();
        this._unbakedAmmo = new ArrayList<UnbakedAmmoEntry>();
        
        this._subGroups = ImmutableList.of();
        this._ammo = ImmutableList.of();
    }
    
    /**
     * Returns whether or not this ammo group has been baked.
     * @return True if this ammo group has been baked;
     * otherwise, false.
     */
    public final boolean isBaked()
    {
        return this._isBaked;
    }
    
    /**
     * Returns a list of all sub-groups.
     * <p>
     * The returned list is immutable.
     * </p>
     * @return All sub-groups
     */
    public final List<AmmoGroup> subGroups()
    {
        return this._subGroups;
    }
    
    /**
     * Adds the specified sub-group to this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code subGroup} is null,
     * then no add operation will be performed and this method
     * will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups added will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be added when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * sub-group to be added. The sub-group can be removed by
     * calling any of the {@link removeSubGroup} methods with
     * the sub-group that was added.
     * </p>
     * @param subGroup The sub group to add.
     * @return True if the sub-group was added; otherwise,
     * false.
     */
    public final boolean addSubGroup(AmmoGroup subGroup)
    {
        if (this._isBaked || subGroup == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.add(subGroup));
        return true;
    }

    /**
     * Adds the sub-group with the specified id to this ammo
     * group.
     * <p>
     * If {@link isBaked} is false or {@code key} is null, then
     * no add operation will be performed and this method will
     * return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups added will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be added when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * sub-group to be added. The sub-group can be removed by
     * calling any of the {@link removeSubGroup} methods with
     * the sub-group that was added.
     * </p>
     * @param key The id/key of the sub group to add.
     * @return True if the sub-group was added; otherwise,
     * false.
     */
    public final boolean addSubGroup(ResourceLocation key)
    {
        if (this._isBaked || key == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.add(key));
        return true;
    }

    /**
     * Adds the sub-group inside the specified registry object
     * to this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no add operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups added will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be added when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * sub-group to be added. The sub-group can be removed by
     * calling any of the {@link removeSubGroup} methods with
     * the sub-group that was added.
     * </p>
     * @param registryObject The registry object of the sub
     * group to add.
     * @return True if the sub-group was added; otherwise,
     * false.
     */
    public final boolean addSubGroup(RegistryObject<AmmoGroup> registryObject)
    {
        if (this._isBaked || registryObject == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.add(registryObject));
        return true;
    }

    /**
     * Removes the specified sub-group from this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code subGroup} is null,
     * then no remove operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups removed will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be removed when
     * this ammo group is built. This method only <b>QUEUES</b>
     * the sub-group to be removed. The sub-group can be added
     * again by calling one of the {@link addSubGroup} methods.
     * </p>
     * @param subGroup The sub group to remove.
     * @return True if the sub-group was removed; otherwise,
     * false.
     */
    public final boolean removeSubGroup(AmmoGroup subGroup)
    {
        if (this._isBaked || subGroup == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.remove(subGroup));
        return true;
    }

    /**
     * Removes the sub-group with the specified id from this
     * ammo group.
     * <p>
     * If {@link isBaked} is false or {@code key} is null, then
     * no remove operation will be performed and this method
     * will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups removed will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be removed when
     * this ammo group is built. This method only <b>QUEUES</b>
     * the sub-group to be removed. The sub-group can be added
     * again by calling one of the {@link addSubGroup} methods.
     * </p>
     * @param key The id/key of the sub group to remove.
     * @return True if the sub-group was removed; otherwise,
     * false.
     */
    public final boolean removeSubGroup(ResourceLocation key)
    {
        if (this._isBaked || key == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.remove(key));
        return true;
    }


    /**
     * Removes the sub-group inside the specified registry
     * object from this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no remove operation will be performed and
     * this method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any sub-groups removed will not show up in
     * {@link subGroups} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the sub-group will be removed when
     * this ammo group is built. This method only <b>QUEUES</b>
     * the sub-group to be removed. The sub-group can be added
     * again by calling one of the {@link addSubGroup} methods.
     * </p>
     * @param registryObject The registry object of the sub
     * group to remove.
     * @return True if the sub-group was removed; otherwise,
     * false.
     */
    public final boolean removeSubGroup(RegistryObject<AmmoGroup> registryObject)
    {
        if (this._isBaked || registryObject == null)
            return false;
        
        this._unbakedSubGroups.add(UnbakedAmmoGroupEntry.remove(registryObject));
        return true;
    }

    /**
     * Returns a list of the ammo associated with this group.
     * <p>
     * The returned list is immutable.
     * </p>
     * @return The ammo associated with this group.
     */
    public final List<IAmmo> ammo()
    {
        return this._ammo;
    }

    /**
     * Adds the specified ammo to this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no add operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo added will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be added when this ammo
     * group is built. This method only <b>QUEUES</b> the ammo
     * to be added. The ammo can be removed by calling any of
     * the {@link removeAmmo} methods with the ammo that was
     * added.
     * </p>
     * @param ammo The ammo to add.
     * @return True if the ammo was added; otherwise,
     * false.
     */
    public final boolean addAmmo(IAmmo ammo)
    {
        if (this._isBaked || ammo == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.add(ammo));
        return true;
    }

    /**
     * Adds the ammo with the specified id to this ammo
     * group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no add operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo added will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be added when this ammo
     * group is built. This method only <b>QUEUES</b> the ammo
     * to be added. The ammo can be removed by calling any of
     * the {@link removeAmmo} methods with the ammo that was
     * added.
     * </p>
     * @param The id/key of the ammo to add.
     * @return True if the ammo was added; otherwise,
     * false.
     */
    public final boolean addAmmo(ResourceLocation key)
    {
        if (this._isBaked || key == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.add(key));
        return true;
    }

    /**
     * Adds the ammo inside the specified registry object to
     * this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no add operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo added will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be added when this ammo
     * group is built. This method only <b>QUEUES</b> the ammo
     * to be added. The ammo can be removed by calling any of
     * the {@link removeAmmo} methods with the ammo that was
     * added.
     * </p>
     * @param registryObject The registry object of the ammo to
     * add.
     * @return True if the ammo was added; otherwise,
     * false.
     */
    public final boolean addAmmo(RegistryObject<? extends IAmmo> registryObject)
    {
        if (this._isBaked || registryObject == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.add(registryObject));
        return true;
    }

    /**
     * Removes the specified ammo from this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code ammo} is null,
     * then no remove operation will be performed and this
     * method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo removed will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be removed when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * ammo to be removed. The ammo can be added again by
     * calling one of the {@link addAmmo} methods.
     * </p>
     * @param ammo The ammo to remove.
     * @return True if the ammo was removed; otherwise,
     * false.
     */
    public final boolean removeAmmo(IAmmo ammo)
    {
        if (this._isBaked || ammo == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.remove(ammo));
        return true;
    }

    /**
     * Removes the ammo with the specified id from this ammo
     * group.
     * <p>
     * If {@link isBaked} is false or {@code key} is null, then
     * no remove operation will be performed and this method
     * will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo removed will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be removed when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * ammo to be removed. The ammo can be added again by
     * calling one of the {@link addAmmo} methods.
     * </p>
     * @param key The id/key of the ammo to remove.
     * @return True if the ammo was removed; otherwise,
     * false.
     */
    public final boolean removeAmmo(ResourceLocation key)
    {
        if (this._isBaked || key == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.remove(key));
        return true;
    }

    /**
     * Removes the ammo that's inside the specified registry
     * object from this ammo group.
     * <p>
     * If {@link isBaked} is false or {@code registryObject} is
     * null, then no remove operation will be performed and
     * this method will return false.
     * </p>
     * <p>
     * It's only recommended to call this method when
     * constructing this ammo group, or when modifying ammo
     * groups via the {@link AmmoGroupBakeEvent} event fired on
     * the {@link MinecraftForge.EVENT_BUS} event bus.
     * </p>
     * <p>
     * Keep in mind any ammo removed will not show up in
     * {@link ammo} until this ammo group is baked.
     * </p>
     * <p>
     * Just because this method returned true <b>DOES NOT
     * GUARANTEE</b> that the ammo will be removed when this
     * ammo group is built. This method only <b>QUEUES</b> the
     * ammo to be removed. The ammo can be added again by
     * calling one of the {@link addAmmo} methods.
     * </p>
     * @param registryObject The registry object of the ammo to
     * remove.
     * @return True if the ammo was removed; otherwise,
     * false.
     */
    public final boolean removeAmmo(RegistryObject<? extends IAmmo> registryObject)
    {
        if (this._isBaked || registryObject == null)
            return false;
        
        this._unbakedAmmo.add(UnbakedAmmoEntry.remove(registryObject));
        return true;
    }

    /**
     * Returns whether or not the specified ammo is contained in this group, or any of it's
     * sub-groups.
     * @param ammo The ammo to check
     * @return true if the ammo is contained in this group or any sub-group; false, otherwise.
     */
    public final boolean includes(IAmmo ammo)
    {
        if (this._ammo.contains(ammo))
            return true;
        
        for (AmmoGroup subGroup : this._subGroups)
        {
            if (subGroup.includes(ammo))
                return true;
        }
        
        return false;
    }
    
    private final void bake(ResourceLocation key, IForgeRegistry<AmmoGroup> registry, IForgeRegistry<IAmmo> ammoRegistry)
    {
        if (this._isBaked)
            return;

        BakedGroupListNode thisNode = new BakedGroupListNode(key, this);
        this.doBake(key, registry, ammoRegistry, thisNode);
    }
    
    private final void bake(ResourceLocation key, IForgeRegistry<AmmoGroup> registry, IForgeRegistry<IAmmo> ammoRegistry, BakedGroupListNode parentNode)
    {
        if (this._isBaked)
            return;

        if (parentNode.has(this))
        {
            this._isBaked = true;
            
            StringBuilder cyclicalPathBuilder = new StringBuilder();
            
            // create a stack of the path
            List<BakedGroupListNode> stackPath = new ArrayList<BakedGroupListNode>();
            
            BakedGroupListNode current = parentNode;
            
            while (current.group != this)
            {
                stackPath.add(current);
                // @WARNING SUPPRESS CS8601 Possible null reference assignment
                // REASON: Because parentNode.has returned
                //         true, then there must be a valid
                //         path to the parent which means
                //         parentNode will not be null.
                current = current.parentNode;
                // @WARNING RESTORE CS8601
            }
            
            stackPath.add(current);
            
            // keep in mind stack path will exclude this.
            
            // loop from end to start to ensure stack behaviour.
            for (int index = stackPath.size() - 1; index >= 0; index--)
            {
                cyclicalPathBuilder.append('"');
                cyclicalPathBuilder.append(stackPath.get(index).key.toString());
                cyclicalPathBuilder.append("\" -> ");
            }
            
            cyclicalPathBuilder.append('"');
            cyclicalPathBuilder.append(key.toString());
            cyclicalPathBuilder.append('"');
            
            throw new RuntimeException("Cyclical reference detected with entry '" + key.toString() + "'. (" + cyclicalPathBuilder.toString() + ")");
        }

        BakedGroupListNode thisNode = new BakedGroupListNode(parentNode, key, this);
        this.doBake(key, registry, ammoRegistry, thisNode);
    }
    
    private final void doBake(ResourceLocation key, IForgeRegistry<AmmoGroup> registry, IForgeRegistry<IAmmo> ammoRegistry, BakedGroupListNode thisNode)
    {
        MinecraftForge.EVENT_BUS.post(new AmmoGroupBakeEvent(this, key));
        
        HashMap<ResourceLocation, AmmoGroup> bakedSubGroups = new HashMap<ResourceLocation, AmmoGroup>();
        
        @Nullable
        Throwable failedToBakeSubGroupsThrowable = null;
        for (UnbakedAmmoGroupEntry subGroupEntry : this._unbakedSubGroups)
        {
            try
            {
                subGroupEntry.bake(bakedSubGroups, registry, ammoRegistry, thisNode);
            }
            catch (Throwable error)
            {
                if (failedToBakeSubGroupsThrowable == null)
                {
                    failedToBakeSubGroupsThrowable = error;
                }
                else
                {
                    failedToBakeSubGroupsThrowable.addSuppressed(error);
                }
            }
        }
        
        this._unbakedSubGroups = ImmutableList.of();
        this._subGroups = ImmutableList.copyOf(bakedSubGroups.values());

        HashMap<ResourceLocation, IAmmo> bakedAmmo = new HashMap<ResourceLocation, IAmmo>();
        
        for (UnbakedAmmoEntry ammoEntry : this._unbakedAmmo)
        {
            ammoEntry.bake(bakedAmmo, ammoRegistry);
        }
        
        this._unbakedAmmo = ImmutableList.of();
        this._ammo = ImmutableList.copyOf(bakedAmmo.values());

        this._isBaked = true;
        if (failedToBakeSubGroupsThrowable != null)
        {
            throw new RuntimeException("Failed to bake sub groups", failedToBakeSubGroupsThrowable);
        }
        
    }
    
    @Deprecated
    @Internal
    public static void bakeRegistry(IForgeRegistryInternal<AmmoGroup> registry, RegistryManager registryManager)
    {
        IForgeRegistry<IAmmo> ammoRegistry = registryManager.getRegistry(TGCustomRegistries.AMMO);
        
        ImmutableList<ResourceLocation> entryKeys = ImmutableList.copyOf(registry.getKeys());
        @Nullable
        Throwable bakeError = null;
        for (int index = 0; index < entryKeys.size(); index++)
        {
            ResourceLocation key = entryKeys.get(index);
            
            try
            {
                registry.getValue(key).bake(key, registry, ammoRegistry);
            }
            catch (Throwable error)
            {
                if (bakeError == null)
                    bakeError = error;
                else
                    bakeError.addSuppressed(error);
            }
        }
        
        if (bakeError != null)
            throw new RuntimeException("Error occurred whilst baking ammo groups", bakeError);
    }
    
    private static final class BakedGroupListNode
    {
        @Nullable
        public final BakedGroupListNode parentNode;
        public final ResourceLocation key;
        public final AmmoGroup group;
        
        public BakedGroupListNode(ResourceLocation key, AmmoGroup group)
        {
            this.parentNode = null;
            this.key = key;
            this.group = group;
        }
        
        public BakedGroupListNode(BakedGroupListNode parent, ResourceLocation key, AmmoGroup group)
        {
            this.parentNode = parent;
            this.key = key;
            this.group = group;
        }
        
        public final boolean has(AmmoGroup existing)
        {
            return this.group == existing ||
                    (this.parentNode != null && this.parentNode.has(existing));
        }
    }
    
    private static abstract class UnbakedAmmoGroupEntry
    {
        public final boolean isRemove;
        
        private UnbakedAmmoGroupEntry(boolean isRemove)
        {
            this.isRemove = isRemove;
        }

        @Nullable
        public abstract ResourceLocation tryGetKey(IForgeRegistry<AmmoGroup> registry);
        
        @Nullable
        public abstract AmmoGroup tryGetValue(IForgeRegistry<AmmoGroup> registry);
        
        public abstract boolean bake(
                Map<ResourceLocation, AmmoGroup> bakedEntries,
                IForgeRegistry<AmmoGroup> registry,
                IForgeRegistry<IAmmo> ammoRegistry,
                BakedGroupListNode thisNode);
        
        public static UnbakedAmmoGroupEntry add(AmmoGroup value)
        {
            return new OfValue(value, false);
        }
        public static UnbakedAmmoGroupEntry add(ResourceLocation key)
        {
            return new OfKey(key, false);
        }
        public static UnbakedAmmoGroupEntry add(RegistryObject<AmmoGroup> registryObject)
        {
            return new OfRegistryObject(registryObject, false);
        }
        public static UnbakedAmmoGroupEntry remove(AmmoGroup value)
        {
            return new OfValue(value, true);
        }
        public static UnbakedAmmoGroupEntry remove(ResourceLocation key)
        {
            return new OfKey(key, true);
        }
        public static UnbakedAmmoGroupEntry remove(RegistryObject<AmmoGroup> registryObject)
        {
            return new OfRegistryObject(registryObject, true);
        }
        
        private static final class OfValue extends UnbakedAmmoGroupEntry
        {
            public final AmmoGroup value;
            
            public OfValue(AmmoGroup value, boolean isRemove)
            {
                super(isRemove);
                this.value = value;
            }
            
            @Override
            @Nullable 
            public ResourceLocation tryGetKey(IForgeRegistry<AmmoGroup> registry)
            {
                return registry.getKey(this.value);
            }
            
            @Override
            @NotNull
            public final AmmoGroup tryGetValue(IForgeRegistry<AmmoGroup> registry)
            {
                return this.value;
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, AmmoGroup> bakedEntries,
                    IForgeRegistry<AmmoGroup> registry,
                    IForgeRegistry<IAmmo> ammoRegistry,
                    BakedGroupListNode thisNode)
            {
                @Nullable
                ResourceLocation key = registry.getKey(this.value);
                if (key == null)
                    return false;
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(key) != null;
                }
                
                if (bakedEntries.containsKey(key))
                    return false;
                
                this.value.bake(key, registry, ammoRegistry, thisNode);
                bakedEntries.put(key, this.value);
                return true;
            }
        }
        
        private static final class OfKey extends UnbakedAmmoGroupEntry
        {
            public final ResourceLocation key;
            
            public OfKey(ResourceLocation key, boolean isRemove)
            {
                super(isRemove);
                
                this.key = key;
            }
            
            @Override
            @NotNull
            public final ResourceLocation tryGetKey(IForgeRegistry<AmmoGroup> registry)
            {
                return this.key;
            }
            
            @Override
            @Nullable 
            public final AmmoGroup tryGetValue(IForgeRegistry<AmmoGroup> registry)
            {
                return registry.getValue(this.key);
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, AmmoGroup> bakedEntries,
                    IForgeRegistry<AmmoGroup> registry,
                    IForgeRegistry<IAmmo> ammoRegistry,
                    BakedGroupListNode thisNode)
            {
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(this.key) != null;
                }
                
                @Nullable
                AmmoGroup value = registry.getValue(this.key);
                
                if (value == null || bakedEntries.containsKey(this.key))
                    return false;
                
                value.bake(this.key, registry, ammoRegistry, thisNode);
                bakedEntries.put(this.key, value);
                return true;
            }
        }
        
        private static final class OfRegistryObject extends UnbakedAmmoGroupEntry
        {
            public final RegistryObject<AmmoGroup> registryObject;
            
            public OfRegistryObject(RegistryObject<AmmoGroup> registryObject, boolean isRemove)
            {
                super(isRemove);
                
                this.registryObject = registryObject;
            }
            
            @Override
            @Nullable 
            public final ResourceLocation tryGetKey(IForgeRegistry<AmmoGroup> registry)
            {
                @Nullable
                ResourceKey<AmmoGroup> key = this.registryObject.getKey();
                if (key == null)
                    return null;
                else
                    return key.location();
            }
            
            @Override
            @Nullable
            public final AmmoGroup tryGetValue(IForgeRegistry<AmmoGroup> registry)
            {
                if (this.registryObject.isPresent())
                    return this.registryObject.get();
                else
                    return null;
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, AmmoGroup> bakedEntries,
                    IForgeRegistry<AmmoGroup> registry,
                    IForgeRegistry<IAmmo> ammoRegistry,
                    BakedGroupListNode thisNode)
            {
                @Nullable
                ResourceKey<AmmoGroup> resourceKey = this.registryObject.getKey();
                if (resourceKey == null)
                    return false;
                
                ResourceLocation key = resourceKey.location();
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(key) != null;
                }
                
                if (bakedEntries.containsKey(key) || !this.registryObject.isPresent())
                    return false;
                
                AmmoGroup value = this.registryObject.get();
                
                value.bake(key, registry, ammoRegistry, thisNode);
                bakedEntries.put(key, value);
                return true;
            }
        }
    }
    
    private static abstract class UnbakedAmmoEntry
    {
        public final boolean isRemove;
        
        private UnbakedAmmoEntry(boolean isRemove)
        {
            this.isRemove = isRemove;
        }

        @Nullable
        public abstract ResourceLocation tryGetKey(IForgeRegistry<IAmmo> registry);
        
        @Nullable
        public abstract IAmmo tryGetValue(IForgeRegistry<IAmmo> registry);
        
        public abstract boolean bake(
                Map<ResourceLocation, IAmmo> bakedEntries,
                IForgeRegistry<IAmmo> registry);
        
        public static UnbakedAmmoEntry add(IAmmo value)
        {
            return new OfValue(value, false);
        }
        public static UnbakedAmmoEntry add(ResourceLocation key)
        {
            return new OfKey(key, false);
        }
        public static UnbakedAmmoEntry add(RegistryObject<? extends IAmmo> registryObject)
        {
            return new OfRegistryObject(registryObject, false);
        }
        public static UnbakedAmmoEntry remove(IAmmo value)
        {
            return new OfValue(value, true);
        }
        public static UnbakedAmmoEntry remove(ResourceLocation key)
        {
            return new OfKey(key, true);
        }
        public static UnbakedAmmoEntry remove(RegistryObject<? extends IAmmo> registryObject)
        {
            return new OfRegistryObject(registryObject, true);
        }
        
        private static final class OfValue extends UnbakedAmmoEntry
        {
            public final IAmmo value;
            
            public OfValue(IAmmo value, boolean isRemove)
            {
                super(isRemove);
                this.value = value;
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, IAmmo> bakedEntries,
                    IForgeRegistry<IAmmo> registry)
            {
                @Nullable
                ResourceLocation key = registry.getKey(this.value);
                if (key == null)
                    return false;
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(key) != null;
                }
                
                if (bakedEntries.containsKey(key))
                    return false;
                
                bakedEntries.put(key, this.value);
                return true;
            }
            
            @Override
            @Nullable 
            public final ResourceLocation tryGetKey(IForgeRegistry<IAmmo> registry)
            {
                return registry.getKey(this.value);
            }
            
            @Override
            @NotNull
            public final IAmmo tryGetValue(IForgeRegistry<IAmmo> registry)
            {
                return this.value;
            }
        }
        
        private static final class OfKey extends UnbakedAmmoEntry
        {
            public final ResourceLocation key;
            
            public OfKey(ResourceLocation key, boolean isRemove)
            {
                super(isRemove);
                
                this.key = key;
            }
            
            @Override
            @NotNull
            public ResourceLocation tryGetKey(IForgeRegistry<IAmmo> registry)
            {
                return this.key;
            }
            
            @Override
            @Nullable 
            public final IAmmo tryGetValue(IForgeRegistry<IAmmo> registry)
            {
                return registry.getValue(this.key);
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, IAmmo> bakedEntries,
                    IForgeRegistry<IAmmo> registry)
            {
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(this.key) != null;
                }
                
                @Nullable
                IAmmo value = registry.getValue(this.key);
                
                if (value == null || bakedEntries.containsKey(this.key))
                    return false;
                
                bakedEntries.put(this.key, value);
                return true;
            }
        }
        
        private static final class OfRegistryObject extends UnbakedAmmoEntry
        {
            public final RegistryObject<? extends IAmmo> registryObject;
            
            public OfRegistryObject(RegistryObject<? extends IAmmo> registryObject, boolean isRemove)
            {
                super(isRemove);
                
                this.registryObject = registryObject;
            }
            
            @Override
            @Nullable 
            public final ResourceLocation tryGetKey(IForgeRegistry<IAmmo> registry)
            {
                @Nullable
                ResourceKey<? extends IAmmo> key = this.registryObject.getKey();
                if (key == null)
                    return null;
                else
                    return key.location();
            }
            
            @Override
            @Nullable
            public final IAmmo tryGetValue(IForgeRegistry<IAmmo> registry)
            {
                if (this.registryObject.isPresent())
                    return this.registryObject.get();
                else
                    return null;
            }

            @Override
            public final boolean bake(
                    Map<ResourceLocation, IAmmo> bakedEntries,
                    IForgeRegistry<IAmmo> registry)
            {
                @Nullable
                ResourceKey<? extends IAmmo> resourceKey = this.registryObject.getKey();
                if (resourceKey == null)
                    return false;
                
                ResourceLocation key = resourceKey.location();
                
                if (this.isRemove)
                {
                    return bakedEntries.remove(key) != null;
                }
                
                if (bakedEntries.containsKey(key) || !this.registryObject.isPresent())
                    return false;
                
                IAmmo value = this.registryObject.get();
                bakedEntries.put(key, value);
                return true;
            }
        }
    }
}
