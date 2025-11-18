package com.example.randomuser.presentation.createuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.randomuser.domain.model.User
import com.example.randomuser.presentation.components.RandomUserTopBar
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.presentation.theme.RandomUserTheme

@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserCreated: (String) -> Unit
) {
    CreateUserScreenContent(
        uiState = viewModel.uiState,
        selectedGender = viewModel.selectedGender,
        selectedNat = viewModel.selectedNat,
        onGenderSelected = viewModel::onGenderSelected,
        onNatSelected = viewModel::onNatSelected,
        onGenerateClick = { viewModel.generateUser(onUserCreated) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreenContent(
    uiState: UiState<User>,
    selectedGender: String?,
    selectedNat: String?,
    onGenderSelected: (String?) -> Unit,
    onNatSelected: (String?) -> Unit,
    onGenerateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            RandomUserTopBar(
                title = "Generate User",
                canNavigateBack = true,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Select Gender:",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(8.dp))

                val genderOptions = listOf("Any", "Male", "Female")
                var genderExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = when (selectedGender) {
                            "male" -> "Male"
                            "female" -> "Female"
                            else -> "Any"
                        },
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    val g = when (option) {
                                        "Male" -> "male"
                                        "Female" -> "female"
                                        else -> null
                                    }
                                    onGenderSelected(g)
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Select Nationality:",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(8.dp))

                val natOptions = listOf("Any", "US", "GB", "DE", "FR", "AU")
                var natExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = natExpanded,
                    onExpandedChange = { natExpanded = !natExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedNat?.uppercase() ?: "Any",
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = natExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = natExpanded,
                        onDismissRequest = { natExpanded = false }
                    ) {
                        natOptions.forEach { nat ->
                            DropdownMenuItem(
                                text = { Text(nat) },
                                onClick = {
                                    val v = if (nat == "Any") null else nat.lowercase()
                                    onNatSelected(v)
                                    natExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onGenerateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GENERATE")
                }
            }

            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is UiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
                    )
                }

                is UiState.Empty -> {
                    Text(
                        text = uiState.message,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
                    )
                }

                else -> Unit
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun CreateUserScreenPreview_Idle() {
    RandomUserTheme {
        CreateUserScreenContent(
            uiState = UiState.Idle,
            selectedGender = null,
            selectedNat = null,
            onGenderSelected = {},
            onNatSelected = {},
            onGenerateClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserScreenPreview_Error() {
    RandomUserTheme {
        CreateUserScreenContent(
            uiState = UiState.Error("Server error: 500"),
            selectedGender = "male",
            selectedNat = "gb",
            onGenderSelected = {},
            onNatSelected = {},
            onGenerateClick = {},
            onBackClick = {}
        )
    }
}
