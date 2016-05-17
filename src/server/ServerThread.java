/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class ServerThread extends Thread{
    private Socket socket;
    private BufferedReader in;
    
    public ServerThread(Socket socket){
        try{
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void run(){
        try{
            while(true){
                String RSSI = in.readLine();
                Server.print2RSSI(RSSI);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
