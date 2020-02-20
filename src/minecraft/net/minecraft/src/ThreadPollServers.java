// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.IOException;
import java.net.*;

// Referenced classes of package net.minecraft.src:
//            ServerNBTStorage, GuiSlotServer, GuiMultiplayer

class ThreadPollServers extends Thread
{

    ThreadPollServers(GuiSlotServer guislotserver, ServerNBTStorage servernbtstorage)
    {
        field_35600_b = guislotserver;
        field_35601_a = servernbtstorage;
    }

    public void run() {
        boolean var27 = false;

        label183: {
           label184: {
              label185: {
                 label186: {
                    label187: {
                       try {
                          var27 = true;
                          this.field_35601_a.field_35791_d = "\u00a78Polling..";
                          long var1 = System.nanoTime();
                          GuiMultiplayerMenu.func_35336_a(this.field_35600_b.field_35410_a, this.field_35601_a);
                          long var3 = System.nanoTime();
                          this.field_35601_a.field_35792_e = (var3 - var1) / 1000000L;
                          var27 = false;
                          break label183;
                       } catch (UnknownHostException var35) {
                          this.field_35601_a.field_35792_e = -1L;
                          this.field_35601_a.field_35791_d = "\u00a74Can\'t resolve hostname";
                          var27 = false;
                       } catch (SocketTimeoutException var36) {
                          this.field_35601_a.field_35792_e = -1L;
                          this.field_35601_a.field_35791_d = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label187;
                       } catch (ConnectException var37) {
                          this.field_35601_a.field_35792_e = -1L;
                          this.field_35601_a.field_35791_d = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label186;
                       } catch (IOException var38) {
                          this.field_35601_a.field_35792_e = -1L;
                          this.field_35601_a.field_35791_d = "\u00a74Communication error";
                          var27 = false;
                          break label185;
                       } catch (Exception var39) {
                          this.field_35601_a.field_35792_e = -1L;
                          this.field_35601_a.field_35791_d = "ERROR: " + var39.getClass();
                          var27 = false;
                          break label184;
                       } finally {
                          if(var27) {
                             synchronized(GuiMultiplayerMenu.func_35321_g()) {
                            	 GuiMultiplayerMenu.func_35335_o();
                             }
                          }
                       }

                       synchronized(GuiMultiplayerMenu.func_35321_g()) {
                    	   GuiMultiplayerMenu.func_35335_o();
                          return;
                       }
                    }

                    synchronized(GuiMultiplayerMenu.func_35321_g()) {
                    	GuiMultiplayerMenu.func_35335_o();
                       return;
                    }
                 }

                 synchronized(GuiMultiplayerMenu.func_35321_g()) {
                	 GuiMultiplayerMenu.func_35335_o();
                    return;
                 }
              }

              synchronized(GuiMultiplayerMenu.func_35321_g()) {
            	  GuiMultiplayerMenu.func_35335_o();
                 return;
              }
           }

           synchronized(GuiMultiplayerMenu.func_35321_g()) {
        	   GuiMultiplayerMenu.func_35335_o();
              return;
           }
        }

        synchronized(GuiMultiplayerMenu.func_35321_g()) {
        	GuiMultiplayerMenu.func_35335_o();
        }

     }

    final ServerNBTStorage field_35601_a; /* synthetic field */
    final GuiSlotServer field_35600_b; /* synthetic field */
}
