package io.ditclear.app.viewmodel

import android.databinding.ObservableField
import io.ditclear.app.model.Animal

/**
 * 页面描述：AnimalViewModel
 * @param animal 数据源Model(MVVM 中的M),负责提供ViewModel中需要处理的数据
 * Created by ditclear on 2017/11/17.
 */
class AnimalViewModel(val animal: Animal){

    //////////////////data//////////////
    val info= ObservableField<String>("${animal.name} 叫了 ${animal.shoutCount}声..")


    //////////////////binding//////////////
    fun shout(){
        animal.shoutCount++
        info.set("${animal.name} 叫了 ${animal.shoutCount}声..")
    }
}