package net.client.replay.record;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SnapshotSample {
   public long timeNanos;
   public List<EntitySnapshot> entities = new ArrayList<>();

   public void write(DataOutput out) throws IOException {
      out.writeLong(this.timeNanos);
      out.writeInt(this.entities.size());

      for (EntitySnapshot entity : this.entities) {
         entity.write(out);
      }
   }

   public static SnapshotSample read(DataInput in) throws IOException {
      SnapshotSample sample = new SnapshotSample();
      sample.timeNanos = in.readLong();
      int count = in.readInt();
      sample.entities = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
         sample.entities.add(EntitySnapshot.read(in));
      }

      return sample;
   }
}
