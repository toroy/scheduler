package com.clubfactory.platform.scheduler.server.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event;

public class PathCacheDemo {

	private static final String PATH = "/pathCache";

    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer();
        CuratorFramework client = CuratorFrameworkFactory
        		.builder().connectionTimeoutMs(3000)
        		.connectString(server.getConnectString())
        		.retryPolicy(new ExponentialBackoffRetry(1000, 3))
        		.build();
        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, PATH, true);
        cache.start();
        
//        Watcher w = new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                Event.EventType type = watchedEvent.getType();
//                if (type.equals(Event.EventType.NodeDeleted)) {
//                    System.out.println(watchedEvent.getPath());
//                }
//            }
//        };
        
 
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PATH,"new content".getBytes());
        
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("监听器watchedEvent：" + watchedEvent);
            }
        }).forPath(PATH);
        
//        client.getChildren().usingWatcher(w).forPath("/msg_node_list/b");
//        
//        PathChildrenCacheListener cacheListener = (client1, event) -> {
//            System.out.println("事件类型：" + event.getType());
//            if (null != event.getData()) {
//                System.out.println("节点数据：" + event.getData().getPath() + " = " + new String(event.getData().getData()));
//            }
//        };
//        cache.getListenable().addListener(cacheListener);
//        
//        
//        
//        client.create().creatingParentsIfNeeded().forPath("/example/pathCache/test02", "02".getBytes());
//        Thread.sleep(10);
//        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/example/pathCache/test01", "01".getBytes());
//        Thread.sleep(10);
//       // client.setData().forPath("/example/pathCache/test01", "01_V2".getBytes());
//        Thread.sleep(10);
//        for (ChildData data : cache.getCurrentData()) {
//            System.out.println("getCurrentData:" + data.getPath() + " = " + new String(data.getData()));
//        }
//        //client.delete().forPath("/example/pathCache/test01");
//        Thread.sleep(10);
//        client.delete().forPath("/example/pathCache/test02");
        Thread.sleep(100000 * 5);
        cache.close();
        client.close();
        System.out.println("OK!");
    }

}
