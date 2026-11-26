package net.client;

import java.util.HashMap;
import java.util.Map;

public final class Lang {
   private static final Map<String, String[]> TEXT = new HashMap<>();

   private Lang() {
   }

   private static void t(String key, String zh, String en) {
      TEXT.put(key, new String[]{zh, en});
   }

   public static boolean chinese() {
      return ClientConfig.langIndex() == 0;
   }

   public static String tr(String key) {
      String[] pair = TEXT.get(key);
      if (pair == null) {
         return key;
      } else {
         return chinese() ? pair[0] : pair[1];
      }
   }

   static {
      t("title", "Amethyst Client", "Amethyst Client");
      t("menu.open", "客户端设置", "Client Settings");
      t("menu.account", "切换账号", "Accounts");
      t("menu.replay", "回放", "Replays");
      t("acc.title", "账号", "Accounts");
      t("acc.current", "当前", "Active");
      t("acc.hint", "点击账号立即切换 · ESC 返回", "Click an account to switch · ESC to go back");
      t("acc.applied", "已切换账号", "Account applied");
      t("acc.offline", "离线", "Offline");
      t("acc.ms", "正版", "Microsoft");
      t("acc.skin", "皮肤站", "Skin Site");
      t("acc.token", "Token", "Token");
      t("acc.ok", "确定", "OK");
      t("acc.cancel", "取消", "Cancel");
      t("acc.del.title", "删除这个账号？", "Delete this account?");
      t("acc.del.ok", "删除", "Delete");
      t("acc.waiting", "正在登录…", "Signing in…");
      t("acc.f.name", "用户名", "Username");
      t("acc.f.token", "AccessToken", "AccessToken");
      t("acc.f.uuid", "UUID（可选）", "UUID (optional)");
      t("acc.f.site", "站点地址", "Site URL");
      t("acc.f.client", "Client ID", "Client ID");
      t("acc.f.email", "邮箱 / 账号", "Email / Username");
      t("acc.f.password", "密码", "Password");
      t("acc.f.empty", "点击输入", "Click to type");
      t("acc.ms.waiting", "正在获取设备码…", "Requesting device code…");
      t("acc.ms.go", "在浏览器打开", "Open in browser");
      t("acc.ms.code", "输入代码", "Enter code");
      t("acc.ms.ok", "授权成功，点确定保存", "Authorized, press OK to save");
      t("acc.ms.error", "登录失败", "Sign-in failed");
      t("acc.err.name", "请填写用户名", "Username is required");
      t("acc.err.token", "Token 和用户名都要填", "Both token and username are required");
      t("acc.err.empty", "请填完所有输入框", "Please fill in every field");
      t("acc.err", "失败", "Failed");
      t("back", "返回", "Back");
      t("hint", "RShift / Esc 关闭 · 滚轮或拖动滚动条翻页", "RShift / Esc: close · wheel / drag to scroll");
      t("on", "开", "ON");
      t("off", "关", "OFF");
      t("picker.title", "选择颜色", "Pick a color");
      t("picker.preset", "预设", "Presets");
      t("picker.hue", "色相", "Hue");
      t("picker.sv", "饱和度 / 明度", "Saturation / Value");
      t("picker.alpha", "透明度", "Alpha");
      t("picker.done", "完成", "Done");
      t("grp.anim", "动画与视角", "Animation & Camera");
      t("grp.fov", "FOV", "FOV");
      t("grp.render", "渲染与颜色", "Render & Colors");
      t("grp.weather", "天气", "Weather");
      t("grp.hud", "HUD 与界面", "HUD & Interface");
      t("grp.misc", "其他", "Misc");
      t("noHurtCam", "受击无抖动", "No Hurt Camera Shake");
      t("fullbright", "伽马满亮度（替代夜视）", "Gamma Fullbright");
      t("minimalBobbing", "最小视角摇晃", "Minimal View Bobbing");
      t("autoSprint", "自动疾跑", "Auto Sprint");
      t("fovStatic", "静态 FOV", "Static FOV");
      t("fovBase", "基础 FOV", "Base FOV");
      t("customGlint", "自定义附魔光效", "Custom Glint Color");
      t("glintColor", "光效颜色", "Glint Color");
      t("fogModify", "自定义雾", "Custom Fog");
      t("fogColor", "雾颜色", "Fog Color");
      t("fogDensity", "雾浓度", "Fog Density");
      t("fogSkyFollow", "天空颜色跟随雾颜色", "Sky Color Follows Fog");
      t("highlightBlock", "方块高亮", "Block Highlight");
      t("highlightFill", "填充颜色", "Highlight Fill");
      t("highlightOutline", "边框颜色", "Highlight Outline");
      t("selfNameTag", "自己名字标签", "Self Name Tag");
      t("nameBgColor", "名字背景色", "Name Background");
      t("nameTextColor", "名字文字色", "Name Text");
      t("chatBackground", "聊天框背景", "Chat Background");
      t("chatBgColor", "聊天框背景色", "Chat Color");
      t("scoreboardBackground", "计分板背景", "Scoreboard Background");
      t("scoreboardBgColor", "计分板背景色", "Scoreboard Color");
      t("weatherMode", "天气", "Weather");
      t("weatherThroughBlocks", "天气粒子穿过屋顶", "Weather Passes Through Roofs");
      t("language", "语言", "Language");
      t("val.weatherMode.0", "跟随服务器", "Follow");
      t("val.weatherMode.1", "晴", "Clear");
      t("val.weatherMode.2", "雨", "Rain");
      t("val.weatherMode.3", "雷暴", "Storm");
      t("val.weatherMode.4", "雪", "Snow");
      t("val.language.0", "跟随游戏", "Auto");
      t("val.language.1", "简体中文", "简体中文");
      t("val.language.2", "English", "English");
   }
}
