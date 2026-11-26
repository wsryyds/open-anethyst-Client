package net.client.replay.record;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class SoundEventRecord {
   public static final int MAGIC = 1397706311;
   public long timeNanos;
   public String eventPath;
   public String soundPath;
   public float x;
   public float y;
   public float z;
   public float volume;
   public float pitch;
   public boolean attenuated;
   public boolean repeatable;

   public void writeTable(DataOutput out) throws IOException {
      out.writeLong(this.timeNanos);
      out.writeUTF(this.eventPath == null ? "" : this.eventPath);
      out.writeUTF(this.soundPath == null ? "" : this.soundPath);
      out.writeFloat(this.x);
      out.writeFloat(this.y);
      out.writeFloat(this.z);
      out.writeFloat(this.volume);
      out.writeFloat(this.pitch);
      out.writeBoolean(this.attenuated);
      out.writeBoolean(this.repeatable);
   }

   public static SoundEventRecord readTable(DataInput in) throws IOException {
      SoundEventRecord e = new SoundEventRecord();
      e.timeNanos = in.readLong();
      e.eventPath = in.readUTF();
      e.soundPath = in.readUTF();
      e.x = in.readFloat();
      e.y = in.readFloat();
      e.z = in.readFloat();
      e.volume = in.readFloat();
      e.pitch = in.readFloat();
      e.attenuated = in.readBoolean();
      e.repeatable = in.readBoolean();
      return e;
   }
}
