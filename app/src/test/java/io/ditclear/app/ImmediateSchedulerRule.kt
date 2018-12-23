package io.ditclear.app

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.TimeUnit

/**
 * 页面描述：ImmediateSchedulerRule
 * 使用RxJavaPlugins和RxAndroidPlugins这些类用TestScheduler覆盖默认的scheduler。
 *
 * Created by ditclear on 2018/11/19.
 */
class ImmediateSchedulerRule private constructor(): TestRule {

    private object Holder { val INSTANCE = ImmediateSchedulerRule () }

    companion object {
        val instance: ImmediateSchedulerRule by lazy { Holder.INSTANCE }
    }

    private val immediate = TestScheduler()

    override fun apply(base: Statement, d: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxJavaPlugins.setInitIoSchedulerHandler { immediate }
                RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
                RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }

                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }
    //将时间提前xx ms
    fun advanceTimeBy(milliseconds:Long){
        immediate.advanceTimeBy(milliseconds,TimeUnit.MILLISECONDS)

    }
    //将时间提前到xx ms
    fun advanceTimeTo(milliseconds:Long){
        immediate.advanceTimeTo(milliseconds,TimeUnit.MILLISECONDS)

    }
}
