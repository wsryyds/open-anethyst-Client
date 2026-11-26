package com.mcpacks.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.client.util.ClientLog;

public final class JsonUtils {
   private JsonUtils() {
   }

   public static JsonUtils.PackMeta parsePackMeta(InputStream is) {
      if (is == null) {
         return new JsonUtils.PackMeta(1, "");
      } else {
         try {
            JsonUtils.PackMeta var11;
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
               JsonParser parser = new JsonParser();
               JsonElement root = parser.parse(reader);
               if (root != null && root.isJsonObject()) {
                  JsonObject packObj = root.getAsJsonObject().getAsJsonObject("pack");
                  if (packObj == null) {
                     return new JsonUtils.PackMeta(1, "");
                  }

                  int format = packObj.has("pack_format") ? packObj.get("pack_format").getAsInt() : 1;
                  String desc = "";
                  if (packObj.has("description")) {
                     JsonElement descElem = packObj.get("description");
                     desc = descElem.isJsonPrimitive() ? descElem.getAsString() : descElem.toString();
                  }

                  return new JsonUtils.PackMeta(format, desc);
               }

               var11 = new JsonUtils.PackMeta(1, "");
            }

            return var11;
         } catch (Exception var13) {
            ClientLog.warn("解析 pack.mcmeta 失败，使用默认描述", var13);
            return new JsonUtils.PackMeta(1, "");
         }
      }
   }

   public static class PackMeta {
      public final int packFormat;
      public final String description;

      public PackMeta(int packFormat, String description) {
         this.packFormat = packFormat;
         this.description = description != null ? description : "";
      }
   }
}
