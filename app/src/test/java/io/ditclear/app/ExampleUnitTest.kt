package io.ditclear.app

import io.reactivex.Single
import org.junit.Test
import java.math.BigDecimal

/**
 * 页面描述：ExampleUnitTest
 *
 * Created by ditclear on 2018/11/22.
 */
class ExampleUnitTest{

    // if {@code this > val}, {@code -1} if {@code this < val},
    //         {@code 0} if {@code this == val}.
    @Test fun `test which is bigger `(){
        print(BigDecimal(0.00).compareTo(BigDecimal(0.000)))
    }

    @Test fun `practice rxJava operator`(){
        Single.just(2)
                .doOnSuccess {
                    println("----------doOnSuccess--------")
                }
                .map { 3 }
                .doOnSubscribe {
                    println("----------doOnSubscribe--------")
                }
                .doAfterTerminate {
                    println("----------doAfterTerminate--------")
                }
                .subscribe({
                    println("----------onSuccess --- $it-----")
                },{
                    println(it.message)
                })

    }
}