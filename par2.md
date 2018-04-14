

![](https://user-gold-cdn.xitu.io/2017/11/19/15fd45485bae61e6?w=1240&h=607&f=png&s=161835)

### 写在前面

这是使用Kotlin开发MVVM应用程序的第二部分—Retrofit及RxJava

在前一部分中我们简单了解了MVVM的基本概念和写法。如果你没有看过上一篇，请先快速浏览一遍，因为本系列是循序渐进的。可以在这里查看[使用Kotlin构建MVVM应用程序—第一部分：入门篇](http://www.jianshu.com/p/80926d9e64f7)

如果第一篇是入了门，那这一篇就有点实战的意思了，更加贴近我们具体的需求，本文将阐述如何在MVVM中处理网络数据。

### Retrofit及RxJava

我们先加入依赖

```groovy
	//rx android
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    compile 'io.reactivex.rxjava2:rxjava:2.1.3'
    //retrofit
    compile 'com.squareup.retrofit2:retrofit:2.3.0'
    compile 'com.squareup.retrofit2:converter-gson:2.3.0'
    compile 'com.squareup.retrofit2:adapter-rxjava2:2.3.0'
    compile 'com.google.code.gson:gson:2.8.0'
```

这次相比上一篇稍微加了点难度，这次加入了网络请求库Retrofit和Rxjava。

[Retrofit](http://square.github.io/retrofit/)是现在主流的网络请求库，不了解的看[官网](http://square.github.io/retrofit/)

[RxJava](https://github.com/ReactiveX/RxJava)是一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库。不了解的当然是推荐经久不衰的[给 Android 开发者的 RxJava 详解](http://gank.io/post/560e15be2dca930e00da1083)

准备工作做好后，先看看现在的MVVM结构

![MVVM](https://user-gold-cdn.xitu.io/2017/11/19/15fd4548593cc229?w=400&h=568&f=png&s=10081)

这次我们的Model层的数据源来自网络，并且在ViewModel中使用RxJava进行数据的转换。

开始正文

### 在MVVM中是怎么处理网络数据的？

> 带着问题看文章是个好习惯。—ditclear

这次我们先来看看xml布局文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools">

    <data>
        <!--需要的viewModel,通过mBinding.vm=mViewMode注入-->
        <variable
                name="vm"
                type="io.ditclear.app.viewmodel.PaoViewModel"/>
    </data>
        <!--省略-->
        <Button
                 <!--省略-->
                android:onClick="@{()->vm.loadArticle()}"
                android:text="load article"/>
        <TextView
                 <!--省略-->
                android:text="@{vm.articleDetail}"
                tools:text="点击按钮，调用ViewModel中的loadArticle方法，通过DataBinding更新UI"/>
</layout>
```

要做的和上一篇差不多，只是现在多了网络请求。

看看现在的项目结构：

![结构](https://user-gold-cdn.xitu.io/2017/11/19/15fd45485b2868c4?w=400&h=363&f=png&s=59185)

相比上一篇，多了一个`PaoService.kt`

```kotlin
interface PaoService{
    //文章详情
    @GET("article_detail.php")
    fun getArticleDetail(@Query("id") id: Int): Single<Article>
}
```

为了简单起见，就只有一个加载文章详情的接口`getArticleDetail`

#### 简单示例

我们现在使用`PaoService`作为我们的数据源(Model层)，提供数据给我们的`PaoViewModel`(ViewModel层)

```kotlin
class PaoViewModel(val remote: PaoService) {
    //////////////////data//////////////
    val articleDetail = ObservableField<String>()
    //////////////////binding//////////////
    fun loadArticle() {
        //为了简单起见这里先写个默认的id
        remote.getArticleDetail(8773)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t: Article? ->
                    articleDetail.set(t?.toString())
                }, { t: Throwable? ->
                    articleDetail.set(t?.message ?: "error")
                })
    }
}
```

和上一篇对比来看相差也不大，只是现在我们的数据来自网络。
再来看看`PaoActivity.kt`(View层)

```kotlin
class PaoActivity : AppCompatActivity() {

    lateinit var mBinding : PaoActivityBinding
    lateinit var mViewMode : PaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.pao_activity)

        //////model
        val remote=Retrofit.Builder()
                .baseUrl("http://api.jcodecraeer.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PaoService::class.java)

        /////ViewModel
        mViewMode= PaoViewModel(remote)
        ////binding
        mBinding.vm=mViewMode
    }
}
```

基本一模一样，只是改变了数据源而已。

看一下效果：

![](https://user-gold-cdn.xitu.io/2017/11/19/15fd454864a1bfb7?w=466&h=538&f=gif&s=1318392)

好的，目的达到了。你可以在这里查看变更

<https://github.com/ditclear/MVVM-Android/commit/efd77a850069803d53b5128e62f9ff4f259641fa>

#### 优化

为了更有说服力，我优化了一下UI，并加入loading的效果。
![](https://user-gold-cdn.xitu.io/2017/11/19/15fd4548627bbb9e?w=466&h=678&f=gif&s=6888656)



还算有点模样，那么现在就到了本篇的重点了，**怎么像这样处理返回的网络数据？**。

再来看看xml布局文件，由于篇幅原因，所以这里只截取主要部分，详细请查看[pao_activity.xml](https://github.com/ditclear/MVVM-Android/blob/f4adf1d534b92e2596335f5461039cf8f09b858f/app/src/main/res/layout/pao_activity.xml)

```xml
<!--省略-->
                <us.feras.mdv.MarkdownView
                        android:id="@+id/web_view"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        app:markdown="@{vm.content}"
                        android:visibility="@{vm.loading?View.GONE:View.VISIBLE}"/>
<!--省略-->
```

这里使用到了一个第三方库[MarkdownView](https://github.com/falnatsheh/MarkdownView)，使用方法是这样的

> markdownView.loadMarkdown("## Hello Markdown");

并没有提供`setMarkDown(markdown:String)`方法，相信这种情况很常见，经常需要我们改动第三方库达到项目的需求。所以这里需要自定义一下`BindingAdapter`，具体见[NormalBinds.kt](https://github.com/ditclear/MVVM-Android/blob/f4adf1d534b92e2596335f5461039cf8f09b858f/app/src/main/java/io/ditclear/app/helper/NormalBinds.kt)

```kotlin
@BindingAdapter(value = "markdown")
fun bindMarkDown(v: MarkdownView, markdown: String?) {
    markdown?.let {
        v.setMarkdown(it)
    }
}
```

眼尖的同学就发现了，这不是有`setMarkdown`方法吗？

别急，其实这只是使用`kotlin`给`MarkdownView`添加的扩展函数，具体见[NormalExtens.kt](https://github.com/ditclear/MVVM-Android/blob/f4adf1d534b92e2596335f5461039cf8f09b858f/app/src/main/java/io/ditclear/app/helper/NormalExtens.kt)

```
fun MarkdownView.setMarkdown(markdown : String?){
    loadMarkdown(markdown)
}
```

再来瞧瞧我们的[PaoActivity.kt](https://github.com/ditclear/MVVM-Android/blob/f4adf1d534b92e2596335f5461039cf8f09b858f/app/src/main/java/io/ditclear/app/viewmodel/PaoViewModel.kt)，依然捡重点

```kotlin
override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menu?.let {
        menuInflater.inflate(R.menu.detail_menu,it)
    }
    return super.onCreateOptionsMenu(menu)
}

override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    item?.let {
        when(it.itemId){
            R.id.action_refresh -> mViewMode.loadArticle()
        }
    }
    return super.onOptionsItemSelected(item)
}
```

啊？这叫重点吗？不就是操作一下menu菜单吗？

嗯，不错，很平常的操作，关键在于`R.id.action_refresh -> mViewMode.loadArticle()`这里。

由于许多无法预料的原因，不可避免的我们无法在xml文件中去绑定数据和事件，需要在Activity/Fragment调用viewmodel里的方法。为什么要提这一点呢？当然是后面需要用到。

再来瞧瞧我们的`PaoViewModel.kt`

```kotlin
class PaoViewModel(val remote: PaoService) {
    //////////////////data//////////////
    val loading=ObservableBoolean(false)//加载
    val content = ObservableField<String>()//内容
    val title = ObservableField<String>()//标题
    //////////////////binding//////////////
    fun loadArticle() {
        //为了简单起见这里先写个默认的id
        remote.getArticleDetail(8773)
                .subscribeOn(Schedulers.io())
                .delay(1000,TimeUnit.MILLISECONDS)//为了加载效果更明显，这里延迟1秒
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loading.set(true) }//开始请求数据，设置加载为true
                .doAfterTerminate { loading.set(false) }//请求完成，设置加载为false
                .subscribe({ t: Article? ->
                    t?.let {
                        title.set(it.title)
                        it.content?.let {
                            val articleContent=Utils.processImgSrc(it)
                            content.set(articleContent)
                        }
                    }
                }, { t: Throwable? ->  })
    }
}
```

这里一个重点：`doOnSubscribe`和`doAfterTerminate`

`doOnSubscribe`是在订阅开始时会触发的方法，可以代替onStart()

而`doAfterTerminate`是在Single成功或者失败之后会触发的方法，可以代替onComplete()

我们再来优化一下`loadArticle`方法

1. **使用kotlin的扩展将异步操作组合起来**

这里我们定义一个Rxjava的扩展函数

```kotlin
fun <T> Single<T>.async(withDelay: Long = 0): Single<T> =
        this.subscribeOn(Schedulers.io())
                .delay(withDelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
```

所以可以将其转换成这样

```kotlin
remote.getArticleDetail(8773)
                .async(1000)
                .doOnSubscribe { loading.set(true) }
                .doAfterTerminate { loading.set(false) }
                .subscribe(...)
```

2 . **不依赖于具体实现**

loading.set(true) 和 loading.set(false) 现在就能达到我们想要的效果

但是如果万一我们需要使用另一种加载方式，那么就需要去改这里，一个方法还好，如果多个方法都这么写，就比较麻烦了。所以最好定义两个方法`startLoad()`和`stopLoad()`，代表开始加载和结束加载。

```kotlin
fun loadArticle() {
        //为了简单起见这里先写个默认的id
        remote.getArticleDetail(8773)
                .async(1000)
                .doOnSubscribe { startLoad()}
                .doAfterTerminate { stopLoad() }
                .subscribe({ t: Article? ->
                    t?.let {
                        title.set(it.title)
                        it.content?.let {
                            val articleContent=Utils.processImgSrc(it)
                            content.set(articleContent)
                        }

                    }

                }, { t: Throwable? ->
                })
    }
//...
fun startLoad()=loading.set(true)
fun stopLoad()=loading.set(false)
```

嗯好的，现在看着顺眼些了，那么还有一个问题，如果出现error了怎么处理，好像还没处理到，假设这里有一个需求是：当加载失败的时候，使用Snackbar或者Toast、Diglog提示错误信息。

假设是Toast，那我们需要调用

> Toast.makeText(context,"error msg",Toast.LENGTH_SHORT).show()

这里需要上下文Context，错误的做法是

1. 将activity或者fragment的Context作为参数传进了，然后直接在ViewModel里使用。❌

   **因为ViewModel里不应该有任何上下文Context的引用（除了App的ApplicationContext），而应该尽量是纯Java/kotlin代码。一是为了单元测试的便捷性，二是为了防止内存泄漏。**

2. 使用一个回调到Activity或者Fragment中去处理 ❌

   **这也就是我在第一部分中提过的跑偏了，这不就又变为MVP了吗（ps:自己也在这条路上跑了好几步）**

3. 聪明点的做法是再自定义一个`@BindAdapter`，通过绑定使用View的context ✔️·

   ```
   @BindingAdapter(value = "toast")
   fun bindToast(v: View,msg:Throwable ?){
       msg?.let {
           Toast.makeText(v.context,it.message,Toast.LENGTH_SHORT).show()
       }
   }
   ```

   这种做法可行，但我个人来说不太喜欢，因为我比较喜欢下一种。

4. 充分利用RxJava ✔️·

   其实Rxjava和MVVM的思想上有一致的地方。

   - Observable.create/just/from...等操作符用于提供数据源，可以认为是MVVM的M层
   - Observable.map/flatMap/reduce...等操作符用于数据的转换，将其变为订阅者需要的数据，这不正是ViewModel的功能
   - Subscriber...就相当于View层去使用这些数据

   其实更像是MVP，因为在Subscriber中成功和失败。。。等等的回调，既然Subscriber中就有这些回调，那为什么不加以利用？

   ### 怎么充分利用RxJava

   只需要将`loadArticle`方法改造成为一个`Single`

   ```kotlin
   fun loadArticle():Single<Article> =
           remote.getArticleDetail(8773)
                   .async(1000)
                   .doOnSuccess { t: Article? ->
                       t?.let {
                           title.set(it.title)
                           it.content?.let {
                               val articleContent=Utils.processImgSrc(it)
                               content.set(articleContent)
                           }

                       }
                   }
                   .doOnSubscribe { startLoad()}
                   .doAfterTerminate { stopLoad() }
   ```

   在`doOnSuccess`操作符中我们对数据进行了处理，然后在acticity中需要更改一下调用时的写法。

   ```kotlin
   override fun onOptionsItemSelected(item: MenuItem?): Boolean {
       item?.let {
           when(it.itemId){
               R.id.action_refresh -> mViewMode.loadArticle()
                       .subscribe { _, error -> dispatchError(error) }
               else -> { }
           }
       }
       return super.onOptionsItemSelected(item)
   }

   //依旧不依赖于具体实现，可以是Toast/Dialog/Snackbar等等
   private fun dispatchError(error:Throwable?){
       error?.let {
           Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
       }
   }
   ```

   到此，在MVVM中怎么处理网络数据就基本告一段落。

   接下来

   ### 处理内存泄漏问题RxLifecycle

   不多说，可以使用`CompositeDisposable`将所有的订阅都统一解除，我习惯于使用[RxLifecycle](https://github.com/trello/RxLifecycle)，更加方便

   ```kotlin
   override fun onOptionsItemSelected(item: MenuItem?): Boolean {
           item?.let {
               when(it.itemId){
                   R.id.action_refresh -> mViewMode.loadArticle().compose(bindToLifecycle())
                           .subscribe { _, error -> dispatchError(error) }
                   else -> { }
               }
           }
           return super.onOptionsItemSelected(item)
       }
   ```

   使用compose操作符绑定一下就好了，这也是我更倾向于这样的写法的原因之一。

   ### 结尾

   本项目的github地址：<https://github.com/ditclear/MVVM-Android>

   更多的例子可以查看：<https://github.com/ditclear/PaoNet>

   这是使用Kotlin构建MVVM项目的第二部分，主要讲了怎么在MVVM中较好的处理从网络返回的数据和解决内存泄漏问题。

   其实回过头来看会发现，这样的方式基本告别了回调，写着都感觉好舒服，于是问自己为什么以前没想到呢?本来就该这样处理啊！至于原因的话，可能是现在Android项目中使用MVVM的例子太少，这样的方式在github上很少出现，导致自己没转过弯。所以写本文的目的之一是分享，二是希望android开发者不要盲目追从MVP，而遗忘了MVVM。

   第三篇我会在本项目的基础上进行数据持久化，即加入android架构组件的Room数据库，敬请期待。