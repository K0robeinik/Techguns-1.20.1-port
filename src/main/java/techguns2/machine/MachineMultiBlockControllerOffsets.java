//package techguns2.machine;
//
//import net.minecraft.core.BlockPos;
//
//public final class MachineMultiBlockControllerOffsets
//{
//    private MachineMultiBlockControllerOffsets()
//    { }
//
//    public static final int MASK_NORTHSOUTH_VALUE = 0b00000011;
//    public static final int MASK_NORTHSOUTH_SIGN  = 0b00000100;
//    public static final int MASK_NORTHSOUTH       = 0b00000111;
//    public static final int MASK_EASTWEST_VALUE   = 0b01100000;
//    public static final int MASK_EASTWEST_SIGN    = 0b10000000;
//    public static final int MASK_EASTWEST         = 0b11100000;
//    public static final int MASK_VERTICAL         = 0b00011000;
//
//    public static final int VALUE_NORTH1 = 0b00000101;
//    public static final int VALUE_NORTH2 = 0b00000110;
//    public static final int VALUE_NORTH3 = 0b00000111;
//    public static final int VALUE_SOUTH1 = 0b00000001;
//    public static final int VALUE_SOUTH2 = 0b00000010;
//    public static final int VALUE_SOUTH3 = 0b00000011;
//    
//    public static final int VALUE_EAST1 = 0b00100000;
//    public static final int VALUE_EAST2 = 0b01000000;
//    public static final int VALUE_EAST3 = 0b01100000;
//    public static final int VALUE_WEST1 = 0b10100000;
//    public static final int VALUE_WEST2 = 0b11000000;
//    public static final int VALUE_WEST3 = 0b11100000;
//    
//    public static final int VALUE_DOWN1 = 0b00001000;
//    public static final int VALUE_DOWN2 = 0b00010000;
//    public static final int VALUE_DOWN3 = 0b00011000;
//    
//    public static int getXOffset(byte value)
//    {
//        return -((MASK_EASTWEST_SIGN & value) >> 7) *
//                ((MASK_EASTWEST_VALUE & value) >> 5);
//    }
//    
//    public static int getYOffset(byte value)
//    {
//        return -((MASK_VERTICAL & value) >> 3);
//    }
//    
//    public static int getZOffset(byte value)
//    {
//        return -((MASK_NORTHSOUTH_SIGN & value) >> 2) *
//                (MASK_NORTHSOUTH_VALUE & value);
//    }
//    
//    private static byte getValue(
//            BlockPos sourcePos,
//            BlockPos controllerPos,
//            int xSize,
//            int ySize,
//            int zSize)
//    {
//        int xDif = Math.max(-xSize + 1, Math.min(xSize - 1, controllerPos.getX() - sourcePos.getX()));
//        int yDif = Math.max(-ySize + 1, Math.min(0, controllerPos.getY() - sourcePos.getY()));
//        int zDif = Math.max(-zSize + 1, Math.min(zSize - 1, controllerPos.getZ() - sourcePos.getZ()));
//        
//        return (byte)((zDif < 0 ? (-zDif + 0b00000100) : zDif) 
//                | (-yDif << 3)
//                | ((xDif < 0 ? (-xDif + 0b00000100) : xDif) << 5));
//    }
//    
//    private static BlockPos getControllerPos(
//            BlockPos sourcePos,
//            byte value,
//            int xSize,
//            int ySize,
//            int zSize)
//    {
//        int northSouthValue = value & MASK_NORTHSOUTH_VALUE;
//        int eastWestValue = value & MASK_EASTWEST_VALUE;
//        int verticalValue = value & MASK_VERTICAL;
//        
//        int xOffset = Math.max(-xSize + 1, Math.min(xSize - 1, -((MASK_EASTWEST_SIGN & value) >> 7) * (eastWestValue >> 5)));
//        int yOffset = Math.max(-ySize + 1, Math.min(0, -(verticalValue >> 3)));
//        int zOffset = Math.max(-zSize + 1, Math.min(zSize - 1, -((MASK_NORTHSOUTH_SIGN & value) >> 2) * northSouthValue));
//        
//        if (zOffset == 0 && yOffset == 0 && zOffset == 0)
//            return sourcePos;
//        else
//            return new BlockPos(
//                    sourcePos.getX() + xOffset,
//                    sourcePos.getY() + yOffset,
//                    sourcePos.getZ() + zOffset);
//    }
//    
//    public static byte getValue2x2x2(BlockPos sourcePos, BlockPos controllerPos)
//    {
//        return getValue(sourcePos, controllerPos, 2, 2, 2);
//    }
//    
//    public static BlockPos getControllerPos2x2x2(BlockPos sourcePos, byte value)
//    {
//        return getControllerPos(sourcePos, value, 2, 2, 2);
//    }
//    
//    public static byte getValue3x4x3(BlockPos sourcePos, BlockPos controllerPos)
//    {
//        return getValue(sourcePos, controllerPos, 3, 4, 3);
//    }
//    
//    public static BlockPos getControllerPos3x4x3(BlockPos sourcePos, byte value)
//    {
//        return getControllerPos(sourcePos, value, 3, 4, 3);
//    }
//    
//}
