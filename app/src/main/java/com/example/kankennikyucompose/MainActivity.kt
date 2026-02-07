package com.example.kankennikyucompose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kankennikyucompose.ui.theme.KankenNikyuComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KankenNikyuComposeTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun StaggerGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows)
        val rowHeights = IntArray(rows)

        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = rowHeights[row].coerceAtLeast(placeable.height)
            placeable
        }

        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows)
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        layout(width, height) {
            val rowX = IntArray(rows)
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
private fun MyApp() {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        // Here you can change which screen to display
        Greetings()
    }
}

@Composable
fun MyCustomColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val width = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (placeables.sumOf { it.height }).coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width, height) {
            var yPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}

fun Modifier.firstBaselineToTop(
    firstBaseLineToTop: Dp
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseLine = placeable[FirstBaseline]
    val placeableY = firstBaseLineToTop.roundToPx() - firstBaseLine
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        placeable.placeRelative(0, placeableY)
    }
}

@Composable
fun ScrollingList() {
    val listSize = 100
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }
            ) {
                Text(text = "Top")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(listSize - 1)
                    }
                }
            ) {
                Text(text = "End")
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            items(listSize) {
                ImageListItem(it)
            }
        }
    }
}

@Composable
fun ImageListItem(index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun Question() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "問題") },
                actions = {
                    IconButton(onClick = { /* TODO: Bookmark */ }) {
                        Icon(Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        )
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Text(
            text = "次の線が引かれている部分の読みをひらがなで記せ。",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = "旦夕",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.size(16.dp))
        
        MyCustomColumn {
            Text("MyCustomColumn", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            Text("places items")
            Text("vertically.")
            Text("We've done it by hand!")
        }
    }
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Card(
        elevation = 4.dp,
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = { })
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) { }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = "cassiaさん", fontWeight = FontWeight.Bold)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text("Last login: かなり前", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

@Composable
private fun Greetings(names: List<String> = listOf("本日の学習", "昨日の学習", "テーマ別学習")) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp)) {
        items(items = names) { name ->
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
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(24.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Beat")
                Text(
                    text = "$name!",
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "挟む" else "展開")
            }
        }
        if (expanded) {
            GreetingExpandedContent(name)
        }
    }
}

@Composable
private fun GreetingExpandedContent(name: String) {
    val randomStepNoRange = (1..28)
    val powerupList = listOf(
        "読み①", "読み②", "読み③", "読み④",
        "書き取り①", "書き取り②", "書き取り③", "送りがな", "部首", "熟語の構成",
        "四字熟語", "対義語・類義語", "同訓・同音異字", "誤字訂正"
    )

    Spacer(modifier = Modifier.height(16.dp))

    when (name) {
        "本日の学習" -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ステップ ${randomStepNoRange.random()}")
                    Text(text = "パワーアップ ${powerupList.random()}")
                }
                Button(onClick = { /* TODO */ }) {
                    Text(text = "学習開始")
                }
            }
        }
        "昨日の学習" -> {
            Text(text = "昨日の学習内容がここに表示されます。")
        }
        "テーマ別学習" -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(modifier = Modifier.weight(1f), onClick = { }) { Text("ステップ") }
                    Button(modifier = Modifier.weight(1f), onClick = { }) { Text("力だめし") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(modifier = Modifier.weight(1f), onClick = { }) { Text("パワーアップ") }
                    Button(modifier = Modifier.weight(1f), onClick = { }) { Text("総まとめ") }
                }
            }
        }
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
            Text("Beat 漢検2級!", style = MaterialTheme.typography.h4)
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("スタート")
            }
        }
    }
}

@Composable
private fun Steps(names: List<String> = List(28) { "${it + 1}" }) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp)) {
        items(items = names) { name ->
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
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "ステップ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraLight)
                )
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Filled.KeyboardArrowUp else Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
        }
        if (expanded) {
            val kanjiText = when (name) {
                "1" -> "挨　曖　宛　嵐　畏　萎　椅"
                "2" -> "彙　茨　咽　淫　唄　鬱　怨"
                "28" -> "瑠　呂　賂　弄　籠　麓　脇"
                else -> "漢字のリストがここに表示されます。"
            }
            Text(kanjiText, Modifier.padding(top = 12.dp))
            OutlinedButton(
                modifier = Modifier.padding(top = 12.dp),
                onClick = { /* TODO */ }
            ) {
                Text(text = "学習開始")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    KankenNikyuComposeTheme { Greetings() }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnBoardingPreview() {
    KankenNikyuComposeTheme { OnboardingScreen(onContinueClicked = {}) }
}

@Preview(showBackground = true, widthDp = 320, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun StepsPreview() {
    KankenNikyuComposeTheme { Steps() }
}
