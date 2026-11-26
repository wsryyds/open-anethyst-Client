package com.mcpacks.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class IconUtils {
   private static BufferedImage folderIconCache = null;
   private static BufferedImage parentNavIconCache = null;
   private static BufferedImage defaultPackIconCache = null;

   private IconUtils() {
   }

   public static synchronized BufferedImage getFolderIcon() {
      if (folderIconCache != null) {
         return folderIconCache;
      } else {
         int size = 64;
         BufferedImage img = new BufferedImage(size, size, 2);
         Graphics2D g = img.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         GradientPaint backPaint = new GradientPaint(0.0F, 10.0F, new Color(245, 180, 50), 0.0F, 56.0F, new Color(210, 140, 20));
         g.setPaint(backPaint);
         g.fillRoundRect(6, 12, 52, 44, 8, 8);
         g.fillRoundRect(6, 6, 24, 14, 6, 6);
         g.setColor(new Color(240, 240, 245));
         g.fillRoundRect(12, 16, 40, 36, 4, 4);
         g.setColor(new Color(200, 200, 210));
         g.fillRect(16, 22, 22, 3);
         g.fillRect(16, 28, 32, 3);
         g.fillRect(16, 34, 28, 3);
         GradientPaint frontPaint = new GradientPaint(0.0F, 24.0F, new Color(255, 205, 80), 0.0F, 56.0F, new Color(230, 160, 30));
         g.setPaint(frontPaint);
         int[] xPoints = new int[]{6, 58, 56, 8};
         int[] yPoints = new int[]{24, 24, 56, 56};
         g.fillPolygon(xPoints, yPoints, 4);
         g.setColor(new Color(255, 235, 150, 180));
         g.drawLine(7, 25, 57, 25);
         g.setColor(new Color(140, 90, 10, 220));
         g.drawRoundRect(6, 12, 52, 44, 8, 8);
         g.drawPolygon(xPoints, yPoints, 4);
         g.dispose();
         folderIconCache = img;
         return folderIconCache;
      }
   }

   public static synchronized BufferedImage getParentNavIcon() {
      if (parentNavIconCache != null) {
         return parentNavIconCache;
      } else {
         int size = 64;
         BufferedImage img = new BufferedImage(size, size, 2);
         Graphics2D g = img.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.setColor(new Color(40, 45, 55, 200));
         g.fillRoundRect(4, 4, 56, 56, 14, 14);
         g.setColor(new Color(90, 100, 120, 220));
         g.drawRoundRect(4, 4, 56, 56, 14, 14);
         g.setColor(new Color(255, 215, 0));
         int[] xArrow = new int[]{32, 16, 24, 24, 40, 40, 48};
         int[] yArrow = new int[]{14, 30, 30, 48, 48, 30, 30};
         g.fillPolygon(xArrow, yArrow, 7);
         g.setColor(new Color(255, 245, 180));
         g.drawPolygon(xArrow, yArrow, 7);
         g.dispose();
         parentNavIconCache = img;
         return parentNavIconCache;
      }
   }

   public static synchronized BufferedImage getDefaultPackIcon() {
      if (defaultPackIconCache != null) {
         return defaultPackIconCache;
      } else {
         int size = 64;
         BufferedImage img = new BufferedImage(size, size, 2);
         Graphics2D g = img.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.setColor(new Color(50, 50, 60));
         g.fillRoundRect(6, 6, 52, 52, 10, 10);
         g.setColor(new Color(80, 80, 95));
         g.drawRoundRect(6, 6, 52, 52, 10, 10);
         g.setColor(new Color(110, 160, 60));
         g.fillRect(14, 14, 36, 12);
         g.setColor(new Color(130, 90, 50));
         g.fillRect(14, 26, 36, 24);
         g.dispose();
         defaultPackIconCache = img;
         return defaultPackIconCache;
      }
   }
}
