## 使用JetpackCompose来搭建前端的大致流程
1. 从基本的Composable组件开始， 即登录，注册页面等
    这里要关注常见的组件， TextField， Button， Text等, 其中Button里的回调函数是我们前端主要的逻辑， 一般都是放到参数中，由上游Composable组件传入， 从而修改上游的状态
2. 使用ViewModel来管理状态
    这里的viewmodel类承载了前面说的回调函数， 可以通过方法向retrofit发送网络请求， 这些方法往往作为button的回调函数， 同时viewmodel也存储了一些数据， 这些数据不会随着页面的刷新而消失， 例如登录成功后， 需要将token存储在viewmodel中， 以便后续的网络请求使用
3. Retrofit网络请求
    主要流程是：
   1. 创建User数据类， 该类包含了登录所需的用户名和密码； 定义一个LoginResponse数据类， 该类包含登录成功后返回的token等信息
   2. 创建RetrofitService接口， 该接口包含了登录所需的网络请求方法， 通过@POST, @GET等注解来定义请求的类型和路径， 返回值是Response<LoginResponse>， 请求体一般是我们定义的User数据类
   3. 创建RetrofitClient单例类， 该类包含了Retrofit的初始化和网络请求的执行， 通过Retrofit.Builder()来创建Retrofit实例， 然后通过create()方法来创建RetrofitService接口的实例
   4. 最后在ViewModel中调用RetrofitClient的网络请求方法， 通过enqueue()方法来异步执行请求， 并在回调中处理响应结果。 简单情况下直接使用viewModelScope.launch来执行网络请求
4. 导航栏
    这里使用的是Navigation组件， 通过NavHost和NavController来实现页面的跳转， 通过NavController.navigate()方法来跳转到指定的页面， 主要就是三个部分
   1. 创建NavGraph， 该类内部使用NavHost组件， 用于定义路由， 传入一个NavHostController和一个startDestination
   2. 在被路由的组件的参数中传入NavController, 并在组件中使用NavController.navigate()方法来跳转到指定的页面， 该方法可以传入一个回调函数，用于处理跳转后的逻辑， 如popUpTo()方法来清除栈中的页面
5. DataStore
    有点像redis，但是是把临时的数据持久化到本地磁盘， 从而保持登录状态。 语法规则就是
    ```kotlin 
    val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
   val USERNAMEKEY = stringPreferencesKey("username")
   val getUsername = context.userDataStore.data.first[USERNAMEKEY] ?: ""
    ```