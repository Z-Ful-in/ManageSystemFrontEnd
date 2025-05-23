package com.example.trafficviolationpicturemanagementsystem.ui.home

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.trafficviolationpicturemanagementsystem.model.UserImage
import com.example.trafficviolationpicturemanagementsystem.ui.state.UploadDialogState
import com.example.trafficviolationpicturemanagementsystem.viewmodel.HomeViewModel

@Composable
fun ManagementAppPortrait(
    homeViewModel: HomeViewModel,
    navController: NavController
){
    Scaffold(
        bottomBar = {
            BottomNavigation(
                homeViewModel,
                modifier = Modifier.fillMaxWidth()
            )
        },
    ) { padding ->
        HomeScreen(
            homeViewModel,
            Modifier.padding(padding),
            navController
        )
    }
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier,
    navController: NavController
) {
    val userName by homeViewModel.userName.observeAsState("")
    val isPageSelected by homeViewModel.isPageSelected.observeAsState(0)


    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "欢迎 $userName",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                )
                IconButton(
                    onClick = {
                        homeViewModel.logout(
                            navigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                ) {
                    Icon(Icons.Default.Output, contentDescription = "Output")
                }
            }
            if (isPageSelected == 0) {
                ViewScreen(
                    homeViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
            else if (isPageSelected == 1) {
                UploadScreen(
                    homeViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun ViewScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier
) {
    val userImageList by homeViewModel.userImageList.observeAsState(emptyList())
    var currentPage by remember { mutableIntStateOf(1) }
    var rawValue by remember { mutableStateOf("1") }
    val totalImages = userImageList.size
    Box(
        modifier = modifier
    ) {
        if(totalImages > 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .weight(5f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageWithText(
                        imageItem = userImageList[currentPage - 1],
                        Modifier.fillMaxWidth()
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentPage = (currentPage - 1).coerceAtLeast(1) }) {
                            Icon(
                                Icons.Default.KeyboardDoubleArrowLeft,
                                contentDescription = "Previous Image"
                            )
                        }
                        Text(
                            text = "$currentPage/$totalImages",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        IconButton(onClick = {
                            currentPage = (currentPage + 1).coerceAtMost(totalImages)
                        }) {
                            Icon(
                                Icons.Default.KeyboardDoubleArrowRight,
                                contentDescription = "Next Image"
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = rawValue,
                            onValueChange = {
                                rawValue = it
                            },
                            label = { Text("跳转到图片") },
                            modifier = Modifier
                                .width(120.dp)
                                .height(60.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(onClick = {
                            val page = rawValue.toIntOrNull()?.takeIf { it in 1..totalImages }
                            page?.let { currentPage = it }
                        }) { Text("跳转") }
                    }
                    DeleteImageButton(
                        onConfirmDelete = {
                            homeViewModel.deleteImage(
                                userImageList[currentPage - 1].id,
                                onDeleteSuccess = {
                                    homeViewModel.getUserImages()
                                }
                            )
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                    )
                }
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No images available",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun ImageWithText(
    imageItem: UserImage,
    modifier: Modifier = Modifier
){
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .wrapContentSize(),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageItem.fullUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = imageItem.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(280.dp)
                    .aspectRatio(4f / 3f)
            )
            Text(
                text = imageItem.description,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun DeleteImageButton(
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
){
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = { showDialog = true },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("删除图片")
    }
    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这张图片吗？此操作无法撤销") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDelete()
                        showDialog = false
                    }
                ) {
                     Text("确认")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun UploadScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier
){
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

    var dialogState by remember { mutableStateOf<UploadDialogState>(UploadDialogState.None) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .border(
                    BorderStroke(2.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ){
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Select Image",
                    modifier = Modifier
                        .size(80.dp),
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "图片描述") },
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (selectedImageUri != null && description.isNotEmpty()) {
                    homeViewModel.uploadImage(
                        selectedImageUri!!,
                        description,
                        username = homeViewModel.userName.value ?: "",
                        onUploadSuccess = {
                            dialogState = UploadDialogState.UploadSuccess("图片上传成功")
                            homeViewModel.getUserImages()
                        },
                        onUploadFailed = {
                            dialogState = UploadDialogState.NetworkError("无效图片，请上传清晰的交通违章照片")
                        }
                    )
                }
                else {
                    dialogState = UploadDialogState.FieldMissingError("请确保选择了图片并填写了描述")
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("点击上传")
        }
        val state = dialogState
        if(state != UploadDialogState.None){
            UploadDialog(
                clearDialogState = {
                    dialogState = UploadDialogState.None
                },
                message = when(state){
                    is UploadDialogState.UploadSuccess -> state.message
                    is UploadDialogState.NetworkError -> state.message
                    is UploadDialogState.FieldMissingError -> state.message
                    else -> ""
                },
                isSuccess = when(state){
                    is UploadDialogState.UploadSuccess -> "成功"
                    is UploadDialogState.NetworkError -> "失败"
                    is UploadDialogState.FieldMissingError -> "失败"
                    else -> ""
                }
            )
        }
    }
}

@Composable
fun UploadDialog(
    clearDialogState: () -> Unit,
    message: String,
    isSuccess: String
){
    AlertDialog(
        onDismissRequest = {
            clearDialogState()
        },
        title = { Text("上传$isSuccess") },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = {
                    clearDialogState()
                }
            ) {
                Text("确认")
            }
        }
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun HomeScreenPreview(){
    val navController = rememberNavController()
    val homeViewModel = HomeViewModel(Application())
    ManagementAppPortrait(
        homeViewModel = homeViewModel,
        navController = navController
    )
}