package net.client.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SettingsSpec {
   public static final int K_HEADER = -1;
   public static final int K_TOGGLE = 0;
   public static final int K_VALUE = 1;
   public static final int K_COLOR = 2;
   public static final int K_CYCLE = 3;
   public static final int K_RESET = 4;
   public static final int K_ACTION = 5;
   public static final int FOV_ON = 6;
   public static final int FOV_BASE = 7;
   public static final int GLINT_ON = 14;
   public static final int GLINT_COLOR = 15;
   public static final int FOG_ON = 16;
   public static final int FOG_COLOR = 17;
   public static final int FOG_DENSITY = 19;
   public static final int HL_ON = 20;
   public static final int HL_FILL = 21;
   public static final int HL_OUTLINE = 22;
   public static final int NAME_ON = 23;
   public static final int NAME_BG = 24;
   public static final int NAME_TEXT = 25;
   public static final int CHAT_ON = 26;
   public static final int CHAT_COLOR = 27;
   public static final int SB_ON = 28;
   public static final int SB_COLOR = 29;
   public static final int WEATHER = 30;
   public static final int WEATHER_ROOF = 31;
   public static final int RAW_INPUT = 32;
   public static final int TIME_ON = 35;
   public static final int TIME_TICKS = 36;
   public static final int BLUR_ON = 37;
   public static final int BLUR_AMOUNT = 38;
   public static final int HURT_ON = 39;
   public static final int HURT_COLOR = 40;
   public static final int DROP_ON = 42;
   public static final int CROSSHAIR_ON = 43;
   public static final int CROSSHAIR_COLOR = 44;
   public static final int CROSSHAIR_EDIT = 45;
   public static final int NO_HURT = 46;
   public static final int FULLBRIGHT = 47;
   public static final int MIN_BOB = 48;
   public static final int AUTO_SPRINT = 49;
   public static final int FREELOOK_ON = 63;
   public static final int FREELOOK_KEY = 64;
   public static final int DROP_REST = 65;
   public static final int UI_OVERRIDE = 66;
   public static final int THEME_COLOR = 103;
   public static final int MODERN_FONT = 104;
   public static final int GLASS_MODE = 110;
   public static final int MOD_LIST = 69;
   public static final int CLICK_NODELAY = 70;
   public static final int HIDE_PARTICLES = 71;
   public static final int FIRE_HEIGHT = 72;
   public static final int SCREEN_DIM = 50;
   public static final int CHAT_VISIBLE = 52;
   public static final int SCORE_VISIBLE = 53;
   public static final int SCORE_NO_NUMBERS = 54;
   public static final int SCORE_CUSTOM = 105;
   public static final int SCORE_EDIT = 106;
   public static final int HAND_FWD = 73;
   public static final int HAND_RIGHT = 74;
   public static final int HAND_UP = 75;
   public static final int LEFT_HANDED = 109;
   public static final int PARTICLE_ON = 76;
   public static final int CRIT_MULT = 77;
   public static final int SHARP_MULT = 78;
   public static final int IME_FIX = 80;
   public static final int RESET_SETTINGS = 81;
   public static final int MINING_PARTICLES = 82;
   public static final int SPRINT_PARTICLES = 83;
   public static final int BLUR_STYLE = 102;
   public static final int BLOCKHIT_ON = 55;
   public static final int HEART_BLINK = 111;
   public static final int AF_ON = 56;
   public static final int AF_COLOR = 57;
   public static final int AF_LIFE = 58;
   public static final int AF_STR = 59;
   public static final int AF_LAYERS = 60;
   public static final int AF_SELF = 61;
   public static final int G_SCREEN = 220;
   public static final int G_HAND = 223;
   public static final int G_PARTICLES = 225;
   public static final int G_GLASS = 226;
   public static final int G_SWING = 200;
   public static final int G_HURT = 201;
   public static final int G_GAMMA = 202;
   public static final int G_BOB = 203;
   public static final int G_SPRINT = 204;
   public static final int G_FOV = 205;
   public static final int G_GLINT = 206;
   public static final int G_FOG = 207;
   public static final int G_HL = 208;
   public static final int G_NAME = 209;
   public static final int G_CHAT = 210;
   public static final int G_SB = 211;
   public static final int G_WEATHER = 212;
   public static final int G_TIME = 213;
   public static final int G_BLUR = 215;
   public static final int G_DROP = 218;
   public static final int G_CROSSHAIR = 219;
   public static final int G_BLOCKHIT = 221;
   public static final int G_AFTERIMAGE = 222;
   public static final int G_FREELOOK = 224;
   public static final int GROUPS = 27;
   public static final int[][] ROWS = new int[][]{
      {-1, -1, 221},
      {0, 221, 55},
      {0, 221, 111},
      {1, 221, 72},
      {-1, -1, 222},
      {0, 222, 56},
      {2, 222, 57},
      {1, 222, 58},
      {1, 222, 59},
      {1, 222, 60},
      {0, 222, 61},
      {-1, -1, 201},
      {0, 201, 46},
      {0, 201, 39},
      {2, 201, 40},
      {-1, -1, 202},
      {0, 202, 47},
      {-1, -1, 203},
      {0, 203, 48},
      {-1, -1, 214},
      {0, 214, 32},
      {-1, -1, 204},
      {0, 204, 49},
      {-1, -1, 205},
      {0, 205, 6},
      {1, 205, 7},
      {0, 205, 84},
      {1, 205, 85},
      {0, 205, 86},
      {1, 205, 87},
      {0, 205, 88},
      {1, 205, 89},
      {0, 205, 90},
      {1, 205, 91},
      {0, 205, 92},
      {1, 205, 93},
      {0, 205, 94},
      {1, 205, 95},
      {0, 205, 96},
      {1, 205, 97},
      {-1, -1, 206},
      {0, 206, 14},
      {2, 206, 15},
      {-1, -1, 207},
      {0, 207, 16},
      {2, 207, 17},
      {1, 207, 19},
      {-1, -1, 208},
      {0, 208, 20},
      {2, 208, 21},
      {2, 208, 22},
      {-1, -1, 209},
      {0, 209, 23},
      {2, 209, 24},
      {2, 209, 25},
      {-1, -1, 210},
      {0, 210, 52},
      {0, 210, 26},
      {2, 210, 27},
      {0, 210, 80},
      {-1, -1, 211},
      {0, 211, 53},
      {0, 211, 54},
      {0, 211, 28},
      {2, 211, 29},
      {5, 211, 106},
      {-1, -1, 212},
      {3, 212, 30},
      {0, 212, 31},
      {-1, -1, 213},
      {0, 213, 35},
      {1, 213, 36},
      {-1, -1, 215},
      {0, 215, 37},
      {1, 215, 38},
      {3, 215, 102},
      {-1, -1, 218},
      {0, 218, 42},
      {0, 218, 65},
      {-1, -1, 220},
      {2, 220, 103},
      {1, 220, 50},
      {0, 220, 104},
      {0, 220, 66},
      {-1, -1, 219},
      {0, 219, 43},
      {2, 219, 44},
      {5, 219, 45},
      {-1, -1, 223},
      {0, 223, 109},
      {1, 223, 73},
      {1, 223, 74},
      {1, 223, 75},
      {-1, -1, 224},
      {0, 224, 63},
      {5, 224, 64},
      {-1, -1, 225},
      {0, 225, 76},
      {1, 225, 77},
      {1, 225, 78},
      {0, 225, 71},
      {0, 225, 82},
      {0, 225, 83},
      {-1, -1, 226},
      {3, 226, 110}
   };
   public static final String[] GROUP_NAME_ZH = new String[27];
   public static final String[] GROUP_NAME_EN = new String[27];
   public static final String[][] LABELS = new String[][]{
      {"", ""},
      {"受击无抖动", "No hurt camera shake"},
      {"伽马满亮度", "Gamma fullbright"},
      {"最小视角摇晃", "Minimal view bobbing"},
      {"自动疾跑", "Auto sprint"},
      {"客户端 FOV", "Client FOV"},
      {"默认 FOV", "Default FOV"},
      {"疾跑强度", "Sprint strength"},
      {"速度药水强度", "Speed strength"},
      {"迟缓强度", "Slowness strength"},
      {"飞行强度", "Flying strength"},
      {"拉弓强度", "Aiming strength"},
      {"水下强度", "Submerged strength"},
      {"自定义附魔光效", "Custom glint"},
      {"光效颜色", "Glint color"},
      {"自定义迷雾", "Custom fog"},
      {"迷雾颜色", "Fog color"},
      {"", ""},
      {"迷雾浓度", "Fog density"},
      {"准星方块高亮", "Block highlight"},
      {"高亮填充色", "Highlight fill"},
      {"高亮描边色", "Highlight outline"},
      {"自己名字标签", "Self name tag"},
      {"名字背景色", "Name background"},
      {"名字文字色", "Name text"},
      {"自定义聊天框背景", "Chat background"},
      {"聊天框颜色", "Chat color"},
      {"自定义计分板背景", "Scoreboard background"},
      {"计分板背景颜色", "Scoreboard background color"},
      {"天气", "Weather"},
      {"天气可穿过头顶方块", "Weather through roofs"},
      {"线性鼠标灵敏度（仅限电脑端）", "Linear mouse sensitivity (desktop only)"},
      {"", ""},
      {"界面语言", "Language"},
      {"覆盖世界时间（仅画面）", "Override world time (visual)"},
      {"时间点", "Time of day "},
      {"动态模糊", "Motion blur"},
      {"模糊强度", "Blur strength"},
      {"自定义受击颜色", "Custom hurt color"},
      {"受击颜色", "Hurt color"},
      {"", ""},
      {"真实掉落物", "Realistic 3D item drops"},
      {"自定义准星", "Custom crosshair"},
      {"准星颜色", "Crosshair color"},
      {"编辑准星", "Edit crosshair "},
      {"受击无抖动", "No hurt camera shake"},
      {"伽马满亮度", "Gamma fullbright"},
      {"最小视角摇晃", "Minimal view bobbing"},
      {"自动疾跑", "Auto sprint"},
      {"背景模糊强度", "Backdrop blur strength"},
      {"", ""},
      {"隐藏聊天栏", "Hide chat"},
      {"隐藏计分板", "Hide scoreboard"},
      {"隐藏计分板红色数字", "Hide red score numbers"},
      {"旧动画", "Old animations"},
      {"受击残影", "Hit afterimage"},
      {"残影颜色", "Afterimage color"},
      {"残影持续时间", "Afterimage duration "},
      {"残影不透明度", "Afterimage opacity"},
      {"柔光层数", "Glow layers"},
      {"自己也留下残影", "Include yourself"},
      {"", ""},
      {"自由视角", "Freelook "},
      {"绑定按键", "Bind key "},
      {"落地后完全静止", "Rest fixed on ground "},
      {"接管原版界面", "Override vanilla UI"},
      {"", ""},
      {"", ""},
      {"已安装模组", "Installed mods"},
      {"", ""},
      {"隐藏自身粒子", "Hide own particles"},
      {"第一人称火焰高度", "First-person fire height"},
      {"前后偏移", "Forward / back offset"},
      {"左右偏移", "Left / right offset"},
      {"上下偏移", "Up / down offset"},
      {"自定义粒子", "Custom particles"},
      {"暴击粒子倍率", "Crit particle multiplier"},
      {"锋利粒子倍率", "Sharpness particle multiplier"},
      {"", ""},
      {"中文输入法修复（仅限电脑端）", "Chinese IME fix (desktop only)"},
      {"重置所有设置", "Reset all settings"},
      {"隐藏挖掘粒子", "Hide mining particles"},
      {"隐藏脚下粒子", "Hide footstep particles"},
      {"平滑过渡", "Smooth transition"},
      {"整体倍率", "Overall multiplier"},
      {"疾跑 FOV 开关", "Sprint FOV"},
      {"疾跑 FOV", "Sprint FOV value"},
      {"速度 FOV 开关", "Speed FOV"},
      {"速度 FOV", "Speed FOV value"},
      {"迟缓 FOV 开关", "Slowness FOV"},
      {"迟缓 FOV", "Slowness FOV value"},
      {"飞行 FOV 开关", "Flying FOV"},
      {"飞行 FOV", "Flying FOV value"},
      {"瞄准 FOV 开关", "Aiming FOV"},
      {"瞄准 FOV", "Aiming FOV value"},
      {"水下 FOV 开关", "Underwater FOV"},
      {"水下 FOV", "Underwater FOV value"},
      {"攻击手感优化", "Attack feel"},
      {"", ""},
      {"", ""},
      {"", ""},
      {"动态模糊样式", "Blur style"},
      {"主题色", "Theme color"},
      {"现代字体", "Modern font"},
      {"自定义计分板内容", "Custom scoreboard content"},
      {"编辑计分板内容", "Edit scoreboard content"},
      {"", ""},
      {"", ""},
      {"左手模式", "Left hand"},
      {"隐藏玻璃", "Hide glass"},
      {"关闭受击闪烁", "No hurt blink"}
   };
   public static final String[][] WEATHER_NAMES = new String[][]{{"跟随", "Follow"}, {"晴", "Clear"}, {"雨", "Rain"}, {"雷暴", "Storm"}, {"雪", "Snow"}};
   public static final String[][] GLASS_MODE_NAMES = new String[][]{{"关闭", "Off"}, {"全部玻璃（含染色）", "All glass (incl. stained)"}, {"仅未染色玻璃", "Clear glass only"}};
   public static final String[][] BLUR_STYLE_NAMES = new String[][]{{"时间累积", "Temporal"}, {"线性拖影", "Linear"}, {"速度模糊", "Velocity"}, {"柔化累积", "Soft"}};
   private static final Comparator<SettingsSpec.SettingGroup> BY_GROUP_NAME = new Comparator<SettingsSpec.SettingGroup>() {
      public int compare(SettingsSpec.SettingGroup a, SettingsSpec.SettingGroup b) {
         return a.key().compareTo(b.key());
      }
   };
   private static final Comparator<SettingsSpec.SettingUnit> BY_SETTING_NAME = new Comparator<SettingsSpec.SettingUnit>() {
      public int compare(SettingsSpec.SettingUnit a, SettingsSpec.SettingUnit b) {
         return a.key().compareTo(b.key());
      }
   };

   public static List<String> validate() {
      List<String> problems = new ArrayList<>();
      Set<Integer> groups = new HashSet<>();
      Set<Integer> keys = new HashSet<>();

      for (int[] row : ROWS) {
         if (row[0] == -1 && row[2] > 0 && !groups.add(row[2])) {
            problems.add("分组 id 重复: " + row[2]);
         }
      }

      for (int[] rowx : ROWS) {
         int kind = rowx[0];
         int parent = rowx[1];
         int key = rowx[2];
         if (kind != -1) {
            if (parent > 0 && !groups.contains(parent)) {
               problems.add("分组未定义: " + parent + "（key " + key + "）");
            }

            if (key > 0) {
               if (!keys.add(key)) {
                  problems.add("功能 key 重复: " + key);
               }

               if (key > LABELS.length) {
                  problems.add("key " + key + " 超出 LABELS 范围(" + LABELS.length + ")");
               } else if (LABELS[key - 1][0].trim().isEmpty() && LABELS[key - 1][1].trim().isEmpty()) {
                  problems.add("key " + key + " 没有文案（行表/文案表错位）");
               }
            }
         }
      }

      List<int[]> strayRows = new ArrayList<>();
      List<SettingsSpec.SettingGroup> ordered = parseGroups(strayRows);
      String lastGroupKey = null;
      String lastGroupName = null;

      for (SettingsSpec.SettingGroup group : ordered) {
         String groupKey = group.key();
         if (lastGroupKey != null && groupKey.compareTo(lastGroupKey) < 0) {
            problems.add("分类顺序不是 A→Z: " + lastGroupName + " 之后出现 " + group.name());
         }

         lastGroupKey = groupKey;
         lastGroupName = group.name();
         if (group.units.isEmpty()) {
            problems.add("分类 " + group.id + " 没有任何条目");
         }

         String lastUnitKey = null;
         String lastUnitName = null;
         int start = 0;
         if (group.id == 205) {
            if (!group.units.isEmpty() && group.units.get(0).head[2] == 6) {
               lastUnitKey = null;
               lastUnitName = null;
               start = 1;
            } else {
               problems.add("FOV 分类没有把客户端 FOV 开关置顶");
            }
         }

         for (int ui = start; ui < group.units.size(); ui++) {
            SettingsSpec.SettingUnit unit = group.units.get(ui);
            String unitKey = unit.key();
            if (lastUnitKey != null && unitKey.compareTo(lastUnitKey) < 0) {
               problems.add("分类 " + group.id + " 内条目顺序不是 A→Z: " + lastUnitName + " 之后出现 " + unit.name());
            }

            lastUnitKey = unitKey;
            lastUnitName = unit.name();
         }
      }

      for (int[] stray : strayRows) {
         if (stray[1] > 0) {
            problems.add("分类内的行被别的行隔开（parent " + stray[1] + "，key " + stray[2] + "），会从界面上消失");
         }
      }

      return problems;
   }

   private static void gname(int id, String zh, String en) {
      GROUP_NAME_ZH[id - 200] = zh;
      GROUP_NAME_EN[id - 200] = en;
   }

   private static List<SettingsSpec.SettingGroup> parseGroups(List<int[]> looseOut) {
      List<SettingsSpec.SettingGroup> groups = new ArrayList<>();
      int i = 0;

      while (i < ROWS.length) {
         int[] row = ROWS[i];
         if (row[0] == -1) {
            SettingsSpec.SettingGroup group = new SettingsSpec.SettingGroup(row[2], row);
            i++;

            for (; i < ROWS.length && ROWS[i][0] != -1 && ROWS[i][1] == row[2]; i++) {
               int[] member = ROWS[i];
               if (startsNewUnit(group, member)) {
                  group.units.add(new SettingsSpec.SettingUnit(member));
               } else {
                  group.units.get(group.units.size() - 1).rows.add(member);
               }
            }

            groups.add(group);
         } else {
            if (looseOut != null) {
               looseOut.add(row);
            }

            i++;
         }
      }

      return groups;
   }

   private static boolean startsNewUnit(SettingsSpec.SettingGroup group, int[] member) {
      return !group.units.isEmpty() && member[0] != 0 ? group.units.get(group.units.size() - 1).head[0] != 0 : true;
   }

   private static void sortAlphabetically() {
      List<int[]> loose = new ArrayList<>();
      List<SettingsSpec.SettingGroup> groups = parseGroups(loose);
      Collections.sort(groups, BY_GROUP_NAME);

      for (SettingsSpec.SettingGroup group : groups) {
         if (group.id == 205) {
            for (int i = 0; i < group.units.size(); i++) {
               if (group.units.get(i).head[2] == 6) {
                  SettingsSpec.SettingUnit first = group.units.remove(i);
                  group.units.add(0, first);
                  break;
               }
            }

            int ix = 1;
            if (ix < group.units.size()) {
               List<SettingsSpec.SettingUnit> rest = group.units.subList(1, group.units.size());
               Collections.sort(rest, BY_SETTING_NAME);
            }
         } else {
            Collections.sort(group.units, BY_SETTING_NAME);
         }
      }

      int n = 0;

      for (SettingsSpec.SettingGroup groupx : groups) {
         ROWS[n++] = groupx.header;

         for (SettingsSpec.SettingUnit unit : groupx.units) {
            for (int[] row : unit.rows) {
               ROWS[n++] = row;
            }
         }
      }

      for (int[] row : loose) {
         ROWS[n++] = row;
      }

      if (n != ROWS.length) {
         throw new IllegalStateException("设置表重排后行数不一致: " + n + " != " + ROWS.length);
      }
   }

   private SettingsSpec() {
   }

   static {
      gname(200, "旧动画", "Old Animations");
      gname(201, "受击反馈", "HURT FEEDBACK");
      gname(202, "亮度", "BRIGHTNESS");
      gname(203, "视角摇晃", "VIEW BOBBING");
      gname(204, "疾跑", "SPRINT");
      gname(205, "FOV / 视场角", "FIELD OF VIEW");
      gname(206, "附魔光效", "ENCHANT GLINT");
      gname(207, "迷雾", "FOG");
      gname(208, "方块高亮", "BLOCK HIGHLIGHT");
      gname(209, "名字标签", "NAME TAG");
      gname(210, "聊天框", "CHAT");
      gname(211, "计分板", "SCOREBOARD");
      gname(212, "天气", "WEATHER");
      gname(213, "世界时间", "WORLD TIME");
      gname(214, "鼠标灵敏度", "MOUSE");
      gname(215, "动态模糊", "MOTION BLUR");
      gname(218, "真实掉落物", "REALISTIC DROPS");
      gname(219, "准星", "CROSSHAIR");
      gname(220, "UI选项", "UI OPTIONS");
      gname(221, "动画", "ANIMATIONS");
      gname(222, "受击残影", "HIT AFTERIMAGE");
      gname(223, "第一人称手臂", "FIRST PERSON HAND");
      gname(224, "自由视角", "FREELOOK");
      gname(225, "粒子", "PARTICLES");
      gname(226, "玻璃", "GLASS");
      sortAlphabetically();
   }

   private static final class SettingGroup {
      final int id;
      final int[] header;
      final List<SettingsSpec.SettingUnit> units = new ArrayList<>();

      SettingGroup(int id, int[] header) {
         this.id = id;
         this.header = header;
      }

      String key() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      String name() {
         int i = this.id - 200;
         String en = i >= 0 && i < SettingsSpec.GROUP_NAME_EN.length ? SettingsSpec.GROUP_NAME_EN[i] : "";
         return en == null ? "" : en;
      }
   }

   private static final class SettingUnit {
      final int[] head;
      final List<int[]> rows = new ArrayList<>();

      SettingUnit(int[] head) {
         this.head = head;
         this.rows.add(head);
      }

      String key() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      String name() {
         int i = this.head[2] - 1;
         String en = i >= 0 && i < SettingsSpec.LABELS.length ? SettingsSpec.LABELS[i][1] : "";
         return en == null ? "" : en;
      }
   }
}
