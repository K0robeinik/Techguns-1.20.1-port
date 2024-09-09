package techguns2.util;

public enum DirtyableResult
{
    SUCCESSFUL(true, false),
    SUCCESSFUL_DIRTY(true, true),
    FAILED(false, false),
    FAILED_DIRTY(false, false);
    
    public final boolean success;
    public final boolean dirty;
    
    private DirtyableResult(boolean success, boolean dirty)
    {
        this.success = success;
        this.dirty = dirty;
    }
}
