package techguns2.entity.projectile;

import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import techguns2.TGDamageTypes;
import techguns2.TGEntityTypes;
import techguns2.api.npc.ITechgunsNPCShooter;
import techguns2.api.npc.factions.FactionNeutrality;
import techguns2.api.npc.factions.ITGNpcTeam;
import techguns2.damage.DamageSystem;
import techguns2.damage.TGDamageSource;
import techguns2.deathfx.DeathFXKind;
import techguns2.networking.TechgunsPacketHandler;
import techguns2.networking.packets.ProjectileImpactFXPacket;

public class GenericProjectile extends Entity
{
    // TODO: figure out EntitySelectors.CAN_AI_TARGET equivalent.
    public static final Predicate<Entity> BULLET_TARGETS = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.CAN_BE_COLLIDED_WITH);
    
    @Nullable
    private UUID _ownerUUID;
    @Nullable
    private LivingEntity _cachedOwner;
    
    protected int _lifespan;
    protected int _age;
    protected float _speed;
    protected float _armorPenetration;
    protected float _damage;
    protected float _minDamage;
    protected float _damageDropoffStart;
    protected float _damageDropoffEnd;
    
    protected boolean _startPositionInitialized;
    protected double _startPosX;
    protected double _startPosY;
    protected double _startPosZ;
    
    protected float _radius;
    protected float _gravity;

    public GenericProjectile(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }
    
    @Nullable
    public final LivingEntity getOwner()
    {
        if (this._cachedOwner != null && !this._cachedOwner.isRemoved())
            return this._cachedOwner;
        else if (this._ownerUUID != null && this.level() instanceof ServerLevel serverLevel)
        {
            Entity ownerEntity = serverLevel.getEntity(this._ownerUUID);
            if (ownerEntity instanceof LivingEntity)
                this._cachedOwner = (LivingEntity)ownerEntity;
            else
                this._cachedOwner = null;
            
            return this._cachedOwner;
        }
        else
            return null;
    }
    
    public final void setOwner(@Nullable LivingEntity owner)
    {
        if (owner == null)
            return;
        
        this._ownerUUID = owner.getUUID();
        this._cachedOwner = owner;
    }
    
    public void initProjectile(
            LivingEntity owner,
            double posX,
            double posY,
            double posZ,
            float yaw,
            float pitch,
            float speed,
            int lifespan,
            float spread,
            float armorPenetration,
            float damage,
            float damageDropoffStart,
            float damageDropoffEnd,
            float minDamage,
            float gravity,
            ProjectileFireSide side)
    {
        float offsetSide = 16F;
        float offsetHeight = 0F;
        
        if (owner != null && owner instanceof ITechgunsNPCShooter npcShooter)
        {
            offsetSide += npcShooter.getProjectileSideOffset();
            offsetHeight += npcShooter.getProjectileHeightOffset();
        }
        
        float yRot = yaw + ((float)(spread - (2 * Math.random() * spread)) * 40);
        float xRot = pitch + ((float)(spread - (2 * Math.random() * spread)) * 40);
        
        switch (side)
        {
            case RIGHT:
                posX -= Mth.cos(yRot / 180 * Mth.PI) * offsetSide;
                posZ -= Mth.sin(yRot / 180 * Mth.PI) * offsetSide;
                break;
            case LEFT:
                posX += Mth.cos(yRot / 180 * Mth.PI) * offsetSide;
                posZ += Mth.sin(yRot / 180 * Mth.PI) * offsetSide;
                break;
            default:
                break;
                
        }
        
        posY += -0.10000000149011612D + offsetHeight;
        this.setPos(posX, posY, posZ);
        this.setRot(yRot, xRot);
        
        float f = 0.4F;
        double motionX = -Mth.sin(yRot / 180 * Mth.PI) *
                Math.cos(xRot / 180 * Mth.PI) * f;
        double motionZ = Mth.cos(yRot / 180 * Mth.PI) *
                Math.cos(xRot / 180 * Mth.PI) * f;
        double motionY = -Mth.sin(xRot / 180 * Mth.PI) * f;
        
        double magnitude = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= magnitude;
        motionY /= magnitude;
        motionZ /= magnitude;
        
        motionX *= speed;
        motionY *= speed;
        motionZ *= speed;
        
        this.setDeltaMovement(motionX, motionY, motionZ);
        
        this._age = 0;
        this._lifespan = lifespan;
        this._armorPenetration = armorPenetration;
        this._damage = damage;
        this._minDamage = minDamage;
        this._damageDropoffStart = damageDropoffStart;
        this._damageDropoffEnd = damageDropoffEnd;
        this._speed = speed;
    }
    
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = Mth.sqrt((float)(x * x + y * y + z * z));
        
        x /= f;
        y /= f;
        z /= f;
        
        x += this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        y += this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        z += this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        
        x *= velocity;
        y *= velocity;
        z *= velocity;
        
        this.setDeltaMovement(x, y, z);
        
        double f1 = Mth.sqrt((float)(x * x + z * z));
        float rotY = (float)(Mth.atan2(x, z) * (180 / Math.PI));
        float rotX = (float)(Mth.atan2(y, f1) * (180 / Math.PI));
        this.setRot(rotY, rotX);
        this.xRotO = rotX;
        this.yRotO = rotY;
    }
    
    protected void initStartPos()
    {
        Vec3 position = this.position();
        this._startPosX = position.x;
        this._startPosY = position.y;
        this._startPosZ = position.z;
    }
    
    @Override
    public void tick()
    {
        Level level = this.level();
        
        if (!this._startPositionInitialized && !level.isClientSide)
        {
            this.initStartPos();
        }
        
        this.setOldPosAndRot();
        
        super.tick();
        
        Vec3 deltaMovement = this.getDeltaMovement();
        
        if (this.xRotO == 0.0F && this.yRotO == 0.0F)
        {
            float f = Mth.sqrt((float)(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z));

            float rotationYaw = (float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (180D / Math.PI));
            
            float rotationPitch = (float)(Mth.atan2(deltaMovement.y, f) * (180D / Math.PI));
            
            this.setRot(rotationYaw, rotationPitch);
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
        }
        
        this._age++;
        
        Vec3 start = this.position();
        Vec3 end = start.add(deltaMovement);
        
        @Nullable
        HitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        
        if (hitResult.getType() != HitResult.Type.MISS)
            end = hitResult.getLocation();
        
        @Nullable
        EntityHitResult entityHitResult = this.findHitEntity(start, end);
        
        if (entityHitResult != null)
        {
            hitResult = entityHitResult;
        }
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY)
        {
            Entity target = ((EntityHitResult)hitResult).getEntity();
            @Nullable
            LivingEntity source = this.getOwner();
            
            if (source instanceof Player sourcePlayer &&
                    target instanceof Player targetPlayer &&
                    !sourcePlayer.canHarmPlayer(targetPlayer))
            {
                hitResult = null;
                entityHitResult = null;
            }
        }
        
        if (hitResult != null)
        {
            this.onHit(hitResult);
        }
        
        this.setPos(this.position().add(deltaMovement));
        float f4 = Mth.sqrt((float)(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z));

        float rotationYaw = (float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (180D / Math.PI));
        
        float rotationPitch = (float)(Mth.atan2(deltaMovement.y, f4) * (180D / Math.PI));
        
        if (rotationPitch - this.xRotO < -180F)
        {
            this.xRotO -= 360;
        }
        if (rotationPitch - this.xRotO >= 180F)
        {
            this.xRotO += 360;
        }
        
        if (rotationYaw - this.yRotO < -180F)
        {
            this.yRotO -= 360;
        }
        if (rotationYaw - this.yRotO >= 180F)
        {
            this.yRotO += 360;
        }
        
        rotationPitch = this.xRotO + (rotationPitch - this.xRotO) * 0.2F;
        rotationYaw = this.yRotO + (rotationYaw - this.yRotO) * 0.2F;
        
        float f1 = 0.99F;
        f1 = this.inWaterUpdateBehaviour(f1);
        
        deltaMovement = deltaMovement.scale(f1);
        
        if (this._gravity != 0)
        {
            deltaMovement = deltaMovement.subtract(0, this._gravity, 0);
        }
        
        this.reapplyPosition();
        this.doBlockCollisions();
        
        if (this._age >= this._lifespan)
        {
            this.discard();
        }
    }
    
    public void shiftForward(float factor)
    {
        this.setPos(this.position().add(this.getDeltaMovement().scale(factor)));
    }
    
    private void doBlockCollisions()
    {
        // TODO
    }
    
    protected float inWaterUpdateBehaviour(float f1)
    {
        
        if (this.isInWater())
        {
            Vec3 deltaMovement = this.getDeltaMovement();
            Vec3 particlePos = this.position().subtract(deltaMovement.scale(0.25));
            
            
            for (int i = 0; i < 4; i++)
            {
                this.level().addParticle(ParticleTypes.BUBBLE,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        deltaMovement.x,
                        deltaMovement.y,
                        deltaMovement.z);
            }
            
            f1 = 0.85F;
        }
        
        return f1;
    }
    
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 start, Vec3 end)
    {
        return ProjectileUtil.getEntityHitResult(
                this.level(),
                this,
                start,
                end,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                BULLET_TARGETS);
    }
    
    protected void onHit(HitResult hitResult)
    {
        Level level = this.level();
        if (level.isClientSide ||  hitResult.getType() == HitResult.Type.MISS)
            return;
        
        if (hitResult instanceof EntityHitResult entityHitResult)
        {
        
            TGDamageSource damageSource = this.createDamageSource();
            
            Entity hitEntity = entityHitResult.getEntity();
            
            if (hitEntity instanceof LivingEntity livingEntity)
            {
                float dmg = DamageSystem.getDamageFactor(
                        this.getOwner(),
                        livingEntity,
                        this.getServer()) * this.getDamage();
                
                if (dmg <= 0)
                {
                    this.discard();
                    return;
                }
                
                if (livingEntity.hurt(damageSource, dmg))
                {
                    this.setAIRevengeTarget(livingEntity);
                    
                    this.onHitEffect(livingEntity, hitResult);
                }
            }
            else
            {
                hitEntity.hurt(damageSource, this.getDamage());
            }
            
            this.discard();
        }
    }
    
    protected void setAIRevengeTarget(LivingEntity entity)
    {
        boolean shootBack = false;
        
        LivingEntity owner = this.getOwner();
        if (owner == null)
        {
            return;
        }
        
        if (owner instanceof ITGNpcTeam ownerTeam && entity instanceof ITGNpcTeam entityTeam)
        {
            shootBack = entityTeam.getFaction().getNeutrality(ownerTeam.getFaction()) != FactionNeutrality.PASSIVE;
        }
        else if (owner instanceof Player)
        {
            shootBack = true;
        }
        
        if (shootBack && entity instanceof Mob mob && mob.canAttack(owner))
        {
            mob.setTarget(entity);
        }
    }
    
    /**
     * Override in a subclass for hit effect (potion effects, etc)
     * @param entity Entity
     * @param hitResult Hit result.
     */
    protected void onHitEffect(LivingEntity entity, HitResult hitResult)
    {
        
    }
    
    /**
     * Override for block hit effect.
     * @param hitResult Hit result
     */
    protected void hitBlock(BlockHitResult hitResult)
    {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockPos);
        
        SoundType soundType = blockState.getBlock().getSoundType(blockState, this.level(), blockPos, this);
        this.doImpactEffects(hitResult, soundType);
    }
    
    protected void doImpactEffects(HitResult hitResult, SoundType sound)
    {
        Vec3 location = hitResult.getLocation();
        
        double x = location.x;
        double y = location.y;
        double z = location.z;
        
        float pitch = 0.0f;
        float yaw = 0.0f;
        
        if (hitResult.getType() != HitResult.Type.MISS && hitResult instanceof BlockHitResult blockHitResult)
        {
            Direction sideHit = blockHitResult.getDirection();
            
            if (sideHit == Direction.UP)
            {
                pitch = -90f;
            }
            else if (sideHit == Direction.DOWN)
            {
                pitch = 90;
            }
            else
            {
                yaw = sideHit.toYRot();
            }
        }
        else
        {
            pitch = -this.getXRot();
            yaw = -this.getYRot();
        }
        
        int type = -1;
        if (sound == SoundType.STONE)
        {
            type = 0;
        }
        else if (sound == SoundType.WOOD || sound == SoundType.LADDER)
        {
            type = 1;
        }
        else if (sound == SoundType.GLASS)
        {
            type = 2;
        }
        else if (sound == SoundType.METAL || sound == SoundType.ANVIL)
        {
            type = 3;
        }
        
        this.sendImpactFX(x, y, z, pitch, yaw, type);
    }
    
    protected void sendImpactFX(double x, double y, double z, float pitch, float yaw, int type)
    {
        this.sendImpactFX(x, y, z, pitch, yaw, type, false);
    }
    
    @SuppressWarnings("resource")
    protected void sendImpactFX(double x, double y, double z, float pitch, float yaw, int type, boolean incendiary)
    {
        if (!this.level().isClientSide)
        {
            TechgunsPacketHandler.INSTANCE.send(
                    PacketDistributor.NEAR.with(() -> new TargetPoint(
                            x,
                            y,
                            z,
                            (double)TGEntityTypes.bulletTrackRange,
                            this.level().dimension())),
                    new ProjectileImpactFXPacket(
                            (short)type,
                            x,
                            y,
                            z,
                            pitch,
                            yaw,
                            incendiary));
        }
    }
    
    @Override
    protected float getEyeHeight(Pose p_19976_, EntityDimensions p_19977_)
    {
        return 0.25F;
    }

    @Override
    protected void defineSynchedData()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        this._speed = tag.getFloat("speed");
        this._age = tag.getInt("age");
        this._lifespan = tag.getInt("lifespan");
        
        this._damage = tag.getFloat("damage");
        
        CompoundTag damageDropoff = tag.getCompound("damageDropoff");
        this._damageDropoffStart = damageDropoff.getFloat("start");
        this._damageDropoffEnd = damageDropoff.getFloat("end");
        
        this._gravity = tag.getFloat("gravity");
        
        if (tag.contains("owner", Tag.TAG_INT_ARRAY))
        {
            this._ownerUUID = tag.getUUID("owner");
            this._cachedOwner = null;
        }
        else
        {
            this._ownerUUID = null;
            this._cachedOwner = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putFloat("speed", this._speed);
        tag.putInt("age", this._age);
        tag.putInt("lifespan", this._lifespan);
        tag.putFloat("damage", this._damage);
        CompoundTag damageDropoff = new CompoundTag();
        damageDropoff.putFloat("start", this._damageDropoffStart);
        damageDropoff.putFloat("end", this._damageDropoffEnd);
        tag.put("damageDropoff", damageDropoff);
        tag.putFloat("gravity", this._gravity);
        if (this._ownerUUID != null)
        {
            tag.putUUID("owner", this._ownerUUID);
        }
        
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet)
    {
        super.recreateFromPacket(packet);
        Entity entity = this.level().getEntity(packet.getData());
        if (entity instanceof LivingEntity livingEntity)
        {
            this.setOwner(livingEntity);
        }
    }
    
    /**
     * Override to change damage type.
     * @return The damage source of the projectile.
     */
    protected TGDamageSource createDamageSource()
    {
        return TGDamageTypes.createShot(
                this.level().registryAccess(),
                this,
                this.getOwner(),
                DeathFXKind.GORE,
                this::applyProperties);
    }
    
    /**
     * Apply this projectile's properties to the specified properties.
     * @param properties The properties to apply to.
     * @return The applied properties.
     */
    protected TGDamageSource.Properties applyProperties(TGDamageSource.Properties properties)
    {
        return properties
                .withArmorPenetration(this._armorPenetration)
                .withoutKnockback();
    }
    
    protected double getDistanceTravelled()
    {
        Vec3 start = new Vec3(this._startPosX, this._startPosY, this._startPosZ);
        
        return start.distanceTo(this.position());
    }

    protected float getDamage()
    {
        if (this._damageDropoffEnd <= 0)
            return this._damage;
        
        double distance = this.getDistanceTravelled();
        
        if (distance <= this._damageDropoffStart)
            return this._damage;
        else if (distance >= this._damageDropoffEnd)
            return this._minDamage;
        else
        {
            float factor = 1.0f - (float)((distance - this._damageDropoffStart) / (this._damageDropoffEnd - this._damageDropoffStart));
            
            return this._minDamage + (this._damage - this._minDamage) * factor;
        }
    }
}
