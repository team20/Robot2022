package frc.robot;

public class LinearRangeFinder extends RangeFinder {
    double[] m_distances = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}; // sorted in increasing order
    double[] m_bestAngle= {12, 12, 12, 12, 15, 15, 16.5, 17, 17, 17.5, 18, 19, 19.5};
    double[] m_bestRPM = {2900, 3100, 3150, 3375, 3300, 3425, 3465, 3450, 3485, 3550, 3675, 3725, 3775};
    public LinearRangeFinder() {
        //test();
    }
        
 
    // Utitility function that uses point-slope form of a line to get a good value
    // in between two know points
    //
    // Returns f(x) given [x1, y1], [x2, y2]
    static double interpolate(double x, double x1, double y1, double x2, double y2) {
        double m = (y2 - y1) / (x2- x1);
       // point-slope formula  
       return m * (x - x1) + y1;
    }
 
    public double[] getAngleAndRPM(double distance) {
       // figure out x1 - largest saved distance less than target distance
       int indexX1 = 0;
       for (int i = 0; i < m_distances.length; ++i) {
           if (m_distances[i] <= distance) {
               indexX1 = i;
           }
       }
       // figure out x2 - smallest saved distance greater than target distance
       int indexX2 = indexX1 +1;
       if (indexX2 >= m_distances[m_distances.length-1]) { indexX2 = m_distances.length - 1; }
 
       double x1 = m_distances[indexX1];
       double x2 = m_distances[indexX2];
       double y1 = m_bestAngle[indexX1];
       double y2 = m_bestAngle[indexX2];
       // interpolate angle
       double angle = interpolate(distance, x1, y1, x2, y2);
    
       y1 = m_bestRPM[indexX1];
       y2 = m_bestRPM[indexX2];
       // interpolate distance
       double power = interpolate(distance, x1, y1, x2, y2);
       double[] ret = {angle-12,power};
       return ret;
    }
 
    static void expectEqual(double a, double b) {
       if (Math.abs(a - b) > .001) {System.out.println("Expected " + a + " got " + b); throw new Error("not equal"); }
    }
  
   public void test() {
 
         // test aim at 1 foot is our saved point for 1 foot
         double [] aim1 = getAngleAndRPM(1);
         double d = m_distances[0];
         double a = m_bestAngle[0];
         double r = m_bestRPM[0];
 
        expectEqual(d, 1.0);
        //System.out.println("expect d= " + d + " angle=" + a + " rpm=" + r);
        //System.out.println("got d= " + d + " angle=" + aim1[0] + " rpm=" + aim1[1]);

         expectEqual(a, aim1[0]);
         expectEqual(r, aim1[1]);
 
         // test aim at 2 foot is our saved point for 2 foot
         double [] aim2 = getAngleAndRPM(2);
         d = m_distances[1];
         double a2 = m_bestAngle[1];
         double r2 = m_bestRPM[1];
 
        expectEqual(d, 2.0);
         expectEqual(aim2[0], a2);
         expectEqual(aim2[1], r2);
 
         // test aim at 13 foot is our saved point for 13 foot
         double[] aim = getAngleAndRPM(13);
         d = m_distances[12];
        a = m_bestAngle[12];
        r = m_bestRPM[12];
 
        expectEqual(d, 13);
         expectEqual(aim[0], a);
         expectEqual(aim[1], r);
 
 
 
        }
    }