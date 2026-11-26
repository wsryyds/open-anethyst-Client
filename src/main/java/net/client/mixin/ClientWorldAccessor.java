package net.client.mixin;

import java.util.Set;
import net.minecraft.class_478;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_478.class)
public interface ClientWorldAccessor {
   @Accessor("field_1658")
   Set<class_864> client$entities();

   @Accessor("field_1664")
   Set<class_864> client$worldEntities();
}
