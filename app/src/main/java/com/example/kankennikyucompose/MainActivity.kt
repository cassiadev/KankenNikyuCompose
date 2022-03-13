package com.example.kankennikyucompose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kankennikyucompose.ui.theme.KankenNikyuComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KankenNikyuComposeTheme {
                MyApp()
            }
        }
    }
}

@Composable
private fun MyApp() {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = {shouldShowOnboarding = false})
    } else {
        Greetings()
//        Steps()
    }
}

@Composable
private fun Greetings(names: List<String> = listOf("本日の学習", "昨日の学習", "テーマ別学習")) {
    Column(Modifier.padding(vertical = 4.dp)) {
        for (name in names) {
            Greeting(name = name)
        }
    }
}

@Composable
private fun Greeting(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        GreetingCardContent(name)
    }
}

@Composable
private fun GreetingCardContent(name: String) {
    var expanded by remember { mutableStateOf(false)}
    Column(modifier = Modifier
        .padding(24.dp)
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )) {
        Row() {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "Beat")
                Text(text = "$name!")
            }
            OutlinedButton(
                onClick = { expanded = !expanded }
            ) {
                Text(if (expanded) "挟む" else "展開")
            }
        }
        if (expanded) {
            val randomStepNoRange = (1..28)
            val powerupList = listOf<String>(
                "読み①", "読み②", "読み③", "読み④",
                "書き取り①", "書き取り②", "書き取り③", "送りがな", "部首", "熟語の構成",
                "四字熟語", "対義語・類義語", "同訓・同音異字", "誤字訂正"
            )
            when (name) {
                "本日の学習" -> {
                    Row() {
                        Column() {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "ステップ ${randomStepNoRange.random()}"
                            )
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "パワーアップ ${powerupList.random()}"
                            )
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(text = "学習開始")
                        }
                    }
                }
                "昨日の学習" -> {
                    //TODO 전날 학습내용 모아둔 것 전부 전개
                }
                "テーマ別学習" -> {
                    Row() {
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO: Steps() 실행*/ }
                        ) {
                            Text(text = "ステップ")
                        }
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "ステップ 力だめし")
                        }
                    }
                    Row() {
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "パワーアップ")
                        }
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "総まとめ")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun DefaultPreview() {
    KankenNikyuComposeTheme {
        Greetings()
    }
}

@Composable
private fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Beat 漢検2級!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ){
                Text("スタート")
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 320)
@Composable
private fun OnBoardingPreview() {
    KankenNikyuComposeTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Composable
private fun Steps(names: List<String> = List(28){ "${it.toInt()+1}" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 3.dp)) {
        items(items = names) {name ->
            Step(name = name)
        }
    }
}

@Composable
private fun Step(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
            StepCardContent(name)
    }
}

@Composable
private fun StepCardContent(name: String) {
    var expanded by remember { mutableStateOf(false)}
    val extraPadding by animateDpAsState(
        if (expanded) 36.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "ステップ")
                Text(text = name, style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraLight
                    )
                )
                if (expanded) {
                    when(name) {
                        "1" -> {
                            Text("挨　曖　宛　嵐　畏　萎　椅", Modifier.padding(top = 12.dp))
                        }
                        "2" -> {
                            Text("彙　茨　咽　淫　唄　鬱　怨", Modifier.padding(top = 12.dp))
                        }
                        //...
                        "28" -> {
                            Text("瑠　呂　賂　弄　籠　麓　脇", Modifier.padding(top = 12.dp))
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.padding(vertical = 6.dp),
                        onClick = { /*TODO: 화면이동*/ }
                    ) {
                        Text(text = "学習開始")
                    }
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Filled.KeyboardArrowUp else Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) {
                        stringResource(id = R.string.show_less)
                    } else {
                        stringResource(id = R.string.show_more)
                    }
                )
            }
        }
    }
}
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark")
@Composable
private fun StepsPreview() {
    KankenNikyuComposeTheme {
        Steps()
    }
}