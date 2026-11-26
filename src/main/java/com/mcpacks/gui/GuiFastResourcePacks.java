package com.mcpacks.gui;

import com.mcpacks.async.AsyncIconLoader;
import com.mcpacks.core.PackEntry;
import com.mcpacks.core.PackManager;
import com.mcpacks.core.PackType;
import com.mcpacks.repository.FolderPackRepository;
import com.mcpacks.util.FolderUtils;
import com.mcpacks.util.Lang;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.client.ime.ImeBridge;
import net.client.ui.ClientScroll;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Button;
import net.client.ui.md3.widget.Md3IconButton;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.minecraft.class_1600;
import net.minecraft.class_1653;
import net.minecraft.class_388;
import net.minecraft.class_1657.class_1659;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiFastResourcePacks extends class_388 {
   private int rowH = 56;
   private final class_388 parentScreen;
   private final FolderPackRepository repo;
   private final List<PackEntry> selectedPacks = new ArrayList<>();
   private final List<PackEntry> availablePacks = new ArrayList<>();
   private final Md3Canvas canvas = new Md3Canvas();
   private final Md3TextField search = new Md3TextField();
   private final ClientScroll availableScroll = new ClientScroll();
   private final ClientScroll selectedScroll = new ClientScroll();
   private final List<GuiFastResourcePacks.Crumb> crumbs = new ArrayList<>();
   private final Md3IconButton closeButton = new Md3IconButton();
   private final Md3Button btnDone = new Md3Button(Md3Button.Variant.FILLED, "");
   private final Md3Button btnOpen = new Md3Button(Md3Button.Variant.TONAL, "");
   private final Md3Button btnNew = new Md3Button(Md3Button.Variant.OUTLINED, "");
   private final Md3Button btnRefresh = new Md3Button(Md3Button.Variant.OUTLINED, "");
   private float[] availRowYs = new float[0];
   private float[] selRowYs = new float[0];
   private float cardX;
   private float cardY;
   private float cardH;
   private float availX;
   private float rightX;
   private float colW;
   private float footerY;
   private float availListY;
   private float availListH;
   private float selListY;
   private float selListH;
   private float crumbY;
   private boolean compactHeader;
   private boolean ultraCompact;
   private boolean iconOnlyFooter;
   private boolean searchFocus;
   private boolean isOpeningSubDialog;
   private boolean awaitingIndexRefresh;
   private boolean saved;
   private long lastNanos;
   private float dt;
   private boolean mouseDown;
   private float mouseNowX;
   private float mouseNowY;
   private static final float DRAG_THRESHOLD = 5.0F;
   private boolean dragPending;
   private boolean dragActive;
   private boolean dragFromSelected;
   private PackEntry dragEntry;
   private int dragSourceDisplay;
   private float dragStartX;
   private float dragStartY;
   private int dragMode;
   private int dragSlot;

   public GuiFastResourcePacks(class_388 parentScreen) {
      this.parentScreen = parentScreen;
      this.repo = new FolderPackRepository(class_1600.method_2965());
   }

   @Override
   public void method_1044() {
      Keyboard.enableRepeatEvents(true);
      this.field_1232.clear();
      this.saved = false;
      this.isOpeningSubDialog = false;
      this.clearResourceDrag();
      AsyncIconLoader.getInstance().initDefaultTextures();
      if (!PackManager.getInstance().getIndexer().hasIndex()) {
         PackManager.getInstance().refresh();
      }

      this.ultraCompact = this.field_1230 < 560.0F || this.field_1231 < 300.0F;
      float margin = this.ultraCompact ? 8.0F : 12.0F;
      float gap = this.ultraCompact ? 8.0F : 12.0F;
      this.cardX = margin;
      this.cardY = margin;
      this.cardH = this.field_1231 - margin * 2.0F;
      this.colW = (this.field_1230 - margin * 2.0F - gap) / 2.0F;
      this.availX = this.cardX;
      this.rightX = this.cardX + this.colW + gap;
      this.footerY = this.cardY + this.cardH - (this.ultraCompact ? 44.0F : 50.0F);
      this.compactHeader = this.colW < 390.0F;
      this.rowH = this.ultraCompact ? 40 : (this.compactHeader ? 46 : 56);
      this.crumbY = this.cardY + (this.ultraCompact ? 80.0F : (this.compactHeader ? 78.0F : 64.0F));
      this.availListY = this.cardY + (this.ultraCompact ? 106.0F : (this.compactHeader ? 98.0F : 112.0F));
      this.availListH = Math.max(30.0F, this.footerY - (this.ultraCompact ? 6.0F : 8.0F) - this.availListY);
      this.selListY = this.cardY + (this.ultraCompact ? 56.0F : (this.compactHeader ? 50.0F : 52.0F));
      this.selListH = Math.max(30.0F, this.footerY - (this.ultraCompact ? 20.0F : 28.0F) - this.selListY);
      this.search.variant = Md3TextField.Variant.OUTLINED;
      this.search.label = "";
      this.search.supporting = "";
      this.search.leadingIcon = "search";
      this.search.maxLength = 40;
      float searchW = this.compactHeader ? this.colW - (this.ultraCompact ? 24.0F : 36.0F) : Math.min(210.0F, this.colW * 0.46F);
      float searchX = this.compactHeader ? this.availX + (this.ultraCompact ? 12.0F : 18.0F) : this.availX + this.colW - 18.0F - searchW;
      float searchY = this.cardY + (this.compactHeader ? 40.0F : 12.0F);
      this.search.setBounds(searchX, searchY, searchW, this.ultraCompact ? 28.0F : (this.compactHeader ? 34.0F : 40.0F));
      this.closeButton.variant = Md3IconButton.Variant.STANDARD;
      this.closeButton.icon = "close";
      float closeSize = this.ultraCompact ? 32.0F : 38.0F;
      this.closeButton
         .setBounds(
            this.rightX + this.colW - (this.ultraCompact ? 8.0F : 10.0F) - closeSize, this.cardY + (this.ultraCompact ? 8.0F : 10.0F), closeSize, closeSize
         );
      float footerPad = this.ultraCompact ? 10.0F : 18.0F;
      float footerH = this.ultraCompact ? 34.0F : 40.0F;
      float leftAvail = this.colW - footerPad * 2.0F;
      this.iconOnlyFooter = leftAvail < 286.0F;
      if (this.iconOnlyFooter) {
         float bs = this.ultraCompact ? 34.0F : 40.0F;
         this.btnOpen.setBounds(this.availX + footerPad, this.footerY, bs, footerH);
         this.btnNew.setBounds(this.btnOpen.x + bs + 8.0F, this.footerY, bs, footerH);
         this.btnRefresh.setBounds(this.btnNew.x + bs + 8.0F, this.footerY, bs, footerH);
      } else {
         float normal = 306.0F;
         float scale = leftAvail >= normal ? 1.0F : Math.max(0.7F, (leftAvail - 16.0F) / 290.0F);
         float w1 = 104.0F * scale;
         float w2 = 110.0F * scale;
         float w3 = 76.0F * scale;
         this.btnOpen.setBounds(this.availX + footerPad, this.footerY, w1, footerH);
         this.btnNew.setBounds(this.btnOpen.x + w1 + 8.0F, this.footerY, w2, footerH);
         this.btnRefresh.setBounds(this.btnNew.x + w2 + 8.0F, this.footerY, w3, footerH);
      }

      float rightAvail = this.colW - footerPad * 2.0F;
      float rightGap = 8.0F;
      float doneW = Math.min(100.0F, Math.max(70.0F, rightAvail * 0.47F));
      this.btnDone.setBounds(this.rightX + this.colW - footerPad - doneW, this.footerY, doneW, footerH);
      this.btnDone.label = Lang.s("Done", "完成");
      this.btnOpen.label = Lang.s("Open Folder", "打开文件夹");
      this.btnNew.label = Lang.s("New Folder", "新建文件夹");
      this.btnRefresh.label = Lang.s("Refresh", "刷新");
      if (this.iconOnlyFooter) {
         this.btnOpen.label = "";
         this.btnNew.label = "";
         this.btnRefresh.label = "";
      }

      this.btnOpen.icon = "folder_open";
      this.btnNew.icon = "create_new_folder";
      this.btnRefresh.icon = "refresh";
      this.btnDone.icon = "check";
      if (this.selectedPacks.isEmpty()) {
         this.loadInitialSelectedPacks();
      } else {
         this.rebuildSelectedList();
      }

      this.refreshAvailableEntries();
      this.awaitingIndexRefresh = PackManager.getInstance().getIndexer().isIndexing();
      this.availableScroll.jump(0.0F);
      this.selectedScroll.jump(0.0F);
   }

   private void loadInitialSelectedPacks() {
      this.selectedPacks.clear();
      File rootDir = PackManager.getInstance().getRootDir();

      for (Object obj : this.repo.getSelectedRepositoryEntries()) {
         if (obj instanceof class_1659 entry) {
            File packFile = FolderPackRepository.getFileFromEntry(entry);
            if (packFile != null && packFile.exists()) {
               int depth = FolderUtils.calculateDepth(rootDir, packFile.getParentFile());
               PackEntry pe = new PackEntry(PackType.PACK, packFile, rootDir, depth);
               pe.setRepositoryEntry(entry);
               this.selectedPacks.add(pe);
            }
         }
      }

      this.rebuildSelectedList();
   }

   public void refreshAvailableEntries() {
      this.availablePacks.clear();
      PackManager pm = PackManager.getInstance();
      String query = this.search.text.trim();

      for (PackEntry entry : query.length() >= 2 ? pm.getIndexer().searchGlobal(query) : pm.getEntriesInCurrentFolder(query)) {
         if (!entry.isPack() || !this.isPackAlreadySelected(entry)) {
            this.availablePacks.add(entry);
         }
      }

      this.btnNew.enabled = FolderUtils.canCreateSubfolder(pm.getRootDir(), pm.getCurrentDir());
   }

   public void rebuildSelectedList() {
      this.selRowYs = new float[this.selectedPacks.size()];
   }

   private boolean isPackAlreadySelected(PackEntry pack) {
      if (pack != null && pack.getFile() != null) {
         String path = pack.getFile().getAbsolutePath();

         for (PackEntry selected : this.selectedPacks) {
            if (selected.getFile() != null && selected.getFile().getAbsolutePath().equalsIgnoreCase(path)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void selectPack(PackEntry entry) {
      if (entry != null && entry.isPack() && !this.isPackAlreadySelected(entry)) {
         this.selectedPacks.add(entry);
         this.rebuildSelectedList();
         this.refreshAvailableEntries();
      }
   }

   public void unselectPack(PackEntry entry) {
      if (entry != null) {
         this.selectedPacks.remove(entry);
         this.rebuildSelectedList();
         this.refreshAvailableEntries();
      }
   }

   public void movePackUp(int index) {
      int internal = this.selectedPacks.size() - 1 - index;
      if (internal >= 0 && internal < this.selectedPacks.size() - 1) {
         Collections.swap(this.selectedPacks, internal, internal + 1);
         this.rebuildSelectedList();
      }
   }

   public void movePackDown(int index) {
      int internal = this.selectedPacks.size() - 1 - index;
      if (internal > 0 && internal < this.selectedPacks.size()) {
         Collections.swap(this.selectedPacks, internal, internal - 1);
         this.rebuildSelectedList();
      }
   }

   private void cancelAndClose() {
      this.saved = true;
      this.field_1229.method_2928(this.parentScreen);
   }

   public void saveAndClose() {
      if (!this.saved) {
         this.saved = true;
         this.repo.applySelectedPacks(this.selectedPacks);
      }

      this.field_1229.method_2928(this.parentScreen);
   }

   @Override
   public void method_1033() {
      if (this.awaitingIndexRefresh && !PackManager.getInstance().getIndexer().isIndexing()) {
         this.awaitingIndexRefresh = false;
         this.refreshAvailableEntries();
      }

      super.method_1033();
   }

   @Override
   public void method_1030() {
      Keyboard.enableRepeatEvents(false);
      if (!this.saved && !this.isOpeningSubDialog) {
         this.saved = true;
         this.repo.applySelectedPacks(this.selectedPacks);
      }

      this.isOpeningSubDialog = false;
      super.method_1030();
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, t.colors.surface);
      this.canvas.roundRect(this.availX, this.cardY, this.colW, this.cardH, 24.0F, t.colors.surfaceContainerLow);
      this.canvas.roundRect(this.rightX, this.cardY, this.colW, this.cardH, 24.0F, t.colors.surfaceContainerLow);
      this.renderHeader(t, mouseX, mouseY);
      this.renderBreadcrumbs(t, mouseX, mouseY);
      this.renderAvailable(t, mouseX, mouseY);
      this.renderSelected(t, mouseX, mouseY);
      this.renderFooter(t, mouseX, mouseY);
      this.renderDragGhost(t);
      this.canvas.end();
   }

   private void renderHeader(Md3Theme t, int mouseX, int mouseY) {
      this.canvas.text(Lang.s("Resource Packs", "资源包"), this.availX + 18.0F, this.cardY + 14.0F, Md3Type.TITLE_LARGE, t.colors.onSurface);
      String count = this.selectedPacks.size() + " / " + (this.selectedPacks.size() + this.availablePacks.size());
      float tw = this.canvas.textWidth(Lang.s("Resource Packs", "资源包"), Md3Type.TITLE_LARGE);
      this.canvas
         .roundRect(
            this.availX + 18.0F + tw + 10.0F,
            this.cardY + 14.0F,
            this.canvas.textWidth(count, Md3Type.LABEL_MEDIUM) + 16.0F,
            22.0F,
            11.0F,
            t.colors.primaryContainer
         );
      Md3Text.centered(
         this.canvas,
         count,
         this.availX + 18.0F + tw + 18.0F + this.canvas.textWidth(count, Md3Type.LABEL_MEDIUM) / 2.0F,
         this.cardY + 25.0F,
         Md3Type.LABEL_MEDIUM,
         t.colors.onPrimaryContainer
      );
      this.search.trailingIcon = this.search.text.isEmpty() ? null : "close";
      this.search.surface = t.colors.surfaceContainer;
      this.search.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.searchFocus) {
         ImeBridge.onFieldRender((int)this.search.x + 16, (int)this.search.y + 28, (int)this.search.w - 32, this.search.text, this.search.text.length(), true);
      }

      if (this.search.text.isEmpty()) {
         this.canvas
            .text(
               Lang.s("Search packs...", "搜索资源包..."),
               this.search.x + (this.ultraCompact ? 36.0F : 44.0F),
               Md3Text.topForLineCenter(this.search.y + this.search.h / 2.0F, Md3Type.BODY_MEDIUM),
               Md3Type.BODY_MEDIUM,
               Md3Colors.alpha(t.colors.onSurfaceVariant, 0.72F)
            );
      }
   }

   private void renderBreadcrumbs(Md3Theme t, int mouseX, int mouseY) {
      this.crumbs.clear();
      float y = this.crumbY;
      this.canvas.text(Lang.s("Path: ", "路径: "), this.availX + 18.0F, y + 7.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      float x = this.availX + 18.0F + this.canvas.textWidth(Lang.s("Path: ", "路径: "), Md3Type.BODY_SMALL) + 4.0F;
      PackManager pm = PackManager.getInstance();
      List<PackManager.BreadcrumbNode> nodes = pm.getBreadcrumbs();
      float limit = this.availX + this.colW - 96.0F;

      for (int i = 0; i < nodes.size(); i++) {
         PackManager.BreadcrumbNode node = nodes.get(i);
         String label = ClientUi.fit(this.canvas, i == 0 ? Lang.s("Root", "根目录") : node.name, Md3Type.LABEL_MEDIUM, 110.0F);
         float pw = this.canvas.textWidth(label, Md3Type.LABEL_MEDIUM) + 16.0F;
         if (x + pw > limit && i > 0) {
            this.canvas.text("…", x, y + 6.0F, Md3Type.LABEL_MEDIUM, t.colors.onSurfaceVariant);
            break;
         }

         boolean current = i == nodes.size() - 1;
         boolean hot = ClientUi.hit(mouseX, mouseY, x, y, pw, 22.0F);
         int bg = current ? t.colors.primaryContainer : (hot ? t.colors.surfaceContainerHighest : t.colors.surfaceContainer);
         this.canvas.roundRect(x, y, pw, 22.0F, 11.0F, bg);
         Md3Text.centered(this.canvas, label, x + pw / 2.0F, y + 11.0F, Md3Type.LABEL_MEDIUM, current ? t.colors.onPrimaryContainer : t.colors.onSurfaceVariant);
         GuiFastResourcePacks.Crumb crumb = new GuiFastResourcePacks.Crumb();
         crumb.x = x;
         crumb.y = y;
         crumb.w = pw;
         crumb.h = 22.0F;
         crumb.dir = node.targetDir;
         this.crumbs.add(crumb);
         x += pw + 4.0F;
         if (i < nodes.size() - 1) {
            this.canvas.icon("chevron_right", x, y + 5.0F, 12.0F, t.colors.onSurfaceVariant);
            x += 12.0F;
         }
      }

      String depth = Lang.s("Layer ", "层级 ") + (pm.getCurrentDepth() + 1) + "/3";
      this.canvas
         .text(
            depth,
            this.availX + this.colW - 18.0F - this.canvas.textWidth(depth, Md3Type.LABEL_MEDIUM),
            y + 7.0F,
            Md3Type.LABEL_MEDIUM,
            t.colors.onSurfaceVariant
         );
   }

   private void renderAvailable(Md3Theme t, int mouseX, int mouseY) {
      if (!this.compactHeader) {
         float sectionY = this.availListY - 24.0F;
         String section = Lang.s("Available Packs & Folders", "可用资源包");
         this.canvas.text(section, this.availX + 18.0F, sectionY, Md3Type.TITLE_SMALL, t.colors.onSurface);
         String n = Integer.toString(this.availablePacks.size());
         this.canvas
            .text(n, this.availX + this.colW - 18.0F - this.canvas.textWidth(n, Md3Type.LABEL_MEDIUM), sectionY, Md3Type.LABEL_MEDIUM, t.colors.primary);
      }

      this.availableScroll.measure(this.availablePacks.size() * this.rowH, this.availListH);
      this.availableScroll.update(this.dt);
      this.canvas.pushClip(this.availX + 8.0F, this.availListY, this.colW - 20.0F, this.availListH);
      this.availRowYs = new float[this.availablePacks.size()];
      if (this.availablePacks.isEmpty()) {
         this.emptyState(t, this.availX + 8.0F, this.availListY, this.colW - 20.0F, this.availListH, Lang.s("No resource packs found", "未找到资源包"), "folder");
      } else {
         float base = this.availListY - this.availableScroll.value;

         for (int i = 0; i < this.availablePacks.size(); i++) {
            float y = base + i * this.rowH;
            this.availRowYs[i] = y;
            if (!(y + this.rowH < this.availListY) && !(y > this.availListY + this.availListH)) {
               this.renderAvailableRow(t, this.availablePacks.get(i), y, this.colW - 28.0F, mouseX, mouseY);
            }
         }
      }

      if (this.dragActive && this.dragMode == 2) {
         float slotY = this.availListY - this.availableScroll.value + this.dragSlot * this.rowH;
         slotY = Math.max(this.availListY + 1.0F, Math.min(this.availListY + this.availListH - 2.0F, slotY));
         this.canvas.fill(this.availX + 12.0F, slotY - 1.0F, this.colW - 28.0F, 2.0F, t.colors.primary);
      }

      this.canvas.popClip();
      this.availableScroll.renderBar(this.canvas, t, this.availX + this.colW - 10.0F, this.availListY, 4.0F);
   }

   private void renderSelected(Md3Theme t, int mouseX, int mouseY) {
      String title = Lang.s("Loaded Resource Packs", "已加载资源包");
      this.canvas.text(title, this.rightX + 18.0F, this.cardY + 14.0F, Md3Type.TITLE_LARGE, t.colors.onSurface);
      float tw = this.canvas.textWidth(title, Md3Type.TITLE_LARGE);
      String n = Integer.toString(this.selectedPacks.size());
      float chipX = this.rightX + 18.0F + tw + 10.0F;
      this.canvas.roundRect(chipX, this.cardY + 14.0F, this.canvas.textWidth(n, Md3Type.LABEL_MEDIUM) + 16.0F, 22.0F, 11.0F, t.colors.primaryContainer);
      Md3Text.centered(
         this.canvas,
         n,
         chipX + 8.0F + this.canvas.textWidth(n, Md3Type.LABEL_MEDIUM) / 2.0F,
         this.cardY + 25.0F,
         Md3Type.LABEL_MEDIUM,
         t.colors.onPrimaryContainer
      );
      this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.selectedScroll.measure(this.selectedPacks.size() * this.rowH, this.selListH);
      this.selectedScroll.update(this.dt);
      this.canvas.pushClip(this.rightX + 8.0F, this.selListY, this.colW - 20.0F, this.selListH);
      this.selRowYs = new float[this.selectedPacks.size()];
      if (this.selectedPacks.isEmpty()) {
         this.emptyState(
            t, this.rightX + 8.0F, this.selListY, this.colW - 20.0F, this.selListH, Lang.s("Select packs from the left list", "从左侧列表选择资源包"), "inventory_2"
         );
      } else {
         float base = this.selListY - this.selectedScroll.value;

         for (int i = 0; i < this.selectedPacks.size(); i++) {
            float y = base + i * this.rowH;
            this.selRowYs[i] = y;
            if (!(y + this.rowH < this.selListY) && !(y > this.selListY + this.selListH)) {
               this.renderSelectedRow(t, this.selectedPacks.get(this.selectedPacks.size() - 1 - i), i, y, this.colW - 28.0F, mouseX, mouseY);
            }
         }
      }

      if (this.dragActive && this.dragMode == 1) {
         float slotY = this.selListY - this.selectedScroll.value + this.dragSlot * this.rowH;
         slotY = Math.max(this.selListY + 1.0F, Math.min(this.selListY + this.selListH - 2.0F, slotY));
         this.canvas.fill(this.rightX + 12.0F, slotY - 1.0F, this.colW - 28.0F, 2.0F, t.colors.primary);
      }

      this.canvas.popClip();
      this.selectedScroll.renderBar(this.canvas, t, this.rightX + this.colW - 10.0F, this.selListY, 4.0F);
   }

   private void emptyState(Md3Theme t, float x, float y, float w, float h, String label, String icon) {
      this.canvas.icon(icon, x + w / 2.0F - 16.0F, y + h / 2.0F - 30.0F, 32.0F, Md3Colors.alpha(t.colors.onSurfaceVariant, 0.55F));
      Md3Text.centered(this.canvas, label, x + w / 2.0F, y + h / 2.0F + 6.0F, Md3Type.BODY_MEDIUM, t.colors.onSurfaceVariant);
   }

   private void renderAvailableRow(Md3Theme t, PackEntry entry, float y, float w, int mouseX, int mouseY) {
      float x = this.availX + 12.0F;
      boolean hot = ClientUi.hit(mouseX, mouseY, x, y, w, this.rowH);
      if (hot) {
         this.canvas.roundRect(x, y, w, this.rowH, 12.0F, Md3Colors.alpha(t.colors.onSurface, 0.055F));
      }

      float iconSize = this.rowH >= 54 ? 38.0F : 32.0F;
      this.drawPackIcon(entry, x + 8.0F, y + (this.rowH - iconSize) / 2.0F, iconSize);
      float textX = x + (this.rowH >= 54 ? 54.0F : 48.0F);
      float textW = w - (textX - x) - 42.0F;
      String name;
      int nameColor;
      if (entry.isParentNav()) {
         name = Lang.s(".. (Up to Parent Folder)", ".. (返回上级文件夹)");
         nameColor = t.colors.tertiary;
      } else if (entry.isFolder()) {
         name = Lang.s("[Dir] ", "[文件夹] ") + entry.getName();
         nameColor = t.colors.tertiary;
      } else {
         name = entry.getName();
         nameColor = entry.isVersionCompatible() ? t.colors.onSurface : t.colors.error;
      }

      this.canvas.text(ClientUi.fit(this.canvas, name, Md3Type.BODY_LARGE, textW), textX, y + (this.rowH >= 54 ? 9.0F : 6.0F), Md3Type.BODY_LARGE, nameColor);
      String desc;
      if (entry.isParentNav()) {
         desc = Lang.s("Click to go up one folder level", "点击返回上一层文件夹");
      } else if (entry.isFolder()) {
         desc = entry.getDescription();
         if (desc == null || desc.isEmpty()) {
            desc = Lang.s("Folder", "文件夹");
         }
      } else {
         desc = entry.getDescription();
         if (desc == null || desc.isEmpty()) {
            desc = Lang.s("No description", "无描述");
         }
      }

      this.canvas
         .text(
            ClientUi.fit(this.canvas, desc, Md3Type.BODY_MEDIUM, textW),
            textX,
            y + (this.rowH >= 54 ? 30.0F : 23.0F),
            Md3Type.BODY_MEDIUM,
            t.colors.onSurfaceVariant
         );
      float buttonSize = this.rowH >= 54 ? 30.0F : 26.0F;
      float buttonX = x + w - buttonSize - 10.0F;
      float buttonY = y + (this.rowH - buttonSize) / 2.0F;
      String action = entry.isPack() ? "add" : "chevron_right";
      int actionColor = entry.isPack() ? t.colors.primary : t.colors.onSurfaceVariant;
      if (!entry.isParentNav() && !entry.isFolder()) {
         this.canvas.roundRect(buttonX, buttonY, buttonSize, buttonSize, buttonSize / 2.0F, hot ? t.colors.primary : t.colors.surfaceContainerHighest);
         this.canvas.icon(action, buttonX + 3.0F, buttonY + 3.0F, buttonSize - 6.0F, hot ? t.colors.onPrimary : actionColor);
      } else {
         this.canvas.icon(action, buttonX + 3.0F, buttonY + 3.0F, buttonSize - 6.0F, actionColor);
      }
   }

   private void renderSelectedRow(Md3Theme t, PackEntry entry, int displayIndex, float y, float w, int mouseX, int mouseY) {
      float x = this.rightX + 12.0F;
      boolean hot = ClientUi.hit(mouseX, mouseY, x, y, w, this.rowH);
      if (hot) {
         this.canvas.roundRect(x, y, w, this.rowH, 12.0F, Md3Colors.alpha(t.colors.onSurface, 0.055F));
      }

      float iconSize = this.rowH >= 54 ? 38.0F : 32.0F;
      this.drawPackIcon(entry, x + 8.0F, y + (this.rowH - iconSize) / 2.0F, iconSize);
      float bs = this.rowH >= 54 ? 24.0F : 20.0F;
      float gap = 2.0F;
      float rx = x + w - bs - 4.0F;
      float bx = rx - gap - bs * 2.0F - gap;
      float by = y + (this.rowH - bs) / 2.0F;
      float textX = x + (this.rowH >= 54 ? 54.0F : 48.0F);
      float textW = w - (textX - x) - (bs * 3.0F + gap * 2.0F + 12.0F);
      this.canvas
         .text(
            ClientUi.fit(this.canvas, entry.getName(), Md3Type.BODY_LARGE, textW),
            textX,
            y + (this.rowH >= 54 ? 9.0F : 6.0F),
            Md3Type.BODY_LARGE,
            t.colors.onSurface
         );
      String desc = entry.getDescription();
      if (desc == null || desc.isEmpty()) {
         desc = entry.isFolder() ? Lang.s("Folder", "文件夹") : Lang.s("Resource pack", "资源包");
      }

      this.canvas
         .text(
            ClientUi.fit(this.canvas, desc, Md3Type.BODY_MEDIUM, textW),
            textX,
            y + (this.rowH >= 54 ? 30.0F : 23.0F),
            Md3Type.BODY_MEDIUM,
            t.colors.onSurfaceVariant
         );
      boolean canUp = displayIndex > 0;
      boolean canDown = displayIndex < this.selectedPacks.size() - 1;
      this.drawMiniIconButton(t, "keyboard_arrow_up", bx, by, bs, canUp, mouseX, mouseY);
      this.drawMiniIconButton(t, "expand_more", bx + bs + gap, by, bs, canDown, mouseX, mouseY);
      boolean removeHot = ClientUi.hit(mouseX, mouseY, rx, by, bs, bs);
      if (removeHot) {
         this.canvas.roundRect(rx, by, bs, bs, bs / 2.0F, Md3Colors.alpha(t.colors.error, 0.1F));
      }

      this.canvas.icon("close", rx + bs / 2.0F - 9.0F, by + bs / 2.0F - 9.0F, 18.0F, removeHot ? t.colors.error : t.colors.onSurfaceVariant);
   }

   private void drawMiniIconButton(Md3Theme t, String icon, float x, float y, float size, boolean enabled, int mouseX, int mouseY) {
      boolean hot = enabled && ClientUi.hit(mouseX, mouseY, x, y, size, size);
      if (hot) {
         this.canvas.roundRect(x, y, size, size, size / 2.0F, t.colors.surfaceContainerHighest);
      }

      float glyph = size - 4.0F;
      this.canvas
         .icon(
            icon, x + 2.0F, y + 2.0F, glyph, enabled ? (hot ? t.colors.primary : t.colors.onSurfaceVariant) : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.3F)
         );
   }

   private void renderFooter(Md3Theme t, int mouseX, int mouseY) {
      this.btnDone.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.btnOpen.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.btnNew.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.btnRefresh.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.selectedPacks.size() > 1) {
         Md3Text.centered(
            this.canvas,
            Lang.s("Top of the list has the highest priority", "列表越靠上优先级越高"),
            this.rightX + this.colW / 2.0F,
            this.footerY - 12.0F,
            Md3Type.BODY_SMALL,
            t.colors.onSurfaceVariant
         );
      }
   }

   private void drawPackIcon(PackEntry entry, float x, float y, float size) {
      class_1653 loc = entry.getIconLocation();
      if (!entry.isIconLoaded() || !entry.isMetadataLoaded()) {
         AsyncIconLoader.getInstance().requestIconAndMeta(entry);
      }

      if (loc == null && this.field_1229 != null) {
         loc = AsyncIconLoader.getInstance().getDefaultPackTextureLoc();
      }

      if (loc != null) {
         try {
            this.field_1229.method_5570().method_5847(loc);
            GL11.glEnable(3553);
            int id = GL11.glGetInteger(32873);
            float tw = Math.max(1, entry.getIconTextureWidth());
            float th = Math.max(1, entry.getIconTextureHeight());
            float dw = size;
            float dh = size;
            if (tw > th) {
               dh = size * th / tw;
            } else {
               dw = size * tw / th;
            }

            this.canvas.image(id, x + (size - dw) / 2.0F, y + (size - dh) / 2.0F, dw, dh, 0.0F, 0.0F, 1.0F, 1.0F, -1);
            return;
         } catch (Throwable var11) {
         }
      }

      this.canvas.roundRect(x, y, size, size, 10.0F, ClientTheme.get().colors.surfaceContainerHighest);
      String icon = !entry.isFolder() && !entry.isParentNav() ? "image" : "folder";
      this.canvas.icon(icon, x + 7.0F, y + 7.0F, size - 14.0F, ClientTheme.get().colors.onSurfaceVariant);
   }

   private boolean inAvailableList(float x, float y) {
      return ClientUi.hit(x, y, this.availX + 4.0F, this.availListY, this.colW - 8.0F, this.availListH);
   }

   private boolean inSelectedList(float x, float y) {
      return ClientUi.hit(x, y, this.rightX + 4.0F, this.selListY, this.colW - 8.0F, this.selListH);
   }

   private int slotAt(float y, float listY, float scroll, int count) {
      float local = y - listY + scroll;
      if (local < 0.0F) {
         return 0;
      } else {
         int slot = (int)(local / this.rowH);
         if (local - slot * this.rowH > this.rowH * 0.5F) {
            slot++;
         }

         return Math.max(0, Math.min(count, slot));
      }
   }

   private void beginResourceDrag(boolean fromSelected, int displayIndex, PackEntry entry, float x, float y) {
      this.dragPending = true;
      this.dragActive = false;
      this.dragFromSelected = fromSelected;
      this.dragEntry = entry;
      this.dragSourceDisplay = displayIndex;
      this.dragStartX = x;
      this.dragStartY = y;
      this.dragMode = fromSelected ? 1 : 0;
      this.dragSlot = fromSelected ? displayIndex : 0;
      this.mouseDown = true;
   }

   private void clearResourceDrag() {
      this.dragPending = false;
      this.dragActive = false;
      this.dragEntry = null;
      this.dragMode = 0;
      this.dragSlot = 0;
   }

   private void updateResourceDragTarget() {
      if (this.dragActive) {
         if (this.inSelectedList(this.mouseNowX, this.mouseNowY)) {
            this.dragMode = 1;
            this.dragSlot = this.slotAt(this.mouseNowY, this.selListY, this.selectedScroll.value, this.selectedPacks.size());
         } else if (this.inAvailableList(this.mouseNowX, this.mouseNowY)) {
            this.dragMode = 2;
            this.dragSlot = this.slotAt(this.mouseNowY, this.availListY, this.availableScroll.value, this.availablePacks.size());
         } else {
            this.dragMode = 0;
         }
      }
   }

   private void autoScrollDuringDrag() {
      if (this.dragActive) {
         if (this.inSelectedList(this.mouseNowX, this.mouseNowY) || this.dragMode == 1) {
            if (this.mouseNowY < this.selListY + 18.0F) {
               this.selectedScroll.scrollBy(-12.0F);
            } else if (this.mouseNowY > this.selListY + this.selListH - 18.0F) {
               this.selectedScroll.scrollBy(12.0F);
            }
         }

         if (this.inAvailableList(this.mouseNowX, this.mouseNowY) || this.dragMode == 2) {
            if (this.mouseNowY < this.availListY + 18.0F) {
               this.availableScroll.scrollBy(-12.0F);
            } else if (this.mouseNowY > this.availListY + this.availListH - 18.0F) {
               this.availableScroll.scrollBy(12.0F);
            }
         }
      }
   }

   private void finishResourceDrag(int mouseX, int mouseY) {
      if (this.dragPending) {
         this.mouseNowX = mouseX;
         this.mouseNowY = mouseY;
         if (!this.dragActive) {
            if (!this.dragFromSelected && this.dragEntry != null && this.dragEntry.isPack()) {
               this.selectPack(this.dragEntry);
               this.selectedScroll.jump(0.0F);
            }

            this.clearResourceDrag();
         } else {
            this.updateResourceDragTarget();
            if (this.dragMode == 1) {
               int slot = Math.max(0, Math.min(this.selectedPacks.size(), this.dragSlot));
               if (this.dragFromSelected) {
                  int source = this.selectedPacks.size() - 1 - this.dragSourceDisplay;
                  if (source >= 0 && source < this.selectedPacks.size() && this.selectedPacks.get(source) == this.dragEntry) {
                     this.selectedPacks.remove(source);
                     int n = this.selectedPacks.size();
                     int insert = Math.max(0, Math.min(n, n - slot));
                     this.selectedPacks.add(insert, this.dragEntry);
                     this.rebuildSelectedList();
                     this.refreshAvailableEntries();
                  }
               } else if (this.dragEntry != null && this.dragEntry.isPack() && !this.isPackAlreadySelected(this.dragEntry)) {
                  int n = this.selectedPacks.size();
                  int insert = Math.max(0, Math.min(n, n - slot));
                  this.selectedPacks.add(insert, this.dragEntry);
                  this.rebuildSelectedList();
                  this.refreshAvailableEntries();
                  this.selectedScroll.jump(0.0F);
               }
            } else if (this.dragMode == 2 && this.dragFromSelected) {
               int source = this.selectedPacks.size() - 1 - this.dragSourceDisplay;
               if (source >= 0 && source < this.selectedPacks.size() && this.selectedPacks.get(source) == this.dragEntry) {
                  this.selectedPacks.remove(source);
                  this.rebuildSelectedList();
                  this.refreshAvailableEntries();
                  this.selectedScroll.jump(0.0F);
               }
            }

            this.clearResourceDrag();
         }
      }
   }

   private void renderDragGhost(Md3Theme t) {
      if (this.dragActive && this.dragEntry != null) {
         String name = this.dragEntry.getName();
         float gw = Math.min(230.0F, this.field_1230 - 24.0F);
         float gx = Math.max(12.0F, Math.min(this.field_1230 - gw - 12.0F, this.mouseNowX + 14.0F));
         float gy = Math.max(12.0F, Math.min(this.field_1231 - 42.0F, this.mouseNowY - 16.0F));
         this.canvas.roundRect(gx, gy, gw, 30.0F, 15.0F, t.colors.surfaceContainerHighest);
         this.canvas.icon("drag_indicator", gx + 8.0F, gy + 7.0F, 16.0F, t.colors.primary);
         this.canvas.text(ClientUi.fit(this.canvas, name, Md3Type.BODY_MEDIUM, gw - 38.0F), gx + 30.0F, gy + 8.0F, Md3Type.BODY_MEDIUM, t.colors.onSurface);
      }
   }

   @Override
   public void method_1035() {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0) {
         if (ClientUi.hit(this.mouseNowX, this.mouseNowY, this.availX, this.availListY, this.colW, this.availListH)) {
            this.availableScroll.scrollBy(-Integer.signum(wheel) * 44.0F);
         } else if (ClientUi.hit(this.mouseNowX, this.mouseNowY, this.rightX, this.selListY, this.colW, this.selListH)) {
            this.selectedScroll.scrollBy(-Integer.signum(wheel) * 44.0F);
         }
      }

      super.method_1035();
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.mouseDown = true;
      }

      if (button == 0) {
         this.mouseNowX = mouseX;
         this.mouseNowY = mouseY;
         if (ClientUi.hit(mouseX, mouseY, this.search.x, this.search.y, this.search.w, this.search.h)) {
            if (!this.search.text.isEmpty() && mouseX >= this.search.x + this.search.w - 36.0F) {
               this.search.clear();
               this.refreshAvailableEntries();
               this.searchFocus = true;
               this.mouseDown = false;
            } else {
               this.searchFocus = true;
               this.search.focused = true;
               this.mouseDown = false;
            }
         } else {
            this.searchFocus = false;
            this.search.focused = false;
            if (ClientUi.hit(mouseX, mouseY, this.closeButton.x, this.closeButton.y, this.closeButton.w, this.closeButton.h)) {
               this.cancelAndClose();
               ClientUi.click();
            } else if (ClientUi.hit(mouseX, mouseY, this.btnDone.x, this.btnDone.y, this.btnDone.w, this.btnDone.h)) {
               this.saveAndClose();
               ClientUi.click();
            } else if (ClientUi.hit(mouseX, mouseY, this.btnOpen.x, this.btnOpen.y, this.btnOpen.w, this.btnOpen.h)) {
               PackManager.getInstance().openCurrentFolderInOS();
               ClientUi.click();
            } else if (this.btnNew.enabled && ClientUi.hit(mouseX, mouseY, this.btnNew.x, this.btnNew.y, this.btnNew.w, this.btnNew.h)) {
               this.isOpeningSubDialog = true;
               this.field_1229.method_2928(new GuiCreateFolderDialog(this));
               ClientUi.click();
            } else if (ClientUi.hit(mouseX, mouseY, this.btnRefresh.x, this.btnRefresh.y, this.btnRefresh.w, this.btnRefresh.h)) {
               PackManager.getInstance().refresh();
               this.refreshAvailableEntries();
               this.awaitingIndexRefresh = true;
               ClientUi.click();
            } else {
               for (GuiFastResourcePacks.Crumb crumb : this.crumbs) {
                  if (ClientUi.hit(mouseX, mouseY, crumb.x, crumb.y, crumb.w, crumb.h)) {
                     PackManager.getInstance().navigateTo(crumb.dir);
                     this.refreshAvailableEntries();
                     this.availableScroll.jump(0.0F);
                     ClientUi.click();
                     return;
                  }
               }

               if (!this.availableScroll.press(mouseX, mouseY, this.availX + this.colW - 10.0F, this.availListY, 4.0F, 4.0F)) {
                  if (!this.selectedScroll.press(mouseX, mouseY, this.rightX + this.colW - 10.0F, this.selListY, 4.0F, 4.0F)) {
                     for (int i = 0; i < this.availRowYs.length && i < this.availablePacks.size(); i++) {
                        float y = this.availRowYs[i];
                        if (!(y < this.availListY)
                           && !(y + this.rowH > this.availListY + this.availListH)
                           && ClientUi.hit(mouseX, mouseY, this.availX + 12.0F, y, this.colW - 28.0F, this.rowH)) {
                           PackEntry entry = this.availablePacks.get(i);
                           float rowX = this.availX + 12.0F;
                           float rowW = this.colW - 28.0F;
                           if (entry.isPack() && mouseX >= rowX + 48.0F && mouseX <= rowX + rowW - 44.0F) {
                              this.beginResourceDrag(false, i, entry, mouseX, mouseY);
                              return;
                           }

                           if (entry.isParentNav()) {
                              PackManager.getInstance().navigateUp();
                              this.refreshAvailableEntries();
                              this.availableScroll.jump(0.0F);
                              ClientUi.click();
                           } else if (entry.isFolder()) {
                              PackManager.getInstance().navigateTo(entry.getFile());
                              this.refreshAvailableEntries();
                              this.availableScroll.jump(0.0F);
                              ClientUi.click();
                           } else if (entry.isPack()) {
                              this.selectPack(entry);
                              this.selectedScroll.jump(0.0F);
                              ClientUi.click();
                           }

                           return;
                        }
                     }

                     for (int ix = 0; ix < this.selRowYs.length && ix < this.selectedPacks.size(); ix++) {
                        float y = this.selRowYs[ix];
                        if (!(y < this.selListY) && !(y + this.rowH > this.selListY + this.selListH)) {
                           PackEntry entryx = this.selectedPacks.get(this.selectedPacks.size() - 1 - ix);
                           float x = this.rightX + 12.0F;
                           float rowWx = this.colW - 28.0F;
                           float bs = this.rowH >= 54 ? 24.0F : 20.0F;
                           float gap = 2.0F;
                           float rx = x + rowWx - bs - 4.0F;
                           float bx = rx - gap - bs * 2.0F - gap;
                           float by = y + (this.rowH - bs) / 2.0F;
                           if (ClientUi.hit(mouseX, mouseY, rx, by, bs, bs)) {
                              this.unselectPack(entryx);
                              ClientUi.click();
                              return;
                           }

                           if (ClientUi.hit(mouseX, mouseY, bx, by, bs, bs) && ix > 0) {
                              this.movePackUp(ix);
                              ClientUi.click();
                              return;
                           }

                           if (ClientUi.hit(mouseX, mouseY, bx + bs + gap, by, bs, bs) && ix < this.selectedPacks.size() - 1) {
                              this.movePackDown(ix);
                              ClientUi.click();
                              return;
                           }

                           if (mouseX >= x + 48.0F && mouseX <= bx - 4.0F) {
                              this.beginResourceDrag(true, ix, entryx, mouseX, mouseY);
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.finishResourceDrag(mouseX, mouseY);
      }

      this.mouseDown = false;
      this.availableScroll.release();
      this.selectedScroll.release();
   }

   @Override
   protected void method_4259(int mouseX, int mouseY, int button, long lastClick) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      if (this.availableScroll.dragging) {
         this.availableScroll.drag(mouseY, this.availListY);
      } else if (this.selectedScroll.dragging) {
         this.selectedScroll.drag(mouseY, this.selListY);
      } else {
         if (this.dragPending) {
            if (!this.dragActive) {
               float dx = mouseX - this.dragStartX;
               float dy = mouseY - this.dragStartY;
               if (dx * dx + dy * dy >= 25.0F) {
                  this.dragActive = true;
               }
            }

            if (this.dragActive) {
               this.updateResourceDragTarget();
               this.autoScrollDuringDrag();
            }
         }
      }
   }

   @Override
   protected void method_1024(char id, int code) {
      if (this.searchFocus) {
         if (!ImeBridge.handleKey(id, code)) {
            if (code == 1) {
               this.searchFocus = false;
               this.search.focused = false;
               this.cancelAndClose();
            } else if (code == 28 || code == 156) {
               this.searchFocus = false;
               this.search.focused = false;
            } else if (code == 14) {
               this.search.backspace();
               this.refreshAvailableEntries();
            } else {
               if (id >= ' ' && id != 127) {
                  this.search.typeChar(id);
                  this.refreshAvailableEntries();
               }
            }
         }
      } else {
         if (code == 1) {
            this.cancelAndClose();
         }
      }
   }

   private static final class Crumb {
      float x;
      float y;
      float w;
      float h;
      File dir;
   }
}
