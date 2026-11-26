package com.amethyst.blur.util;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import net.minecraft.class_1653;
import net.minecraft.class_1655;
import net.minecraft.class_1669;
import net.minecraft.class_1671;
import net.minecraft.class_1680;
import net.minecraft.class_1989;

public class ShaderResourcePack implements class_1655 {
   private static final Set<String> NAMESPACES = ImmutableSet.of("minecraft");
   private final Map<class_1653, String> loadedData = new HashMap<>();

   private static boolean validPath(class_1653 location) {
      return "minecraft".equals(location.method_5891()) && location.method_5890().startsWith("shaders/");
   }

   @Override
   public InputStream method_5897(class_1653 id) throws IOException {
      if (!validPath(id)) {
         throw new FileNotFoundException(id.toString());
      } else {
         String data = this.loadedData.get(id);
         if (data == null) {
            try (InputStream in = ShaderResourcePack.class.getResourceAsStream("/" + id.method_5890())) {
               if (in == null) {
                  throw new FileNotFoundException(id.toString());
               }

               StringBuilder builder = new StringBuilder();
               Scanner scanner = new Scanner(in, "UTF-8");

               try {
                  while (scanner.hasNextLine()) {
                     builder.append(scanner.nextLine()).append('\n');
                  }
               } finally {
                  scanner.close();
               }

               data = builder.toString();
            }

            this.loadedData.put(id, data);
         }

         return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
      }
   }

   @Override
   public boolean method_5900(class_1653 id) {
      return validPath(id) && ShaderResourcePack.class.getResource("/" + id.method_5890()) != null;
   }

   @Override
   public Set<String> method_5901() {
      return NAMESPACES;
   }

   @Override
   public <T extends class_1669> T method_5898(class_1671 serializer, String key) throws IOException {
      return (T)("pack".equals(key) ? new class_1680(new class_1989("Blur shaders"), 3) : null);
   }

   @Override
   public BufferedImage method_5896() throws IOException {
      throw new FileNotFoundException("pack.png");
   }

   @Override
   public String method_5899() {
      return "Blur shaders";
   }
}
