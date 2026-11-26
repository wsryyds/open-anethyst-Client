package net.client.mixin;

import java.util.List;
import net.minecraft.class_705;
import net.minecraft.class_705.class_2578;
import net.minecraft.class_705.class_2579;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_705.class)
public interface ReplayPlayerListS2CPacketAccessor {
   @Accessor("field_11635")
   void client$setAction(class_2578 var1);

   @Accessor("field_11636")
   List<class_2579> client$getEntries();
}
