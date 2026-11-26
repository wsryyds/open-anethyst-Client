package net.client.mixin;

import java.util.List;
import net.minecraft.class_356;
import net.minecraft.class_388;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_388.class)
public interface ScreenAccessor {
   @Accessor("field_1230")
   int client$width();

   @Accessor("field_1231")
   int client$height();

   @Accessor("field_1232")
   List<class_356> client$buttons();
}
