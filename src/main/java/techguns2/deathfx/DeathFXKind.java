package techguns2.deathfx;

public enum DeathFXKind
{
    DEFAULT(0),
    GORE(1),
    BIO(2),
    LASER(3),
    DISMEMBER(4);
    
    public final int value;
    
    private DeathFXKind(int value)
    {
        this.value = value;
    }
}
