

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MiddlewareCache {
    ArrayList<String> hosts = new ArrayList<>();
    ArrayList<Integer> ports = new ArrayList<>();
    ArrayList<ArrayList<Float>> location = new ArrayList<>();

    /*
    hosts: stores the ip address of each Q3.CacheNode, which is a seperate server storing cache of nearby area.
    ports: stores the ports that corresponding to the ip address
    location: stores longitude and latitude of each Q3.CacheNode, so that we can store the data in the nearest Q3.CacheNode to decrease latency.
     */

    public MiddlewareCache(){
        super();
    }
    


    /*
    This method register a new Q3.CacheNode to the middleware, which is a seperate server stores cache
     */
    public boolean addNewCacheNode(String host,int port, float longitude, float latitude ){
        boolean flag_host = this.hosts.contains(host);
        boolean flag_port = this.ports.contains(port);
        if( !(flag_host&&flag_port)){
            hosts.add(host);
            ports.add(port);
            ArrayList<Float> temp = new ArrayList<>();
            temp.add(longitude);
            temp.add(latitude);
            location.add(temp);
            return true;
        }
        return false;
    }
    /*
    This method delete an cacheNode already registered in the middleware.
     */

    public void deteleCacheNode(int pos){
        hosts.remove(pos);
        ports.remove(pos);
        location.remove(pos);
    }
    /*This function use TCP socket to store cache in the nearest cashNode,
    First, the fuction calls "nearest" to fing the nearest node,
    then form an command of <key,value> in form of String,
    next step is send the corresponding node by tcp socket.
     */
    public void put(String key, String value,float lon, float lat) {
        int location = nearest (lon, lat);
        String host = this.hosts.get(location);
        int port = this.ports.get(location);
        String command = key + "," + value;
        try {
            Socket socket = new Socket(host, port);
            PrintWriter to_Node = new PrintWriter(socket.getOutputStream(), true);
            to_Node.println(command);
            to_Node.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* The function get value from Cache
    Similar to put function, it first find the proper node to communicate,
    then it send its request to the node and wait for the reply.
    Finally, it return the reply to the caller.
     */

    public String get(String key,float lon, float lat) {
        int location = nearest(lon, lat);
        String host = this.hosts.get(location);
        int port = this.ports.get(location);
        List<String> ret = new ArrayList<>();
        try {

            Socket socket = new Socket(host, port);
            PrintWriter to_Node = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader from_Node = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            to_Node.println(key);
            String reply;
            while((reply = from_Node.readLine())!=null){
                ret.add(reply);
            }
            to_Node.close();
            from_Node.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return ret.size() >0 &&ret.get(0) != null ? ret.get(0):"err";


    }

    //calculate the nearest Q3.CacheNode, assuming there exist only one cache node in one location
    public int nearest(float longitude, float latitude){
        float lat_0 = this.location.get(0).get(1);
        float lon_0 = this.location.get(0).get(0);
        int pos = 0;
        double min = meterDistanceBetweenPoints(latitude,longitude, lat_0, lon_0);
        for (int i = 1 ; i < this.location.size(); i++){
            float lat = this.location.get(i).get(1);
            float lon = this.location.get(i).get(0);
            double distance = meterDistanceBetweenPoints(latitude,longitude, lat, lon);
            if(distance < min){
                min = distance;
                pos = i;
            }
        }
        return pos;
    }

    //The method is to calculate the distance using longitude and latitude.
    // it is taken from https://stackoverflow.com/questions/8049612/calculating-distance-between-two-geographic-locations
    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }



}
