

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/*
The Q3.CacheNode class serve as a datastore for cache.
This class implement both LRU and time expiring scheme of cache.
The class implement LRU by a double linked list.
Each time certain data is accessed, the data will be moved to the head of the linkedlist.
The class implement time expiring eviction by using Java Timer object, which can perform certain task at a given time
 */

public class CacheNode {
    ServerSocket serverSocket;
    int port;
    Hashtable<String, String> cache = new Hashtable<>();
    Node head = null;
    Node tail = null;
    int length = 0;
    int MAX_LENGTH = 100;


    public CacheNode(int port){
        this.port = port;
    }

    /*
    In the main function, I run a infinite loop to listen to the request from any middleware Cache
    When it accept a request, the node will run a seperate process to handle it.
    This implementation makes the node unblocking so that handle multiple request at the sametime.
    Concurrency will increase the performance.
     */
    public static void main(String[] args){
        try {
            int input = Integer.parseInt(args[0]);
            System.out.println("running on "+ input);
            CacheNode cn = new CacheNode(input);
            cn.serverSocket = new ServerSocket(cn.port);
            while(true){
                Socket socket = cn.serverSocket.accept();
                new SocketThread(socket,cn).start();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /*
    The Thread deal with each request from Q3.MiddlewareCache
     */
    static class SocketThread extends Thread {
        Socket socket;
        CacheNode cn;
        public SocketThread(Socket sk, CacheNode cn){
            this.socket = sk;
            this.cn  = cn;
        }
        @Override
        public void run(){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String msg = null;
                msg=in.readLine();
                System.out.println("Command Received: \"" + msg + "\"");
                if(msg!=null) {
                    String[] command = msg.split(",");
                    //This is the case of fetching from Cache
                    //In the case of cash hit, return the element,
                    // move the element to head of the double-linked list then reset the timer.
                    if(command.length ==1){
                        if(cn.cache.containsKey(command[0])){
                            out.println(cn.cache.get(command[0]));
                            Node pos = cn.head;
                            while(pos != null){
                                if(pos.key.equals(command[0])){
                                    pos.resetTimer(cn,command[0]);
                                }
                                if(pos.prev!= null){
                                    if(pos.next == null){
                                        pos.prev.next = null;
                                        cn.tail = pos.prev;
                                        pos.prev = null;
                                        pos.next = cn.head;
                                        cn.head.prev = pos;
                                        cn.head = pos;
                                    }else{
                                        pos.prev.next = pos.next;
                                        pos.next.prev = pos.prev;
                                        pos.prev = null;
                                        pos.next = cn.head;
                                        cn.head.prev = pos;
                                        cn.head = pos;
                                    }
                                }
                                pos = pos.next;
                            }
                        }
                        //In the case of cache miss, return miss
                        else{
                            out.println("miss");
                        }
                    }
                    //case: the request is to put data into cache
                    else if(command.length ==2){
                        //If it's an empty cache, store the data in the hashtable
                        //then set both head and tail to the data
                        //Also start the timer for the node.
                        if(cn.length ==0){
                            cn.cache.put(command[0],command[1]);
                            Node node = new Node(command[0]);
                            cn.head = node;
                            cn.tail = node;
                            node.startTimer(cn, command[0]);

                        }
                        // If the cache is not full, then put the data in the cache
                        else if(cn.length < cn.MAX_LENGTH){
                            cn.length++;
                            cn.cache.put(command[0],command[1]);
                            Node node = new Node(command[0]);
                            node.next = cn.head;
                                cn.head.prev = node;
                                cn.head = node;

                            node.startTimer(cn, command[0]);
                        }
                        // In the case that the cache is already full,
                        //remove the tail of the linkedlist and corresponding element in the hashtable
                        // put the new data in the hashtable and head of linkedlist.
                        else{
                            cn.cache.remove(cn.tail.key);
                            cn.tail.prev.next = null;
                            cn.tail = cn.tail.prev;
                            Node node = new Node(command[0]);
                            node.next = cn.head;
                            cn.head.prev = node;
                            cn.head = node;
                            node.startTimer(cn, command[0]);
                        }
                    }

                }
                in.close();
                out.close();
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    static class Node{
        String key;
        Timer timer = new Timer();

        Node prev = null;
        Node next = null;

        public Node(String key){
            this.key = key;
        }

        /*
        The function uses Java timer object, which run on the background process.
        When it's the time, the timer will execute the pre_scheduled task,
        in this case, the timer will remove the element in the hashtable and remove it self from the linkedlist
         */
        public void startTimer(CacheNode cn,String key){
            Node prev = this.prev;
            Node next = this.next;
            Hashtable<String,String> cache = cn.cache;
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    if (cache.containsKey(key)){
                        cache.remove(key);
                        if(prev == null){
                            if (next == null){
                                cn.head = null;
                                cn.tail = null;
                            }else {
                                cn.head = next;
                            }
                        }else{
                            if(next == null){
                                prev.next = null;
                                cn.tail = prev;
                            }else{
                                prev.next = next;
                                next.prev = prev;
                            }
                        }

                    }
                }
            }, 1000);

        }
        //similar to start timer
        public void resetTimer(CacheNode cn,String key){
            this.timer.cancel();
            this.timer.purge();
            this.timer = new Timer();
            Node prev = this.prev;
            Node next = this.next;
            Hashtable<String,String> cache = cn.cache;
            timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    if (cache.containsKey(key)){
                        cache.remove(key);
                    }
                    if(prev == null){
                        if (next == null){
                            cn.head = null;
                            cn.tail = null;
                        }else {
                            cn.head = next;
                        }
                    }else{
                        if(next == null){
                            prev.next = null;
                            cn.tail = prev;
                        }else{
                            prev.next = next;
                            next.prev = prev;
                        }
                    }
                }
            }, 1000);
        }
    }



}
