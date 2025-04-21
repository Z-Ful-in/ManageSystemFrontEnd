# 使用JetpackCompose来搭建前端的大致流程
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



# 一些tips
## 页面的主要逻辑
在MainActivity新建了authViewModel和homeViewModel, 这两个viewmodel都有init函数， 
前者会在创建时同步isLoggedIn状态， 读取userDataStore: DataStore中的IS_USER_LOGGER_IN字段，
用于用于判断是否已经登录(每次登录时都会保存dataStore，如果上次已经登录， 就会保存preferences)。
后者的init会调用getUserName和getUserImages函数， 首先从dataStore中读取username, 再发送
Retrofit请求读取该用户的图片列表， 两个变量都会被存储到homeViewModel中

新建两个viewmodel后， 首先创建NavController, 再从authViewModel中读取isLoggedIn状态，随后调用
导航栏组件AppNavGraph， 传入NavController，并根据isLoggedIn决定startDestination, 同时把
homeViewModel和authViewModel传入AppNavGraph中， 以便在路由组件中使用

### AppNavGraph
如上面所说， 接受NavController, startDestination, 以及两个viewmodel参数， 函数内部实现了一个NavHost,
传入参数navController, startDestination, 在lambda表达式中将我们的路由组件传入NavHost, 
这里的路由组件有两个，composable("login") 和 composable("home")， 分别对应登录和主页的composable组件。

### LoginRegisterScreen
在AppNavGraph导航栏中， composable("login")对应的组件是LoginRegisterScreen, 
把authViewModel(用于登录),navController(用于路由跳转),以及homeViewModel(用于登录成功后传入读取用户数据回调函数)
传入。 这里没有将LoginScreen和RegisterScreen分别设置路由， 是因为在LoginRegisterScreen中使用isRegistered字段统一管理了两者的状态。
通过是在LoginRegisterScreen中还从authViewModel中读取了loginRegisterResult，用于将后端的登录结果展示到前端

在实现LoginRegisterScreen时，用了AuthContainer(String, @Composable ()-> Unit)组件， 在LoginScreen和RegisterScreen上方展示统一的Text文本，
并统一两者的布局(Scaffold{Column{}}, 因为刚开始没有使用该布局， 导致实现的LoginRegisterScreen不在屏幕正中央)， 槽位中分别放入LoginScreen和RegisterScreen

#### LoginScreen
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

#### RegisterScreen
RegisterScreen与LoginScreen大同小异， 多了一些username和password的校验。 onRegister注册成功后并不会路由， 而是将isRegistered状态置为true, 进入登陆页面重新登录

### ManagementAppPortrait
进入该页面都是由路由进入。 以前登陆过， 就在NavGraph处直接startDestination为home进入该页面， 以前未登陆过，就会通过login成功后navigate到该页面。

进入ManagementAppPortrait时， 遇到过一个主要的问题： 页面没有数据， 即没有username和userImages数据。根据上述进入home页面的方式， 主要是以下两种原因：
1. 之前登录过， 保存了username到dataStore中，但是homeViewScreen创建的时候调用init时，init内部没有调用getUserName和getUserImages分别从dataStore和服务器获取数据
2. 之前未登陆过， 通过login进入home页面， 主要原因是loadData作为一个异步请求， 如果loadData内部没有传入一个onComplete回调函数， 会导致loadData没结束， 就navigate到home了，导致没有数据

ManagementAppPortrait是一个Scaffold布局， 在bottomBar中有一个BottomNavigation组件， 然后页面上是一个HomeScreen组件

#### BottomNavigation
BottomNavigation组件是一个底部导航栏， 传入了一个homeViewModel参数。

该Composable内部是一个NavigationBar组件，放了两个NavigationBarItem。 lambda表达式里放的是， icon = Icon组件， label = Text组件， selected 由 homeViewModel的isPageSelected判断， 0是View页面， 1是Upload页面， onClick调用homeViewModel的OnViewClick或onUploadClick函数修改homeViewModel的isPageSelected的值

#### HomeScreen
HomeScreen中传入了homeViewModel和NavController参数. 
Surface组件中放了一个Column组件，最上方是一个SpacedBetween的Row组件， 左侧是一个Text, 右侧是一个IconButton，onclick触发后会调用homeViewModel.logout, 
向后端发送logout请求 ， 并在后端返回成功后， preferencesManager.saveLoginStatus， 将该userName的isLoggedIn字段改为false。 在这些都完成后，将isPageSelected改为0，并回调navigateToLogin, 路由到登录页面
下面的内容由homeViewModel的isPageSelected决定， 如果是0就放入ViewScreen组件， 如果是1就放入UploadScreen组件， 两者都传入homeViewModel参数

#####  ViewScreen
ViewScreen接受homeViewScreen参数， 该组件从homeViewScreen持有userImageList状态， 并通过currentPage, rawValue状态来控制页面的跳转， 通过totalImages字段判断是否由图片

该组件是一个Box布局， 在totalImages>0时， 内部为一个Column， 该Column主要由四部分组成

