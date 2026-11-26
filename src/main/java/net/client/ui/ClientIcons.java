package net.client.ui;

public final class ClientIcons {
   private static final String[] GROUP_ICONS = new String[]{
      "gesture",
      "healing",
      "wb_sunny",
      "waves",
      "mouse",
      "directions_run",
      "visibility",
      "auto_awesome",
      "cloud",
      "crop_free",
      "badge",
      "chat",
      "format_list_numbered",
      "thunderstorm",
      "schedule",
      "blur_on",
      "animation",
      "auto_awesome",
      "inventory_2",
      "tune",
      "my_location",
      "pan_tool",
      "360",
      "grain"
   };

   private ClientIcons() {
   }

   public static String group(int groupId) {
      if (groupId == 226) {
         return "crop_free";
      } else {
         if (groupId >= 200 && groupId <= 225) {
            int index = groupId - 200;
            int cursor = 0;
            int[] ids = new int[]{200, 201, 202, 203, 214, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 215, 221, 222, 218, 220, 219, 223, 224, 225};

            for (int i = 0; i < ids.length; i++) {
               if (ids[i] == groupId) {
                  cursor = i;
                  break;
               }
            }

            if (cursor < GROUP_ICONS.length) {
               return GROUP_ICONS[cursor];
            }
         }

         return "settings";
      }
   }
}
