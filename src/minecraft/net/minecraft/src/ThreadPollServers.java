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

    ThreadPollServers(GuiMultiplayerMenu menu, ServerData server)
    {
    	this.menu = menu;
        this.server = server;
    }
Long var1;
    public void run() {
        boolean var27 = false;

        label183: {
           label184: {
              label185: {
                 label186: {
                    label187: {
                       try {
                          var27 = true;
                          this.server.status = "\u00a78Polling..";
                          var1 = System.nanoTime();
                          menu.pollServer(server);
                          long var3 = System.nanoTime();
                          server.ping = (var3 - var1) / 1000000L;
                          var27 = false;
                          break label183;
                       } catch (UnknownHostException var35) {
                          server.ping = -1L;
                          server.status = "\u00a74Can\'t resolve hostname";
                          var27 = false;
                       } catch (SocketTimeoutException var36) {
                          server.ping = -1L;
                          server.status = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label187;
                       } catch (ConnectException var37) {
                          server.ping = -1L;
                          server.status = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label186;
                       } catch (IOException var38) {
                          //server.ping = -1L;
                    	   long var3 = System.nanoTime();
                           server.ping = (var3 - var1) / 1000000L;
                          server.status = "Server Online";
                          var27 = false;
                          break label185;
                       } catch (Exception var39) {
                          server.ping = -1L;
                          server.status = "ERROR: " + var39.getClass();
                          var27 = false;
                          break label184;
                       } finally {
                          if(var27) {
                             synchronized(GuiMultiplayerMenu.getSync()) {
                            	 menu.pingCount--;
                             }
                          }
                       }

                       synchronized(GuiMultiplayerMenu.getSync()) {
                    	   menu.pingCount--;
                          return;
                       }
                    }

                    synchronized(GuiMultiplayerMenu.getSync()) {
                    	menu.pingCount--;
                       return;
                    }
                 }

                 synchronized(GuiMultiplayerMenu.getSync()) {
                	 menu.pingCount--;
                    return;
                 }
              }

              synchronized(GuiMultiplayerMenu.getSync()) {
            	  menu.pingCount--;
                 return;
              }
           }

           synchronized(GuiMultiplayerMenu.getSync()) {
        	   menu.pingCount--;
              return;
           }
        }

        synchronized(GuiMultiplayerMenu.getSync()) {
        	menu.pingCount--;
        }

     }

    final ServerData server;
    final GuiMultiplayerMenu menu;
}
