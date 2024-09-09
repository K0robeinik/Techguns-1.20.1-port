package techguns2.machine;

public abstract class MachineUpgrade
{   
    public static final class Stack extends MachineUpgrade
    {
        private Stack()
        { }
        
        private static final Stack SINGLETON = new Stack();
        
        public static Stack of()
        {
            return SINGLETON;
        }
    }
}
