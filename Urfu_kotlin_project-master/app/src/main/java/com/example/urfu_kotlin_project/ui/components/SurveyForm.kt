package com.example.urfu_kotlin_project.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.urfu_kotlin_project.R
import com.example.urfu_kotlin_project.ui.theme.Urfu_kotlin_projectTheme
import coil.compose.rememberAsyncImagePainter

@Composable
fun SurveyForm() {

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf(25f) }
    var gender by rememberSaveable { mutableStateOf("") }
    var isSubscribed by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("") }
    var avatarUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()

    val genderMaleLabel = stringResource(R.string.gender_male)

    LaunchedEffect(Unit) {
        if (gender.isBlank()) {
            gender = genderMaleLabel
        }
    }

    val subscriptionYes = stringResource(R.string.subscription_yes)
    val subscriptionNo = stringResource(R.string.subscription_no)
    val resultTemplate = stringResource(R.string.result)

    val isNameValid = name.isNotBlank()
    val isButtonEnabled = isNameValid

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
    }

    val avatarModifier = Modifier
        .size(150.dp)
        .padding(top = 38.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        AvatarSection(
            avatarUri = avatarUri,
            onAvatarClick = { imagePickerLauncher.launch("image/*") },
            modifier = avatarModifier
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormSection(
            name = name,
            onNameChange = { name = it },
            age = age,
            onAgeChange = { age = it },
            gender = gender,
            onGenderChange = { gender = it },
            isSubscribed = isSubscribed,
            onSubscribeChange = { isSubscribed = it },
            result = result,
            onSubmit = {
                val subscriptionStatus = if (isSubscribed) subscriptionYes else subscriptionNo
                result = String.format(resultTemplate, name, age.toInt(), gender, subscriptionStatus)
            },
            isButtonEnabled = isButtonEnabled,
            isNameValid = isNameValid
        )
    }
}

@Composable
fun AvatarSection(
    avatarUri: Uri?,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (avatarUri != null) {
            Image(
                painter = rememberAsyncImagePainter(avatarUri),
                contentDescription = "User avatar",
                modifier = modifier
            )
        } else {
            Image(
                imageVector = Icons.Filled.Person,
                contentDescription = "Default avatar",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = modifier
            )
        }

        TextButton(onClick = onAvatarClick) {
            Text(stringResource(R.string.button_select_avatar))
        }
    }
}

@Composable
fun FormSection(
    name: String,
    onNameChange: (String) -> Unit,
    age: Float,
    onAgeChange: (Float) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    isSubscribed: Boolean,
    onSubscribeChange: (Boolean) -> Unit,
    result: String,
    onSubmit: () -> Unit,
    isButtonEnabled: Boolean,
    isNameValid: Boolean
) {
    val genderMaleLabel = stringResource(R.string.gender_male)
    val genderFemaleLabel = stringResource(R.string.gender_female)

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.hint_name)) },
        isError = !isNameValid,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        supportingText = {
            if (!isNameValid) {
                Text(stringResource(R.string.error_name_empty))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.age, age.toInt()),
            modifier = Modifier.width(110.dp)
        )
        Slider(
            value = age,
            onValueChange = onAgeChange,
            valueRange = 1f..100f,
            steps = 98,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.gender), modifier = Modifier.padding(end = 16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            RadioButton(
                selected = gender == genderMaleLabel,
                onClick = { onGenderChange(genderMaleLabel) }
            )
            Text(genderMaleLabel)
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            RadioButton(
                selected = gender == genderFemaleLabel,
                onClick = { onGenderChange(genderFemaleLabel) }
            )
            Text(genderFemaleLabel)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSubscribed,
            onCheckedChange = onSubscribeChange
        )
        Text(stringResource(R.string.subscribe))
    }

    Button(
        onClick = onSubmit,
        enabled = isButtonEnabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.button_send))
    }

    if (result.isNotEmpty()) {
        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Preview(name = "Light Mode - Portrait")
@Composable
fun UserFormPreviewLight() {
    Urfu_kotlin_projectTheme(darkTheme = false) {
        Surface {
            SurveyForm()
        }
    }
}

@Preview(name = "Dark Mode - Portrait")
@Composable
fun UserFormPreviewDark() {
    Urfu_kotlin_projectTheme(darkTheme = true) {
        Surface {
            SurveyForm()
        }
    }
}