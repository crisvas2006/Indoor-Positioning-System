package server;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Server {

    private static int s1 = 0, s2 = 0, s3; //rssis
    private static Point t1, t2, t3; //pos of towers
    private static Point[] towPos = new Point[3]; //array with the pos of towers
    private static int txPower1 = -25, txPower2 = -28, txPower3 = -31; //txpowers
    private static ServerSocket ss;
    private static ArrayList<ServerThread> clienti; //no idea what this is for
    private static Semaphore semaphore = new Semaphore(1);
    private static String rssi1, rssi2 = ""; //used for received values from clients
    private static boolean safe = true; //used to signal if the patient is safe or not
    private static int count = 0; //counts the time the patient is spending in the bathroom
    static JFrame frame = new JFrame(); //for GUI
    static Container contentPane = frame.getContentPane();
    static int[] avg1 = new int[5]; //used for more smooth results of the position of the patient
    static int[] avg2 = new int[5];
    static int[] avg3 = new int[5];
    static int avg11, avg22, avg33;

    public static void main(String[] args) {

        frame.setTitle("CN");
        frame.setSize(1200, 850);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        contentPane.add(new GUI(0, 800, true));
        frame.setVisible(true);

     // GUI app1=new GUI(500,200,false);
        //contentPane.add(app1);
        t1 = new Point(5, 0);
        t2 = new Point(0, 1);
        t3 = new Point(5, 3);
        towPos[0] = new Point(t1.getX(), t1.getY());
        towPos[1] = new Point(t2.getX(), t2.getY());
        towPos[2] = new Point(t3.getX(), t3.getY());

        try {
            clienti = new ArrayList<>();
            ss = new ServerSocket(4321);

            while (true) {

                FileInputStream fstream1 = new FileInputStream("Log.txt");
                BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream1));

                while ((rssi1 = br1.readLine()) != null) {
                    if (!(rssi1.substring(21).compareTo("n/a") == 0)) {
                        rssi2 = rssi1;
                    }
                }
                rssi2 = rssi2.substring(22);
                s3 = Integer.parseInt(rssi2);
                //System.out.println(rssi2);
                br1.close();
                for (int i = 0; i < 4; i++) {
                    avg3[i] = avg3[i + 1];
                }
                avg3[4] = s3;
                avg33 = 0;
                for (int i = 0; i < 4; i++) {
                    avg33 += avg3[i];
                }
                avg33 = avg33 / 5;

                //System.out.println(RSSItoM(s3,txPower1));
                //Thread.sleep(1000);
                ServerThread st = new ServerThread(ss.accept());
                st.start();

                try {

                    semaphore.acquire();
                    clienti.add(st);
                } finally {
                    semaphore.release();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print2RSSI(String RSSI) {

        double d1, d2, d3;
        try {
            semaphore.acquire();

            RSSI = decrypt(RSSI);
            System.out.println("RSSI:" + RSSI);

            if (RSSI.charAt(0) == '1') {
                if (!(RSSI.substring(1).compareTo("n/a") == 0)) {
                    RSSI = RSSI.substring(1);
                    s1 = Integer.parseInt(RSSI);
                    for (int i = 0; i < 4; i++) {
                        avg1[i] = avg1[i + 1];
                    }
                    avg1[4] = s1;
                }
            } else if (RSSI.charAt(0) == '2') {
                if (!(RSSI.substring(1).compareTo("n/a") == 0)) {
                    RSSI = RSSI.substring(1);
                    s2 = Integer.parseInt(RSSI);
                    for (int i = 0; i < 4; i++) {
                        avg2[i] = avg2[i + 1];
                    }
                    avg2[4] = s2;
                }
            }
            avg11 = avg22 = 0;
            for (int i = 0; i < 4; i++) {
                avg11 += avg1[i];
            }
            avg11 = avg11 / 5;
            for (int i = 0; i < 4; i++) {
                avg22 += avg2[i];
            }
            avg22 = avg22 / 5;

                //s1=11.19;
            //s2=11.19;
                    /*d1=RSSItoM(s1,txPower1);
             d2=RSSItoM(s2,txPower2);
             d3=RSSItoM(s3,txPower3);*/
            d1 = RSSItoM(avg11, txPower1);
            d2 = RSSItoM(avg22, txPower2);
            d3 = RSSItoM(avg33, txPower3);
            System.out.println("d1=" + d1);
            System.out.println("d2=" + d2);
            System.out.println("d3=" + d3);
            System.out.println("s1=" + s1);
            System.out.println("s2=" + s2);
            System.out.println("s3=" + s3);
            System.out.println("d t1-t2=" + getDistance(t1, t2));

            //s3=7;
            Point dev = findPos(d1, d2, getDistance(t1, t2), d3, towPos);
            Point dev1 = findPos2(towPos[0], towPos[1], towPos[2], d1, d2, d3);
            Point dev2 = findPos3(towPos[0], d1, towPos[1], d2, towPos[2], d3);
            dev.setX(dev1.getX());
            dev.setY(dev1.getY());
            //dev.setX(dev2.getX()); dev.setY(dev2.getY());

            if (dev.getX() < 4 && dev.getY() > 3 && dev.getX() > 0 && dev.getY() < 6) {
                safe = false;
                count++;
            } else {
                safe = true;
                count = 0;
            }
            if (count >0) {

                try {
                    AudioStream as = new AudioStream(new FileInputStream("alarm.wav"));
                    AudioPlayer.player.start(as);
                    
                    //       try {
                    Thread.sleep(1000);
            //} catch (InterruptedException ex) {
                    //     Thread.currentThread().interrupt();
                    //  }
                    AudioPlayer.player.stop(as);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                count = 0;
            }

            contentPane.add(new GUI((int) dev.getX() * 120, 600 - (int) dev.getY() * 120, safe));
            frame.setVisible(true);
            //Point dev=findPos(s1, s2, 20, s3, towPos);
            System.out.println(dev.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static Point findPos(double t1d, double t2d, double t12d, double t3d, Point[] towPos) {
        Point posd;
        double x, y;
        //convention: p(0,0): bottom left
        //[1]and[2] are for finding pos, [3] is for deciding semiplane
        Point[] towers = orderTowers(towPos);

        x = (t2d / (t1d + t2d)) * t12d;//10

        y = sqrt(t2d * t2d - x * x);
        System.out.println("YYYY " + y + " t1d " + t1d + " x " + x);

        double projDist;
        projDist = sqrt(pow(towers[2].getX() - x, 2) + pow(towers[2].getY() - y, 2));
        if (t3d - 1 > projDist) {
            y = (-1) * y;
        }
        posd = new Point(x, abs(y));
        return posd;
    }

    //third tower for decision is in the middle
    public static Point[] orderTowers(Point[] tow) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tow[i].getX() < tow[j].getX()) {
                    Point aux = tow[i];
                    tow[i] = tow[j];
                    tow[j] = aux;
                }
            }
        }
        Point aux = tow[1];
        tow[1] = tow[2];
        tow[2] = aux;

        return tow;
    }

    public static double getDistance(Point p1, Point p2) {
        double dist;
        dist = sqrt(pow(p1.getX() - p2.getX(), 2) + pow(p1.getY() - p2.getY(), 2));

        return dist;
    }

    public static double RSSItoM(int rssi, int txPower) {
        /*        RSSI (dBm) = -10n log10(d) + txPower
         Where txPower is the received signal strength in dBm at 1 metre
         d = 10 ^ ((TxPower - RSSI) / (10 * n))
         */
        return pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    public static String decrypt(String d) {
        String s = "";
        int l = d.length();
        int square = (int) sqrt(l);  //indicates the sizes of the array
        char[][] grid = new char[square][square];
        int charPos = 0;
        for (int i = 0; i < square; i++) {
            for (int j = 0; j < square; j++) {
                if (charPos < l) {
                    grid[i][j] = d.charAt(charPos);
                    charPos++;
                } else {
                    grid[i][j] = ' ';
                }
            }
        }

        for (int i = 0; i < square; i++) {
            for (int j = 0; j < square; j++) {
                s = s + grid[j][i];
            }
        }
        int index = l - 1;
        while (s.charAt(index) == ' ') {
            index--;
        }

        s = s.substring(0, index + 1);
        return s;

    }

    public static double RSSItoM2(double signalLevelInDb) {
        double exp = (-27.55 - (20 * Math.log10(2417)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    public static Point findPos2(
            Point a, Point b, Point c,
            double dA,
            double dB,
            double dC) {
        double W, Z, x, y, y2;
        W = dA * dA - dB * dB - a.getX() * a.getX() - a.getY() * a.getY() + b.getX() * b.getX() + b.getY() * b.getY();
        Z = dB * dB - dC * dC - b.getX() * b.getX() - b.getY() * b.getY() + c.getX() * c.getX() + c.getY() * c.getY();

        x = (W * (c.getY() - b.getY()) - Z * (b.getY() - a.getY())) / (2 * ((b.getX() - a.getX()) * (c.getY() - b.getY()) - (c.getX() - b.getX()) * (b.getY() - a.getY())));
        y = (W - 2 * x * (b.getX() - a.getX())) / (2 * (b.getY() - a.getY()));
        //y2 is a second measure of y to mitigate errors
        y2 = (Z - 2 * x * (c.getX() - b.getX())) / (2 * (c.getY() - b.getY()));
        y = (y + y2) / 2;
        return new Point(x, y); //"Position: " + x + " , " + y;
    }

    public static Point findPos3(
            Point ponto1, double distance1,
            Point ponto2, double distance2,
            Point ponto3, double distance3) {

        Point retorno = new Point(0, 0);
        double[] P1 = new double[2];
        double[] P2 = new double[2];
        double[] P3 = new double[2];
        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];
        double jval = 0;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        double triptx;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        P1[0] = ponto1.getX();
        P1[1] = ponto1.getY();
        P2[0] = ponto2.getX();
        P2[1] = ponto2.getY();
        P3[0] = ponto3.getX();
        P3[1] = ponto3.getY();

        distance1 = (distance1 / 100000);
        distance2 = (distance2 / 100000);
        distance3 = (distance3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            t = t1 - t2;
            temp += (t * t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            exx = (t1 - t2) / (Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1 * t2);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t = t1 - t2 - t3;
            p3p1i += (t * t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3) / Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1 * t2);
        }
        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2)) / (2 * d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2)) / (2 * jval)) - ((ival / jval) * xval);

        t1 = ponto1.getX();
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;
        retorno.setX(triptx);
        t1 = ponto1.getY();
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        triptx = t1 + t2 + t3;
        retorno.setY(triptx);

        return retorno;
    }

}