###### ImageWithText组件
该组件接受一个ImageItem, 是data class UserImage对象，内部有id, url, description字段，其中url是图片的服务器地址如http://10.0.0.2:8000/images/test.jpg。
该组件是一个Surface布局，内部是一个Column组件，通过coil依赖从服务器上获取图片， 放着一个AsyncImage组件， model字段中的url为ImageItem的fullUrl()，效果是将url与服务器的baseUrl拼接成完整的url，
contentScale设定为ContentScale.Crop, 这样就可以实现图片的缩放， 大小设定为280.dp, 4:3， 下方是一个Text组件， 放着图片的description

###### Row组件
该row内部由三部分组成， 左箭头的IconButton， 显示当前页面和总页面的Text， 右箭头的IconButton, onClick后会改变currentPage的值触发重组

###### Row组件
该row内部主要是由两部分组成， OutlinedTextField， 用于输入页面跳转， onValueChange会修改rawValue的值， 在右侧有一个Button， 该Button触发onClick后会将currentPage的值修改为rawValue的值触发重组

###### DeleteImageButton组件
该组件接受一个onConfirmDelete回调函数， 持有一个showDialog状态， 用于控制是否展示删除图片的对话框。 该组件主要就是一个Button， onClick后会将showDialog置为true， 触发AlertDialog组件，
该组件传入onDismissRequest回调函数，将showDialog置为false。
传入confirmButton和dismissButton都是TextButton组件，前者onClick后触发onConfirmDelete回调函数， 该函数会调用homeViewModel.deleteImage函数， 该函数会向后端发送删除图片的请求，并有一个回调函数，调用getImageLists()修改当前imageList。 删除成功后会将showDialog置为false， 触发重组。 
后者onClick后会将showDialog置为false， 触发重组

##### UploadScreen
同样接收一个homeViewModel参数， 持有一个selectImageUri状态， 一个description状态， 一个dialogState状态， 以及一个launcher用于选择图片， 回调函数将selectImageUri的值修改为选择的uri

该组件是一个Column布局， 内部由Box组件(选择图片)，OutlinedTextField(输入图片描述)，Button(上传图片)， 以及一个Dialog(上传成功/失败提示)组成

###### 选择图片
Box组件设置modifier.clickable 点击Box后触发事件 ```launcher.launch("image/*")```, 该事件会弹出一个选择图片的对话框， 选择图片后会将selectImageUri的值修改为选择的uri, 触发重组。

当选择到图片后，uri不为null，所以会显示一个Image组件放置选中的图片， painter字段为rememberAsyncImagePainter(selectImageUri)。

如果没有选择图片，即uri为null， 就会放一个Icon，是一个加号， 暗示用户选择图片上传

###### 输入图片描述
没什么好说的

###### 上传图片
作为一个Button， 如果uri不为空，description也不为空， 就触发homeViewModel.uploadImage函数，传入参数selectedImageUri, description, username, onUploadSuccess, onUploadFailed。
uploadSuccess和onUploadFailed都是回调函数， 前者会将dialogState置为UploadDialogState.UploadSuccess("图片上传成功")， 触发重组， 显示上传成功的对话框， 后者会将dialogState置为UploadDialogState.NetworkError("网络连接失败， 请检查你的网络设置")， 触发重组， 显示上传失败的对话框。

关于homeViewModel.uploadImage， 会开一个inputStream来读取uri的内容， 再通过File.createTempFile()方法创建一个临时文件， 再开一个outputStream(file)， 通过调用inputStream?.copyTo(outputStream)的方式将内容写入临时文件。
再通过
``` val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())```
的方式将临时文件转换为RequestBody， 然后通过MultipartBody.Part.createFormData("image", file.name, requestFile)方法将临时文件转换为MultipartBody.Part， 该方法的第一个参数是表单的key， 第二个参数是文件名， 第三个参数是RequestBody， 这就是imagePart的获取方式

对于descriptionPart, 直接description.toRequestBody("text/plain".toMediaTypeOrNull())即可. username同理。 最后调用ApiService: uploadImage把图片上传到服务器， 并指定url

```kotlin
interface ApiService{
    @Multipart
    @POST("/images/upload_image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("userName") userName: RequestBody
    ): Boolean
}
```

对于没有选Image或者没有输入description的情况， 点击后直接触发dialogState = UploadDialogState.FieldMissingError("请确保选择了图片并填写了描述")

###### Dialog

这里我们说一下DialogState：
```kotlin
sealed class UploadDialogState {
    object None: UploadDialogState()
    data class UploadSuccess(val message: String): UploadDialogState()
    data class NetworkError(val message: String): UploadDialogState()
    data class FieldMissingError(val message: String): UploadDialogState()
}
```
通过这种方法来管理不同场景下的DialogState， 在最后展示的时候，单独创建一个UploadDialog组件， 传入message和清空dialogState的回调函数， 该函数会将dialogState置为UploadDialogState.None, 触发重组, 最后只需要一个判断： dialogState不为None时，展示此Dialog组件即可