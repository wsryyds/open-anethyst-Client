package net.client.replay.session;

import java.io.File;
import java.util.List;
import net.client.mixin.ReplayGameRendererAccessor;
import net.client.replay.ReplayClient;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_518;
import net.minecraft.class_625;
import net.minecraft.class_864;
import net.minecraft.class_988;

public final class CameraController {
   public static final int MODE_PLAYER = 0;
   public static final int MODE_FREE = 1;
   public static final int MODE_ENTITY = 2;
   private final class_1600 client;
   private CameraPath path;
   private final File pathFile;
   private ReplayCameraEntity camera;
   private class_518 cameraActor;
   private int mode = 0;
   private int perspective = 0;
   private int targetEntityId = -1;
   private double freeX;
   private double freeY;
   private double freeZ;
   private float freeYaw;
   private float freePitch;
   private float freeFov = 70.0F;
   private boolean pathEnabled;
   private boolean saved;
   private double savedX;
   private double savedY;
   private double savedZ;
   private double savedPrevX;
   private double savedPrevY;
   private double savedPrevZ;
   private float savedYaw;
   private float savedPitch;
   private float savedPrevYaw;
   private float savedPrevPitch;
   private boolean savedInvisible;
   private float savedFov;
   private class_864 hiddenTarget;
   private boolean hiddenTargetWasInvisible;
   private boolean handHidden;
   private boolean savedRenderHand;
   private static class_1631 firstPersonHandPlayer;
   private static class_988 cameraOverridePlayer;

   public static class_1631 getFirstPersonHandPlayer() {
      return firstPersonHandPlayer;
   }

   public static class_988 getCameraOverridePlayer() {
      return cameraOverridePlayer;
   }

   public CameraController(class_1600 client, ReplayReader reader) {
      this.client = client;
      String configured = ReplayClient.getConfig() == null ? "" : ReplayClient.getConfig().cameraFile;
      File file;
      if (configured != null && !configured.isEmpty()) {
         file = new File(configured);
      } else {
         String name = reader.file.getName();
         int dot = name.lastIndexOf(46);
         String base = dot > 0 ? name.substring(0, dot) : name;
         file = new File(reader.file.getParentFile(), base + ".camera.json");
      }

      this.pathFile = file;
      this.path = CameraPath.load(file);
      if (this.path == null) {
         this.path = new CameraPath();
      }

      this.pathEnabled = this.hasPath();
      if (client.field_3823 != null) {
         this.perspective = client.field_3823.field_949;
      }

      if (client.field_10310 != null) {
         this.freeX = client.field_10310.field_3252;
         this.freeY = client.field_10310.field_3253 + client.field_10310.method_2544();
         this.freeZ = client.field_10310.field_3254;
         this.freeYaw = client.field_10310.field_3258;
         this.freePitch = client.field_10310.field_3193;
      }
   }

   public void setPlayhead(double seconds) {
      if (this.path == null) {
         this.path = new CameraPath();
      }

      this.path.editorSeconds = Math.max(0.0, seconds);
   }

   public double getSavedPlayhead() {
      return this.path == null ? -1.0 : this.path.editorSeconds;
   }

   public File getPathFile() {
      return this.pathFile;
   }

   public boolean hasPath() {
      return this.path != null && !this.path.keyframes.isEmpty();
   }

   public boolean isPathActive() {
      return this.pathEnabled && this.hasPath();
   }

   public int getMode() {
      return this.mode;
   }

   public int getPerspective() {
      return this.perspective;
   }

   public void setPerspective(int perspective) {
      this.perspective = Math.max(0, Math.min(2, perspective));
   }

   public int getTargetEntityId() {
      return this.targetEntityId;
   }

   public void copyStateFrom(CameraController other) {
      if (other != null) {
         this.mode = other.mode;
         this.perspective = other.perspective;
         this.pathEnabled = other.pathEnabled;
         this.targetEntityId = other.targetEntityId;
         this.freeX = other.freeX;
         this.freeY = other.freeY;
         this.freeZ = other.freeZ;
         this.freeYaw = other.freeYaw;
         this.freePitch = other.freePitch;
         this.freeFov = other.freeFov;
         this.path = other.path;
      }
   }

