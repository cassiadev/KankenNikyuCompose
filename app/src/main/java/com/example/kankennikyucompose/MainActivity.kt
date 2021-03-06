package com.example.kankennikyucompose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil.compose.rememberImagePainter
import com.example.kankennikyucompose.ui.theme.KankenNikyuComposeTheme
import kotlinx.coroutines.launch

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
fun StaggerGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Keep track of the width of each row
        val rowWidths = IntArray(rows) { 0 }

        // Keep track of the max height of each row
        val rowHeights = IntArray(rows) { 0 }

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.mapIndexed { index, measureble ->
            // Measure each child
            val placeable = measureble.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        // Grid's height is the sum of the tallest element of each row
        // coerced to the height constraints
        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        // Set the size of the parent layout
        layout(width, height) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

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
        OnboardingScreen(onContinueClicked = {shouldShowOnboarding = false})
    } else {
//        Greetings()
//        Steps()
//        PhotographerCard()
//        Question()
        ScrollingList()
    }
}

@Composable
fun MyCustomColumn(
    modifier: Modifier = Modifier,
    // custom layout attributes
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(constraints)
        }
        // Track the y co-ord we have placed children up to
        var yPosition = 0

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            //Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

fun Modifier.firstBaselineToTop(
    firstBaseLineToTop: Dp
) = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseLine = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaseLineToTop.roundToPx() - firstBaseLine
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable are placed
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    KankenNikyuComposeTheme() {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    KankenNikyuComposeTheme() {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}

@Composable
fun ScrollingList() {
    val listSize = 100
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()
    // We save the coroutine scope where our animated scroll will be executed
    val coroutineScope = rememberCoroutineScope()
    
    Row() {
        Button(onClick = { 
            coroutineScope.launch { 
                // 0 is the first item index
                scrollState.animateScrollToItem(0)
            }
        }) {
            Text(text = "Scroll to the top")
        }
        Button(onClick = { 
            coroutineScope.launch { 
                // listSize -1 is the last index of the list
                scrollState.animateScrollToItem(listSize - 1)
            }
        }) {
            Text(text = "Scroll to the end")
        }
    }

    LazyColumn(state = scrollState) {
        items(100) {
            ImageListItem(it)
        }
    }
}

@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun Question() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "??????")
                },
                actions = {
                    IconButton(onClick = { /*TODO ??????????????? ?????? ?????? ??????*/ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "????????????????????????????????????????????????????????????????????????")
        Text(text = "??????")
    }
    MyCustomColumn(modifier.padding(8.dp)) {
        Text("MyOwnColumn")
        Text("places items")
        Text("vertically.")
        Text("We've done it by hand!")
    }
}

@Preview
@Composable
fun QuestionPreview() {
    KankenNikyuComposeTheme() {
        Question()
    }
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row(
        modifier
            .clickable(onClick = {/* Igonoring onClkick */ })
            .padding(16.dp)
    )
    {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            //Image goes here
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = "cassia??????", fontWeight = FontWeight.Bold)
            // LocalContentAlpha is defining opacity level of its children
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("Last login: ??????????????????????????????", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Preview
@Composable
fun PhotographerCaradPreview() {
    KankenNikyuComposeTheme() {
        PhotographerCard()
    }
}

@Composable
private fun Greetings(names: List<String> = listOf("???????????????", "???????????????", "??????????????????")) {
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
                Text(if (expanded) "??????" else "??????")
            }
        }
        if (expanded) {
            val randomStepNoRange = (1..28)
            val powerupList = listOf<String>(
                "?????????", "?????????", "?????????", "?????????",
                "???????????????", "???????????????", "???????????????", "????????????", "??????", "???????????????",
                "????????????", "?????????????????????", "?????????????????????", "????????????"
            )
            when (name) {
                "???????????????" -> {
                    Row() {
                        Column() {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "???????????? ${randomStepNoRange.random()}"
                            )
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "?????????????????? ${powerupList.random()}"
                            )
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(text = "????????????")
                        }
                    }
                }
                "???????????????" -> {
                    //TODO ?????? ???????????? ????????? ??? ?????? ??????
                }
                "??????????????????" -> {
                    Row() {
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO: Steps() ??????*/ }
                        ) {
                            Text(text = "????????????")
                        }
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "???????????? ????????????")
                        }
                    }
                    Row() {
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "??????????????????")
                        }
                        Button(
                            modifier = Modifier.padding(6.dp),
                            onClick = { /*TODO*/ }) {
                            Text(text = "????????????")
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
            Text("Beat ??????2???!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ){
                Text("????????????")
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
                Text(text = "????????????")
                Text(text = name, style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraLight
                    )
                )
                if (expanded) {
                    when(name) {
                        "1" -> {
                            Text("???????????????????????????????????????", Modifier.padding(top = 12.dp))
                        }
                        "2" -> {
                            Text("???????????????????????????????????????", Modifier.padding(top = 12.dp))
                        }
                        //...
                        "28" -> {
                            Text("???????????????????????????????????????", Modifier.padding(top = 12.dp))
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.padding(vertical = 6.dp),
                        onClick = { /*TODO: ????????????*/ }
                    ) {
                        Text(text = "????????????")
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