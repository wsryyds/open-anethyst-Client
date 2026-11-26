package net.client;

import com.amethyst.blur.Blur;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.client.ime.ImeBridge;
import net.client.replay.ReplayClient;
import net.client.replay.gui.ReplayControlScreen;
import net.client.settings.SettingsSpec;
import net.client.ui.ClientIcons;
import net.client.ui.ClientScroll;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3IconButton;
import net.client.ui.md3.widget.Md3Menu;
import net.client.ui.md3.widget.Md3Surfaces;
import net.client.ui.md3.widget.Md3Switch;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.minecraft.class_388;
import net.minecraft.class_624;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ClientScreen extends class_388 {
   private static final int[] PRESETS = new int[]{
      -1, -1092784, -22746, -4520, -10044566, -14244198, -14043402, -10720320, -5552196, -7508381, -7297874, -16777216
   };
   private static final String DEFAULT_MASK = "000010000/000010000/000010000/000010000/111111111/000010000/000010000/000010000/000010000";
   private final Md3Canvas canvas = new Md3Canvas();
   private final ClientScroll scroll = new ClientScroll();
   private final List<ClientScreen.Tile> tiles = new ArrayList<>();
   private final List<ClientScreen.TileBox> tileBoxes = new ArrayList<>();
   private final List<ClientScreen.TabBox> tabBoxes = new ArrayList<>();
   private final Md3TextField search = new Md3TextField();
   private final Map<Integer, Md3Switch> switches = new HashMap<>();
   private final Map<Integer, Boolean> lastSwitchOn = new HashMap<>();
   private final List<ClientScreen.RowBox> boxes = new ArrayList<>();
   private final Md3IconButton themeButton = new Md3IconButton();
   private final Md3IconButton closeButton = new Md3IconButton();
   private final Md3IconButton menuButton = new Md3IconButton();
   private final Md3IconButton backButton = new Md3IconButton();
   private final Md3Menu appMenu = new Md3Menu();
   private boolean appMenuOpen;
   private List<ClientScreen.Group> groups = new ArrayList<>();
   private int selectedCat = 0;
   private int detailGid = -1;
   private ClientScreen.Group detailGroup;
   private long lastNanos;
   private float dt;
   private int openFrames;
   private float mouseNowX;
   private float mouseNowY;
   private boolean mouseDown;
   private float appBarX;
   private float appBarY;
   private float appBarW;
   private float appBarH;
   private float panelX;
   private float panelY;
   private float panelW;
   private float panelH;
   private float tileH = 60.0F;
   private float stackTitleCy;
   private float stackSubCy;
   private float contentX;
   private float contentY;
   private float contentW;
   private float contentH;
   private float tabsX;
   private float tabsY;
   private float tabsW;
   private float tabsH = 36.0F;
   private float tabScrollX;
   private float tabsContentW;
   private float cardsTotalHeight;
   private float menuBtnX;
   private float menuBtnY;
   private float menuBtnS = 38.0F;
   private float themeBtnX;
   private float themeBtnY;
   private float themeBtnS = 38.0F;
   private float closeBtnX;
   private float closeBtnY;
   private float closeBtnS = 38.0F;
   private float searchX;
   private float searchY;
   private float searchW;
   private float searchH = 44.0F;
   private final float headerH = 56.0F;
   private boolean searchFocus;
   private String searchText = "";
   private int sliderKey = -1;
   private float sliderNorm;
   private int sliderStartX;
   private float sliderStartNorm;
   private boolean bindingKey;
   private int pickerKey = -1;
   private float pickerH;
   private float pickerS;
   private float pickerV;
   private int pickerA;
   private int pickerDrag = -1;
   private float dialogX;
   private float dialogY;
   private float dialogW;
   private float dialogH;
   private float pickerBarX;
   private float pickerBarY;
   private float pickerBarW;
   private float pickerBarStep = 28.0F;
   private boolean crosshairEditor;
   private int editorDrag = -1;
   private int menuKey = -1;
   private final Md3Menu menu = new Md3Menu();
   private int menuIndex = -1;
   private long resetConfirmUntil;
   private String snack;
   private long snackUntil;

   public ClientScreen() {
      ClientConfig.ensureLoaded();
   }

   @Override
   public void method_1030() {
      ClientConfig.save();
      super.method_1030();
   }

   @Override
   public void method_1044() {
      this.field_1232.clear();
      this.buildGroups();
      this.applyFilter();
      this.detailGroup = this.findGroup(this.detailGid);
      if (this.detailGid >= 0 && this.detailGroup == null) {
         this.detailGid = -1;
      }

      this.lastNanos = 0L;
      this.openFrames = 0;
      float margin = Math.round(Math.min(12.0F, this.field_1230 * 0.025F));
      this.panelW = Math.min(760.0F, Math.max(this.field_1230 * 0.6F, Math.min(this.field_1230 - 16.0F, 380.0F)));
      this.panelH = Math.min(590.0F, this.field_1231 * 0.72F);
      this.panelX = (this.field_1230 - this.panelW) / 2.0F;
      this.panelY = this.field_1231 - this.panelH - margin;
      this.appBarX = this.panelX + 12.0F;
      this.appBarY = this.panelY + 8.0F;
      this.appBarW = this.panelW - 24.0F;
      this.appBarH = 44.0F;
      this.closeBtnS = 32.0F;
      this.themeBtnS = 32.0F;
      this.menuBtnS = 32.0F;
      this.searchH = 28.0F;
      this.tabsH = 30.0F;
      this.closeBtnX = this.appBarX + this.appBarW - 10.0F - this.closeBtnS;
      this.closeBtnY = this.appBarY + (this.appBarH - this.closeBtnS) / 2.0F;
      this.themeBtnX = this.closeBtnX - 4.0F - this.themeBtnS;
      this.themeBtnY = this.closeBtnY;
      this.menuBtnX = this.themeBtnX - 4.0F - this.menuBtnS;
      this.menuBtnY = this.closeBtnY;
      this.searchX = this.appBarX + (this.showLogo() ? 42.0F : 6.0F);
      this.searchW = Math.max(96.0F, this.menuBtnX - 10.0F - this.searchX);
      this.searchY = this.appBarY + (this.appBarH - this.searchH) / 2.0F;
      this.tabsX = this.panelX + 12.0F;
      this.tabsY = this.panelY + this.appBarH + 7.0F;
      this.tabsW = this.panelW - 24.0F;
      this.contentX = this.panelX + 12.0F;
      this.contentY = this.tabsY + this.tabsH + 7.0F;
      this.contentW = this.panelW - 24.0F - 12.0F;
      this.contentH = Math.max(90.0F, this.panelY + this.panelH - 12.0F - this.contentY);
      this.search.variant = Md3TextField.Variant.OUTLINED;
      this.search.label = "";
      this.search.supporting = "";
      this.search.leadingIcon = "search";
      this.search.trailingIcon = null;
      this.search.maxLength = 32;
      this.search.setBounds(this.searchX, this.searchY, this.searchW, this.searchH);
      this.search.focused = this.searchFocus;
      this.themeButton.variant = Md3IconButton.Variant.STANDARD;
      this.themeButton.setBounds(this.themeBtnX, this.themeBtnY, this.themeBtnS, this.themeBtnS);
      this.closeButton.variant = Md3IconButton.Variant.STANDARD;
      this.closeButton.icon = "close";
      this.closeButton.setBounds(this.closeBtnX, this.closeBtnY, this.closeBtnS, this.closeBtnS);
      this.menuButton.variant = Md3IconButton.Variant.STANDARD;
      this.menuButton.icon = "more_vert";
      this.menuButton.setBounds(this.menuBtnX, this.menuBtnY, this.menuBtnS, this.menuBtnS);
      this.backButton.variant = Md3IconButton.Variant.STANDARD;
      this.backButton.icon = "arrow_back";
      this.backButton.setBounds(this.appBarX + 8.0F, this.appBarY + (this.appBarH - this.menuBtnS) / 2.0F, this.menuBtnS, this.menuBtnS);
      this.scroll.jump(0.0F);
      this.tabScrollX = 0.0F;
      this.appMenuOpen = false;
   }

   private void buildGroups() {
      this.groups.clear();
      ClientScreen.Group current = null;

      for (int[] row : SettingsSpec.ROWS) {
         if (row[0] == -1) {
            current = new ClientScreen.Group(row[2]);
            this.groups.add(current);
         } else if (current != null) {
            ClientScreen.Row item = new ClientScreen.Row(row[0], row[2]);
            if (item.kind == 0) {
               current.units.add(new ClientScreen.Unit(item));
            } else if (item.kind == 2) {
               current.units.add(new ClientScreen.Unit(item));
            } else if (item.key == 72) {
               current.units.add(new ClientScreen.Unit(item));
            } else if (!current.units.isEmpty() && current.units.get(current.units.size() - 1).head.kind == 0) {
               current.units.get(current.units.size() - 1).rows.add(item);
            } else {
               current.units.add(new ClientScreen.Unit(item));
            }
         }
      }
   }

   private ClientScreen.Group findGroup(int gid) {
      for (ClientScreen.Group group : this.groups) {
         if (group.id == gid) {
            return group;
         }
      }

      return null;
   }

   private void applyFilter() {
      this.tiles.clear();
      String needle = this.searchText.trim().toLowerCase();
      if (!needle.isEmpty()) {
         this.selectedCat = 0;
      }

      for (ClientScreen.Group group : this.groups) {
         if ((this.selectedCat == 0 || this.categoryOf(group.id) == this.selectedCat) && (needle.isEmpty() || this.groupMatches(group, needle))) {
            this.tiles.add(new ClientScreen.Tile(group));
         }
      }

      boolean showReplay = ReplayClient.isReady()
         && (needle.isEmpty() || needle.contains("回放") || needle.contains("replay"))
         && (this.selectedCat == 0 || this.selectedCat == 5);
      if (showReplay) {
         String replayKey = this.field_1229 != null && this.field_1229.field_3803 != null ? "Replay recording" : "Replay console";
         int pos = this.tiles.size();

         for (int i = 0; i < this.tiles.size(); i++) {
            ClientScreen.Group g = this.tiles.get(i).group;
            String en = g == null ? "" : g.nameEn();
            if (en != null && en.compareToIgnoreCase(replayKey) > 0) {
               pos = i;
               break;
            }
         }

         this.tiles.add(pos, new ClientScreen.Tile(null));
      }

      this.scroll.jump(0.0F);
   }

   private boolean groupMatches(ClientScreen.Group group, String needle) {
      String names = (group.nameZh() + " " + group.nameEn()).toLowerCase();
      if (names.contains(needle)) {
         return true;
      } else {
         for (ClientScreen.Unit unit : group.units) {
            if (this.label(unit.head.key).toLowerCase().contains(needle)) {
               return true;
            }

            for (ClientScreen.Row row : unit.rows) {
               if (this.label(row.key).toLowerCase().contains(needle)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private int categoryOf(int gid) {
      switch (gid) {
         case 203:
         case 205:
         case 219:
         case 223:
         case 224:
            return 2;
         case 204:
         case 214:
            return 4;
         case 206:
         case 207:
         case 208:
         case 212:
         case 213:
         case 215:
         case 216:
         case 217:
         case 218:
         case 221:
         case 222:
         default:
            return 1;
         case 209:
         case 210:
         case 211:
         case 220:
            return 3;
      }
   }

   private String categoryLabel(int index) {
      switch (index) {
         case 1:
            return "DISPLAY";
         case 2:
            return "CAMERA";
         case 3:
            return "HUD";
         case 4:
            return "INPUT";
         case 5:
            return "MISC";
         default:
            return "ALL";
      }
   }

   private boolean matchesUnit(ClientScreen.Unit unit, String needle) {
      if (this.label(unit.head.key).toLowerCase().contains(needle)) {
         return true;
      } else {
         for (ClientScreen.Row row : unit.rows) {
            if (this.label(row.key).toLowerCase().contains(needle)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.openFrames++;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, this.field_1229 != null && this.field_1229.field_3803 != null);
      this.renderPanel(t);
      if (this.detailGid >= 0) {
         this.renderDetailAppBar(t, mouseX, mouseY);
         this.renderDetailContent(t, mouseX, mouseY);
      } else {
         this.renderAppBar(t, mouseX, mouseY);
         this.renderTabs(t, mouseX, mouseY);
         this.renderContent(t, mouseX, mouseY);
         this.renderAppMenu(t, mouseX, mouseY);
      }

      this.renderSnack(t);
      this.renderMenu(t, mouseX, mouseY);
      if (this.pickerKey >= 0) {
         this.renderColorDialog(t, mouseX, mouseY);
      }

      if (this.crosshairEditor) {
         this.renderCrosshairDialog(t, mouseX, mouseY);
      }

      this.canvas.end();
   }

   private void renderPanel(Md3Theme t) {
      this.canvas.shadow(this.panelX, this.panelY, this.panelW, this.panelH, 28.0F, 2);
      this.canvas.roundRect(this.panelX, this.panelY, this.panelW, this.panelH, 24.0F, t.colors.surfaceContainerLow);
      this.canvas.strokeRoundRect(this.panelX, this.panelY, this.panelW, this.panelH, 24.0F, 1.0F, Md3Colors.alpha(t.colors.outlineVariant, 0.35F));
   }

   private void renderAppBar(Md3Theme t, int mouseX, int mouseY) {
      float logoX = this.appBarX + 2.0F;
      float logoY = this.appBarY + (this.appBarH - 30.0F) / 2.0F;
      if (this.showLogo()) {
         this.canvas.roundRect(logoX, logoY, 30.0F, 30.0F, 10.0F, t.colors.primaryContainer);
         this.canvas.icon("auto_awesome", logoX + 5.0F, logoY + 5.0F, 20.0F, t.colors.onPrimaryContainer);
      }

      this.search.trailingIcon = this.search.text.isEmpty() ? null : "close";
      this.search.surface = t.colors.surfaceContainer;
      this.search.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.searchFocus) {
         ImeBridge.onFieldRender((int)this.search.x + 16, (int)this.search.y + 22, (int)this.search.w - 32, this.search.text, this.search.text.length(), true);
      }

      if (this.search.text.isEmpty()) {
         String hint = this.searchW < 150.0F ? this.tr("搜索", "Search") : this.tr("搜索设置", "Search settings");
         this.canvas
            .text(
               ClientUi.fit(this.canvas, hint, Md3Type.BODY_MEDIUM, this.searchW - 52.0F),
               this.searchX + 44.0F,
               Md3Text.topForLineCenter(this.searchY + this.searchH / 2.0F, Md3Type.BODY_MEDIUM),
               Md3Type.BODY_MEDIUM,
               Md3Colors.alpha(t.colors.onSurfaceVariant, 0.72F)
            );
      }

      this.themeButton.icon = ClientTheme.dark() ? "light_mode" : "dark_mode";
      this.themeButton.surface = t.colors.surfaceContainer;
      this.themeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.themeButton.consumeClick()) {
         ClientConfig.lightTheme = !ClientConfig.lightTheme;
         ClientConfig.saveSoon();
      }

      this.menuButton.surface = t.colors.surfaceContainer;
      this.menuButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.closeButton.surface = t.colors.surfaceContainer;
      this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.closeButton.consumeClick()) {
         this.close();
      }
   }

   private boolean showLogo() {
      return this.appBarW >= 360.0F;
   }

   private void openAppMenu() {
      this.appMenu.items = new String[]{this.tr("已安装模组", "Installed mods"), this.tr("重置所有设置", "Reset all settings")};
      this.appMenu.selected = -1;
      this.appMenu.x = Math.max(this.appBarX + 8.0F, this.menuBtnX - 150.0F);
      this.appMenu.y = this.appBarY + this.appBarH + 6.0F;
      float menuBottomLimit = this.field_1231 - 6.0F;
      float menuH = 8.0F + this.appMenu.items.length * 42.0F;
      if (this.appMenu.y + menuH > menuBottomLimit) {
         this.appMenu.y = Math.max(4.0F, menuBottomLimit - menuH);
      }

      this.appMenu.w = 190.0F;
      this.appMenu.itemHeight = 42.0F;
      this.appMenu.open();
      this.appMenuOpen = true;
      ClientUi.click();
   }

   private void renderAppMenu(Md3Theme t, int mouseX, int mouseY) {
      if (this.appMenuOpen) {
         this.appMenu.update(this.dt);
         this.appMenu.render(this.canvas, t, mouseX, mouseY);
      }
   }

   private void renderTabs(Md3Theme t, int mouseX, int mouseY) {
      this.canvas.pushClip(this.tabsX + 6.0F, this.tabsY + 2.0F, this.tabsW - 12.0F, this.tabsH - 4.0F);
      this.tabBoxes.clear();
      float tx = this.tabsX + 8.0F - this.tabScrollX;
      float total = 0.0F;

      for (int i = 0; i <= 5; i++) {
         String label = this.categoryLabel(i);
         float tw = this.canvas.textWidth(label, Md3Type.LABEL_MEDIUM) + 14.0F;
         float th = 24.0F;
         float ty = this.tabsY + (this.tabsH - th) / 2.0F;
         boolean active = this.selectedCat == i;
         int base = active ? t.colors.primaryContainer : t.colors.surfaceContainerHigh;
         int content = active ? t.colors.onPrimaryContainer : t.colors.onSurfaceVariant;
         this.canvas.roundRect(tx, ty, tw, th, 12.0F, base);
         Md3Text.centered(this.canvas, label, tx + tw / 2.0F, ty + th / 2.0F, Md3Type.LABEL_MEDIUM, content);
         ClientScreen.TabBox box = new ClientScreen.TabBox();
         box.index = i;
         box.x = tx;
         box.y = ty;
         box.w = tw;
         box.h = th;
         this.tabBoxes.add(box);
         tx += tw + 4.0F;
         total = tx - (this.tabsX + 8.0F - this.tabScrollX);
      }

      this.tabsContentW = total;
      this.tabScrollX = Math.max(0.0F, Math.min(this.tabScrollX, Math.max(0.0F, total - (this.tabsW - 16.0F))));
      this.canvas.popClip();
      if (this.tabsContentW > this.tabsW - 16.0F) {
         float frac = (this.tabsW - 16.0F) / this.tabsContentW;
         float bw = Math.max(36.0F, (this.tabsW - 16.0F) * frac);
         float bx = this.tabsX + 8.0F + (this.tabsW - 16.0F - bw) * (this.tabScrollX / Math.max(1.0F, this.tabsContentW - (this.tabsW - 16.0F)));
         this.canvas.roundRect(bx, this.tabsY + this.tabsH - 4.0F, bw, 2.0F, 1.0F, Md3Colors.alpha(t.colors.primary, 0.65F));
      }
   }

   private void renderContent(Md3Theme t, int mouseX, int mouseY) {
      this.boxes.clear();
      this.tileBoxes.clear();
      this.layoutTiles();
      this.scroll.measure(this.cardsTotalHeight, this.contentH);
      this.scroll.update(this.dt);
      this.canvas.pushClip(this.contentX, this.contentY, this.contentW, this.contentH);
      if (this.tiles.isEmpty()) {
         this.canvas.roundRect(this.contentX, this.contentY, this.contentW, Math.min(220.0F, this.contentH), 18.0F, t.colors.surfaceContainerLow);
         this.canvas.icon("search", this.contentX + this.contentW / 2.0F - 13.0F, this.contentY + 44.0F, 26.0F, t.colors.onSurfaceVariant);
         Md3Text.centered(
            this.canvas,
            this.tr("没有匹配的设置", "No matching settings"),
            this.contentX + this.contentW / 2.0F,
            this.contentY + 92.0F,
            Md3Type.TITLE_SMALL,
            t.colors.onSurfaceVariant
         );
         this.canvas.popClip();
      } else {
         for (ClientScreen.Tile tile : this.tiles) {
            float y = this.contentY + tile.y - this.scroll.value;
            if (!(y + this.tileH < this.contentY - 8.0F) && !(y > this.contentY + this.contentH + 8.0F)) {
               this.renderTile(t, tile, y, mouseX, mouseY);
            }
         }

         this.canvas.popClip();
         this.scroll.renderBar(this.canvas, t, this.contentX + this.contentW + 3.0F, this.contentY + 2.0F, 3.0F);
      }
   }

   private void renderDetailAppBar(Md3Theme t, int mouseX, int mouseY) {
      this.backButton.setBounds(this.appBarX + 8.0F, this.appBarY + (this.appBarH - this.menuBtnS) / 2.0F, this.menuBtnS, this.menuBtnS);
      this.backButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.backButton.consumeClick()) {
         this.closeDetail();
         ClientUi.click();
      } else {
         ClientScreen.Group group = this.detailGroup;
         if (group != null) {
            this.canvas.text(group.name(), this.appBarX + 54.0F, this.appBarY + 11.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
            this.canvas.text(this.trCount(group.rowCount()), this.appBarX + 54.0F, this.appBarY + 31.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
         }

         this.closeButton.setBounds(this.closeBtnX, this.closeBtnY, this.closeBtnS, this.closeBtnS);
         this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
         if (this.closeButton.consumeClick()) {
            this.close();
         }
      }
   }

   private void renderDetailContent(Md3Theme t, int mouseX, int mouseY) {
      this.boxes.clear();
      this.tileBoxes.clear();
      ClientScreen.Group group = this.detailGroup;
      if (group != null) {
         this.contentX = this.panelX + 12.0F;
         this.contentY = this.panelY + this.appBarH + 24.0F;
         this.contentW = this.panelW - 24.0F - 12.0F;
         this.contentH = Math.max(80.0F, this.panelY + this.panelH - 12.0F - this.contentY);
         float listW = Math.min(780.0F, this.contentW);
         float lx = this.contentX + (this.contentW - listW) / 2.0F;
         int cols = group.id == 205 ? 1 : (listW >= 500.0F ? 2 : 1);
         float gap = 10.0F;
         float cardW = (listW - gap * (cols - 1)) / cols;
         int unitCount = group.units.size();
         int rows = (unitCount + cols - 1) / cols;
         float total = 0.0F;
         float[] rowH = new float[rows];

         for (int i = 0; i < unitCount; i++) {
            int row = i / cols;
            rowH[row] = Math.max(rowH[row], this.unitHeight(group.units.get(i)) + 20.0F);
         }

         for (int i = 0; i < rows; i++) {
            total += rowH[i] + gap;
         }

         total = Math.max(0.0F, total - gap);
         this.scroll.measure(total, this.contentH);
         this.scroll.update(this.dt);
         this.canvas.pushClip(this.contentX, this.contentY, this.contentW, this.contentH);
         float ry = this.contentY - this.scroll.value;

         for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
               int i = row * cols + col;
               if (i < unitCount) {
                  ClientScreen.Unit unit = group.units.get(i);
                  float cx = lx + col * (cardW + gap);
                  if (ry + rowH[row] >= this.contentY - 8.0F && ry <= this.contentY + this.contentH + 8.0F) {
                     this.canvas.roundRect(cx, ry, cardW, rowH[row], 16.0F, t.colors.surfaceContainerHigh);
                     this.renderUnit(t, unit, cx + 16.0F, ry + 10.0F, cardW - 32.0F, mouseX, mouseY);
                  }
               }
            }

            ry += rowH[row] + gap;
         }

         this.canvas.popClip();
         this.scroll.renderBar(this.canvas, t, this.contentX + this.contentW + 3.0F, this.contentY + 2.0F, 3.0F);
      }
   }

   private void openDetail(ClientScreen.Group group) {
      this.detailGid = group.id;
      this.detailGroup = group;
      this.scroll.jump(0.0F);
      this.searchFocus = false;
      this.search.focused = false;
      this.appMenuOpen = false;
   }

   private void closeDetail() {
      this.detailGid = -1;
      this.detailGroup = null;
      this.scroll.jump(0.0F);
      this.applyGridBounds();
   }

   private void applyGridBounds() {
      this.contentX = this.panelX + 12.0F;
      this.contentY = this.tabsY + this.tabsH + 7.0F;
      this.contentW = this.panelW - 24.0F - 12.0F;
      this.contentH = Math.max(90.0F, this.panelY + this.panelH - 12.0F - this.contentY);
   }

   private void detailClick(int mouseX, int mouseY, int button) {
      if (button == 1) {
         for (ClientScreen.RowBox box : this.boxes) {
            if (box.kind == 1 && ClientUi.hit(mouseX, mouseY, box.ctrlX, box.ctrlY - 6.0F, box.ctrlW, box.ctrlH + 12.0F)) {
               this.stepSlider(box.key, -1);
               return;
            }
         }
      } else if (button == 0) {
         if (!this.scroll.press(mouseX, mouseY, this.contentX + this.contentW + 3.0F, this.contentY + 2.0F, 3.0F, 3.0F)) {
            float backX = this.appBarX + 8.0F;
            float backY = this.appBarY + (this.appBarH - this.menuBtnS) / 2.0F;
            if (ClientUi.hit(mouseX, mouseY, backX, backY, this.menuBtnS, this.menuBtnS)) {
               this.closeDetail();
               ClientUi.click();
            } else if (ClientUi.hit(mouseX, mouseY, this.closeBtnX, this.closeBtnY, this.closeBtnS, this.closeBtnS)) {
               this.close();
            } else if (!ClientUi.hit(mouseX, mouseY, this.appBarX, this.appBarY - 4.0F, this.appBarW, this.appBarH + 8.0F)) {
               if (ClientUi.hit(mouseX, mouseY, this.contentX, this.contentY, this.contentW, this.contentH)) {
                  for (ClientScreen.RowBox boxx : this.boxes) {
                     boolean inside = ClientUi.hit(mouseX, mouseY, boxx.x, boxx.y, boxx.w, boxx.h);
                     if (boxx.kind != 1) {
                        if (boxx.kind != 0) {
                           if (inside || ClientUi.hit(mouseX, mouseY, boxx.ctrlX, boxx.ctrlY, boxx.ctrlW, boxx.ctrlH)) {
                              this.rowAction(boxx);
                              return;
                           }
                        } else if (inside) {
                           this.toggle(boxx.key);
                           this.syncSwitch(boxx.key);
                           ClientConfig.save();
                           ClientUi.click();
                           if (boxx.key == 66) {
                              this.close();
                              return;
                           }
                        }
                     } else if (ClientUi.hit(mouseX, mouseY, boxx.ctrlX, boxx.ctrlY - 6.0F, boxx.ctrlW, boxx.ctrlH + 12.0F)) {
                        this.sliderKey = boxx.key;
                        float track = Math.max(1.0F, boxx.ctrlW - 14.0F);
                        float firstNorm = this.clamp01((mouseX - boxx.ctrlX - 7.0F) / track);
                        this.sliderNorm = firstNorm;
                        this.sliderStartX = mouseX;
                        this.sliderStartNorm = firstNorm;
                        this.applyNormSmooth(boxx.key, firstNorm);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private void layoutTiles() {
      if (this.tiles.isEmpty()) {
         this.cardsTotalHeight = 0.0F;
      } else {
         int cols = this.contentW >= 900.0F ? 4 : (this.contentW >= 300.0F ? 3 : (this.contentW >= 190.0F ? 2 : 1));
         float gap = 8.0F;
         float tileW = (this.contentW - gap * (cols - 1)) / cols;
         boolean wide = tileW >= 230.0F;
         boolean compact = !wide && tileW >= 132.0F;
         float pad = wide ? 14.0F : (compact ? 10.0F : 9.0F);
         float icon = wide ? 26.0F : 22.0F;
         float titleH = (wide ? Md3Type.TITLE_MEDIUM : Md3Type.TITLE_SMALL).lineHeight;
         float subH = (wide ? Md3Type.BODY_SMALL : Md3Type.LABEL_SMALL).lineHeight;
         if (compact) {
            this.tileH = pad * 2.0F + Math.max(icon, titleH + 1.0F + subH);
         } else if (wide) {
            this.tileH = pad * 2.0F + Math.max(icon, titleH + 2.0F + subH);
         } else {
            this.stackTitleCy = pad + icon + 2.0F + titleH / 2.0F;
            this.stackSubCy = this.stackTitleCy + titleH / 2.0F + 1.0F + subH / 2.0F;
            this.tileH = this.stackSubCy + subH / 2.0F + pad;
         }

         this.tileH = Math.max(this.tileH, 52.0F);

         for (int i = 0; i < this.tiles.size(); i++) {
            ClientScreen.Tile tile = this.tiles.get(i);
            int col = i % cols;
            int row = i / cols;
            tile.x = this.contentX + col * (tileW + gap);
            tile.y = row * (this.tileH + gap);
            tile.w = tileW;
         }

         int rows = (this.tiles.size() + cols - 1) / cols;
         this.cardsTotalHeight = Math.max(0.0F, rows * (this.tileH + gap) - gap);
      }
   }

   private void renderTile(Md3Theme t, ClientScreen.Tile tile, float y, int mouseX, int mouseY) {
      ClientScreen.Group group = tile.group;
      float x = tile.x;
      if (group == null) {
         this.renderReplayTile(t, tile, y, mouseX, mouseY);
      } else {
         float w = tile.w;
         boolean hot = ClientUi.hit(mouseX, mouseY, x, y, w, this.tileH);
         int bg = hot ? Md3Colors.composite(Md3Colors.alpha(t.colors.primary, 0.1F), t.colors.surfaceContainerHigh) : t.colors.surfaceContainerHigh;
         this.canvas.roundRect(x, y, w, this.tileH, 18.0F, bg);
         String sub = this.trCount(group.rowCount());
         String en = group.nameEn();
         if (w >= 230.0F && en != null && !en.isEmpty()) {
            sub = sub + " · " + en.toUpperCase();
         }

         if (w >= 230.0F) {
            float ix = x + 14.0F;
            float iy = y + 14.0F;
            this.canvas.roundRect(ix, iy, 26.0F, 26.0F, 8.0F, t.colors.primaryContainer);
            this.canvas.icon(ClientIcons.group(group.id), ix + 4.0F, iy + 4.0F, 18.0F, t.colors.onPrimaryContainer);
            float nameX = ix + 36.0F;
            float nameW = Math.max(40.0F, w - 74.0F);
            ClientUi.textFit(this.canvas, group.name(), nameX, y + 11.0F, Md3Type.TITLE_MEDIUM, nameW, t.colors.onSurface);
            ClientUi.textFit(this.canvas, sub, nameX, y + 32.0F, Md3Type.BODY_SMALL, nameW, t.colors.onSurfaceVariant);
            this.canvas
               .icon(
                  "chevron_right",
                  x + w - 30.0F,
                  y + (this.tileH - 18.0F) / 2.0F,
                  16.0F,
                  hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F)
               );
         } else if (w >= 132.0F) {
            float ix = x + 10.0F;
            float iy = y + 11.0F;
            this.canvas.roundRect(ix, iy, 22.0F, 22.0F, 7.0F, t.colors.primaryContainer);
            this.canvas.icon(ClientIcons.group(group.id), ix + 3.0F, iy + 3.0F, 16.0F, t.colors.onPrimaryContainer);
            float nameX = x + 38.0F;
            float nameW = Math.max(30.0F, w - 66.0F);
            ClientUi.textFit(this.canvas, group.name(), nameX, y + 9.0F, Md3Type.TITLE_SMALL, nameW, t.colors.onSurface);
            ClientUi.textFit(this.canvas, sub, nameX, y + 27.0F, Md3Type.LABEL_SMALL, nameW, t.colors.onSurfaceVariant);
            this.canvas
               .icon(
                  "chevron_right",
                  x + w - 22.0F,
                  y + (this.tileH - 16.0F) / 2.0F,
                  14.0F,
                  hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F)
               );
         } else {
            float ix = x + 9.0F;
            float iy = y + 9.0F;
            this.canvas.roundRect(ix, iy, 22.0F, 22.0F, 7.0F, t.colors.primaryContainer);
            this.canvas.icon(ClientIcons.group(group.id), ix + 3.0F, iy + 3.0F, 16.0F, t.colors.onPrimaryContainer);
            ClientUi.textFit(this.canvas, group.name(), x + 9.0F, y + this.stackTitleCy, Md3Type.TITLE_SMALL, w - 18.0F, t.colors.onSurface);
            ClientUi.textFit(this.canvas, sub, x + 9.0F, y + this.stackSubCy, Md3Type.LABEL_SMALL, w - 18.0F, t.colors.onSurfaceVariant);
            this.canvas.icon("chevron_right", x + w - 22.0F, y + 10.0F, 14.0F, hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F));
         }

         ClientScreen.TileBox box = new ClientScreen.TileBox();
         box.gid = group.id;
         box.x = x;
         box.y = y;
         box.w = w;
         box.h = this.tileH;
         this.tileBoxes.add(box);
      }
   }

   private void renderReplayTile(Md3Theme t, ClientScreen.Tile tile, float y, int mouseX, int mouseY) {
      float x = tile.x;
      float w = tile.w;
      boolean hot = ClientUi.hit(mouseX, mouseY, x, y, w, this.tileH);
      int bg = hot ? Md3Colors.composite(Md3Colors.alpha(t.colors.primary, 0.1F), t.colors.surfaceContainerHigh) : t.colors.surfaceContainerHigh;
      this.canvas.roundRect(x, y, w, this.tileH, 18.0F, bg);
      String name = this.tr("回放", "Replay");
      String sub = "";
      if (w >= 230.0F) {
         float ix = x + 14.0F;
         float iy = y + 14.0F;
         this.canvas.roundRect(ix, iy, 26.0F, 26.0F, 8.0F, t.colors.primaryContainer);
         this.canvas.icon("animation", ix + 4.0F, iy + 4.0F, 18.0F, t.colors.onPrimaryContainer);
         ClientUi.textFit(this.canvas, name, ix + 36.0F, y + 11.0F, Md3Type.TITLE_MEDIUM, Math.max(40.0F, w - 74.0F), t.colors.onSurface);
         ClientUi.textFit(this.canvas, sub, ix + 36.0F, y + 32.0F, Md3Type.BODY_SMALL, Math.max(40.0F, w - 74.0F), t.colors.onSurfaceVariant);
         this.canvas
            .icon(
               "chevron_right",
               x + w - 30.0F,
               y + (this.tileH - 18.0F) / 2.0F,
               16.0F,
               hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F)
            );
      } else if (w >= 132.0F) {
         float ix = x + 10.0F;
         float iy = y + 11.0F;
         this.canvas.roundRect(ix, iy, 22.0F, 22.0F, 7.0F, t.colors.primaryContainer);
         this.canvas.icon("animation", ix + 3.0F, iy + 3.0F, 16.0F, t.colors.onPrimaryContainer);
         ClientUi.textFit(this.canvas, name, x + 38.0F, y + 9.0F, Md3Type.TITLE_SMALL, Math.max(30.0F, w - 66.0F), t.colors.onSurface);
         ClientUi.textFit(this.canvas, sub, x + 38.0F, y + 27.0F, Md3Type.LABEL_SMALL, Math.max(30.0F, w - 66.0F), t.colors.onSurfaceVariant);
         this.canvas
            .icon(
               "chevron_right",
               x + w - 22.0F,
               y + (this.tileH - 16.0F) / 2.0F,
               14.0F,
               hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F)
            );
      } else {
         float ix = x + 9.0F;
         float iy = y + 9.0F;
         this.canvas.roundRect(ix, iy, 22.0F, 22.0F, 7.0F, t.colors.primaryContainer);
         this.canvas.icon("animation", ix + 3.0F, iy + 3.0F, 16.0F, t.colors.onPrimaryContainer);
         ClientUi.textFit(this.canvas, name, x + 9.0F, y + this.stackTitleCy, Md3Type.TITLE_SMALL, w - 18.0F, t.colors.onSurface);
         ClientUi.textFit(this.canvas, sub, x + 9.0F, y + this.stackSubCy, Md3Type.LABEL_SMALL, w - 18.0F, t.colors.onSurfaceVariant);
         this.canvas.icon("chevron_right", x + w - 22.0F, y + 10.0F, 14.0F, hot ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.65F));
      }

      ClientScreen.TileBox box = new ClientScreen.TileBox();
      box.gid = -1;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = this.tileH;
      this.tileBoxes.add(box);
   }

   private int indexOf(ClientScreen.Group group) {
      for (int i = 0; i < this.groups.size(); i++) {
         if (this.groups.get(i).id == group.id) {
            return i;
         }
      }

      return 0;
   }

   private String trCount(int count) {
      if (ClientConfig.langIndex() == 0) {
         return count + " 项设置";
      } else {
         return count == 1 ? "1 setting" : count + " settings";
      }
   }

   private float unitHeight(ClientScreen.Unit unit) {
      int headH = this.headHeight(unit.head.kind);
      if (unit.head.kind == 0 && !unit.rows.isEmpty()) {
         float childH = 0.0F;

         for (ClientScreen.Row row : unit.rows) {
            childH += this.childHeight(row.kind);
         }

         return headH + 4.0F + childH + 16.0F;
      } else {
         return headH;
      }
   }

   private int headHeight(int kind) {
      switch (kind) {
         case 1:
            return 66;
         case 2:
         case 3:
         case 5:
            return 52;
         case 4:
            return 58;
         default:
            return 48;
      }
   }

   private int childHeight(int kind) {
      switch (kind) {
         case 1:
            return 58;
         case 2:
         case 3:
         case 5:
            return 44;
         case 4:
         default:
            return 48;
      }
   }

   private void renderUnit(Md3Theme t, ClientScreen.Unit unit, float x, float y, float w, int mouseX, int mouseY) {
      ClientScreen.Row head = unit.head;
      if (head.kind != 0) {
         this.renderChild(t, head, x, y, w, this.headHeight(head.kind), mouseX, mouseY);
      } else {
         this.renderToggle(t, head, x, y, w, mouseX, mouseY);
         if (!unit.rows.isEmpty()) {
            float panelY = y + 50.0F;
            float panelH = 8.0F;

            for (ClientScreen.Row row : unit.rows) {
               panelH += this.childHeight(row.kind);
            }

            this.canvas.roundRect(x + 10.0F, panelY, w - 20.0F, panelH, 14.0F, t.colors.surfaceContainer);
            float cy = panelY + 4.0F;

            for (int i = 0; i < unit.rows.size(); i++) {
               ClientScreen.Row row = unit.rows.get(i);
               float rh = this.childHeight(row.kind);
               this.renderChild(t, row, x + 22.0F, cy, w - 44.0F, rh, mouseX, mouseY);
               if (i < unit.rows.size() - 1) {
                  this.canvas.fill(x + 34.0F, cy + rh - 1.0F, w - 68.0F, 1.0F, Md3Colors.alpha(t.colors.outlineVariant, 0.7F));
               }

               cy += rh;
            }
         }
      }
   }

   private void renderToggle(Md3Theme t, ClientScreen.Row row, float x, float y, float w, int mouseX, int mouseY) {
      float cy = y + 24.0F;
      ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, cy, Md3Type.TITLE_SMALL, w - 76.0F, t.colors.onSurface);
      boolean on = this.isOn(row.key);
      Md3Switch sw = this.switches.get(row.key);
      if (sw == null) {
         sw = new Md3Switch();
         sw.setChecked(on);
         this.switches.put(row.key, sw);
      }

      Boolean last = this.lastSwitchOn.get(row.key);
      if (last == null) {
         sw.setChecked(on);
         this.lastSwitchOn.put(row.key, on);
      } else if (last != on) {
         sw.setCheckedAnimated(on);
         this.lastSwitchOn.put(row.key, on);
      }

      sw.setBounds(x + w - 54.0F, y + 8.0F, 54.0F, 32.0F);
      sw.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      sw.consumeClick();
      ClientScreen.RowBox box = new ClientScreen.RowBox();
      box.kind = row.kind;
      box.key = row.key;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = 48.0F;
      box.ctrlX = x + w - 54.0F;
      box.ctrlY = y + 8.0F;
      box.ctrlW = 54.0F;
      box.ctrlH = 32.0F;
      this.boxes.add(box);
   }

   private void renderChild(Md3Theme t, ClientScreen.Row row, float x, float y, float w, float h, int mouseX, int mouseY) {
      switch (row.kind) {
         case 1:
            this.renderValueRow(t, row, x, y, w, h, mouseX, mouseY);
            return;
         case 2:
            this.renderColorRow(t, row, x, y, w, h, mouseX, mouseY);
            return;
         case 3:
            this.renderCycleRow(t, row, x, y, w, h, mouseX, mouseY);
            return;
         case 4:
         default:
            ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, y + h / 2.0F, Md3Type.TITLE_SMALL, w - 8.0F, t.colors.onSurface);
            return;
         case 5:
            this.renderActionRow(t, row, x, y, w, h, mouseX, mouseY);
      }
   }

   private void renderValueRow(Md3Theme t, ClientScreen.Row row, float x, float y, float w, float h, int mouseX, int mouseY) {
      float cy = y + 14.0F;
      ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, cy, Md3Type.TITLE_SMALL, w - 76.0F, t.colors.onSurface);
      String value = this.valueText(row.key);
      float vw = this.canvas.textWidth(value, Md3Type.LABEL_MEDIUM);
      this.canvas.roundRect(x + w - vw - 18.0F, cy - 10.0F, vw + 14.0F, 20.0F, 10.0F, t.colors.secondaryContainer);
      Md3Text.centered(this.canvas, value, x + w - vw / 2.0F - 11.0F, cy, Md3Type.LABEL_MEDIUM, t.colors.onSecondaryContainer);
      float sx = x + 6.0F;
      float sy = y + 36.0F;
      float sw = w - 12.0F;
      float norm;
      if (this.sliderKey == row.key) {
         norm = this.sliderNorm;
      } else {
         norm = this.clamp01(this.normOf(row.key));
      }

      float trackY = sy + 10.0F;
      boolean hot = ClientUi.hit(mouseX, mouseY, sx, sy, sw, 32.0F);
      this.canvas.roundRect(sx, trackY, sw, 4.0F, 2.0F, t.colors.surfaceContainerHighest);
      if (norm > 0.0F) {
         this.canvas.roundRect(sx, trackY, Math.max(4.0F, norm * sw), 4.0F, 2.0F, t.colors.primary);
      }

      float thumbX = sx + 7.0F + norm * Math.max(0.0F, sw - 14.0F);
      float thumbCenterY = trackY + 2.0F;
      if (hot || this.sliderKey == row.key) {
         this.canvas.roundRect(thumbX - 14.0F, thumbCenterY - 14.0F, 28.0F, 28.0F, 14.0F, Md3Colors.alpha(t.colors.primary, 0.1F));
      }

      this.canvas.roundRect(thumbX - 6.0F, thumbCenterY - 6.0F, 12.0F, 12.0F, 6.0F, t.colors.primary);
      ClientScreen.RowBox box = new ClientScreen.RowBox();
      box.kind = row.kind;
      box.key = row.key;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = h;
      box.ctrlX = sx;
      box.ctrlY = sy;
      box.ctrlW = sw;
      box.ctrlH = 32.0F;
      this.boxes.add(box);
   }

   private void renderColorRow(Md3Theme t, ClientScreen.Row row, float x, float y, float w, float h, int mouseX, int mouseY) {
      float cy = y + h / 2.0F;
      ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, cy, Md3Type.TITLE_SMALL, w - 56.0F, t.colors.onSurface);
      float d = 24.0F;
      float bx = x + w - d - 6.0F;
      float by = cy - d / 2.0F;
      this.drawChecker(this.canvas, bx, by, d, d, 4.0F);
      this.canvas.roundRect(bx, by, d, d, d / 2.0F, this.colorOf(row.key));
      this.canvas.strokeRoundRect(bx, by, d, d, d / 2.0F, 1.0F, t.colors.outline);
      if (ClientUi.hit(mouseX, mouseY, bx - 6.0F, by - 6.0F, d + 12.0F, d + 12.0F)) {
         this.canvas.strokeRoundRect(bx - 5.0F, by - 5.0F, d + 10.0F, d + 10.0F, (d + 10.0F) / 2.0F, 1.0F, Md3Colors.alpha(t.colors.primary, 0.5F));
      }

      ClientScreen.RowBox box = new ClientScreen.RowBox();
      box.kind = row.kind;
      box.key = row.key;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = h;
      box.ctrlX = x;
      box.ctrlY = y;
      box.ctrlW = w;
      box.ctrlH = h;
      this.boxes.add(box);
   }

   private void renderCycleRow(Md3Theme t, ClientScreen.Row row, float x, float y, float w, float h, int mouseX, int mouseY) {
      float cy = y + h / 2.0F;
      String value = this.cycleText(row.key);
      float cw = Math.max(92.0F, this.canvas.textWidth(value, Md3Type.LABEL_LARGE) + 42.0F);
      ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, cy, Md3Type.TITLE_SMALL, w - cw - 12.0F, t.colors.onSurface);
      float cx = x + w - cw - 2.0F;
      float ch = 30.0F;
      boolean hot = ClientUi.hit(mouseX, mouseY, cx, cy - ch / 2.0F, cw, ch);
      this.canvas
         .roundRect(
            cx,
            cy - ch / 2.0F,
            cw,
            ch,
            ch / 2.0F,
            hot ? Md3Colors.composite(Md3Colors.alpha(t.colors.primary, 0.08F), t.colors.surfaceContainerHighest) : t.colors.surfaceContainerHighest
         );
      Md3Text.leftMiddle(
         this.canvas, ClientUi.fit(this.canvas, value, Md3Type.LABEL_MEDIUM, cw - 34.0F), cx + 12.0F, cy, Md3Type.LABEL_MEDIUM, t.colors.onSurface
      );
      this.canvas.icon("expand_more", cx + cw - 24.0F, cy - 9.0F, 18.0F, t.colors.onSurfaceVariant);
      ClientScreen.RowBox box = new ClientScreen.RowBox();
      box.kind = row.kind;
      box.key = row.key;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = h;
      box.ctrlX = cx;
      box.ctrlY = cy - ch / 2.0F;
      box.ctrlW = cw;
      box.ctrlH = ch;
      this.boxes.add(box);
   }

   private void renderActionRow(Md3Theme t, ClientScreen.Row row, float x, float y, float w, float h, int mouseX, int mouseY) {
      float cy = y + h / 2.0F;
      String label = row.key == 64 && this.bindingKey ? this.tr("按下按键…", "press a key...") : this.actionText(row.key);
      float aw = Math.max(92.0F, this.canvas.textWidth(label, Md3Type.LABEL_LARGE) + 40.0F);
      ClientUi.textFit(this.canvas, this.label(row.key), x + 4.0F, cy, Md3Type.TITLE_SMALL, w - aw - 12.0F, t.colors.onSurface);
      float ax = x + w - aw - 2.0F;
      float ah = 30.0F;
      boolean hot = ClientUi.hit(mouseX, mouseY, ax, cy - ah / 2.0F, aw, ah);
      this.canvas.roundRect(ax, cy - ah / 2.0F, aw, ah, ah / 2.0F, hot ? t.colors.primary : t.colors.primaryContainer);
      this.canvas.icon(row.key == 45 ? "my_location" : "keyboard", ax + 10.0F, cy - 9.0F, 18.0F, hot ? t.colors.onPrimary : t.colors.onPrimaryContainer);
      Md3Text.leftMiddle(
         this.canvas,
         ClientUi.fit(this.canvas, label, Md3Type.LABEL_MEDIUM, aw - 34.0F),
         ax + 32.0F,
         cy,
         Md3Type.LABEL_MEDIUM,
         hot ? t.colors.onPrimary : t.colors.onPrimaryContainer
      );
      ClientScreen.RowBox box = new ClientScreen.RowBox();
      box.kind = row.kind;
      box.key = row.key;
      box.x = x;
      box.y = y;
      box.w = w;
      box.h = h;
      box.ctrlX = ax;
      box.ctrlY = cy - ah / 2.0F;
      box.ctrlW = aw;
      box.ctrlH = ah;
      this.boxes.add(box);
   }

   private void renderMenu(Md3Theme t, int mouseX, int mouseY) {
      if (this.menuKey >= 0) {
         this.menu.update(this.dt);
         this.menu.render(this.canvas, t, mouseX, mouseY);
      }
   }

   private void renderSnack(Md3Theme t) {
      String text = this.currentSnack();
      if (text != null) {
         float w = Math.min(this.contentW - 24.0F, this.canvas.textWidth(text, Md3Type.BODY_MEDIUM) + 44.0F);
         float h = 40.0F;
         float x = this.contentX + (this.contentW - w) / 2.0F;
         float y = this.contentY + this.contentH - h - 12.0F;
         Md3Surfaces.snackbar(this.canvas, t, x, y, w, h);
         Md3Text.centered(
            this.canvas,
            ClientUi.fit(this.canvas, text, Md3Type.BODY_MEDIUM, w - 28.0F),
            x + w / 2.0F,
            y + h / 2.0F,
            Md3Type.BODY_MEDIUM,
            t.colors.inverseOnSurface
         );
      }
   }

   private String currentSnack() {
      long now = System.currentTimeMillis();
      if (now < this.snackUntil) {
         return this.snack;
      } else {
         return now < this.resetConfirmUntil ? this.tr("再次点击右上角菜单里的「重置所有设置」确认", "Open the overflow menu and choose Reset again to confirm") : null;
      }
   }

   private void renderColorDialog(Md3Theme t, int mouseX, int mouseY) {
      float cardW = Math.min(400.0F, this.field_1230 - 24.0F);
      float cardH = Math.min(430.0F, this.field_1231 - 12.0F);
      float x = (this.field_1230 - cardW) / 2.0F;
      float y = (this.field_1231 - cardH) / 2.0F;
      this.dialogX = x;
      this.dialogY = y;
      this.dialogW = cardW;
      this.dialogH = cardH;
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, Md3Colors.alpha(t.colors.scrim, 0.58F));
      Md3Surfaces.dialog(this.canvas, t, x, y, cardW, cardH);
      this.canvas.text(this.tr("选择颜色", "Pick a color"), x + 24.0F, y + 18.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
      int argb = hsvToArgb(this.pickerH, this.pickerS, this.pickerV, this.pickerA);
      String hex = ClientConfig.toHex(argb).toUpperCase();
      float hw = this.canvas.textWidth(hex, Md3Type.LABEL_MEDIUM);
      this.canvas.roundRect(x + cardW - 24.0F - hw - 16.0F, y + 18.0F, hw + 16.0F, 24.0F, 12.0F, t.colors.surfaceContainerHighest);
      Md3Text.centered(this.canvas, hex, x + cardW - 24.0F - hw / 2.0F - 8.0F, y + 30.0F, Md3Type.LABEL_MEDIUM, t.colors.onSurfaceVariant);
      float sw = 48.0F;
      float sx = x + 24.0F;
      float sy = y + 52.0F;
      this.drawChecker(this.canvas, sx, sy, sw, sw, 6.0F);
      this.canvas.roundRect(sx, sy, sw, sw, 12.0F, argb);
      this.canvas.strokeRoundRect(sx, sy, sw, sw, 12.0F, 1.0F, t.colors.outline);
      float cell = 20.0F;
      float gap = 4.0F;
      float gx = x + 24.0F;
      float gy = y + 116.0F;

      for (int i = 0; i < PRESETS.length; i++) {
         float cx = gx + i % 6 * (cell + gap) + cell / 2.0F;
         float cy = gy + i / 6 * (cell + gap) + cell / 2.0F;
         this.drawChecker(this.canvas, cx - cell / 2.0F, cy - cell / 2.0F, cell, cell, 4.0F);
         this.canvas.roundRect(cx - cell / 2.0F + 1.0F, cy - cell / 2.0F + 1.0F, cell - 2.0F, cell - 2.0F, (cell - 2.0F) / 2.0F, PRESETS[i]);
         if (ClientUi.hit(mouseX, mouseY, cx - cell / 2.0F - 1.0F, cy - cell / 2.0F - 1.0F, cell + 2.0F, cell + 2.0F)) {
            this.canvas
               .strokeRoundRect(cx - cell / 2.0F - 2.0F, cy - cell / 2.0F - 2.0F, cell + 4.0F, cell + 4.0F, (cell + 4.0F) / 2.0F, 2.0F, t.colors.primary);
         }
      }

      float barX = x + 190.0F;
      if (barX > x + cardW - 120.0F) {
         barX = x + cardW * 0.47F;
      }

      float barW = Math.max(60.0F, cardW - (barX - x) - 52.0F);
      float barY = y + 58.0F;
      float step = Math.min(44.0F, (cardH - 128.0F) / 4.0F);
      this.pickerBarX = barX;
      this.pickerBarY = barY;
      this.pickerBarW = barW;
      this.pickerBarStep = step;
      String[] tags = new String[]{"H", "S", "V", "A"};
      float[] vals = new float[]{this.pickerH, this.pickerS, this.pickerV, this.pickerA / 255.0F};

      for (int ix = 0; ix < 4; ix++) {
         float by = barY + ix * step + 14.0F;
         this.canvas.text(tags[ix], barX - 22.0F, by - 7.0F, Md3Type.LABEL_MEDIUM, t.colors.onSurfaceVariant);
         this.drawGradientBar(ix, barX, by, barW, vals[ix]);
         String num = ix == 0 ? String.format("%.0f", vals[ix] * 360.0F) : String.format("%d%%", Math.round(vals[ix] * 100.0F));
         this.canvas.text(num, barX + barW + 8.0F, by - 7.0F, Md3Type.LABEL_MEDIUM, t.colors.onSurfaceVariant);
      }

      float bw = 100.0F;
      float bh = 38.0F;
      float bx = x + cardW - 24.0F - bw;
      float by = y + cardH - 16.0F - bh;
      boolean hot = ClientUi.hit(mouseX, mouseY, bx, by, bw, bh);
      this.canvas
         .roundRect(bx, by, bw, bh, bh / 2.0F, hot ? Md3Colors.composite(Md3Colors.alpha(t.colors.onPrimary, 0.12F), t.colors.primary) : t.colors.primary);
      Md3Text.centered(this.canvas, this.tr("完成", "Done"), bx + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onPrimary);
   }

   private void drawGradientBar(int kind, float x, float y, float w, float value) {
      if (kind == 3) {
         this.drawChecker(this.canvas, x - 2.0F, y - 4.0F, w + 4.0F, 16.0F, 4.0F);
      }

      int bands = 40;

      for (int i = 0; i < bands; i++) {
         float p0 = (float)i / bands;
         float p1 = (float)(i + 1) / bands;
         int color = Md3Colors.lerp(this.gradientColor(kind, p0), this.gradientColor(kind, p1), 0.5F);
         this.canvas.fill(x + 1.0F + (w - 2.0F) * p0, y - 2.0F, (w - 2.0F) * (p1 - p0) + 0.5F, 12.0F, color);
      }

      this.canvas.strokeRoundRect(x, y - 3.0F, w, 14.0F, 7.0F, 1.0F, Md3Colors.alpha(ClientTheme.get().colors.outline, 0.35F));
      float thumbX = x + 7.0F + value * Math.max(0.0F, w - 14.0F);
      this.canvas.roundRect(thumbX - 7.0F, y - 5.0F, 14.0F, 18.0F, 7.0F, -1);
      this.canvas.strokeRoundRect(thumbX - 7.0F, y - 5.0F, 14.0F, 18.0F, 7.0F, 1.0F, Md3Colors.alpha(ClientTheme.get().colors.onSurface, 0.22F));
   }

   private int gradientColor(int kind, float v) {
      if (kind == 0) {
         return hsvToArgb(v, 1.0F, 1.0F, 255);
      } else if (kind == 1) {
         return hsvToArgb(this.pickerH, v, this.pickerV, 255);
      } else {
         return kind == 2 ? hsvToArgb(this.pickerH, this.pickerS, v, 255) : hsvToArgb(this.pickerH, this.pickerS, this.pickerV, Math.round(v * 255.0F));
      }
   }

   private void renderCrosshairDialog(Md3Theme t, int mouseX, int mouseY) {
      float cardW = Math.min(340.0F, this.field_1230 - 24.0F);
      float cardH = Math.min(410.0F, this.field_1231 - 12.0F);
      float x = (this.field_1230 - cardW) / 2.0F;
      float y = (this.field_1231 - cardH) / 2.0F;
      this.dialogX = x;
      this.dialogY = y;
      this.dialogW = cardW;
      this.dialogH = cardH;
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, Md3Colors.alpha(t.colors.scrim, 0.58F));
      Md3Surfaces.dialog(this.canvas, t, x, y, cardW, cardH);
      this.canvas.text(this.tr("编辑准星", "Edit crosshair"), x + 22.0F, y + 16.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
      this.canvas.text(this.tr("左键点亮 / 右键擦掉，可拖动", "Left: paint / right: erase"), x + 22.0F, y + 38.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      float preview = 48.0F;
      float px = x + cardW - 22.0F - preview;
      float py = y + 14.0F;
      this.canvas.roundRect(px, py, preview, preview, 10.0F, t.colors.surfaceContainerHighest);
      float cellP = preview / 9.0F;

      for (int r = 0; r < 9; r++) {
         for (int c = 0; c < 9; c++) {
            if (this.maskAt(r, c)) {
               this.canvas.fill(px + c * cellP + 1.0F, py + r * cellP + 1.0F, cellP - 2.0F, cellP - 2.0F, ClientConfig.crosshairColor);
            }
         }
      }

      float grid = this.crosshairGrid(cardW, cardH);
      float gx = x + (cardW - grid) / 2.0F;
      float gy = y + 66.0F;
      float cell = grid / 9.0F;
      this.canvas.roundRect(gx - 6.0F, gy - 6.0F, grid + 12.0F, grid + 12.0F, 12.0F, t.colors.surfaceContainerHighest);

      for (int r = 0; r < 9; r++) {
         for (int cx = 0; cx < 9; cx++) {
            float cxx = gx + cx * cell;
            float cy = gy + r * cell;
            boolean on = this.maskAt(r, cx);
            boolean hot = ClientUi.hit(mouseX, mouseY, cxx, cy, cell, cell);
            this.canvas
               .roundRect(
                  cxx + 1.0F,
                  cy + 1.0F,
                  cell - 2.0F,
                  cell - 2.0F,
                  4.0F,
                  on ? ClientConfig.crosshairColor : (hot ? Md3Colors.alpha(t.colors.primary, 0.18F) : t.colors.surface)
               );
         }
      }

      float bh = 40.0F;
      float bw = Math.min(96.0F, (cardW - 60.0F) / 2.0F);
      float by = y + cardH - 18.0F - bh;
      float bx2 = x + cardW - 22.0F - bw;
      float bx1 = bx2 - 10.0F - bw;
      boolean doneHot = ClientUi.hit(mouseX, mouseY, bx2, by, bw, bh);
      this.canvas
         .roundRect(bx2, by, bw, bh, bh / 2.0F, doneHot ? Md3Colors.composite(Md3Colors.alpha(t.colors.onPrimary, 0.12F), t.colors.primary) : t.colors.primary);
      Md3Text.centered(this.canvas, this.tr("完成", "Done"), bx2 + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onPrimary);
      boolean resetHot = ClientUi.hit(mouseX, mouseY, bx1, by, bw, bh);
      this.canvas
         .roundRect(
            bx1,
            by,
            bw,
            bh,
            bh / 2.0F,
            resetHot ? Md3Colors.composite(Md3Colors.alpha(t.colors.primary, 0.06F), t.colors.surfaceContainerHighest) : t.colors.surfaceContainerHighest
         );
      Md3Text.centered(this.canvas, this.tr("重置", "Reset"), bx1 + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onSurface);
   }

   @Override
   public void method_1035() {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0 && this.pickerKey < 0 && !this.crosshairEditor && this.menuKey < 0 && !this.appMenuOpen) {
         if (this.detailGid < 0 && ClientUi.hit(this.mouseNowX, this.mouseNowY, this.tabsX, this.tabsY, this.tabsW, this.tabsH)) {
            this.tabScrollX = this.tabScrollX + -Integer.signum(wheel) * 54.0F;
         } else if (ClientUi.hit(this.mouseNowX, this.mouseNowY, this.contentX, this.contentY, this.contentW, this.contentH)) {
            this.scroll.scrollBy(-Integer.signum(wheel) * 56.0F);
         }
      }

      super.method_1035();
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (this.openFrames >= 3) {
         this.mouseNowX = mouseX;
         this.mouseNowY = mouseY;
         if (button == 0) {
            this.mouseDown = true;
         }

         if (this.appMenuOpen) {
            if (ClientUi.hit(mouseX, mouseY, this.menuBtnX, this.menuBtnY, this.menuBtnS, this.menuBtnS)) {
               this.appMenuOpen = false;
            } else {
               int index = this.appMenu.indexAt(mouseX, mouseY);
               this.appMenuOpen = false;
               if (index == 0) {
                  this.field_1229.method_2928(new ModListScreen(this));
               } else if (index == 1) {
                  long now = System.currentTimeMillis();
                  if (now < this.resetConfirmUntil) {
                     this.resetAll();
                  } else {
                     this.resetConfirmUntil = now + 3000L;
                     this.snack = this.tr("再点一次「重置所有设置」确认", "Use Reset again to confirm");
                     this.snackUntil = now + 3000L;
                     ClientUi.click();
                  }
               }
            }
         } else if (this.menuKey >= 0) {
            int index = this.menu.indexAt(mouseX, mouseY);
            if (index >= 0) {
               this.setCycle(this.menuKey, index);
               ClientConfig.saveSoon();
               ClientUi.click();
            }

            this.closeMenu();
         } else if (this.pickerKey >= 0) {
            this.handlePickerClick(mouseX, mouseY);
         } else if (this.crosshairEditor) {
            this.crosshairClick(mouseX, mouseY, button);
         } else if (this.detailGid >= 0) {
            this.detailClick(mouseX, mouseY, button);
         } else if (button == 1) {
            for (ClientScreen.RowBox box : this.boxes) {
               if (box.kind == 1 && ClientUi.hit(mouseX, mouseY, box.ctrlX, box.ctrlY - 6.0F, box.ctrlW, box.ctrlH + 12.0F)) {
                  this.stepSlider(box.key, -1);
                  return;
               }
            }
         } else if (button == 0) {
            if (ClientUi.hit(mouseX, mouseY, this.searchX, this.searchY, this.searchW, this.searchH)) {
               if (!this.search.text.isEmpty() && mouseX >= this.searchX + this.searchW - 36.0F) {
                  this.search.clear();
                  this.searchText = "";
                  this.applyFilter();
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
               if (ClientUi.hit(mouseX, mouseY, this.closeBtnX, this.closeBtnY, this.closeBtnS, this.closeBtnS)) {
                  this.close();
               } else if (ClientUi.hit(mouseX, mouseY, this.menuBtnX, this.menuBtnY, this.menuBtnS, this.menuBtnS)) {
                  if (this.appMenuOpen) {
                     this.appMenuOpen = false;
                  } else {
                     this.openAppMenu();
                  }
               } else if (!ClientUi.hit(mouseX, mouseY, this.themeBtnX, this.themeBtnY, this.themeBtnS, this.themeBtnS)) {
                  for (ClientScreen.TabBox tab : this.tabBoxes) {
                     if (ClientUi.hit(mouseX, mouseY, tab.x, tab.y, tab.w, tab.h)) {
                        ClientUi.click();
                        this.selectedCat = tab.index;
                        this.applyFilter();
                        return;
                     }
                  }

                  if (!this.scroll.press(mouseX, mouseY, this.contentX + this.contentW + 3.0F, this.contentY + 2.0F, 3.0F, 3.0F)) {
                     if (ClientUi.hit(mouseX, mouseY, this.contentX, this.contentY, this.contentW, this.contentH)) {
                        for (ClientScreen.TileBox boxx : this.tileBoxes) {
                           if (ClientUi.hit(mouseX, mouseY, boxx.x, boxx.y, boxx.w, boxx.h)) {
                              ClientUi.click();
                              if (boxx.gid == -1) {
                                 if (this.field_1229.field_3803 != null) {
                                    this.field_1229.method_2928(new ReplayControlScreen(this));
                                 } else {
                                    this.close();
                                    ReplayClient.openConsole();
                                 }

                                 return;
                              }

                              ClientScreen.Group group = this.findGroup(boxx.gid);
                              if (group != null) {
                                 this.openDetail(group);
                              }

                              return;
                           }
                        }

                        for (ClientScreen.RowBox boxxx : this.boxes) {
                           boolean inside = ClientUi.hit(mouseX, mouseY, boxxx.x, boxxx.y, boxxx.w, boxxx.h);
                           if (boxxx.kind != 1) {
                              if (boxxx.kind != 0) {
                                 if (inside || ClientUi.hit(mouseX, mouseY, boxxx.ctrlX, boxxx.ctrlY, boxxx.ctrlW, boxxx.ctrlH)) {
                                    this.rowAction(boxxx);
                                    return;
                                 }
                              } else if (inside) {
                                 this.toggle(boxxx.key);
                                 this.syncSwitch(boxxx.key);
                                 ClientConfig.save();
                                 ClientUi.click();
                                 if (boxxx.key == 66) {
                                    this.close();
                                    return;
                                 }
                              }
                           } else if (ClientUi.hit(mouseX, mouseY, boxxx.ctrlX, boxxx.ctrlY - 6.0F, boxxx.ctrlW, boxxx.ctrlH + 12.0F)) {
                              this.sliderKey = boxxx.key;
                              float track = Math.max(1.0F, boxxx.ctrlW - 14.0F);
                              float firstNorm = this.clamp01((mouseX - boxxx.ctrlX - 7.0F) / track);
                              this.sliderNorm = firstNorm;
                              this.sliderStartX = mouseX;
                              this.sliderStartNorm = firstNorm;
                              this.applyNormSmooth(boxxx.key, firstNorm);
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

   private void rowAction(ClientScreen.RowBox box) {
      if (box.kind == 2) {
         this.openPicker(box.key);
         ClientUi.click();
      } else if (box.kind == 3) {
         this.openMenu(box.key, box);
         ClientUi.click();
      } else if (box.kind == 5) {
         if (box.key == 45) {
            this.crosshairEditor = true;
            this.editorDrag = -1;
            ClientUi.click();
         } else if (box.key == 64) {
            this.bindingKey = true;
            ClientUi.click();
         } else if (box.key == 106) {
            this.field_1229.method_2928(new ScoreboardSettingsScreen(this));
            ClientUi.click();
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
      this.sliderKey = -1;
      this.scroll.release();
      this.pickerDrag = -1;
      this.editorDrag = -1;
   }

   @Override
   protected void method_4259(int mouseX, int mouseY, int button, long mouseLastClicked) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      if (this.pickerKey >= 0 && this.pickerDrag >= 0) {
         this.updatePicker(mouseX);
      } else if (this.crosshairEditor && this.editorDrag >= 0) {
         this.crosshairDrag(mouseX, mouseY);
      } else if (this.menuKey < 0) {
         if (this.sliderKey >= 0) {
            ClientScreen.RowBox box = this.boxFor(this.sliderKey);
            if (box != null) {
               this.updateSlider(mouseX, box);
            }
         } else {
            if (this.scroll.dragging) {
               this.scroll.drag(mouseY, this.contentY + 2.0F);
            }
         }
      }
   }

   @Override
   protected void method_1024(char id, int code) {
      if (this.openFrames >= 3) {
         if (this.appMenuOpen) {
            if (code == 1) {
               this.appMenuOpen = false;
            }
         } else if (this.pickerKey >= 0) {
            if (code == 1) {
               this.pickerKey = -1;
            }
         } else if (this.crosshairEditor) {
            if (code == 1) {
               this.crosshairEditor = false;
            }
         } else if (this.menuKey >= 0) {
            if (code == 1) {
               this.closeMenu();
            }
         } else if (this.bindingKey) {
            if (code != 1) {
               ClientConfig.freelookKey = code;
               ClientConfig.saveSoon();
            }

            this.bindingKey = false;
         } else if (this.detailGid >= 0) {
            if (code == 1) {
               this.closeDetail();
            } else if (code == 208) {
               this.scroll.scrollBy(56.0F);
            } else if (code == 200) {
               this.scroll.scrollBy(-56.0F);
            }
         } else if (this.searchFocus) {
            if (!ImeBridge.handleKey(id, code)) {
               if (code == 1) {
                  this.search.clear();
                  this.searchText = "";
                  this.searchFocus = false;
                  this.search.focused = false;
                  this.applyFilter();
               } else if (code == 28 || code == 156) {
                  this.searchFocus = false;
                  this.search.focused = false;
               } else if (code == 14) {
                  this.search.backspace();
                  this.searchText = this.search.text;
                  this.applyFilter();
               } else {
                  if (id >= ' ' && id != 127) {
                     this.search.typeChar(id);
                     this.searchText = this.search.text;
                     this.applyFilter();
                  }
               }
            }
         } else {
            if (code == 1) {
               this.close();
            } else if (code == 208) {
               this.scroll.scrollBy(56.0F);
            } else if (code == 200) {
               this.scroll.scrollBy(-56.0F);
            }
         }
      }
   }

   private void close() {
      ClientConfig.save();
      if (this.field_1229.field_3803 == null) {
         this.field_1229.method_2928(new class_624());
      } else {
         this.field_1229.method_2928(null);
      }
   }

   private void openMenu(int key, ClientScreen.RowBox box) {
      this.menuKey = key;
      this.menuIndex = this.cycleIndex(key);
      this.menu.items = this.cycleNames(key);
      this.menu.selected = this.menuIndex;
      float width = Math.max(140.0F, box.ctrlW + 8.0F);
      float height = this.menu.height();
      float mx = box.ctrlX + box.ctrlW - width;
      if (mx < this.contentX + 8.0F) {
         mx = this.contentX + 8.0F;
      }

      float my = box.ctrlY + box.ctrlH + 4.0F;
      if (my + height > this.field_1231 - 8.0F) {
         my = box.ctrlY - height - 4.0F;
      }

      if (my < 8.0F) {
         my = 8.0F;
      }

      this.menu.x = mx;
      this.menu.y = my;
      this.menu.w = width;
      this.menu.open();
   }

   private void closeMenu() {
      this.menuKey = -1;
      this.menuIndex = -1;
   }

   private void openPicker(int key) {
      int color = this.colorOf(key);
      float[] hsv = argbToHsv(color);
      this.pickerH = hsv[0];
      this.pickerS = hsv[1];
      this.pickerV = hsv[2];
      this.pickerA = color >> 24 & 0xFF;
      this.pickerKey = key;
      this.pickerDrag = -1;
   }

   private void handlePickerClick(int mouseX, int mouseY) {
      float x = this.dialogX;
      float y = this.dialogY;
      float cardW = this.dialogW;
      float cardH = this.dialogH;
      float bw = 100.0F;
      float bh = 38.0F;
      float bx = x + cardW - 24.0F - bw;
      float by = y + cardH - 16.0F - bh;
      if (ClientUi.hit(mouseX, mouseY, bx, by, bw, bh)) {
         this.pickerKey = -1;
         this.pickerDrag = -1;
         ClientUi.click();
      } else {
         float cell = 20.0F;
         float gap = 4.0F;
         float gx = x + 24.0F;
         float gy = y + 116.0F;

         for (int i = 0; i < PRESETS.length; i++) {
            float cx = gx + i % 6 * (cell + gap) + cell / 2.0F;
            float cy = gy + i / 6 * (cell + gap) + cell / 2.0F;
            if (ClientUi.hit(mouseX, mouseY, cx - cell / 2.0F - 1.0F, cy - cell / 2.0F - 1.0F, cell + 2.0F, cell + 2.0F)) {
               float[] hsv = argbToHsv(PRESETS[i]);
               this.pickerH = hsv[0];
               this.pickerS = hsv[1];
               this.pickerV = hsv[2];
               this.pickerA = PRESETS[i] >> 24 & 0xFF;
               this.applyPicker();
               ClientUi.click();
               return;
            }
         }

         for (int ix = 0; ix < 4; ix++) {
            float by2 = this.pickerBarY + ix * this.pickerBarStep + 14.0F;
            if (ClientUi.hit(mouseX, mouseY, this.pickerBarX - 8.0F, by2 - 12.0F, this.pickerBarW + 16.0F, 28.0F)) {
               this.pickerDrag = ix;
               this.updatePicker(mouseX);
               return;
            }
         }
      }
   }

   private void updatePicker(int mouseX) {
      float v = (mouseX - this.pickerBarX) / Math.max(1.0F, this.pickerBarW);
      if (v < 0.0F) {
         v = 0.0F;
      }

      if (v > 1.0F) {
         v = 1.0F;
      }

      if (this.pickerDrag == 0) {
         this.pickerH = v;
      } else if (this.pickerDrag == 1) {
         this.pickerS = v;
      } else if (this.pickerDrag == 2) {
         this.pickerV = v;
      } else if (this.pickerDrag == 3) {
         this.pickerA = Math.round(v * 255.0F);
      }

      this.applyPicker();
   }

   private void applyPicker() {
      if (this.pickerKey >= 0) {
         this.setColor(this.pickerKey, hsvToArgb(this.pickerH, this.pickerS, this.pickerV, this.pickerA));
         ClientConfig.saveSoon();
      }
   }

   private float crosshairGrid(float cardW, float cardH) {
      float grid = Math.min(234.0F, cardW - 48.0F);
      grid = Math.min(grid, cardH - 140.0F);
      return Math.max(54.0F, grid);
   }

   private void crosshairClick(int mouseX, int mouseY, int button) {
      float cardW = this.dialogW;
      float cardH = this.dialogH;
      float x = this.dialogX;
      float y = this.dialogY;
      float bh = 40.0F;
      float bw = Math.min(96.0F, (cardW - 60.0F) / 2.0F);
      float by = y + cardH - 18.0F - bh;
      float bx2 = x + cardW - 22.0F - bw;
      float bx1 = bx2 - 10.0F - bw;
      if (button == 0 && ClientUi.hit(mouseX, mouseY, bx2, by, bw, bh)) {
         this.crosshairEditor = false;
         this.editorDrag = -1;
         ClientConfig.saveSoon();
         ClientUi.click();
      } else if (button == 0 && ClientUi.hit(mouseX, mouseY, bx1, by, bw, bh)) {
         ClientConfig.crosshairMask = "000010000/000010000/000010000/000010000/111111111/000010000/000010000/000010000/000010000";
         ClientConfig.saveSoon();
         ClientUi.click();
      } else if (button == 0 || button == 1) {
         float grid = this.crosshairGrid(cardW, cardH);
         float gx = x + (cardW - grid) / 2.0F;
         float gy = y + 66.0F;
         float cell = grid / 9.0F;
         if (ClientUi.hit(mouseX, mouseY, gx, gy, grid, grid)) {
            int col = (int)((mouseX - gx) / cell);
            int row = (int)((mouseY - gy) / cell);
            if (col >= 0 && col < 9 && row >= 0 && row < 9) {
               this.setMask(row, col, button == 0);
               ClientConfig.saveSoon();
               this.editorDrag = button == 0 ? 1 : 0;
            }
         }
      }
   }

   private void crosshairDrag(int mouseX, int mouseY) {
      float grid = this.crosshairGrid(this.dialogW, this.dialogH);
      float gx = this.dialogX + (this.dialogW - grid) / 2.0F;
      float gy = this.dialogY + 66.0F;
      float cell = grid / 9.0F;
      if (ClientUi.hit(mouseX, mouseY, gx, gy, grid, grid)) {
         int col = (int)((mouseX - gx) / cell);
         int row = (int)((mouseY - gy) / cell);
         boolean on = this.editorDrag == 1;
         if (col >= 0 && col < 9 && row >= 0 && row < 9 && this.maskAt(row, col) != on) {
            this.setMask(row, col, on);
            ClientConfig.saveSoon();
         }
      }
   }

   private void updateSlider(int mouseX, ClientScreen.RowBox box) {
      float track = Math.max(1.0F, box.ctrlW - 14.0F);
      float v = this.sliderStartNorm + (mouseX - this.sliderStartX) / track;
      this.sliderNorm = this.clamp01(v);
      this.applyNormSmooth(box.key, this.sliderNorm);
   }

   private void applyNormSmooth(int key, float norm) {
      float min = this.minOf(key);
      float max = this.maxOf(key);
      this.setValue(key, min + norm * (max - min));
      ClientConfig.saveSoon();
   }

   private void stepSlider(int key, int dir) {
      float norm = this.clamp01(this.normOf(key) + dir * this.stepOf(key) / (this.maxOf(key) - this.minOf(key)));
      this.applyNorm(key, norm);
      ClientUi.click();
   }

   private ClientScreen.RowBox boxFor(int key) {
      for (ClientScreen.RowBox box : this.boxes) {
         if (box.key == key) {
            return box;
         }
      }

      return null;
   }

   private void resetAll() {
      ClientConfig.resetAll();
      ClientConfig.save();
      ImeBridge.onConfigChanged();
      this.lastSwitchOn.clear();
      this.pickerKey = -1;
      this.crosshairEditor = false;
      this.resetConfirmUntil = 0L;
      this.snack = this.tr("已恢复默认设置", "Defaults restored");
      this.snackUntil = System.currentTimeMillis() + 2400L;
      this.buildGroups();
      this.applyFilter();
      this.scroll.jump(0.0F);
      ClientUi.click();
   }

   private String label(int key) {
      int i = key - 1;
      return i >= 0 && i < SettingsSpec.LABELS.length ? SettingsSpec.LABELS[i][ClientConfig.langIndex()] : "";
   }

   private String actionText(int key) {
      if (key == 45) {
         return this.tr("编辑", "Edit");
      } else {
         return key == 64 ? Keyboard.getKeyName(ClientConfig.freelookKey) : this.tr("打开", "Open");
      }
   }

   private boolean isOn(int key) {
      switch (key) {
         case 6:
            return ClientConfig.fovClient;
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 17:
         case 19:
         case 21:
         case 22:
         case 24:
         case 25:
         case 27:
         case 29:
         case 30:
         case 33:
         case 34:
         case 36:
         case 38:
         case 40:
         case 41:
         case 44:
         case 45:
         case 50:
         case 51:
         case 57:
         case 58:
         case 59:
         case 60:
         case 62:
         case 64:
         case 67:
         case 68:
         case 69:
         case 70:
         case 72:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 79:
         case 81:
         case 85:
         case 87:
         case 89:
         case 91:
         case 93:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 106:
         case 107:
         case 108:
         case 110:
         default:
            return false;
         case 14:
            return ClientConfig.customGlint;
         case 16:
            return ClientConfig.fogModify;
         case 18:
            return ClientConfig.fogSky;
         case 20:
            return ClientConfig.highlightBlock;
         case 23:
            return ClientConfig.selfNameTag;
         case 26:
            return ClientConfig.chatBackground;
         case 28:
            return ClientConfig.scoreboardBackground;
         case 31:
            return ClientConfig.weatherThroughBlocks;
         case 32:
            return ClientConfig.rawInput;
         case 35:
            return ClientConfig.timeOverride;
         case 37:
            return ClientConfig.motionBlur;
         case 39:
            return ClientConfig.hurtColorOn;
         case 42:
            return ClientConfig.realDrops;
         case 43:
            return ClientConfig.customCrosshair;
         case 46:
            return ClientConfig.noHurtCam;
         case 47:
            return ClientConfig.fullbright;
         case 48:
            return ClientConfig.minimalBobbing;
         case 49:
            return ClientConfig.autoSprint;
         case 52:
            return !ClientConfig.chatVisible;
         case 53:
            return ClientConfig.hideScoreboard;
         case 54:
            return ClientConfig.hideScoreNumbers;
         case 55:
            return ClientConfig.blockHitAnim;
         case 56:
            return ClientConfig.afterimage;
         case 61:
            return ClientConfig.afterimageSelf;
         case 63:
            return ClientConfig.freelook;
         case 65:
            return ClientConfig.dropRest;
         case 66:
            return ClientConfig.uiOverride;
         case 71:
            return ClientConfig.hideOwnParticles;
         case 76:
            return ClientConfig.customParticles;
         case 80:
            return ClientConfig.imeFix;
         case 82:
            return ClientConfig.hideMiningParticles;
         case 83:
            return ClientConfig.hideSprintParticles;
         case 84:
            return ClientConfig.fovSmooth;
         case 86:
            return ClientConfig.fovSprintOn;
         case 88:
            return ClientConfig.fovSpeedOn;
         case 90:
            return ClientConfig.fovSlowOn;
         case 92:
            return ClientConfig.fovFlyOn;
         case 94:
            return ClientConfig.fovAimOn;
         case 96:
            return ClientConfig.fovWaterOn;
         case 104:
            return ClientConfig.modernFont;
         case 105:
            return ClientConfig.scoreboardCustom;
         case 109:
            return ClientConfig.leftHanded;
         case 111:
            return ClientConfig.hurtBlinkOff;
      }
   }

   private void syncSwitch(int key) {
      Md3Switch sw = this.switches.get(key);
      if (sw != null) {
         sw.setCheckedAnimated(this.isOn(key));
      }

      this.lastSwitchOn.put(key, this.isOn(key));
   }

   private void toggle(int key) {
      switch (key) {
         case 6:
            ClientConfig.fovClient = !ClientConfig.fovClient;
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 17:
         case 19:
         case 21:
         case 22:
         case 24:
         case 25:
         case 27:
         case 29:
         case 30:
         case 33:
         case 34:
         case 36:
         case 38:
         case 40:
         case 41:
         case 44:
         case 45:
         case 50:
         case 51:
         case 57:
         case 58:
         case 59:
         case 60:
         case 62:
         case 64:
         case 67:
         case 68:
         case 69:
         case 70:
         case 72:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 79:
         case 81:
         case 85:
         case 87:
         case 89:
         case 91:
         case 93:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 106:
         case 107:
         case 108:
         case 110:
         default:
            break;
         case 14:
            ClientConfig.customGlint = !ClientConfig.customGlint;
            break;
         case 16:
            ClientConfig.fogModify = !ClientConfig.fogModify;
            break;
         case 18:
            ClientConfig.fogSky = !ClientConfig.fogSky;
            break;
         case 20:
            ClientConfig.highlightBlock = !ClientConfig.highlightBlock;
            break;
         case 23:
            ClientConfig.selfNameTag = !ClientConfig.selfNameTag;
            break;
         case 26:
            ClientConfig.chatBackground = !ClientConfig.chatBackground;
            break;
         case 28:
            ClientConfig.scoreboardBackground = !ClientConfig.scoreboardBackground;
            break;
         case 31:
            ClientConfig.weatherThroughBlocks = !ClientConfig.weatherThroughBlocks;
            break;
         case 32:
            ClientConfig.rawInput = !ClientConfig.rawInput;
            break;
         case 35:
            ClientConfig.timeOverride = !ClientConfig.timeOverride;
            break;
         case 37:
            ClientConfig.motionBlur = !ClientConfig.motionBlur;
            break;
         case 39:
            ClientConfig.hurtColorOn = !ClientConfig.hurtColorOn;
            break;
         case 42:
            ClientConfig.realDrops = !ClientConfig.realDrops;
            break;
         case 43:
            ClientConfig.customCrosshair = !ClientConfig.customCrosshair;
            break;
         case 46:
            ClientConfig.noHurtCam = !ClientConfig.noHurtCam;
            break;
         case 47:
            ClientConfig.fullbright = !ClientConfig.fullbright;
            break;
         case 48:
            ClientConfig.minimalBobbing = !ClientConfig.minimalBobbing;
            break;
         case 49:
            ClientConfig.autoSprint = !ClientConfig.autoSprint;
            break;
         case 52:
            ClientConfig.chatVisible = !ClientConfig.chatVisible;
            break;
         case 53:
            ClientConfig.hideScoreboard = !ClientConfig.hideScoreboard;
            break;
         case 54:
            ClientConfig.hideScoreNumbers = !ClientConfig.hideScoreNumbers;
            break;
         case 55:
            ClientConfig.blockHitAnim = !ClientConfig.blockHitAnim;
            break;
         case 56:
            ClientConfig.afterimage = !ClientConfig.afterimage;
            break;
         case 61:
            ClientConfig.afterimageSelf = !ClientConfig.afterimageSelf;
            break;
         case 63:
            ClientConfig.freelook = !ClientConfig.freelook;
            break;
         case 65:
            ClientConfig.dropRest = !ClientConfig.dropRest;
            break;
         case 66:
            ClientConfig.uiOverride = !ClientConfig.uiOverride;
            ClientConfig.save();
            break;
         case 71:
            ClientConfig.hideOwnParticles = !ClientConfig.hideOwnParticles;
            break;
         case 76:
            ClientConfig.customParticles = !ClientConfig.customParticles;
            break;
         case 80:
            ClientConfig.imeFix = !ClientConfig.imeFix;
            ImeBridge.onConfigChanged();
            break;
         case 82:
            ClientConfig.hideMiningParticles = !ClientConfig.hideMiningParticles;
            break;
         case 83:
            ClientConfig.hideSprintParticles = !ClientConfig.hideSprintParticles;
            break;
         case 84:
            ClientConfig.fovSmooth = !ClientConfig.fovSmooth;
            break;
         case 86:
            ClientConfig.fovSprintOn = !ClientConfig.fovSprintOn;
            break;
         case 88:
            ClientConfig.fovSpeedOn = !ClientConfig.fovSpeedOn;
            break;
         case 90:
            ClientConfig.fovSlowOn = !ClientConfig.fovSlowOn;
            break;
         case 92:
            ClientConfig.fovFlyOn = !ClientConfig.fovFlyOn;
            break;
         case 94:
            ClientConfig.fovAimOn = !ClientConfig.fovAimOn;
            break;
         case 96:
            ClientConfig.fovWaterOn = !ClientConfig.fovWaterOn;
            break;
         case 104:
            ClientConfig.modernFont = !ClientConfig.modernFont;
            break;
         case 105:
            ClientConfig.scoreboardCustom = !ClientConfig.scoreboardCustom;
            break;
         case 109:
            ClientConfig.leftHanded = !ClientConfig.leftHanded;
            break;
         case 111:
            ClientConfig.hurtBlinkOff = !ClientConfig.hurtBlinkOff;
      }
   }

   private String valueText(int key) {
      switch (key) {
         case 7:
            return String.format("%.0f", ClientConfig.fovBase);
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 76:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 86:
         case 88:
         case 90:
         case 92:
         case 94:
         case 96:
         default:
            return "";
         case 19:
            return String.format("%.2f", ClientConfig.fogDensity);
         case 36:
            return ticksToClock(ClientConfig.timeTicks);
         case 38:
            return String.format("%.0f%%", ClientConfig.blurAmount * 100.0F);
         case 50:
            return Blur.radiusValue() + "%";
         case 58:
            return ClientConfig.afterimageLife + " ms";
         case 59:
            return String.format("%.2fx", ClientConfig.afterimageStrength);
         case 60:
            return Integer.toString(ClientConfig.afterimageLayers);
         case 72:
            return ClientConfig.fireHeight + "%";
         case 73:
            return this.handText(ClientConfig.handForward);
         case 74:
            return this.handText(ClientConfig.handRight);
         case 75:
            return this.handText(ClientConfig.handUp);
         case 77:
            return String.format("%.1fx", ClientConfig.critParticleMultiplier);
         case 78:
            return String.format("%.1fx", ClientConfig.sharpParticleMultiplier);
         case 85:
            return String.format("%.0f%%", ClientConfig.fovGlobal * 100.0F);
         case 87:
            return String.format("%.0f", ClientConfig.fovSprint);
         case 89:
            return String.format("%.0f", ClientConfig.fovSpeed);
         case 91:
            return String.format("%.0f", ClientConfig.fovSlow);
         case 93:
            return String.format("%.0f", ClientConfig.fovFly);
         case 95:
            return String.format("%.0f", ClientConfig.fovAim);
         case 97:
            return String.format("%.0f", ClientConfig.fovWater);
      }
   }

   private String handText(float v) {
      return Math.abs(v) < 0.001F ? "0.00" : String.format("%+.2f", v);
   }

   private float minOf(int key) {
      switch (key) {
         case 7:
            return 30.0F;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 76:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 86:
         case 88:
         case 90:
         case 92:
         case 94:
         case 96:
         default:
            return 0.0F;
         case 19:
            return 0.5F;
         case 36:
            return 0.0F;
         case 38:
            return 0.05F;
         case 50:
            return 0.0F;
         case 58:
            return 300.0F;
         case 59:
            return 0.2F;
         case 60:
            return 1.0F;
         case 72:
            return 0.0F;
         case 73:
         case 74:
         case 75:
            return -2.0F;
         case 77:
         case 78:
            return 0.0F;
         case 85:
            return 0.5F;
         case 87:
         case 89:
         case 91:
         case 93:
         case 95:
         case 97:
            return 10.0F;
      }
   }

   private float maxOf(int key) {
      switch (key) {
         case 7:
            return 110.0F;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 76:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 86:
         case 88:
         case 90:
         case 92:
         case 94:
         case 96:
         default:
            return 1.0F;
         case 19:
            return 30.0F;
         case 36:
            return 23999.0F;
         case 38:
            return 1.0F;
         case 50:
            return 1.0F;
         case 58:
            return 3000.0F;
         case 59:
            return 2.0F;
         case 60:
            return 10.0F;
         case 72:
            return 100.0F;
         case 73:
         case 74:
         case 75:
            return 2.0F;
         case 77:
         case 78:
            return 5.0F;
         case 85:
            return 1.5F;
         case 87:
         case 89:
         case 91:
         case 93:
         case 95:
         case 97:
            return 160.0F;
      }
   }

   private float stepOf(int key) {
      switch (key) {
         case 19:
            return 0.1F;
         case 36:
            return 100.0F;
         case 38:
         case 50:
         case 85:
            return 0.01F;
         case 58:
            return 10.0F;
         case 59:
         case 77:
         case 78:
            return 0.01F;
         case 72:
            return 1.0F;
         case 73:
         case 74:
         case 75:
            return 0.01F;
         default:
            return 1.0F;
      }
   }

   private float valueOf(int key) {
      switch (key) {
         case 7:
            return ClientConfig.fovBase;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 76:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 86:
         case 88:
         case 90:
         case 92:
         case 94:
         case 96:
         default:
            return 0.0F;
         case 19:
            return ClientConfig.fogDensity;
         case 36:
            return ClientConfig.timeTicks;
         case 38:
            return ClientConfig.blurAmount;
         case 50:
            return Blur.radiusValue() / 100.0F;
         case 58:
            return ClientConfig.afterimageLife;
         case 59:
            return ClientConfig.afterimageStrength;
         case 60:
            return ClientConfig.afterimageLayers;
         case 72:
            return ClientConfig.fireHeight;
         case 73:
            return ClientConfig.handForward;
         case 74:
            return ClientConfig.handRight;
         case 75:
            return ClientConfig.handUp;
         case 77:
            return ClientConfig.critParticleMultiplier;
         case 78:
            return ClientConfig.sharpParticleMultiplier;
         case 85:
            return ClientConfig.fovGlobal;
         case 87:
            return ClientConfig.fovSprint;
         case 89:
            return ClientConfig.fovSpeed;
         case 91:
            return ClientConfig.fovSlow;
         case 93:
            return ClientConfig.fovFly;
         case 95:
            return ClientConfig.fovAim;
         case 97:
            return ClientConfig.fovWater;
      }
   }

   private void setValue(int key, float v) {
      switch (key) {
         case 7:
            ClientConfig.fovBase = clampF(v, 30.0F, 110.0F);
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 76:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 86:
         case 88:
         case 90:
         case 92:
         case 94:
         case 96:
         default:
            break;
         case 19:
            ClientConfig.fogDensity = clampF(v, 0.5F, 30.0F);
            break;
         case 36:
            ClientConfig.timeTicks = (int)clampF(v, 0.0F, 23999.0F);
            break;
         case 38:
            ClientConfig.blurAmount = clampF(v, 0.05F, 1.0F);
            break;
         case 50:
            Blur.setRadiusValue(Math.round(clampF(v, 0.01F, 1.0F) * 100.0F));
            break;
         case 58:
            ClientConfig.afterimageLife = (int)clampF(v, 300.0F, 3000.0F);
            break;
         case 59:
            ClientConfig.afterimageStrength = clampF(v, 0.2F, 2.0F);
            break;
         case 60:
            ClientConfig.afterimageLayers = (int)clampF(v, 1.0F, 10.0F);
            break;
         case 72:
            ClientConfig.fireHeight = (int)clampF(v, 0.0F, 100.0F);
            break;
         case 73:
            ClientConfig.handForward = clampF(v, -2.0F, 2.0F);
            break;
         case 74:
            ClientConfig.handRight = clampF(v, -2.0F, 2.0F);
            break;
         case 75:
            ClientConfig.handUp = clampF(v, -2.0F, 2.0F);
            break;
         case 77:
            ClientConfig.critParticleMultiplier = clampF(v, 0.0F, 5.0F);
            break;
         case 78:
            ClientConfig.sharpParticleMultiplier = clampF(v, 0.0F, 5.0F);
            break;
         case 85:
            ClientConfig.fovGlobal = clampF(v, 0.5F, 1.5F);
            break;
         case 87:
            ClientConfig.fovSprint = clampF(v, 10.0F, 160.0F);
            break;
         case 89:
            ClientConfig.fovSpeed = clampF(v, 10.0F, 160.0F);
            break;
         case 91:
            ClientConfig.fovSlow = clampF(v, 10.0F, 160.0F);
            break;
         case 93:
            ClientConfig.fovFly = clampF(v, 10.0F, 160.0F);
            break;
         case 95:
            ClientConfig.fovAim = clampF(v, 10.0F, 160.0F);
            break;
         case 97:
            ClientConfig.fovWater = clampF(v, 10.0F, 160.0F);
      }
   }

   private float normOf(int key) {
      float min = this.minOf(key);
      return (this.valueOf(key) - min) / (this.maxOf(key) - min);
   }

   private void applyNorm(int key, float norm) {
      float min = this.minOf(key);
      float max = this.maxOf(key);
      float step = this.stepOf(key);
      float raw = min + norm * (max - min);
      float snapped = min + Math.round((raw - min) / step) * step;
      this.setValue(key, snapped);
      ClientConfig.saveSoon();
   }

   private int colorOf(int key) {
      switch (key) {
         case 15:
            return ClientConfig.glintColor;
         case 17:
            return ClientConfig.fogColor;
         case 21:
            return ClientConfig.highlightFill;
         case 22:
            return ClientConfig.highlightOutline;
         case 24:
            return ClientConfig.nameBgColor;
         case 25:
            return ClientConfig.nameTextColor;
         case 27:
            return ClientConfig.chatBgColor;
         case 29:
            return ClientConfig.scoreboardBgColor;
         case 40:
            return ClientConfig.hurtColor;
         case 44:
            return ClientConfig.crosshairColor;
         case 57:
            return ClientConfig.afterimageColor;
         case 103:
            return ClientConfig.themeColor;
         default:
            return -16777216;
      }
   }

   private void setColor(int key, int argb) {
      switch (key) {
         case 15:
            ClientConfig.glintColor = argb;
            break;
         case 17:
            ClientConfig.fogColor = argb;
            break;
         case 21:
            ClientConfig.highlightFill = argb;
            break;
         case 22:
            ClientConfig.highlightOutline = argb;
            break;
         case 24:
            ClientConfig.nameBgColor = argb;
            break;
         case 25:
            ClientConfig.nameTextColor = argb;
            break;
         case 27:
            ClientConfig.chatBgColor = argb;
            break;
         case 29:
            ClientConfig.scoreboardBgColor = argb;
            break;
         case 40:
            ClientConfig.hurtColor = argb;
            break;
         case 44:
            ClientConfig.crosshairColor = argb;
            break;
         case 57:
            ClientConfig.afterimageColor = argb;
            break;
         case 103:
            ClientConfig.themeColor = 0xFF000000 | argb & 16777215;
      }
   }

   private String[] cycleNames(int key) {
      int lang = ClientConfig.langIndex();
      if (key == 30) {
         String[] out = new String[SettingsSpec.WEATHER_NAMES.length];

         for (int i = 0; i < out.length; i++) {
            out[i] = SettingsSpec.WEATHER_NAMES[i][lang];
         }

         return out;
      } else if (key == 102) {
         String[] out = new String[SettingsSpec.BLUR_STYLE_NAMES.length];

         for (int i = 0; i < out.length; i++) {
            out[i] = SettingsSpec.BLUR_STYLE_NAMES[i][lang];
         }

         return out;
      } else if (key != 110) {
         return new String[0];
      } else {
         String[] out = new String[SettingsSpec.GLASS_MODE_NAMES.length];

         for (int i = 0; i < out.length; i++) {
            out[i] = SettingsSpec.GLASS_MODE_NAMES[i][lang];
         }

         return out;
      }
   }

   private String cycleText(int key) {
      if (key == 30) {
         int i = idx(ClientConfig.weatherMode, SettingsSpec.WEATHER_NAMES.length);
         return SettingsSpec.WEATHER_NAMES[i][ClientConfig.langIndex()];
      } else if (key == 102) {
         int i = idx(ClientConfig.blurStyle, SettingsSpec.BLUR_STYLE_NAMES.length);
         return SettingsSpec.BLUR_STYLE_NAMES[i][ClientConfig.langIndex()];
      } else if (key == 110) {
         int i = idx(ClientConfig.glassMode, SettingsSpec.GLASS_MODE_NAMES.length);
         return SettingsSpec.GLASS_MODE_NAMES[i][ClientConfig.langIndex()];
      } else {
         return "";
      }
   }

   private int cycleIndex(int key) {
      if (key == 30) {
         return idx(ClientConfig.weatherMode, SettingsSpec.WEATHER_NAMES.length);
      } else if (key == 102) {
         return idx(ClientConfig.blurStyle, SettingsSpec.BLUR_STYLE_NAMES.length);
      } else {
         return key == 110 ? idx(ClientConfig.glassMode, SettingsSpec.GLASS_MODE_NAMES.length) : 0;
      }
   }

   private void setCycle(int key, int index) {
      if (key == 30) {
         ClientConfig.weatherMode = index % SettingsSpec.WEATHER_NAMES.length;
      } else if (key == 102) {
         ClientConfig.blurStyle = index % SettingsSpec.BLUR_STYLE_NAMES.length;
      } else if (key == 110) {
         ClientConfig.glassMode = index % SettingsSpec.GLASS_MODE_NAMES.length;
      }
   }

   private boolean maskAt(int row, int col) {
      String[] rows = ClientConfig.crosshairMask.split("/");
      return rows.length == 9 && row >= 0 && row < rows.length && col >= 0 && col < rows[row].length() && rows[row].charAt(col) == '1';
   }

   private void setMask(int row, int col, boolean on) {
      String[] rows = ClientConfig.crosshairMask.split("/");
      if (rows.length == 9) {
         StringBuilder sb = new StringBuilder();

         for (int r = 0; r < 9; r++) {
            StringBuilder line = new StringBuilder(rows[r]);

            while (line.length() < 9) {
               line.append('0');
            }

            if (r == row) {
               line.setCharAt(col, (char)(on ? '1' : '0'));
            }

            if (r > 0) {
               sb.append('/');
            }

            sb.append(line, 0, 9);
         }

         ClientConfig.crosshairMask = sb.toString();
      }
   }

   private void drawChecker(Md3Canvas c, float x, float y, float w, float h, float cell) {
      int light = -1448980;
      int dark = -3488560;
      boolean flip = false;
      float yy = 0.0F;

      while (yy < h) {
         boolean rowFlip = flip;

         for (float xx = 0.0F; xx < w; xx += cell) {
            float cw = Math.min(cell, w - xx);
            float chh = Math.min(cell, h - yy);
            c.fill(x + xx, y + yy, cw, chh, rowFlip ? dark : light);
            rowFlip = !rowFlip;
         }

         flip = !flip;
         yy += cell;
      }
   }

   private String tr(String zh, String en) {
      return ClientConfig.t(zh, en);
   }

   private static String ticksToClock(int ticks) {
      int totalMin = ticks % 24000 * 1440 / 24000;
      int hh = 6 + totalMin / 60;
      int mm = totalMin % 60;
      return String.format("%02d:%02d", hh % 24, mm);
   }

   private static int idx(int value, int len) {
      return value >= 0 && value < len ? value : 0;
   }

   private static float clampF(float v, float min, float max) {
      return v < min ? min : (v > max ? max : v);
   }

   private float clamp01(float v) {
      return v < 0.0F ? 0.0F : (v > 1.0F ? 1.0F : v);
   }

   private static float[] argbToHsv(int argb) {
      float r = (argb >> 16 & 0xFF) / 255.0F;
      float g = (argb >> 8 & 0xFF) / 255.0F;
      float b = (argb & 0xFF) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float h;
      if (max <= 0.0F) {
         h = 0.0F;
      } else if (max == r) {
         h = ((g - b) / (max - min) / 6.0F + 1.0F) % 1.0F;
      } else if (max == g) {
         h = ((b - r) / (max - min) + 2.0F) / 6.0F;
      } else {
         h = ((r - g) / (max - min) + 4.0F) / 6.0F;
      }

      if (max - min < 1.0E-5F) {
         h = 0.0F;
      }

      float s = max <= 0.0F ? 0.0F : (max - min) / max;
      return new float[]{h, s, max};
   }

   private static int hsvToArgb(float h, float s, float v, int a) {
      float hh = (h - (float)Math.floor(h)) * 6.0F;
      int i = (int)hh;
      float f = hh - i;
      float p = v * (1.0F - s);
      float q = v * (1.0F - s * f);
      float t = v * (1.0F - s * (1.0F - f));
      float r;
      float g;
      float b;
      switch (i % 6) {
         case 0:
            r = v;
            g = t;
            b = p;
            break;
         case 1:
            r = q;
            g = v;
            b = p;
            break;
         case 2:
            r = p;
            g = v;
            b = t;
            break;
         case 3:
            r = p;
            g = q;
            b = v;
            break;
         case 4:
            r = t;
            g = p;
            b = v;
            break;
         default:
            r = v;
            g = p;
            b = q;
      }

      return (a & 0xFF) << 24 | (Math.round(r * 255.0F) & 0xFF) << 16 | (Math.round(g * 255.0F) & 0xFF) << 8 | Math.round(b * 255.0F) & 0xFF;
   }

   @Override
   public boolean method_1037() {
      return false;
   }

   private static final class Group {
      final int id;
      final List<ClientScreen.Unit> units = new ArrayList<>();

      Group(int id) {
         this.id = id;
      }

      String nameZh() {
         int i = this.id - 200;
         return i >= 0 && i < SettingsSpec.GROUP_NAME_ZH.length ? SettingsSpec.GROUP_NAME_ZH[i] : "";
      }

      String nameEn() {
         int i = this.id - 200;
         return i >= 0 && i < SettingsSpec.GROUP_NAME_EN.length ? SettingsSpec.GROUP_NAME_EN[i] : "";
      }

      String name() {
         return ClientConfig.langIndex() == 0 ? this.nameZh() : this.nameEn();
      }

      int rowCount() {
         int n = 0;

         for (ClientScreen.Unit unit : this.units) {
            n += 1 + unit.rows.size();
         }

         return n;
      }
   }

   private static final class Row {
      final int kind;
      final int key;

      Row(int kind, int key) {
         this.kind = kind;
         this.key = key;
      }
   }

   private static final class RowBox {
      int kind;
      int key;
      float x;
      float y;
      float w;
      float h;
      float ctrlX;
      float ctrlY;
      float ctrlW;
      float ctrlH;
   }

   private static final class TabBox {
      int index;
      float x;
      float y;
      float w;
      float h;
   }

   private static final class Tile {
      final ClientScreen.Group group;
      float x;
      float y;
      float w;

      Tile(ClientScreen.Group group) {
         this.group = group;
      }
   }

   private static final class TileBox {
      int gid;
      float x;
      float y;
      float w;
      float h;
   }

   private static final class Unit {
      final ClientScreen.Row head;
      final List<ClientScreen.Row> rows = new ArrayList<>();

      Unit(ClientScreen.Row head) {
         this.head = head;
      }
   }
}
