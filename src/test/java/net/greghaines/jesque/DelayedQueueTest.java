package net.greghaines.jesque;

import static net.greghaines.jesque.TestUtils.createJedis;
import static net.greghaines.jesque.utils.JesqueUtils.createKey;
import static net.greghaines.jesque.utils.JesqueUtils.entry;
import static net.greghaines.jesque.utils.JesqueUtils.map;
import static net.greghaines.jesque.utils.ResqueConstants.FAILED;
import static net.greghaines.jesque.utils.ResqueConstants.PROCESSED;
import static net.greghaines.jesque.utils.ResqueConstants.QUEUE;
import static net.greghaines.jesque.utils.ResqueConstants.STAT;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author Animesh Kumar <smile.animesh@gmail.com>
 */
public class DelayedQueueTest {
    
    private static final Config config = new ConfigBuilder().build();
    private static final String testQueue = "foo";
    private static final String delayTestQueue = "fooDelay";

    @Before
    public void resetRedis() {
        TestUtils.resetRedis(config);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCode() throws Exception {
        // Enqueue the job before worker is created and started
        final List<DelayedJob> jobs = new ArrayList<DelayedJob>(10);
        for (int i = 0; i < 10; i++) {
            jobs.add(new DelayedJob("TestAction", delayTestQueue, new Object[] { i, 2.3, true, "test", Arrays.asList("inner", 4.5) }));
        }
        TestUtils.delayEnqueueJobs(delayTestQueue, jobs, config);
        jobs.clear();
        for (int i = 10; i < 20; i++) {
            jobs.add(new DelayedJob("TestAction", delayTestQueue, new Object[] { i, 2.3, true, "test", Arrays.asList("inner", 4.5) }));
        }
        //TestUtils.enqueueJobs(testQueue, jobs, config);

        Jedis jedis = createJedis(config);
        try { // Assert that we enqueued the job
            Assert.assertEquals(Long.valueOf(10),
                    jedis.zcount(createKey(config.getNamespace(), QUEUE, delayTestQueue), "-inf", "+inf"));
        } finally {
            jedis.quit();
        }

        // Create and start worker
        final Worker worker = new WorkerImpl(config, Arrays.asList(delayTestQueue), map(entry("TestAction",
                TestAction.class)));
        final Thread workerThread = new Thread(worker);
        workerThread.start();

        // start second thread
        final Worker worker2 = new WorkerImpl(config, Arrays.asList(testQueue), map(entry("TestAction",
                TestAction.class)));
        final Thread workerThread2 = new Thread(worker2);
        workerThread2.start();

        try { // Wait a bit to ensure the worker had time to process the job
            Thread.sleep(1000 * 6);
        } finally { // Stop the worker
            TestUtils.stopWorker(worker, workerThread);
        }

        // Assert that the job was run by the worker
        jedis = createJedis(config);
        try {
            Assert.assertEquals(String.valueOf(20), jedis.get(createKey(config.getNamespace(), STAT, PROCESSED)));
            Assert.assertNull(jedis.get(createKey(config.getNamespace(), STAT, FAILED)));
            Assert.assertEquals(Long.valueOf(0),
                    jedis.zcount(createKey(config.getNamespace(), QUEUE, delayTestQueue), "-inf", "+inf"));
        } finally {
            jedis.quit();
        }
    }
}