   public double getFreeX() {
      return this.freeX;
   }

   public double getFreeY() {
      return this.freeY;
   }

   public double getFreeZ() {
      return this.freeZ;
   }

   public float getFreeYaw() {
      return this.freeYaw;
   }

   public float getFreePitch() {
      return this.freePitch;
   }

   public float getFreeFov() {
      return this.freeFov;
   }

   public void setMode(int mode) {
      if (this.mode == mode) {
         this.pathEnabled = false;
      } else {
         this.restore();
         this.restoreHiddenTarget();
         this.mode = mode;
         this.pathEnabled = false;
      }
   }

   public void setFreeTransform(double x, double y, double z, float yaw, float pitch, float fov) {
      this.setMode(1);
      this.freeX = x;
      this.freeY = y;
      this.freeZ = z;
      this.freeYaw = yaw;
      this.freePitch = Math.max(-89.9F, Math.min(89.9F, pitch));
      this.freeFov = Math.max(10.0F, Math.min(160.0F, fov));
   }

   public void rotateFree(float deltaYaw, float deltaPitch) {
      if (this.mode == 1) {
         this.pathEnabled = false;
         this.freeYaw = wrapYaw(this.freeYaw + deltaYaw);
         this.freePitch = Math.max(-89.9F, Math.min(89.9F, this.freePitch + deltaPitch));
      }
   }

   public void moveFree(double forward, double strafe, double vertical) {
      this.moveFree(forward, strafe, vertical, 1.0);
   }

   public void moveFree(double forward, double strafe, double vertical, double scale) {
      if (this.mode == 1 && (forward != 0.0 || strafe != 0.0 || vertical != 0.0)) {
         this.pathEnabled = false;
         double yawRad = Math.toRadians(this.freeYaw);
         double pitchRad = Math.toRadians(this.freePitch);
         double lookX = -Math.sin(yawRad) * Math.cos(pitchRad);
         double lookY = -Math.sin(pitchRad);
         double lookZ = Math.cos(yawRad) * Math.cos(pitchRad);
         double rightX = -Math.cos(yawRad);
         double rightZ = -Math.sin(yawRad);
         double speed = 0.38 * Math.max(0.05, scale);
         this.freeX += (lookX * forward + rightX * strafe) * speed;
         this.freeY = this.freeY + (lookY * forward * speed + vertical * 0.45 * Math.max(0.05, scale));
         this.freeZ += (lookZ * forward + rightZ * strafe) * speed;
      }
   }

   public void resetFreeFromPlayer() {
      if (this.client.field_10310 != null) {
         this.freeX = this.client.field_10310.field_3252;
         this.freeY = this.client.field_10310.field_3253 + this.client.field_10310.method_2544();
         this.freeZ = this.client.field_10310.field_3254;
         this.freeYaw = this.client.field_10310.field_3258;
         this.freePitch = this.client.field_10310.field_3193;
      }
   }

   private static float wrapYaw(float yaw) {
      while (yaw < -180.0F) {
         yaw += 360.0F;
      }

      while (yaw >= 180.0F) {
         yaw -= 360.0F;
      }

      return yaw;
   }

   public void setTargetEntity(int entityId) {
      this.setMode(2);
      this.targetEntityId = entityId;
   }

   public void usePath(boolean enabled) {
      if (enabled) {
         this.restore();
         this.restoreHiddenTarget();
      }

      this.pathEnabled = enabled && this.hasPath();
   }

   public CameraPath getPath() {
      return this.path;
   }

   public boolean deleteKeyframe(CameraPath.Keyframe frame) {
      if (this.path != null && frame != null) {
         boolean removed = this.path.keyframes.remove(frame);
         if (removed) {
            if (this.path.keyframes.isEmpty()) {
               this.pathEnabled = false;
            }

            this.savePath();
         }

         return removed;
      } else {
         return false;
      }
   }

