package server;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Point {
    double x,y;
    
    public Point(double x,double y){
        this.x=x;
        this.y=y;
    }
    public void setX(double x){
        this.x=x;
    }
    public void setY(double y){
        this.y=y;
    }
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    @Override
    public String toString() {
        return "Point{" + "x=" + x + ", y=" + y + '}';
    }
    
    
}
