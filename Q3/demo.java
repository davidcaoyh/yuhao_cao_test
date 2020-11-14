public class demo{
    public static void main(String[] args) throws InterruptedException {

        //The demo will show how the cache's time expiring functionality
        //the default timeout is 1000 ms
        //therefore after the the put, the first read should return the value since it the process only sleep for 500ms
        //The rget operation also reset the timer, so in the next 1000ms the data should be still in the cache
        //Thus the second get also return the value.
        //Then the main process sleep for 1500ms which is greater than the expiring time
        //so the third get request is cache miss.

        MiddlewareCache mc = new MiddlewareCache();
        int port = Integer.parseInt(args[1]);
        mc.addNewCacheNode(args[0], port, 105,105);

        mc.put("1", "hello world", 110,110);

        Thread.currentThread().sleep(500);

        String res = mc.get("1", 115,115);
        System.out.println(res);


        Thread.currentThread().sleep(900);

        res = mc.get("1", 115,115);
        System.out.println(res);

        Thread.currentThread().sleep(1500);

        res = mc.get("1", 115,115);
        System.out.println(res);

    }
}