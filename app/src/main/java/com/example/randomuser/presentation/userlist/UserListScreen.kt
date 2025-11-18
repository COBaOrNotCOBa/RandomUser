package com.example.randomuser.presentation.userlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.randomuser.domain.model.User
import com.example.randomuser.presentation.components.UserListItem
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.presentation.preview.sampleUsers
import com.example.randomuser.presentation.theme.RandomUserTheme

@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onCreateUserClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onRequestCreateFirstUser: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Empty) {
            onRequestCreateFirstUser()
        }
    }

    UserListScreenContent(
        state = state,
        onCreateUserClick = onCreateUserClick,
        onUserClick = onUserClick
    )
}


@Composable
fun UserListScreenContent(
    state: UiState<List<User>>,
    onCreateUserClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    Scaffold() { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is UiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is UiState.Empty -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data, key = { it.id }) { user ->
                            UserListItem(
                                user = user,
                                onClick = { onUserClick(user.id) },
                                onMenuClick = {
                                    // TODO: delete user через VM
                                }
                            )
                        }
                    }
                }

                UiState.Idle -> Unit
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onCreateUserClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add user",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListScreenPreview_Success() {
    RandomUserTheme {
        UserListScreenContent(
            state = UiState.Success(sampleUsers),
            onCreateUserClick = {},
            onUserClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserListScreenPreview_Empty() {
    RandomUserTheme {
        UserListScreenContent(
            state = UiState.Empty("You have not created any users yet"),
            onCreateUserClick = {},
            onUserClick = {}
        )
    }
}
