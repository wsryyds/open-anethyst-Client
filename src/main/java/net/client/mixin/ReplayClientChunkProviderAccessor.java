package net.client.mixin;

import java.util.List;
import net.minecraft.class_1196;
import net.minecraft.class_476;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_476.class)
public interface ReplayClientChunkProviderAccessor {
   @Accessor("field_1644")
   List<class_1196> client$getChunks();
}