   public void clearPath() {
      if (this.path != null) {
         this.path.keyframes.clear();
      }

      this.pathEnabled = false;
   }

   public void addKeyframe(double time, double x, double y, double z, float yaw, float pitch, float fov) {
      CameraPath target = this.path;
      if (target != null) {
         CameraPath.Keyframe frame = new CameraPath.Keyframe();
         frame.time = time;
         frame.mode = 1;
         frame.targetEntityId = -1;
         frame.perspective = this.perspective;
         frame.x = x;
         frame.y = y;
         frame.z = z;
         frame.yaw = yaw;
         frame.pitch = pitch;
         frame.fov = fov;
         target.add(frame);
         this.pathEnabled = true;
      }
   }

   public void addCurrentKeyframe(double time) {
      CameraPath target = this.path;
      if (target != null) {
         CameraPath.Keyframe frame = new CameraPath.Keyframe();
         frame.time = time;
         frame.mode = this.mode;
         frame.targetEntityId = this.mode == 2 ? this.targetEntityId : -1;
         frame.perspective = this.client.field_3823 != null ? this.client.field_3823.field_949 : this.perspective;
         frame.fov = this.client.field_3823 != null ? this.client.field_3823.field_958 : this.freeFov;
         if (this.mode == 1) {
            frame.x = this.freeX;
            frame.y = this.freeY;
            frame.z = this.freeZ;
            frame.yaw = this.freeYaw;
            frame.pitch = this.freePitch;
            frame.fov = this.freeFov;
         } else if (this.mode == 2) {
            class_864 entity = this.findTarget(this.targetEntityId);
            if (entity != null) {
               frame.x = entity.field_3252;
               frame.y = entity.field_3253 + entity.method_2544();
               frame.z = entity.field_3254;
               frame.yaw = entity.field_3258;
               frame.pitch = entity.field_3193;
            }
         } else if (this.client.field_10310 != null) {
            frame.x = this.client.field_10310.field_3252;
            frame.y = this.client.field_10310.field_3253 + this.client.field_10310.method_2544();
            frame.z = this.client.field_10310.field_3254;
            frame.yaw = this.client.field_10310.field_3258;
            frame.pitch = this.client.field_10310.field_3193;
            frame.perspective = this.client.field_3823 != null ? this.client.field_3823.field_949 : this.perspective;
         }

         target.add(frame);
         this.pathEnabled = true;
      }
   }

   public int getPerspectiveAt(double seconds) {
      if (this.isPathActive() && this.path != null) {
         CameraPath.Transform transform = this.path.at(seconds);
         if (transform != null) {
            return transform.perspective;
         }
      }

      return this.perspective;
   }

   public void setTrimRange(double start, double end) {
      if (this.path != null) {
         this.path.trimStartSeconds = start;
         this.path.trimEndSeconds = end;
      }
   }

   public double getSavedTrimStart() {
      return this.path == null ? -1.0 : this.path.trimStartSeconds;
   }

   public double getSavedTrimEnd() {
      return this.path == null ? -1.0 : this.path.trimEndSeconds;
   }

   public boolean savePath() {
      if (this.path != null && this.pathFile != null) {
         this.path.save(this.pathFile);
         ReplayClient.chat("Camera track saved: " + this.pathFile.getName());
         return true;
      } else {
         return false;
      }
   }

   public void resetForWorld() {
      firstPersonHandPlayer = null;
      cameraOverridePlayer = null;
      this.cameraActor = null;
      this.restoreHiddenTarget();
      this.setHandHidden(false);
      this.camera = null;
      this.saved = false;
      if (this.client.field_10310 != null) {
         this.freeX = this.client.field_10310.field_3252;
         this.freeY = this.client.field_10310.field_3253 + this.client.field_10310.method_2544();
         this.freeZ = this.client.field_10310.field_3254;
         this.freeYaw = this.client.field_10310.field_3258;
         this.freePitch = this.client.field_10310.field_3193;
      }
   }

