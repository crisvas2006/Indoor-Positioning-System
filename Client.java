package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Andrei
 */
public class Client {

    public static String RSSI;
    public static PrintWriter out;

    public static void main(String[] args) throws IOException {
        while (true) {
            FileInputStream fstream1 = new FileInputStream("Log.txt");
            BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream1));
            String rssi1, rssi2 = "";
            while ((rssi1 = br1.readLine()) != null) {
                if(!rssi1.equals("n/a"))
                    rssi2 = rssi1;
            }
            rssi2 = rssi2.substring(22);
            //1 for tower 1, 2 for tower 2
            rssi2 = "1" + rssi2;
            rssi2 = encrypt(rssi2);
            System.out.println(rssi2);

            try {
                System.out.println("da");
                Socket socket = new Socket("localhost", 4321);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(rssi2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
        }
    }
    public static String encrypt(String d){
        String s ="";
        int l=d.length();
        int square=1;  //indicates the sizes of the array
        while(square*square<l)
            square++;
        char[][] grid = new char[square][square];
        int charPos=0;
        for(int i=0;i<square;i++)
            for(int j=0;j<square;j++)
                if(charPos<l){
                    grid[i][j]=d.charAt(charPos);
                    charPos++;
                }
                else
                    grid[i][j]=' ';
        
        for(int i=0;i<square;i++)
            for(int j=0;j<square;j++)
                s = s + grid[j][i];
       // s=s+"'";
                return s; 
    }
}