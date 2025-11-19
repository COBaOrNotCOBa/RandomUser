package com.example.randomuser.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.randomuser.domain.model.User
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.presentation.preview.DevicePreviews
import com.example.randomuser.presentation.preview.sampleUser
import com.example.randomuser.presentation.preview.sampleUsers
import com.example.randomuser.presentation.theme.AppColors
import com.example.randomuser.presentation.theme.AppTextStyles
import com.example.randomuser.presentation.theme.RandomUserTheme

@Composable
fun UserDetailsScreen(
    viewModel: UserDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is UiState.Error -> {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Empty -> {
                    Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    UserDetailsContent(
                        user = s.data,
                        onBackClick = onBackClick
                    )
                }

                UiState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun UserDetailsContent(
    user: User,
    onBackClick: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val gradientColors = remember(user.gender) {
        if (user.gender?.lowercase() == "female") {
            AppColors.FemaleGradient
        } else {
            AppColors.MaleGradient
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(75.dp))

                Text(
                    text = "Hi how are you today?",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "I'm",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = user.fullName,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DetailsTabsHeader(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            gradientColors = gradientColors
                        )

                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            when (selectedTab) {
                                0 -> UserInfoTab(user)
                                1 -> PhoneTab(user)
                                2 -> EmailTab(user)
                                3 -> LocationTab(user)
                            }
                        }
                    }
                }
            }

            AsyncImage(
                model = user.pictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-70).dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun DetailsTabsHeader(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    gradientColors: List<Color>
) {
    val icons = listOf(
        Icons.Default.Person,
        Icons.Default.Phone,
        Icons.Default.Email,
        Icons.Default.LocationOn
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(gradientColors)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        icons.forEachIndexed { index, icon ->
            val isSelected = index == selectedTab

            val bgColor = if (isSelected) Color.White else Color.Transparent
            val iconTint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
private fun UserInfoTab(user: User) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "First name:", value = user.fullName.split(" ").getOrNull(1) ?: "")
        InfoRow(label = "Last name:", value = user.fullName.split(" ").lastOrNull() ?: "")
        InfoRow(label = "Gender:", value = user.gender ?: "")
        InfoRow(label = "Age:", value = user.age?.toString() ?: "")
        InfoRow(label = "Date of birth:", value = user.dateOfBirth?.substringBefore("T") ?: "")
    }
}

@Composable
private fun PhoneTab(user: User) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Phone:", value = user.phone ?: "")
        InfoRow(label = "Cell:", value = user.cell ?: "")
    }
}

@Composable
private fun EmailTab(user: User) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Email:", value = user.email ?: "")
    }
}

@Composable
private fun LocationTab(user: User) {
    val location = listOfNotNull(
        user.country,
        user.city,
        user.street
    ).joinToString(", ")

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Country:", value = user.country ?: "")
        InfoRow(label = "City:", value = user.city ?: "")
        InfoRow(label = "Street:", value = user.street ?: "")
        InfoRow(label = "Full address:", value = location)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTextStyles.InfoLabel,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            style = AppTextStyles.InfoValue,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@DevicePreviews
@Composable
fun UserDetailsScreenPreviewMale() {
    RandomUserTheme {
        UserDetailsContent(
            user = sampleUser,
            onBackClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun UserDetailsScreenPreview() {
    RandomUserTheme {
        UserDetailsContent(
            user = sampleUsers[1],
            onBackClick = {}
        )
    }
}