   public void update(double seconds) {
      if (this.client.field_10310 != null) {
         if (this.isPathActive()) {
            CameraPath.Segment seg = this.path.segmentAt(seconds);
            if (seg != null) {
               CameraPath.Keyframe a = seg.a;
               CameraPath.Keyframe b = seg.b;
               boolean sameView = a.mode == b.mode && (a.mode != 2 || a.targetEntityId == b.targetEntityId);
               if (sameView) {
                  if (a.mode == 1) {
                     this.setHandHidden(true);
                     if (this.client.field_3823 != null) {
                        this.client.field_3823.field_949 = a.perspective;
                     }

                     CameraPath.Transform p = blend(this.poseOf(a), this.poseOf(b), (float)seg.t);
                     this.applyTransform(p.x, p.y, p.z, p.yaw, p.pitch, p.fov);
                     return;
                  }

                  if (a.mode == 0) {
                     this.restore();
                     this.client.field_10310.method_4452(false);
                     this.setHandHidden(false);
                     if (this.client.field_3823 != null) {
                        this.client.field_3823.field_949 = a.perspective;
                        this.client.field_3823.field_958 = clampFov(a.fov);
                     }

                     return;
                  }

                  class_864 target = this.findTarget(a.targetEntityId);
                  if (target != null && target != this.client.field_10310) {
                     this.applyEntityView(target);
                     if (this.client.field_3823 != null) {
                        this.client.field_3823.field_958 = clampFov(a.fov);
                     }

                     return;
                  }

                  this.restore();
                  this.client.field_10310.method_4452(false);
                  this.setHandHidden(false);
                  return;
               }

               CameraPath.Transform from = this.poseOf(a);
               CameraPath.Transform to = this.poseOf(b);
               CameraPath.Transform p = blend(from, to, (float)seg.t);
               this.setHandHidden(true);
               if (this.client.field_3823 != null) {
                  this.client.field_3823.field_949 = b.perspective;
               }

               this.applyTransform(p.x, p.y, p.z, p.yaw, p.pitch, p.fov);
               return;
            }

            CameraPath.Transform transform = this.path.at(seconds);
            if (transform != null) {
               if (this.client.field_3823 != null) {
                  this.client.field_3823.field_949 = transform.perspective;
               }

               if (transform.mode == 2) {
                  class_864 target = this.findTarget(transform.targetEntityId);
                  if (target != null && target != this.client.field_10310) {
                     this.applyEntityView(target);
                     if (this.client.field_3823 != null) {
                        this.client.field_3823.field_958 = clampFov(transform.fov);
                     }

                     return;
                  }
               }

               if (transform.mode == 1) {
                  this.setHandHidden(true);
                  this.applyTransform(transform.x, transform.y, transform.z, transform.yaw, transform.pitch, transform.fov);
                  return;
               }

               this.restore();
               this.client.field_10310.method_4452(false);
               this.setHandHidden(false);
               if (this.client.field_3823 != null) {
                  this.client.field_3823.field_949 = transform.perspective;
                  this.client.field_3823.field_958 = clampFov(transform.fov);
               }

               return;
            }
         }

         if (this.mode == 1) {
            this.setHandHidden(true);
            this.applyTransform(this.freeX, this.freeY, this.freeZ, this.freeYaw, this.freePitch, this.freeFov);
         } else if (this.mode == 2) {
            if (this.client.field_10310 != null) {
               this.client.field_10310.method_4452(false);
            }

            class_864 target = this.findTarget();
            if (target != null && target != this.client.field_10310) {
               this.applyEntityView(target);
            } else {
               this.restore();
               this.client.field_10310.method_4452(false);
            }
         } else {
            this.restore();
            if (this.mode == 0) {
               this.client.field_10310.method_4452(false);
               if (this.client.field_3823 != null && this.client.field_3823.field_949 != 0 && this.client.method_9388() != this.client.field_10310) {
                  this.client.method_9384(this.client.field_10310);
               }
            }
         }
      }
   }

