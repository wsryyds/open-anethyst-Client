package net.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.client.mixin.SessionAccessor;
import net.minecraft.class_1600;
import net.minecraft.class_355;

public final class Accounts {
   private static final List<Accounts.Account> LIST = new ArrayList<>();
   private static int current = -1;
   private static boolean loaded;
   private static boolean usingLauncher;
   private static Accounts.Account launcher;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   private Accounts() {
   }

   public static void bootstrap() {
      load();
      if (current >= 0 && current < LIST.size()) {
         apply(LIST.get(current));
      } else {
         current = -1;
         applyLauncher();
      }
   }

   public static List<Accounts.Account> list() {
      load();
      return LIST;
   }

   public static int currentIndex() {
      load();
      return current;
   }

   public static Accounts.Account current() {
      load();
      return current >= 0 && current < LIST.size() ? LIST.get(current) : null;
   }

   public static Accounts.Account launcher() {
      load();
      captureLauncher();
      return launcher;
   }

   public static boolean usingLauncher() {
      load();
      return usingLauncher;
   }

   public static void remove(int index) {
      load();
      if (index >= 0 && index < LIST.size()) {
         LIST.remove(index);
         if (current > index) {
            current--;
         } else if (current == index) {
            if (LIST.isEmpty()) {
               current = -1;
               applyLauncher();
            } else {
               current = Math.min(index, LIST.size() - 1);
               apply(LIST.get(current));
            }
         } else if (current < 0) {
            applyLauncher();
         }

         save();
      }
   }

   public static void add(Accounts.Account account) {
      load();

      for (int i = 0; i < LIST.size(); i++) {
         if (LIST.get(i).name.equalsIgnoreCase(account.name) && LIST.get(i).type.equals(account.type)) {
            LIST.set(i, account);
            current = i;
            save();
            apply(account);
            return;
         }
      }

      LIST.add(account);
      current = LIST.size() - 1;
      save();
      apply(account);
   }

   public static void select(int index) {
      load();
      if (index >= 0 && index < LIST.size()) {
         current = index;
         save();
         apply(LIST.get(index));
      }
   }

   public static void selectLauncher() {
      load();
      current = -1;
      save();
      applyLauncher();
   }

   public static boolean apply(Accounts.Account account) {
      if (account == null) {
         return false;
      } else {
         usingLauncher = "launcher".equals(account.type);
         String userType = account.userType;
         if (userType == null || userType.isEmpty()) {
            userType = !"microsoft".equals(account.type) && !"token".equals(account.type) ? "legacy" : "mojang";
         }

         class_355 session = new class_355(account.name, dashed(account.uuid), account.token, userType);

         try {
            ((SessionAccessor)class_1600.method_2965()).client$setSession(session);
            return true;
         } catch (Exception var7) {
            ClientLog.warn("SessionAccessor 写会话失败，转反射兜底", var7);

            try {
               class_1600 client = class_1600.method_2965();
               Field field = class_1600.class.getDeclaredField("session");
               field.setAccessible(true);
               field.set(client, session);
               return true;
            } catch (Exception var6) {
               return false;
            }
         }
      }
   }

   private static void applyLauncher() {
      captureLauncher();
      if (launcher != null) {
         apply(launcher);
      } else {
         usingLauncher = true;
      }
   }

   private static void captureLauncher() {
      if (launcher == null) {
         try {
            class_1600 client = class_1600.method_2965();
            class_355 session = client == null ? null : client.method_5568();
            if (session == null) {
               return;
            }

            Accounts.Account account = new Accounts.Account();
            account.type = "launcher";
            account.name = session.method_5583() == null ? "" : session.method_5583();
            account.uuid = session.method_6667() == null ? "" : session.method_6667();
            account.token = session.method_6668() == null ? "" : session.method_6668();
            account.userType = session.method_6670() == null ? "legacy" : session.method_6670().name().toLowerCase(Locale.ROOT);
            launcher = account;
         } catch (Exception var3) {
            ClientLog.warn("记录启动器账号失败", var3);
         }
      }
   }

   public static String offlineUuid(String name) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");
   }

   public static String dashed(String uuid) {
      if (uuid == null) {
         return "";
      } else {
         String s = uuid.replace("-", "").trim();
         return s.length() != 32
            ? s
            : s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16) + "-" + s.substring(16, 20) + "-" + s.substring(20);
      }
   }

   private static File file() {
      File dir = new File(class_1600.method_2965().field_3762, "config");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      return new File(dir, "client-accounts.json");
   }

   private static void load() {
      if (!loaded) {
         loaded = true;
         captureLauncher();

         try {
            File file = file();
            if (!file.isFile()) {
               return;
            }

            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            Accounts.Saved saved = GSON.fromJson(json, Accounts.Saved.class);
            if (saved != null) {
               if (saved.accounts != null) {
                  LIST.addAll(saved.accounts);
               }

               current = saved.current;
            }
         } catch (Exception var31) {
            ClientLog.warn("读取账号文件失败，使用空列表", var31);
         }
      }
   }

   private static void save() {
      try {
         Accounts.Saved saved = new Accounts.Saved();
         saved.accounts = LIST;
         saved.current = current;
         Files.write(file().toPath(), GSON.toJson(saved).getBytes(StandardCharsets.UTF_8));
      } catch (Exception var11) {
         ClientLog.warn("保存账号文件失败", var11);
      }
   }

   public static final class Account {
      public String type = "offline";
      public String name = "";
      public String uuid = "";
      public String token = "";
      public String site = "";
      public String email = "";
      public String userType = "";

      public String display() {
         String t;
         if ("offline".equals(this.type)) {
            t = "离线";
         } else if ("token".equals(this.type)) {
            t = "Token";
         } else if ("skin".equals(this.type)) {
            t = "皮肤站";
         } else if ("launcher".equals(this.type)) {
            t = "启动器";
         } else {
            t = "正版";
         }

         return this.name + "  ·  " + t;
      }
   }

   private static final class Saved {
      List<Accounts.Account> accounts;
      int current;
   }
}
