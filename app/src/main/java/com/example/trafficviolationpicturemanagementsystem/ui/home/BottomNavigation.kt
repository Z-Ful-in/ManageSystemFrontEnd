package com.example.trafficviolationpicturemanagementsystem.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.trafficviolationpicturemanagementsystem.viewmodel.HomeViewModel

@Composable
fun BottomNavigation(
    homeViewModel: HomeViewModel,
    modifier: Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null
                )
            },
            label = { Text(text = "查看图片",) },
            selected = homeViewModel.isPageSelected.value == 0,
            onClick = {
                homeViewModel.onViewClick()
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null
                )
            },
            label = { Text(text = "上传图片",) },
            selected = homeViewModel.isPageSelected.value == 1,
            onClick = {
                homeViewModel.onUploadClick()
            }
        )
    }
}


