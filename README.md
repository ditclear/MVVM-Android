![](http://upload-images.jianshu.io/upload_images/3722695-8187b588f67e9105.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 目录

- [使用Kotlin构建MVVM应用程序—总览篇](https://www.jianshu.com/p/77e42aebd7bb)
- [使用Kotlin构建MVVM应用程序—第一部分：入门篇](https://www.jianshu.com/p/80926d9e64f7)
- [使用Kotlin构建MVVM应用程序—第二部分：Retrofit及RxJava](https://www.jianshu.com/p/8993b247947a)
- [使用Kotlin构建MVVM应用程序—第三部分：Room](https://www.jianshu.com/p/264d7d0608f0)
- [使用Kotlin构建MVVM应用程序—第四部分：依赖注入Dagger2](https://www.jianshu.com/p/da77266970d8)

### 写在前面

这里是使用Kotlin构建MVVM应用程序—第四部分：依赖注入Dagger2
在前面的一系列文章中，我们了解了在MVVM架构中是如何提供和处理数据的。

```kotlin
//////model
val remote=Retrofit.Builder()
        .baseUrl(Constants.HOST_API)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(PaoService::class.java)
val local=AppDatabase.getInstance(applicationContext).paoDao()
val repo = PaoRepo(remote, local)
```

为了得到给ViewModel层提供数据的仓库repo，我们需要有`remote`(由Retrofit提供来自服务器的数据)和`local`(由Room提供来自本地的数据)。

由于一个应用程序必定有多个不同的`viewmodel`，所以就必须为其提供多个`repo`，那就需要提供多个`remote`和`local`。而麻烦的便是提供`remote`和`local`的写法都差不了多少，但你却又不得不写。

真正的开发者都不会想做没有效率的事情。

因此，省时省力的依赖注入思想就得到了很多开发者的推崇，在android开发中，那当然就是Dagger2了。

### 什么是dagger2?

> A fast dependency injector for Android and Java.

一个适用于Android和Java的快速的依赖注入工具。

##### 那什么又是依赖注入呢？

我们可以先来看一个例子：我们在写面向对象程序时，往往会用到组合，即在一个类中引用另一个类，从而可以调用引用的类的方法完成某些功能,就像下面这样：

```java
public class ClassA {
    ...
    ClassB b;
    ...
    public ClassA() {
        b = new ClassB();
    }
  	public void do() {
       ...
       b.doSomething();
       ...
    }
}
```

这个时候就产生了依赖问题，ClassA依赖于ClassB，必须借助ClassB的方法，才能完成一些功能。这样看好像并没有什么问题，但是我们在ClassA的构造方法里面直接创建了ClassB的实例，问题就出现在这，在ClassA里直接创建ClassB实例，违背了**单一职责原则**，ClassB实例的创建不应由ClassA来完成；其次耦合度增加，扩展性差，如果我们想在实例化ClassB的时候传入参数，那么不得不改动ClassA的构造方法，不符合**开闭原则**。

> 注：
>
> 单一职责原则：一个类，只有一个引起它变化的原因。应该只有一个职责。每一个职责都是变化的一个轴线，如果一个类有一个以上的职责，这些职责就耦合在了一起。这会导致脆弱的设计。当一个职责发生变化时，可能会影响其它的职责。另外，多个职责耦合在一起，会影响复用性
>
> 开闭原则：一个软件实体如类，模块和函数应该对扩展开放，对修改关闭。

因此我们需要一种注入方式，将依赖注入到宿主类（或者叫目标类）中，从而解决上面所述的问题。

### 引入Dagger2

```groovy
// Add Dagger dependencies  当前版本2.16
apply plugin: 'kotlin-kapt'
dependencies {
  implementation 'com.google.dagger:dagger:2.16'
  kapt  'com.google.dagger:dagger-compiler:2.16'
}
```

> 注：
>
> kapt 即 [Kotlin Annotation Processing](https://link.jianshu.com/?t=https%3A%2F%2Fblog.jetbrains.com%2Fkotlin%2F2015%2F05%2Fkapt-annotation-processing-for-kotlin%2F)，就是服务于Kotlin的注解处理器。可在编译时期获取相关注解数据，然后动态生成.java源文件（让机器帮我们写代码），通常是自动产生一些有规律性的重复代码，解决了手工编写重复代码的问题，大大提升编码效率。

Dagger2便借助了注解处理器生成了许多必须的代码。经过编译之后，这些代码可以在

***app/build/generated/source/kapt/..***目录下找到

刚接触Dagger2的人不明白为什么使用了几个注解之后就不需要再一直new Class了，只是觉得太酷了，Magic。

这样便很容易遇到瓶颈，遇到需要稍加变通的情况，便会手足无措，只好放弃。

这也是许多开发者从入门到放弃的原因—**知其然而不知其所以然**。

Dagger2并没有那么神秘，在我们平常开发看不见的角落（build文件夹），它做了许多额外的工作。当你一不留神注意到那个角落的时候，就会恍然大悟。

### 如何依赖注入？

在具体使用Dagger2之前，我们先来思考一下如何将

```kotlin
//////model
val remote=Retrofit.Builder()
        .baseUrl(Constants.HOST_API)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(PaoService::class.java)
val local=AppDatabase.getInstance(applicationContext).paoDao()
val repo = PaoRepo(remote, local)
/////viewmodel
mViewModel=PaoViewModel(repo)
```

这些东西提取出来然后进行统一注入?

假设这些依赖都存在于一个类中，我们把它记为Module:

```kotlin
//提供依赖
class Module constructor(val applicationContext:Context){
    //////model
    val remote=Retrofit.Builder()
        .baseUrl(Constants.HOST_API)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(PaoService::class.java)
	val local=AppDatabase.getInstance(applicationContext).paoDao()
	val repo = PaoRepo(remote, local)
  	/////viewmodel
  	val viewmodel=PaoViewModel(repo)
}
```

现在我们需要把`Provider`中的`viewmodel`赋值给`PaoActivity`中的`mViewModel`

这里我们为Provider和Activity搭建一座桥梁`Component`，并提供一个`inject`方法用于注入这些依赖

```kotlin
//注入依赖
class Component constructor(val module:Module){
  //注入
  fun inject(activity:PaoActivity){
      activity.mViewModel=module.viewmodel
  }
}
```

最后在View层只需要调用一下`inject`方法便可以进行注入

```kotlin
class PaoActivity : RxAppCompatActivity() {

    lateinit var mViewModel : PaoViewModel

 	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      	//依赖
      	val module=Module(applicationContext)
      	//注入
      	Component(module).inject(this)
    }
}
```

Dagger2便是应用的这一套逻辑，不过Dagger2在通过`annotationProcessor`在编译时期对注解进行了处理，自动生成了上面描述的代码逻辑。

### 应用Dagger2

首先我们需要了解Dagger2里的几个注解

1. @Inject

   它用于标识哪些应该被注入，被标识的可以是`public`属性或者`constructor`构造函数

2. @Component

   这里用于标识依赖和待注入对象之间的桥梁

3. @Module

   带有此注解的类，用来提供依赖，里面定义一些用@Provides注解的以provide开头的方法，这些方法就是所提供的依赖，Dagger2会在该类中寻找实例化某个类所需要的依赖

Dagger2通过处理这几个注解之后，便会自动生成我们需要的前文中的代码。

而开发者需要做的不过是根据实际的需要合理运用这几种注解即可。

首先，我们新建我们需要的文件

![di](https://upload-images.jianshu.io/upload_images/3722695-c172003f03b9e0cd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)

AppModule.kt:

```kotlin
@Module
class AppModule(val applicationContext: Context){

    //提供 Retrofit 实例
    @Provides @Singleton
    fun provideRemoteClient(): Retrofit = Retrofit.Builder()
            .baseUrl(Constants.HOST_API)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    //提供 PaoService 实例
    @Provides @Singleton
    fun providePaoService(client:Retrofit) =client.create(PaoService::class.java)

    //提供 数据库 实例
    @Provides @Singleton
    fun provideAppDataBase():AppDatabase = AppDatabase.getInstance(applicationContext)

    //提供PaoDao 实例
    @Provides @Singleton
    fun providePaoDao(dataBase:AppDatabase)=dataBase.paoDao()
}
```



AppComponent.kt:

```kotlin
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{

    fun inject(activity: PaoActivity)
}
```

> modules 表明了哪些依赖可以被提供。

我们我需要使用@Inject标识哪些需要被注入

PaoActivity.kt

```kotlin
class PaoActivity : RxAppCompatActivity() {
  	//标识mViewModel需要被注入
    @Inject
    lateinit var mViewModel : PaoViewModel

 	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      	//di
      	getComponent().inject(this)
    }


    fun getComponent()=DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext)).build()
}
```

PaoViewModel.kt

```kotlin
//@Inject 可用于构造函数，表示构造函数中的参数是被自动注入的
class PaoViewModel @Inject constructor(private val repo: PaoRepo)class PaoViewModel @Inject constructor(private val repo: PaoRepo)
```

PaoRepo.kt

```kotlin
//@Inject 可用于构造函数，表示构造函数中的参数是被自动注入的
class PaoRepo @Inject constructor(private val remote:PaoService, private val local :PaoDao)
```

整个过程可以简单看作是一个交易

Component就类似于商店一样，AppModule是供应商，提供各种商品给商店，PaoActivity可以看作顾客

> AppModule：我把货都给你了
>
> AppComponent：好嘞，收到
>
> 过了一会儿，PaoActivity来采购了
>
> PaoActivity：我需要一个PaoViewModel，有卖的没？
>
> AppComponent：稍等 ，我帮您看看
>
> PaoActivity：嗯哼
>
> AppComponent：一个PaoViewModel需要一个PaoRepo，一个PaoRepo需要有PaoService和PaoDao。嗯，都有，可以成交
>
> 然后便愉快的完成了这单交易

编译之后，通过处理注解会生成以下文件

![build](https://upload-images.jianshu.io/upload_images/3722695-ee625b51a4dacf6b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)





附上编译生成的`DaggerAppComponent.kt`文件

```java
public final class DaggerAppComponent implements AppComponent {
  private Provider<Retrofit> provideRemoteClientProvider;

  private Provider<PaoService> providePaoServiceProvider;

  private Provider<AppDatabase> provideAppDataBaseProvider;

  private Provider<PaoDao> providePaoDaoProvider;

  private DaggerAppComponent(Builder builder) {
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  private PaoRepo getPaoRepo() {
    return new PaoRepo(providePaoServiceProvider.get(), providePaoDaoProvider.get());
  }

  private PaoViewModel getPaoViewModel() {
    return new PaoViewModel(getPaoRepo());
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {
    this.provideRemoteClientProvider =
        DoubleCheck.provider(AppModule_ProvideRemoteClientFactory.create(builder.appModule));
    this.providePaoServiceProvider =
        DoubleCheck.provider(
            AppModule_ProvidePaoServiceFactory.create(
                builder.appModule, provideRemoteClientProvider));
    this.provideAppDataBaseProvider =
        DoubleCheck.provider(AppModule_ProvideAppDataBaseFactory.create(builder.appModule));
    this.providePaoDaoProvider =
        DoubleCheck.provider(
            AppModule_ProvidePaoDaoFactory.create(builder.appModule, provideAppDataBaseProvider));
  }

  @Override
  public void inject(PaoActivity activity) {
    injectPaoActivity(activity);
  }

  private PaoActivity injectPaoActivity(PaoActivity instance) {
    PaoActivity_MembersInjector.injectMViewModel(instance, getPaoViewModel());
    return instance;
  }

  public static final class Builder {
    private AppModule appModule;

    private Builder() {}

    public AppComponent build() {
      if (appModule == null) {
        throw new IllegalStateException(AppModule.class.getCanonicalName() + " must be set");
      }
      return new DaggerAppComponent(this);
    }

    public Builder appModule(AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }
  }
}
```

到此，本篇的文章就结束了。

本项目的github地址：[https://github.com/ditclear/MVVM-Android](https://link.jianshu.com/?t=https://github.com/ditclear/MVVM-Android)

更多的例子可以查看：[https://github.com/ditclear/PaoNet](https://link.jianshu.com/?t=https://github.com/ditclear/PaoNet)

### 写在最后

其实Dagger2理解起来并不难，只要去看看生成的文件，便很容易明白。但是很多android开发者都不喜欢问为什么，更不喜欢探究为什么，不看源码，只懂使用，导致在技术上止步不前，才有那么多次的从入门到放弃。

多看书，多写代码，多读源码，路才能走宽。

#### 参考资料

- [Dagger2从入门到放弃再到恍然大悟](https://www.jianshu.com/p/39d1df6c877d)
- [google dagger2](https://github.com/google/dagger)