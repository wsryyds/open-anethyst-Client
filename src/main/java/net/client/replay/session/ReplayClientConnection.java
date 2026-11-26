package net.client.replay.session;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import net.minecraft.class_1957;
import net.minecraft.class_1982;
import net.minecraft.class_2569;
import net.minecraft.class_700;

public class ReplayClientConnection extends class_1957 {
   public ReplayClientConnection() {
      super(class_2569.field_11578);
   }

   @Override
   public void method_10588(class_700 packet) {
   }

   @Override
   public void method_10589(
      class_700 packet, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
   ) {
   }

   @Override
   public void method_7390() {
   }

   @Override
   public void method_7394(class_1982 reason) {
   }

   @Override
   public boolean method_7403() {
      return true;
   }

   @Override
   public boolean method_7402() {
      return false;
   }

   @Override
   public boolean method_10592() {
      return false;
   }

   @Override
   public SocketAddress method_7400() {
      return null;
   }
}
