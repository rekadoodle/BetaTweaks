// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src.betatweaks;

import java.io.IOException;
import java.net.*;

import net.minecraft.src.betatweaks.gui.GuiServerList;

// Referenced classes of package net.minecraft.src:
//            ServerNBTStorage, GuiSlotServer, GuiMultiplayer

public class ThreadPollServers extends Thread
{

    public ThreadPollServers(GuiServerList menu, ServerData server)
    {
    	this.menu = menu;
        this.server = server;
    }
Long var1;
    public void run() {
        boolean var27 = false;

        label1: {
           label2: {
              label3: {
                 label4: {
                    label5: {
                       try {
                          var27 = true;
                          this.server.status = "\u00a78Polling..";
                          var1 = System.nanoTime();
                          menu.pollServer(server);
                          long var3 = System.nanoTime();
                          server.responseTime = (var3 - var1) / 1000000L;
                          var27 = false;
                          break label1;
                       } catch (UnknownHostException var35) {
                          server.responseTime = -1L;
                          server.status = "\u00a74Can\'t resolve hostname";
                          var27 = false;
                       } catch (SocketTimeoutException var36) {
                          server.responseTime = -1L;
                          server.status = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label5;
                       } catch (ConnectException var37) {
                          server.responseTime = -1L;
                          server.status = "\u00a74Can\'t reach server";
                          var27 = false;
                          break label4;
                       } catch (IOException var38) {
                          //server.ping = -1L;
                    	   long var3 = System.nanoTime();
                           server.responseTime = (var3 - var1) / 1000000L;
                          server.status = "Server Online";
                          var27 = false;
                          break label3;
                       } catch (Exception var39) {
                          server.responseTime = -1L;
                          server.status = "ERROR: " + var39.getClass();
                          var27 = false;
                          break label2;
                       } finally {
                          if(var27) {
                             synchronized(GuiServerList.getSync()) {
                            	 menu.serversBeingPinged--;
                             }
                          }
                       }

                       synchronized(GuiServerList.getSync()) {
                    	   menu.serversBeingPinged--;
                          return;
                       }
                    }

                    synchronized(GuiServerList.getSync()) {
                    	menu.serversBeingPinged--;
                       return;
                    }
                 }

                 synchronized(GuiServerList.getSync()) {
                	 menu.serversBeingPinged--;
                    return;
                 }
              }

              synchronized(GuiServerList.getSync()) {
            	  menu.serversBeingPinged--;
                 return;
              }
           }

           synchronized(GuiServerList.getSync()) {
        	   menu.serversBeingPinged--;
              return;
           }
        }

        synchronized(GuiServerList.getSync()) {
        	menu.serversBeingPinged--;
        }

     }

    final ServerData server;
    final GuiServerList menu;
}
