package techguns2;

public final class TGTranslations
{
    private TGTranslations()
    { }
    
    public static class SecurityMode
    {
        public final String NAME;
        public final String DESCRIPTION;
        
        private SecurityMode(String rootName)
        {
            String base = "techguns2.security_mode." + rootName;
            
            this.NAME = base + ".name";
            this.DESCRIPTION = base + ".description";
        }
        
        public static final SecurityMode PUBLIC = new SecurityMode("public");
        public static final SecurityMode SAME_TEAM_AND_ALLIES = new SecurityMode("team_and_allies");
        public static final SecurityMode SAME_TEAM = new SecurityMode("team");
        public static final SecurityMode OWNER = new SecurityMode("owner");
    }
    
    public static class RedstoneBehaviour
    {
        public final String NAME;
        public final String SIGNAL_NAME;
        
        private RedstoneBehaviour(String rootName)
        {
            String base = "techguns2.redstone_behaviour." + rootName;
            
            this.NAME = base + ".name";
            this.SIGNAL_NAME = base + ".signal_name";
        }
        
        public static final RedstoneBehaviour IGNORE = new RedstoneBehaviour("ignore");
        public static final RedstoneBehaviour POWERED = new RedstoneBehaviour("powered");
        public static final RedstoneBehaviour UNPOWERED = new RedstoneBehaviour("unpowered");
    }
    
    public static final class Machine
    {
        private Machine()
        { }
        
        public static final String ENERGY_TOOLTIP = "techguns2.machine.energy.tooltip";
        
        public static final String REDSTONE_BEHAVIOUR_TOOLTIP = "techguns2.machine.redstone_behaviour.tooltip";
        public static final String REDSTONE_BEHAVIOUR_MODE_TOOLTIP = "techguns2.machine.redstone_behaviour.mode.tooltip";
        public static final String REDSTONE_BEHAVIOUR_SIGNAL_TOOLTIP = "techguns2.machine.redstone_behaviour.signal.tooltip";

        public static final String SECURITY_MODE_TOOLTIP = "techguns2.machine.security_mode.tooltip";
        public static final String SECURITY_MODE_DESCRIPTION_TOOLTIP = "techguns2.machine.security_mode.description.tooltip";
        public static final String SECURITY_MODE_OWNED_BY_TOOLTIP = "techguns2.machine.security_mode.owned_by.tooltip";
        public static final String SECURITY_MODE_OWNED_BY_NO_OWNER_TOOLTIP = "techguns2.machine.security_mode.owned_by.no_owner.tooltip";
        public static final String SECURITY_MODE_OWNED_BY_UNKNOWN_OWNER_TOOLTIP = "techguns2.machine.security_mode.owned_by.unknown_owner.tooltip";

        public static final String EMPTY_TANK_TOOLTIP = "techguns2.machine.empty_tank.tooltip";
        
        public static final String DUMP_TANK_TOOLTIP = "techguns2.machine.dump_tank.tooltip";
        public static final String DUMP_TANK_DESCRIPTION_TOOLTIP = "techguns2.machine.dump_tank.description.tooltip";
        
        
        public static final class AlloyFurnace
        {
            private AlloyFurnace()
            { }
            
            public static final String NAME = "techguns2.machine.alloy_furnace.name";
        }
        
        public static final class AmmoPress
        {
            private AmmoPress()
            { }
            
            public static final String NAME = "techguns2.machine.ammo_press.name";
            public static final String TEMPLATE_TITLE = "techguns2.machine.ammo_press.template_title";
        }
        
        public static final class ChargingStation
        {
            private ChargingStation()
            { }
            
            public static final String NAME = "techguns2.machine.charging_station.name";
        }
        
        public static final class ChemicalLaboratory
        {
            private ChemicalLaboratory()
            { }
            
            public static final String NAME = "techguns2.machine.chemical_laboratory.name";
        }
        
        public static final class Fabricator
        {
            private Fabricator()
            { }
            
            public static final String NAME = "techguns2.machine.fabricator.name";
        }
        
        public static final class Grinder
        {
            private Grinder()
            { }
            
            public static final String NAME = "techguns2.machine.grinder.name";
        }
        
        public static final class MetalPress
        {
            private MetalPress()
            { }
            
            public static final String NAME = "techguns2.machine.metal_press.name";
            
            public static final class StackSplit
            {
                private StackSplit()
                { }
                
                public static final String TITLE = "techguns2.machine.metal_press.stack_split_title";
                public static final String SWITCH = "techguns2.machine.metal_press.stack_split.switch";
                public static final String ON = "techguns2.machine.metal_press.stack_split.on";
                public static final String OFF = "techguns2.machine.metal_press.stack_split.off";
            }
        }
        
        public static final class ReactionChamber
        {
            private ReactionChamber()
            { }
            
            public static final String NAME = "techguns2.machine.reaction_chamber.name";
        }
    }
}
