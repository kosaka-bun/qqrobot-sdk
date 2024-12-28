package de.honoka.qqrobot.starter.util

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object GlobalThreadPools {
    
    //用于执行非即时任务（多而密集，但不需要尽快完成）
    @JvmField
    val pool = ThreadPoolExecutor(
        3, 10, 10, TimeUnit.SECONDS,
        LinkedBlockingQueue(10), ThreadPoolExecutor.AbortPolicy()
    )
    
    //用于执行即时任务（大部分时候都少而不密集，需要尽快完成）
    @JvmField
    val instantPool = ThreadPoolExecutor(
        1, 20, 60, TimeUnit.SECONDS,
        SynchronousQueue(), ThreadPoolExecutor.AbortPolicy()
    )
}
