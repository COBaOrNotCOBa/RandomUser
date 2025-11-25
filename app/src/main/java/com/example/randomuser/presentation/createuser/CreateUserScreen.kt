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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.randomuser.R
import com.example.randomuser.domain.model.NationalityOptions
import com.example.randomuser.presentation.model.Gender
import com.example.randomuser.domain.model.User
import com.example.randomuser.presentation.components.RandomUserTopBar
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.presentation.preview.DevicePreviews
import com.example.randomuser.presentation.theme.AppTextStyles
import com.example.randomuser.presentation.theme.RandomUserTheme

@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserCreated: (String) -> Unit,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    CreateUserScreenContent(
        uiState = state.requestState,
        selectedGender = state.gender,
        selectedNationality = state.nationality,
        onGenderSelected = viewModel::onGenderSelected,
        onNationalitySelected = viewModel::onNationalitySelected,
        onGenerateClick = { viewModel.generateUser(onUserCreated) },
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreenContent(
    uiState: UiState<User>,
    selectedGender: Gender,
    selectedNationality: String?,
    onGenderSelected: (Gender) -> Unit,
    onNationalitySelected: (String?) -> Unit,
    onGenerateClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            RandomUserTopBar(
                title = stringResource(R.string.title_generate_user),
                onBackClick = onBackClick,
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
                    text = stringResource(R.string.label_select_gender),
                    color = MaterialTheme.colorScheme.primary,
                    style = AppTextStyles.SectionTitle
                )

                Spacer(Modifier.height(8.dp))

                val genderOptions = remember { Gender.entries }
                var genderExpanded by remember { mutableStateOf(false) }

                val selectedGenderLabel = stringResource(selectedGender.labelResId)

                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedGenderLabel,
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
                                text = { Text(stringResource(option.labelResId)) },
                                onClick = {
                                    onGenderSelected(option)
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.label_select_nationality),
                    color = MaterialTheme.colorScheme.primary,
                    style = AppTextStyles.SectionTitle
                )

                Spacer(Modifier.height(8.dp))

                val nationalityOptions = remember { NationalityOptions.all }
                var nationalityExpanded by remember { mutableStateOf(false) }

                val selectedNationalityLabel = nationalityOptions
                    .firstOrNull { it.code == selectedNationality }
                    ?.labelResId
                    ?.let { stringResource(it) }
                    ?: stringResource(NationalityOptions.default.labelResId)

                ExposedDropdownMenuBox(
                    expanded = nationalityExpanded,
                    onExpandedChange = { nationalityExpanded = !nationalityExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedNationalityLabel,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = nationalityExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = nationalityExpanded,
                        onDismissRequest = { nationalityExpanded = false }
                    ) {
                        nationalityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(stringResource(option.labelResId)) },
                                onClick = {
                                    onNationalitySelected(option.code)
                                    nationalityExpanded = false
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
                    Text(text = stringResource(R.string.button_generate))
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

@DevicePreviews
@Composable
fun CreateUserScreenPreview_Idle() {
    RandomUserTheme {
        CreateUserScreenContent(
            uiState = UiState.Idle,
            selectedGender = Gender.ANY,
            selectedNationality = null,
            onGenderSelected = {},
            onNationalitySelected = {},
            onGenerateClick = {},
            onBackClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun CreateUserScreenPreview_Error() {
    RandomUserTheme {
        CreateUserScreenContent(
            uiState = UiState.Error("Server error: 500"),
            selectedGender = Gender.FEMALE,
            selectedNationality = "gb",
            onGenderSelected = {},
            onNationalitySelected = {},
            onGenerateClick = {},
            onBackClick = {}
        )
    }
}
