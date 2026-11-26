package net.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.client.ui.ClientScroll;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3IconButton;
import net.client.ui.md3.widget.Md3ListItem;
import net.client.ui.md3.widget.Md3Text;
import net.client.util.ClientLog;
import net.client.util.CurseForgeUpdater;
import net.client.util.EnvCheck;
import net.client.util.ModrinthUpdater;
import net.client.util.OptiFineUpdater;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_388;
import org.lwjgl.input.Mouse;

public class ModListScreen extends class_388 {
   private static final int ROW_H = 48;
   private final class_388 parent;
   private final List<ModListScreen.Entry> mods = new ArrayList<>();
   private final Md3Canvas canvas = new Md3Canvas();
   private final Md3IconButton closeButton = new Md3IconButton();
   private final ClientScroll scroll = new ClientScroll();
   private float cardX;
   private float cardY;
   private float cardW;
   private float cardH;
   private float listX;
   private float listY;
   private float listW;
   private float listH;
   private float[] rowYs = new float[0];
   private long lastNanos;
   private float dt;
   private boolean mouseDown;

   public ModListScreen(class_388 parent) {
      this.parent = parent;
      this.loadMods();
      this.startUpdateCheck();
      CurseForgeUpdater.checkAll(this.ids(), this.names(), this.versions());
      EnvCheck.ensureStarted();
      OptiFineUpdater.ensureStarted();
   }

   private List<String> ids() {
      List<String> out = new ArrayList<>();

      for (ModListScreen.Entry e : this.mods) {
         out.add(e.id);
      }

      return out;
   }

   private Map<String, String> names() {
      Map<String, String> out = new HashMap<>();

      for (ModListScreen.Entry e : this.mods) {
         out.put(e.id, e.name);
      }

      return out;
   }

   private Map<String, String> versions() {
      Map<String, String> out = new HashMap<>();

      for (ModListScreen.Entry e : this.mods) {
         out.put(e.id, e.version);
      }

      return out;
   }

   private void startUpdateCheck() {
      List<String> ids = new ArrayList<>();
      Map<String, String> versions = new HashMap<>();

      for (ModListScreen.Entry e : this.mods) {
         ids.add(e.id);
         versions.put(e.id, e.version);
      }

      ModrinthUpdater.checkAll(ids, versions);
   }

   private void loadMods() {
      this.mods.clear();

      try {
         for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            ModMetadata meta = container.getMetadata();
            String name = meta.getName();
            String id = meta.getId();
            String version = meta.getVersion() == null ? "" : meta.getVersion().getFriendlyString();
            if (name == null || name.trim().isEmpty()) {
               name = id;
            }

            this.mods.add(new ModListScreen.Entry(name, id, version));
         }
      } catch (Exception var7) {
         ClientLog.warn("读取模组元数据失败，跳过异常条目", var7);
      }