   private CameraPath.Transform poseOf(CameraPath.Keyframe frame) {
      CameraPath.Transform pose = new CameraPath.Transform();
      pose.mode = 1;
      pose.perspective = frame.perspective;
      pose.x = frame.x;
      pose.y = frame.y;
      pose.z = frame.z;
      pose.yaw = frame.yaw;
      pose.pitch = frame.pitch;
      pose.fov = frame.fov;
      if (frame.mode == 0 && this.client.field_10310 != null) {
         pose.x = this.client.field_10310.field_3252;
         pose.y = this.client.field_10310.field_3253 + this.client.field_10310.method_2544();
         pose.z = this.client.field_10310.field_3254;
         pose.yaw = this.client.field_10310.field_3258;
         pose.pitch = this.client.field_10310.field_3193;
      } else if (frame.mode == 2) {
         class_864 target = this.findTarget(frame.targetEntityId);
         if (target != null) {
            pose.x = target.field_3252;
            pose.y = target.field_3253 + target.method_2544();
            pose.z = target.field_3254;
            pose.yaw = target.field_3258;
            pose.pitch = target.field_3193;
         }
      }

      return pose;
   }

   private static CameraPath.Transform blend(CameraPath.Transform a, CameraPath.Transform b, float t) {
      CameraPath.Transform out = new CameraPath.Transform();
      out.mode = 1;
      out.perspective = b.perspective;
      out.x = a.x + (b.x - a.x) * t;
      out.y = a.y + (b.y - a.y) * t;
      out.z = a.z + (b.z - a.z) * t;
      out.yaw = a.yaw + wrapAngle(b.yaw - a.yaw) * t;
      out.pitch = a.pitch + (b.pitch - a.pitch) * t;
      out.fov = a.fov + (b.fov - a.fov) * t;
      return out;
   }

   private static float wrapAngle(float delta) {
      while (delta < -180.0F) {
         delta += 360.0F;
      }

      while (delta >= 180.0F) {
         delta -= 360.0F;
      }

      return delta;
   }

   private class_864 findTarget() {
      return this.findTarget(this.targetEntityId);
   }

