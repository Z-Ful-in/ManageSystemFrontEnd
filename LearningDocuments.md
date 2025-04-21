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
   

遵从上述思路， 写出homeScreen倒不是难事



## 一些tips
### 页面的主要逻辑
在MainActivity新建了authViewModel和homeViewModel, 这两个viewmodel都有init函数， 
前者会在创建时同步isLoggedIn状态， 读取userDataStore: DataStore中的IS_USER_LOGGER_IN字段，
用于用于判断是否已经登录(每次登录时都会保存dataStore，如果上次已经登录， 就会保存preferences)。
后者的init会调用getUserName和getUserImages函数， 首先从dataStore中读取username, 再发送
Retrofit请求读取该用户的图片列表， 两个变量都会被存储到homeViewModel中

新建两个viewmodel后， 首先创建NavController, 再从authViewModel中读取isLoggedIn状态，随后调用
导航栏组件AppNavGraph， 传入NavController，并根据isLoggedIn决定startDestination, 同时把
homeViewModel和authViewModel传入AppNavGraph中， 以便在路由组件中使用

#### AppNavGraph
如上面所说， 接受NavController, startDestination, 以及两个viewmodel参数， 函数内部实现了一个NavHost,
传入参数navController, startDestination, 在lambda表达式中将我们的路由组件传入NavHost, 
这里的路由组件有两个，composable("login") 和 composable("home")， 分别对应登录和主页的composable组件。

#### LoginRegisterScreen
在AppNavGraph导航栏中， composable("login")对应的组件是LoginRegisterScreen, 
把authViewModel(用于登录),navController(用于路由跳转),以及homeViewModel(用于登录成功后传入读取用户数据回调函数)
传入。 这里没有将LoginScreen和RegisterScreen分别设置路由， 是因为在LoginRegisterScreen中使用isRegistered字段统一管理了两者的状态。
通过是在LoginRegisterScreen中还从authViewModel中读取了loginRegisterResult，用于将后端的登录结果展示到前端

在实现LoginRegisterScreen时，用了AuthContainer(String, @Composable ()-> Unit)组件， 在LoginScreen和RegisterScreen上方展示统一的Text文本，
并统一两者的布局(Scaffold{Column{}}, 因为刚开始没有使用该布局， 导致实现的LoginRegisterScreen不在屏幕正中央)， 槽位中分别放入LoginScreen和RegisterScreen

##### LoginScreen
接受了五个参数： OnSwitchToRegister, OnLogin, authViewModel, usernameError, passwordError
1. OnSwitchToRegister: 回调函数， 用于点击后切换到注册页面， 再次基础上传入了一个authViewModel.clearResult()函数， 用于切换后刷新登录状态(loginRegisterResult字段置为LoginRegisterResult.None)
2. OnLogin: 回调函数， 用于点击后登录， 传入了authViewModel.login(username, password, navigateToHome)函数， 用于发送登录请求。并在登录成功后调用navigateToHome跳转到主页， navigateToHome内部调用了homeViewModel.loadData(onComplete(()->Unit)?=null)先读取用户信息, 完毕后调用onComplete跳转到主页， 这样可以防止数据加载前就进入了主页
3. authViewModel: 用于发送在点击修改TextField时清空登录状态
4. usernameError: 用于传入后端返回的错误信息，这里主要是用户名或密码错误
5. passwordError: 用于传入后端返回的错误信息，这里主要是用户名或密码错误

该组件内部持有isPasswordVisible状态，usernameErrorState和passwordErrorState状态， 后面两个是前端用来控制管理TextField的状态的参数，
前者是用于控制密码是否可见， 通过TextField中的visualTransformation字段控制：
```kotlin
 visualTransformation = if (isPasswordVisible) {
    VisualTransformation.None
} else {
    PasswordVisualTransformation()
}
```
而文本框最后的眼睛图标由trailingIcon控制， 内部传入一个IconButton组件，onClick后改变isPasswordVisible状态, 并传入一个Icon。

对于切换到Register页面， 只需点击按钮， 调用OnSwitchToRegister函数即可

##### RegisterScreen
RegisterScreen与LoginScreen大同小异， 多了一些username和password的校验。 onRegister注册成功后并不会路由， 而是将isRegistered状态置为true, 进入登陆页面重新登录

#### ManagementAppPortrait
进入该页面都是由路由进入。 以前登陆过， 就在NavGraph处直接startDestination为home进入该页面， 以前未登陆过，就会通过login成功后navigate到该页面。

进入ManagementAppPortrait时， 遇到过一个主要的问题： 页面没有数据， 即没有username和userImages数据。根据上述进入home页面的方式， 主要是以下两种原因：
1. 之前登录过， 保存了username到dataStore中，但是homeViewScreen创建的时候调用init时，init内部没有调用getUserName和getUserImages分别从dataStore和服务器获取数据
2. 之前未登陆过， 通过login进入home页面， 主要原因是loadData作为一个异步请求， 如果loadData内部没有传入一个onComplete回调函数， 会导致loadData没结束， 就navigate到home了，导致没有数据

ManagementAppPortrait是一个Scaffold布局， 在bottomBar中有一个BottomNavigation组件， 然后页面上是一个HomeScreen组件

##### BottomNavigation
BottomNavigation组件是一个底部导航栏， 传入了一个homeViewModel参数。

该Composable内部是一个NavigationBar组件，放了两个NavigationBarItem