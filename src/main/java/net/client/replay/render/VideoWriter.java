package net.client.replay.render;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface VideoWriter extends AutoCloseable {
   void writeFrame(ByteBuffer var1, int var2, int var3) throws IOException;

   @Override
   void close() throws IOException;

   String describe();
}