   private class_864 findTarget(int entityId) {
      if (this.client.field_3803 != null && entityId >= 0) {
         List<class_864> entities = this.client.field_3803.method_3737();
         if (entities == null) {
            return null;
         } else {
            for (class_864 entity : entities) {
               if (entity.method_8365() == entityId) {
                  return entity;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static float clampFov(float fov) {
      return Math.max(10.0F, Math.min(160.0F, fov));
   }

   private void savePlayerState(class_988 player) {
      this.saved = true;
      this.savedX = player.field_3252;
      this.savedY = player.field_3253;
      this.savedZ = player.field_3254;
      this.savedPrevX = player.field_3249;
      this.savedPrevY = player.field_3250;
      this.savedPrevZ = player.field_3251;
      this.savedYaw = player.field_3258;
      this.savedPitch = player.field_3193;
      this.savedPrevYaw = player.field_3194;
      this.savedPrevPitch = player.field_3195;
      this.savedInvisible = player.method_4446();
      this.savedFov = this.client.field_3823.field_958;
   }

   private void applyTransform(double x, double y, double z, float yaw, float pitch, float fov) {
      firstPersonHandPlayer = null;
      cameraOverridePlayer = null;
      class_988 player = this.client.field_10310;
      if (player != null) {
         if (!this.saved) {
            this.savePlayerState(player);
         }

         player.method_4452(false);
         player.field_3211 = player.field_3252;
         player.field_3212 = player.field_3253;
         player.field_3213 = player.field_3254;
         player.field_3249 = player.field_3252;
         player.field_3250 = player.field_3253;
         player.field_3251 = player.field_3254;
         player.field_3194 = player.field_3258;
         player.field_3195 = player.field_3193;
         player.field_6758 = player.field_3258;
         player.field_6759 = player.field_3258;
         player.field_6756 = player.field_3258;
         player.field_6757 = player.field_3258;
         this.client.field_3823.field_958 = Math.max(10.0F, Math.min(160.0F, fov));
         if (this.camera == null && this.client.field_3803 != null) {
            this.camera = new ReplayCameraEntity(this.client.field_3803);
         }

         if (this.camera != null) {
            this.camera.setTransform(x, y, z, yaw, pitch);
            this.client.method_9384(this.camera);
         }

         this.updateCameraActor(x, y, z, yaw, pitch);
      }
   }

   private void updateCameraActor(double x, double y, double z, float yaw, float pitch) {
      if (this.client.field_3803 != null) {
         if (this.cameraActor == null) {
            class_518 networkPlayer = this.client.field_10310;
            if (networkPlayer == null || networkPlayer.field_10626 == null) {
               return;
            }

            this.cameraActor = new class_518(this.client, this.client.field_3803, networkPlayer.field_10626, new class_625());
            this.cameraActor.method_4452(true);
         }

         this.cameraActor.field_3248 = this.client.field_3803;
         this.cameraActor.field_3252 = this.cameraActor.field_3249 = this.cameraActor.field_3211 = x;
         this.cameraActor.field_3253 = this.cameraActor.field_3250 = this.cameraActor.field_3212 = y;
         this.cameraActor.field_3254 = this.cameraActor.field_3251 = this.cameraActor.field_3213 = z;
         this.cameraActor.field_3258 = this.cameraActor.field_3194 = yaw;
         this.cameraActor.field_3193 = this.cameraActor.field_3195 = pitch;
         this.cameraActor.field_5323 = this.client.field_10310.field_5323;
         cameraOverridePlayer = this.cameraActor;
      }
   }

   private void applyEntityView(class_864 target) {
      class_988 player = this.client.field_10310;
      if (player != null && target != player) {
         if (!this.saved) {
            this.savePlayerState(player);
         }

         player.method_4452(false);
         if (this.hiddenTarget != target) {
            this.restoreHiddenTarget();
            this.hiddenTarget = target;
            this.hiddenTargetWasInvisible = target.method_4446();
         }

         boolean firstPerson = this.client.field_3823.field_949 == 0;
         if (firstPerson) {
            if (!target.method_4446()) {
               target.method_4452(true);
            }

            firstPersonHandPlayer = target instanceof class_1631 ? (class_1631)target : null;
         } else {
            target.method_4452(false);
            firstPersonHandPlayer = null;
         }

         cameraOverridePlayer = target instanceof class_988 ? (class_988)target : null;
         this.client.method_9384(target);
         this.client.field_3823.field_958 = Math.max(10.0F, Math.min(160.0F, this.freeFov));
         this.setHandHidden(false);
      }
   }

   public void restore() {
      firstPersonHandPlayer = null;
      cameraOverridePlayer = null;
      this.restoreHiddenTarget();
      this.setHandHidden(false);
      if (this.client.field_10310 == null) {
         this.client.method_9384(null);
         this.saved = false;
      } else if (!this.saved) {
         this.client.method_9384(this.client.field_10310);
      } else {
         class_988 player = this.client.field_10310;
         player.method_4452(this.savedInvisible);
         this.client.field_3823.field_958 = this.savedFov;
         this.client.method_9384(player);
         this.saved = false;
      }
   }

   private void restoreHiddenTarget() {
      if (this.hiddenTarget != null) {
         this.hiddenTarget.method_4452(this.hiddenTargetWasInvisible);
         this.hiddenTarget = null;
      }

      cameraOverridePlayer = null;
   }

   public void syncFirstPersonHand() {
      if (this.client.field_3823 != null && !this.isPathActive()) {
         if (this.mode != 0) {
            this.setHandHidden(true);
         } else {
            this.setHandHidden(this.client.field_3823.field_949 != 0);
         }
      }
   }

   private void setHandHidden(boolean hidden) {
      if (this.handHidden != hidden) {
         try {
            ReplayGameRendererAccessor renderer = (ReplayGameRendererAccessor)this.client.field_3818;
            if (hidden) {
               this.savedRenderHand = renderer.client$getRenderHand();
               renderer.client$setRenderHand(false);
            } else {
               renderer.client$setRenderHand(this.savedRenderHand);
            }

            this.handHidden = hidden;
         } catch (Throwable var3) {
         }
      }
   }
}
