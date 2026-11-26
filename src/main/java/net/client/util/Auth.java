package net.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.client.ClientConfig;

public final class Auth {
   private static String clientId() {
      String id = ClientConfig.msClientId;
      return id == null ? "" : id.trim();
   }

   public static String resolveClientId(String typed) {
      return typed != null && !typed.trim().isEmpty() ? typed.trim() : clientId();
   }

   public static boolean hasMicrosoftClientId() {
      return !clientId().isEmpty();
   }

   private Auth() {
   }

   public static Accounts.Account offline(String name) {
      Accounts.Account account = new Accounts.Account();
      account.type = "offline";
      account.name = name;
      account.uuid = Accounts.offlineUuid(name);
      account.token = "0";
      return account;
   }

   public static Accounts.Account fromToken(String token) throws Exception {
      String trimmed = token == null ? "" : token.trim();
      if (trimmed.isEmpty()) {
         throw new Exception(ClientConfig.t("AccessToken 为空", "empty AccessToken"));
      } else {
         JsonObject profile = getJson("https://api.minecraftservices.com/minecraft/profile", trimmed);
         if (profile != null && profile.has("id") && profile.has("name")) {
            Accounts.Account account = new Accounts.Account();
            account.type = "token";
            account.name = profile.get("name").getAsString();
            account.uuid = profile.get("id").getAsString();
            account.token = trimmed;
            return account;
         } else {
            throw new Exception(ClientConfig.t("Token 无效或已过期", "token invalid or expired"));
         }
      }
   }

   public static Accounts.Account skinSite(String site, String email, String password) throws Exception {
      String base = site.trim();
      if (base.endsWith("/")) {
         base = base.substring(0, base.length() - 1);
      }

      if (!base.startsWith("http")) {
         base = "https://" + base;
      }

      JsonObject body = new JsonObject();
      body.addProperty("username", email);
      body.addProperty("password", password);
      body.addProperty("requestUser", false);
      JsonObject agent = new JsonObject();
      agent.addProperty("name", "Minecraft");
      agent.addProperty("version", 1);
      body.add("agent", agent);
      JsonObject response = post(base + "/authserver/authenticate", body);
      if (response != null && response.has("accessToken")) {
         Accounts.Account account = new Accounts.Account();
         account.type = "skin";
         account.site = base;
         account.email = email;
         account.token = response.get("accessToken").getAsString();
         if (response.has("selectedProfile") && response.get("selectedProfile").isJsonObject()) {
            JsonObject profile = response.getAsJsonObject("selectedProfile");
            account.name = profile.get("name").getAsString();
            account.uuid = profile.get("id").getAsString();
            return account;
         } else {
            throw new Exception(ClientConfig.t("该账号还没有创建角色", "this account has no character yet"));
         }
      } else {
         throw new Exception(ClientConfig.t("皮肤站返回了无效数据", "skin site returned invalid data"));
      }
   }

   public static Auth.DeviceCode requestDeviceCode(String clientIdTyped) throws Exception {
      String id = resolveClientId(clientIdTyped);
      if (id.isEmpty()) {
         throw new Exception(ClientConfig.t("未配置 Microsoft Client ID", "Microsoft Client ID is not configured"));
      } else {
         String url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";
         String form = "client_id=" + enc(id) + "&scope=" + enc("XboxLive.signin offline_access");
         JsonObject json = postForm(url, form);
         if (json != null && json.has("device_code")) {
            Auth.DeviceCode code = new Auth.DeviceCode();
            code.deviceCode = json.get("device_code").getAsString();
            code.userCode = json.get("user_code").getAsString();
            code.verificationUri = json.get("verification_uri").getAsString();
            if (json.has("interval")) {
               code.interval = json.get("interval").getAsInt();
            }

            return code;
         } else {
            throw new Exception(
               ClientConfig.t("无法获取设备码", "failed to get device code")
                  + (json != null && json.has("error_description") ? ": " + json.get("error_description").getAsString() : "")
            );
         }
      }
   }

   public static Accounts.Account awaitMicrosoft(Auth.DeviceCode code, String clientIdTyped) throws Exception {
      String id = resolveClientId(clientIdTyped);
      String tokenUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
      String form = "client_id=" + enc(id) + "&grant_type=" + enc("urn:ietf:params:oauth:grant-type:device_code") + "&device_code=" + enc(code.deviceCode);
      JsonObject token = null;
      long deadline = System.currentTimeMillis() + 300000L;

      while (System.currentTimeMillis() < deadline) {
         Thread.sleep(Math.max(1, code.interval) * 1000L);
         JsonObject result = postForm(tokenUrl, form);
         if (result != null && result.has("access_token")) {
            token = result;
            break;
         }

         if (result != null
            && result.has("error")
            && !"authorization_pending".equals(result.get("error").getAsString())
            && !"slow_down".equals(result.get("error").getAsString())) {
            throw new Exception(result.get("error").getAsString());
         }
      }

      if (token == null) {
         throw new Exception(ClientConfig.t("授权超时", "authorization timed out"));
      } else {
         return microsoftFromAccessToken(token.get("access_token").getAsString());
      }
   }

