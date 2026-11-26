package net.client.mixin;

import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1845;
import net.minecraft.class_2346;
import net.minecraft.class_478;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1845.class)
public interface ReplayClientPlayNetworkHandlerAccessor {
   @Accessor("field_7916")
   class_478 client$getWorld();

   @Accessor("field_7916")
   void client$setWorld(class_478 var1);

   @Accessor("field_7918")
   Map<UUID, class_2346> client$getPlayerListEntries();

   @Accessor("field_7917")
   void client$setPositionLookSetup(boolean var1);
}