      this.mods.sort(Comparator.comparing(e -> e.name == null ? "" : e.name.toLowerCase()));
   }

   @Override
   public void method_1044() {
      this.field_1232.clear();
      this.cardW = Math.min(720.0F, this.field_1230 * 0.62F);
      this.cardH = Math.min(540.0F, this.field_1231 * 0.78F);
      this.cardX = (this.field_1230 - this.cardW) / 2.0F;
      this.cardY = (this.field_1231 - this.cardH) / 2.0F;
      this.listX = this.cardX + 18.0F;
      this.listY = this.cardY + 62.0F;
      this.listW = this.cardW - 36.0F;
      this.listH = Math.max(96.0F, this.cardH - 76.0F);
      this.closeButton.variant = Md3IconButton.Variant.STANDARD;
      this.closeButton.icon = "close";
      this.closeButton.setBounds(this.cardX + this.cardW - 50.0F, this.cardY + 12.0F, 38.0F, 38.0F);
      this.scroll.jump(0.0F);
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, this.field_1229 != null && this.field_1229.field_3803 != null);
      this.canvas.shadow(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, 4);
      this.canvas.roundRect(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, t.colors.surfaceContainerHigh);
      this.canvas.text(this.tr("已安装模组", "Installed mods"), this.cardX + 24.0F, this.cardY + 18.0F, Md3Type.TITLE_LARGE, t.colors.onSurface);
      String count = Integer.toString(this.mods.size());
      float tw = this.canvas.textWidth(this.tr("已安装模组", "Installed mods"), Md3Type.TITLE_LARGE);
      this.canvas.roundRect(this.cardX + 24.0F + tw + 10.0F, this.cardY + 18.0F, 28.0F, 22.0F, 11.0F, t.colors.primaryContainer);
      Md3Text.centered(this.canvas, count, this.cardX + 24.0F + tw + 24.0F, this.cardY + 29.0F, Md3Type.LABEL_MEDIUM, t.colors.onPrimaryContainer);
      this.closeButton.surface = t.colors.surfaceContainerHigh;
      this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.closeButton.consumeClick()) {
         this.close();
      }

      this.renderList(t, mouseX, mouseY);
      this.canvas.end();
   }

   private void renderList(Md3Theme t, int mouseX, int mouseY) {
      this.canvas.roundRect(this.listX, this.listY, this.listW, this.listH, 16.0F, t.colors.surfaceContainerLow);
      float innerH = this.listH - 8.0F;
      this.scroll.measure(this.mods.size() * 48, innerH);
      this.scroll.update(this.dt);
      this.canvas.pushClip(this.listX + 4.0F, this.listY + 4.0F, this.listW - 8.0F, innerH);
      this.rowYs = new float[this.mods.size()];
      float base = this.listY + 4.0F - this.scroll.value;

      for (int i = 0; i < this.mods.size(); i++) {
         float y = base + i * 48;
         this.rowYs[i] = y;
         if (!(y + 48.0F < this.listY) && !(y > this.listY + this.listH)) {
            ModListScreen.Entry entry = this.mods.get(i);
            Md3ListItem item = new Md3ListItem();
            item.headline = entry.name;
            item.supporting = this.supporting(entry);
            item.leadingText = entry.name;
            item.trailingText = entry.version;
            item.setBounds(this.listX + 6.0F, y, this.listW - 12.0F, 48.0F);
            item.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
            if (i < this.mods.size() - 1) {
               this.canvas.fill(this.listX + 30.0F, y + 48.0F - 1.0F, this.listW - 60.0F, 1.0F, t.colors.outlineVariant);
            }
         }
      }

      this.canvas.popClip();
      this.scroll.renderBar(this.canvas, t, this.listX + this.listW - 8.0F, this.listY + 8.0F, 4.0F);
   }

   private String supporting(ModListScreen.Entry entry) {
      List<String> notes = new ArrayList<>();
      if (isLoader(entry)) {
         notes.add(this.loaderNote(entry));
      }

      if (isOptiFine(entry)) {
         notes.add(this.optiFineNote());
      }

      if (isMinecraft(entry)) {
         notes.add(this.javaNote());
      }

      ModrinthUpdater.Info info = ModrinthUpdater.get(entry.id);
      if (info.state == ModrinthUpdater.State.CHECKING) {
         notes.add(this.tr("检查中…", "checking…"));
      } else if (info.state == ModrinthUpdater.State.OUTDATED) {
         notes.add(this.tr("Modrinth 可更新 " + info.latest + " · 点击打开", "Modrinth update " + info.latest + " · click to open"));
      } else {
         CurseForgeUpdater.Info cf = CurseForgeUpdater.get(entry.id);
         if (cf.state == CurseForgeUpdater.State.OUTDATED) {
            notes.add(this.tr("CurseForge " + cf.latest + " · 点击打开", "CurseForge " + cf.latest + " · click to open"));
         }
      }

      if (notes.isEmpty()) {
         return entry.id;
      } else {
         StringBuilder out = new StringBuilder(entry.id);

         for (String note : notes) {
            if (note != null && !note.isEmpty()) {
               out.append("  ·  ").append(note);
            }
         }

         return out.toString();
      }
   }

   private String loaderNote(ModListScreen.Entry entry) {
      EnvCheck.Status status = EnvCheck.loader();
      String text = ClientConfig.langIndex() == 0 ? status.zh : status.en;
      if (text == null) {
         return "";
      } else {
         String zhHead = "Legacy Fabric 加载器 ";
         String enHead = "Legacy Fabric loader ";
         if (text.startsWith(zhHead)) {
            text = text.substring(zhHead.length());
         } else if (text.startsWith(enHead)) {
            text = text.substring(enHead.length());
         }

         String versionHead = entry.version + " · ";
         if (text.startsWith(versionHead)) {
            text = text.substring(versionHead.length());
         }

         return text;
      }
   }

   private String optiFineNote() {
      OptiFineUpdater.Info info = OptiFineUpdater.get();
      if (info == null) {
         return this.tr("OptiFine 检查中…", "checking OptiFine…");
      } else if (info.local == null) {
         return this.tr("OptiFine 未检测到本体", "OptiFine not installed");
      } else if (info.latest == null) {
         return this.tr("OptiFine " + info.local + " · 官网版本未知", "OptiFine " + info.local + " · official version unknown");
      } else {
         return info.outdated
            ? this.tr("OptiFine " + info.local + " · 有新版 " + stripHdU(info.latest), "OptiFine " + info.local + " · newer " + stripHdU(info.latest))
            : this.tr("OptiFine " + info.local + " · 已是官网最新", "OptiFine " + info.local + " · latest on optifine.net");
      }
   }

   private String javaNote() {
      EnvCheck.Status status = EnvCheck.java();
      String text = ClientConfig.langIndex() == 0 ? status.zh : status.en;
      if (text == null) {
         return "";
      } else {
         if (text.startsWith("Java ")) {
            int sep = text.indexOf(" · ");
            if (sep > 0) {
               String version = text.substring(5, sep);
               String state;
               if (text.contains("过旧")) {
                  state = "过旧";
               } else if (text.contains("较旧")) {
                  state = "较旧";
               } else if (text.contains("推荐版本")) {
                  state = this.tr("推荐版本", "recommended");
               } else if (text.contains("可用")) {
                  state = this.tr("可用", "usable");
               } else {
                  state = this.tr("正常", "ok");
               }

               if (text.contains("推荐版本")) {
                  return "Java " + version + " · " + state;
               }

               return "Java " + version + " · " + state + this.tr(" · 推荐25", " · 25 recommended");
            }
         }

         return text;
      }
   }

   private static boolean isLoader(ModListScreen.Entry entry) {
      String id = entry.id == null ? "" : entry.id.toLowerCase();
      String name = entry.name == null ? "" : entry.name.toLowerCase();
      return id.equals("fabricloader") || name.contains("fabric loader");
   }

   private static boolean isOptiFine(ModListScreen.Entry entry) {
      String id = entry.id == null ? "" : entry.id.toLowerCase();
      String name = entry.name == null ? "" : entry.name.toLowerCase();
      return id.contains("optifine") || id.contains("optifabric") || name.contains("optifine") || name.contains("optifabric");
   }

   private static boolean isMinecraft(ModListScreen.Entry entry) {
      String id = entry.id == null ? "" : entry.id.toLowerCase();
      return id.equals("minecraft");
   }

   private static String stripHdU(String v) {
      return v == null ? "" : v.replace("HD_U_", "");
   }

   @Override
   public void method_1035() {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0) {
         this.scroll.scrollBy(-Integer.signum(wheel) * 52.0F);
      }

      super.method_1035();
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.mouseDown = true;
      }

      if (button == 0) {
         if (!ClientUi.hit(mouseX, mouseY, this.closeButton.x, this.closeButton.y, this.closeButton.w, this.closeButton.h)) {
            if (!this.scroll.press(mouseX, mouseY, this.listX + this.listW - 8.0F, this.listY + 8.0F, 4.0F, 4.0F)) {
               for (int i = 0; i < this.rowYs.length && i < this.mods.size(); i++) {
                  float y = this.rowYs[i];
                  if (!(y < this.listY)
                     && !(y + 48.0F > this.listY + this.listH)
                     && ClientUi.hit(mouseX, mouseY, this.listX + 6.0F, y, this.listW - 12.0F, 48.0F)) {
                     ModListScreen.Entry entry = this.mods.get(i);
                     ModrinthUpdater.Info info = ModrinthUpdater.get(entry.id);
                     if (info.state == ModrinthUpdater.State.OUTDATED && info.page != null) {
                        ModrinthUpdater.openPage(info.page);
                        return;
                     }

                     CurseForgeUpdater.Info cf = CurseForgeUpdater.get(entry.id);
                     if (cf.state == CurseForgeUpdater.State.OUTDATED && cf.page != null) {
                        ModrinthUpdater.openPage(cf.page);
                        return;
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
      this.scroll.release();
   }

   @Override
   protected void method_4259(int mouseX, int mouseY, int button, long lastClick) {
      if (this.scroll.dragging) {
         this.scroll.drag(mouseY, this.listY + 8.0F);
      }
   }

   @Override
   protected void method_1024(char id, int code) {
      if (code == 1) {
         this.close();
      } else if (code == 208) {
         this.scroll.scrollBy(52.0F);
      } else if (code == 200) {
         this.scroll.scrollBy(-52.0F);
      }
   }

   private void close() {
      this.field_1229.method_2928(this.parent);
   }

   private String tr(String zh, String en) {
      return ClientConfig.t(zh, en);
   }

   @Override
   public boolean method_1037() {
      return true;
   }

   private static final class Entry {
      final String name;
      final String id;
      final String version;

      Entry(String name, String id, String version) {
         this.name = name;
         this.id = id;
         this.version = version;
      }
   }
}