   private static Accounts.Account microsoftFromAccessToken(String msAccessToken) throws Exception {
      JsonObject properties = new JsonObject();
      properties.addProperty("AuthMethod", "RPS");
      properties.addProperty("SiteName", "user.auth.xboxlive.com");
      properties.addProperty("RpsTicket", "d=" + msAccessToken);
      JsonObject xblBody = new JsonObject();
      xblBody.add("Properties", properties);
      xblBody.addProperty("RelyingParty", "http://auth.xboxlive.com");
      xblBody.addProperty("TokenType", "JWT");
      JsonObject xbl = post("https://user.auth.xboxlive.com/user/authenticate", xblBody);
      String xblToken = xbl.get("Token").getAsString();
      String userHash = xbl.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
      JsonObject xstsProperties = new JsonObject();
      xstsProperties.addProperty("SandboxId", "RETAIL");
      JsonArray tokens = new JsonArray();
      tokens.add(new JsonPrimitive(xblToken));
      xstsProperties.add("UserTokens", tokens);
      JsonObject xstsBody = new JsonObject();
      xstsBody.add("Properties", xstsProperties);
      xstsBody.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      xstsBody.addProperty("TokenType", "JWT");
      JsonObject xsts = post("https://xsts.auth.xboxlive.com/xsts/authorize", xstsBody);
      String xstsToken = xsts.get("Token").getAsString();
      JsonObject mcBody = new JsonObject();
      mcBody.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);
      JsonObject mc = post("https://api.minecraftservices.com/authentication/login_with_xbox", mcBody);
      if (mc != null && mc.has("access_token")) {
         String mcToken = mc.get("access_token").getAsString();
         JsonObject profile = getJson("https://api.minecraftservices.com/minecraft/profile", mcToken);
         if (profile != null && profile.has("id")) {
            Accounts.Account account = new Accounts.Account();
            account.type = "microsoft";
            account.name = profile.get("name").getAsString();
            account.uuid = profile.get("id").getAsString();
            account.token = mcToken;
            return account;
         } else {
            throw new Exception(ClientConfig.t("该微软账号没有 Minecraft Java 版", "this Microsoft account does not own Minecraft Java Edition"));
         }
      } else {
         throw new Exception(ClientConfig.t("Minecraft 服务拒绝了这次登录", "Minecraft service rejected this sign-in"));
      }
   }

   private static String enc(String s) throws Exception {
      return URLEncoder.encode(s, "UTF-8");
   }

   private static JsonObject post(String url, JsonObject body) throws Exception {
      return request("POST", url, body.toString(), true, null);
   }

   private static JsonObject postForm(String url, String form) throws Exception {
      return request("POST", url, form, false, null);
   }

   private static JsonObject getJson(String url, String bearer) throws Exception {
      return request("GET", url, null, true, bearer);
   }

   private static JsonObject request(String method, String url, String body, boolean jsonBody, String bearer) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
      conn.setConnectTimeout(8000);
      conn.setReadTimeout(10000);
      conn.setRequestMethod(method);
      conn.setRequestProperty("User-Agent", "Client/1.0.1 (Minecraft 1.8.9)");
      conn.setRequestProperty("Accept", "application/json");
      if (bearer != null) {
         conn.setRequestProperty("Authorization", "Bearer " + bearer);
      }

      if (body != null) {
         conn.setDoOutput(true);
         conn.setRequestProperty("Content-Type", jsonBody ? "application/json; charset=utf-8" : "application/x-www-form-urlencoded");
         OutputStream out = conn.getOutputStream();
         out.write(body.getBytes(StandardCharsets.UTF_8));
         out.close();
      }

      int code = conn.getResponseCode();
      InputStream stream = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
      String text = read(stream);
      conn.disconnect();
      if (text != null && !text.trim().isEmpty()) {
         JsonObject json = new JsonParser().parse(text).getAsJsonObject();
         if (code >= 400 && !json.has("error")) {
            throw new Exception("HTTP " + code + ": " + text);
         } else {
            return json;
         }
      } else {
         throw new Exception("HTTP " + code);
      }
   }

   private static String read(InputStream stream) {
      if (stream == null) {
         return null;
      } else {
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
               sb.append(line);
            }

            reader.close();
            return sb.toString();
         } catch (Exception var41) {
            return null;
         }
      }
   }

   public static final class DeviceCode {
      public String deviceCode;
      public String userCode;
      public String verificationUri;
      public int interval = 5;
   }
}
