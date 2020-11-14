## Geo Dirtributed LRU Cache

I'm not very familiar with Cache in general, so I tried my best to implement the Cache from my understanding. I'm 
most familiar with Java, so I decide to implement the system using java because it seems a little bit difficult to me.

From my point of view, the geo distributed LRU cache is just like a distributed system. Each server of the system stores
data and one middleware allocate each request to corresponding server. For example, the one client from New York 
wants to access some data, the middleware will find the nearest CacheNode, which located in montreal and check if the data is in
the node. However if a client from Vancouver also wants to fetch something from database, it's highly probable that the data
needed is very different from what client in New York wants because of locality of reference. Therefore the Middleware 
will direct his request to a nearby node in Seattle for example.

I implement the cache with a Hashtable and a double linkedlist. Using Hashtable enables fast query so decreasing the latency.
Using the double linkedlist will help me to achieve the LRU feature. If some data is used, it will be moved to head of
linedlist. So if the cache reaches the maximum length, the tail is removed. To achieve the time-expiring eviction, I add 
a Java timer in the linkedlist. The timer will start a parallel process in the background. If it's time for expiring an
data entry, it will remove itself from the linkedlist as well as from the Hashtable.

###Missing functionality:
Each cache is not able to backup itself as some caches do.

### Usage:

Compile:

javac MiddlewareCache.java

javac CacheNode.java

Run:

To initiate an CacheNode(cache server): java CacheNode <port_number>

I also write a demo to show that the time expiring eviction works, if you want to run it you can run with

javac demo.java

java demo <host_name_of_a_running_cachenode> <port_number_of_a_running_cachenode>

host_name is the ip address of the running CacheNode. If running locally, the host name is "localhost".
