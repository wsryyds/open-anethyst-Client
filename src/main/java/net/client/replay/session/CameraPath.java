package net.client.replay.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CameraPath {
   public List<CameraPath.Keyframe> keyframes = new ArrayList<>();
   public double trimStartSeconds = -1.0;
   public double trimEndSeconds = -1.0;
   public double editorSeconds = -1.0;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public static CameraPath load(File file) {
      if (file != null && file.isFile()) {
         try {
            CameraPath var3;
            try (Reader reader = new FileReader(file)) {
               CameraPath path = GSON.fromJson(reader, CameraPath.class);
               if (path == null) {
                  return null;
               }

               if (path.keyframes == null) {
                  path.keyframes = new ArrayList<>();
               }

               if (path.keyframes.isEmpty() && path.trimStartSeconds < 0.0 && path.editorSeconds < 0.0) {
                  return null;
               }

               path.sort();
               var3 = path;
            }

            return var3;
         } catch (Throwable var6) {
            return null;
         }
      } else {
         return null;
      }
   }

   private void sort() {
      Collections.sort(this.keyframes, new Comparator<CameraPath.Keyframe>() {
         public int compare(CameraPath.Keyframe a, CameraPath.Keyframe b) {
            return Double.compare(a.time, b.time);
         }
      });
   }

   public void save(File file) {
      try {
         if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
         }

         Writer writer = new FileWriter(file);

         try {
            GSON.toJson(this, writer);
         } finally {
            writer.close();
         }
      } catch (Throwable var7) {
      }
   }

   public void add(CameraPath.Keyframe frame) {
      this.keyframes.add(frame);
      this.sort();
   }

   public CameraPath.Segment segmentAt(double seconds) {
      if (this.keyframes.size() < 2) {
         return null;
      } else if (seconds <= this.keyframes.get(0).time) {
         return null;
      } else if (seconds >= this.keyframes.get(this.keyframes.size() - 1).time) {
         return null;
      } else {
         for (int i = 0; i < this.keyframes.size() - 1; i++) {
            CameraPath.Keyframe a = this.keyframes.get(i);
            CameraPath.Keyframe b = this.keyframes.get(i + 1);
            if (seconds >= a.time && seconds < b.time) {
               double span = b.time - a.time;
               double t = span <= 0.0 ? 1.0 : (seconds - a.time) / span;
               return new CameraPath.Segment(a, b, t);
            }
         }

         return null;
      }
   }

   public CameraPath.Transform at(double seconds) {
      if (this.keyframes.isEmpty()) {
         return null;
      } else {
         CameraPath.Keyframe first = this.keyframes.get(0);
         if (seconds <= first.time) {
            return copy(first);
         } else {
            CameraPath.Keyframe last = this.keyframes.get(this.keyframes.size() - 1);
            if (seconds >= last.time) {
               return copy(last);
            } else {
               for (int i = 0; i < this.keyframes.size() - 1; i++) {
                  CameraPath.Keyframe a = this.keyframes.get(i);
                  CameraPath.Keyframe b = this.keyframes.get(i + 1);
                  if (seconds >= b.time) {
                     return copy(b);
                  }

                  if (seconds >= a.time && seconds <= b.time) {
                     if (a.mode == b.mode && a.mode == 1 && a.targetEntityId == b.targetEntityId) {
                        double span = b.time - a.time;
                        float t = span <= 0.0 ? 1.0F : (float)((seconds - a.time) / span);
                        CameraPath.Transform transform = copy(a);
                        transform.x = a.x + (b.x - a.x) * t;
                        transform.y = a.y + (b.y - a.y) * t;
                        transform.z = a.z + (b.z - a.z) * t;
                        transform.yaw = lerpAngle(a.yaw, b.yaw, t);
                        transform.pitch = a.pitch + (b.pitch - a.pitch) * t;
                        transform.fov = a.fov + (b.fov - a.fov) * t;
                        return transform;
                     }

                     return copy(a);
                  }
               }

               return copy(last);
            }
         }
      }
   }

   private static CameraPath.Transform copy(CameraPath.Keyframe frame) {
      CameraPath.Transform transform = new CameraPath.Transform();
      transform.mode = frame.mode;
      transform.targetEntityId = frame.targetEntityId;
      transform.perspective = frame.perspective;
      transform.x = frame.x;
      transform.y = frame.y;
      transform.z = frame.z;
      transform.yaw = frame.yaw;
      transform.pitch = frame.pitch;
      transform.fov = frame.fov;
      return transform;
   }

   private static float lerpAngle(float a, float b, float t) {
      float difference = b - a;

      while (difference < -180.0F) {
         difference += 360.0F;
      }

      while (difference >= 180.0F) {
         difference -= 360.0F;
      }

      return a + difference * t;
   }

   public static final class Keyframe {
      public double time;
      public int mode = 1;
      public int targetEntityId = -1;
      public int perspective = 0;
      public double x;
      public double y;
      public double z;
      public float yaw;
      public float pitch;
      public float fov = 70.0F;
   }

   public static final class Segment {
      public final CameraPath.Keyframe a;
      public final CameraPath.Keyframe b;
      public final double t;

      Segment(CameraPath.Keyframe a, CameraPath.Keyframe b, double t) {
         this.a = a;
         this.b = b;
         this.t = t;
      }
   }

   public static final class Transform {
      public int mode = 1;
      public int targetEntityId = -1;
      public int perspective = 0;
      public double x;
      public double y;
      public double z;
      public float yaw;
      public float pitch;
      public float fov = 70.0F;
   }
}
