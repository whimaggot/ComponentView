### 介绍
ComponentView主要是用于保持业务界面的干净，减少重复代码，提高可阅读性。将琐碎的业务代码进行抽离， 再通过注解的方式重新注入到对应的业务类中，在指定的Activity或Fragment的生命周期中按照自己需要的情况响应执行。

### 使用介绍
在介绍之前，让我们先来感受一个场景：
下面是一个很简单很干净的单聊界面，正常来说没有琐碎的业务我们的代码可以写得很干净很优雅
![1625122418357.jpg](http://pfp.ps.netease.com/kmpvt/file/60dd66918c56749c48c1074cxUKV16u301?sign=WyFpEJD-xppse7DB6j1_ia5COl4=&expire=1630910088)
但是假如这时候麻烦的需求来了，策划灵机一动，说我想让当聊天的对方是自己的好友时，根据自己跟他已经多久没有聊天了，在表情icon上面弹出一个“你跟xx已经yy天没有聊天了，快给他发个表情吧！”的气泡。然后我们只能为了这个业务在聊天类里写一堆后来人看到就脑阔疼的代码。即使我们将这坨东西抽离到单独的一个类里，我们也需要在聊天类的某一个或几个地方调用这个地方，依然侵入了聊天类的代码。
下面我们再来看看怎么用ComponentView实现这个业务：
首先先贴出基类IComponentView的核心代码并介绍：
```
abstract class IComponentView<T : View,D:Any> {
    companion object {
        const val DEFAULT_PRIORITY = 0
    }
    abstract fun viewId(): Int
    abstract fun period(): ExecutePeriod
    abstract fun periodGap(): Int
    abstract fun condition(): ExecuteCondition
    open fun time(): Int {
        return AndroidLifeCycle.RESUME.value
    }


    open fun priority(): Int {
        return DEFAULT_PRIORITY
    }
    
    abstract fun execute(
        activityWrapper: ActivityWrapper,
        androidLifeCycle: AndroidLifeCycle,
        target: T?,
        data: D?
    )

    abstract fun release()

}
```

| 方法名 | 含义 | 备注 |
| --- | --- | --- |
| viewId | 需要处理的View的资源id | 在本案例中我们需要用到表情icon，所以返回表情icon即可，同时在实现的时候，指定子类的实现类型T为ImageView或ImageView父类 |
| period |执行周期  | 本案例中我们每次进入聊天页都要展示，因此不需要限制周期，传NONE即可 |
| periodGap | 执行周期间隔 | 很好理解，比如执行周期选择了秒，这里返回了10，就是指在10秒不会再次执行 |
| condition | 执行条件 | 默认为无条件，目前支持SharedPreferences记录是否已执行，已执行则不会再次执行 |
| time | 执行时间 | 在指定的Activity/Fragment的生命周期内执行，默认为onResume |
| priority | 优先级 | 返回int值，int值越大，优先度越高，优先度会覆盖其他所有执行限制 |
| execute | 执行回调 | 在该方法中处理你的业务逻辑 |
| release | 释放资源 | 记得在这个方法中移除一些异步任务 |

接下来让我们用伪代码处理这个需求：
以下为我们实现的ComponentView：
```
class P2PChatFriendTipsComponentVIew : IComponentView<ImageView, String>() {
    override fun execute(
        activityWrapper: ActivityWrapper,
        androidLifeCycle: AndroidLifeCycle,
        target: ImageView?,
        data: String?
    ) {
        target?.let { 
            if(isMyFirend(data)){
                getLastChatTime(data)?.let{
                    showFriendChatTips(target,it)
                }
            }
        }
    }
    
    fun getLastChatTime(string:String):Int?{
        //ToDo 实现获取时间
    }
    
    fun stopGetLastChatTime(string:String){
        //ToDo 实现停止获取时间异步任务
    }
    
   fun isMyFriend(string:String){
        //ToDo 实现判断是否自己好友
   }
   fun showFriendChatTips(view:ImageView,time:Int){
         //ToDo 实现展示气泡
   }

    override fun period(): ExecutePeriod {
        return ExecutePeriod.NONE
    }

    override fun periodGap(): Int {
        return 0
    }

    override fun condition(): ExecuteCondition {
        return ExecuteCondition.ExecuteConditionNone()
    }

    override fun viewId(): Int {
        return R.id.iv_sticker
    }

    override fun release() {
        stopGetLastChatTime()
    }

}
```
实现了业务逻辑后，再注入到聊天类中，以下为聊天类伪代码：
```
@ComponentView(P2PChatFriendTipsComponentVIew::class)
class P2PMessageActivity: AppCompatActivity() {
    
    //...
    
    //案例中我们需要获取当前聊天页的会话id来判断是否自己的好友，因此用ComponentViewData注解标记这个变量，传递给ComponentView
    @ComponentViewData(P2PChatFriendTipsComponentVIew::class)
    var sessionId:String ="somebody"
    
    //...
}
```
大功告成！

### 其他功能解释

* 优先级。当一个页面中出现多个ComponentView且互相之间的优先级不同时，会先执行最高优先级的ComponentView，优先级高的处理完成后需调用 `IComponentView#finish()`方法才会继续执行较低优先级的，相同优先级则会同时执行。
* 主动调用。调用`ComponentViewManager#callComponentView`会触发页面执行对应的Componentview，若页面不存在对应的ComponentView则不会执行，此时的执行会忽略执行时间，但是执行周期及执行条件的限制依然有效。
* 传递数据。使用`ComponentViewData·注解可以实现从页面传递数据到ComponentView，对页面中的变量使用ComponentViewData注解，并指定这个注解对应的ComponentView类名即可。一个ComponentView只能传递一个被ComponentViewData注解的变量。

