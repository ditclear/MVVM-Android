package io.ditclear.app

import io.reactivex.Flowable
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        Flowable.range(0, 10)
                .doOnNext {

                }
                .doOnSubscribe { s->
                    (0..10).forEach {
                        Thread.sleep(1000)
                        //Flowable是拉取型的，需要手动调request才会走下一个
                        s.request(1)//1代表拉取一个，2就是拉取两个
                    }
                }
                .subscribe({
                    println(it)
                },{ println("onError")})
    }
}