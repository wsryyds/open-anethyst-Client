package net.client.ime;

interface ImeBackend {
   boolean available();

   String composition();

   void setCaret(int var1, int var2);

   void cancel();

   void reset();

   boolean swallowVirtualKey(int var1);
}
