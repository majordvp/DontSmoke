package com.enessaidokur.dontsmoke.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.enessaidokur.dontsmoke.ui.components.acikGriArkaPlan
import com.enessaidokur.dontsmoke.ui.components.anaYesil
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
 fun YatirimEkrani(){
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = anaYesil, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = true)
    }
    Scaffold(
        // --- TOP BAR ---
        topBar = {
           // KalpRitmiAppBar()
        },
        containerColor = acikGriArkaPlan

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
}}