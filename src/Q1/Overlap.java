package Q1;

public class Overlap {
    public static boolean overlap(int x1, int x2, int x3, int x4){
        //we assume x1 is the starting point and x2 is the end point so that x1 < x2. This also applies to x3, x4
        if(x2> x3){
            return true;
        }
        if(x4< x1){
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        System.out.println(overlap(1,3,2,4));
        System.out.println(overlap(1,2,3,4));
        System.out.println(overlap(2,4,1,3));
    }
}
