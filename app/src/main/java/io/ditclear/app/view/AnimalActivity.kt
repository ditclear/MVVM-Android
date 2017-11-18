package io.ditclear.app.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.ditclear.app.R
import io.ditclear.app.databinding.AnimalActivityBinding
import io.ditclear.app.model.Animal
import io.ditclear.app.viewmodel.AnimalViewModel

class AnimalActivity : AppCompatActivity() {

    lateinit var mBinding : AnimalActivityBinding
    lateinit var mViewMode : AnimalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.animal_activity)

        //////model
        val animal= Animal("dog", 0)
        /////ViewModel
        mViewMode= AnimalViewModel(animal)
        ////binding
        mBinding.vm=mViewMode
    }
}
