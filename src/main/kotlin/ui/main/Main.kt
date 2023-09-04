import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.theme.*
import util.isAlphabetic
import util.isAlphabeticOrHyphen
import util.mainWordList

@Composable
@Preview
fun App() {
    var shortlist by remember { mutableStateOf(mainWordList) }

    var includedLetters by remember { mutableStateOf("") }
    var excludedLetters by remember { mutableStateOf("") }
    var guessedPattern by remember { mutableStateOf("") }

    Surface(color = DarkBackground, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "WORDLE GUESSER",
                color = Color.White,
                fontSize = 36.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)
            )

            Divider(color = DarkGrey)

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(2 / 3f)) {
                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 88.dp, end = 24.dp)
                            .fillMaxWidth()
                    ) {
                        val annotatedText = buildAnnotatedString {
                            append("1. Enter ")
                            withStyle(style = SpanStyle(Yellow)) { append("included") }
                            append(" letters:")
                        }
                        Text(
                            text = annotatedText,
                            fontSize = 24.sp,
                            color = Color.White,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(end = 24.dp).align(Alignment.CenterVertically)
                        )

                        TextField(
                            value = includedLetters,
                            onValueChange = {
                                if (it.isAlphabetic()) {
                                    includedLetters = it.uppercase().trim()
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center, color = Color.White,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = DarkBackground,
                                focusedIndicatorColor = Yellow,
                                unfocusedIndicatorColor = DarkGrey,
                                backgroundColor = DarkBackground
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 88.dp, end = 24.dp)
                            .fillMaxWidth()
                    ) {
                        val annotatedText = buildAnnotatedString {
                            append("2. Enter ")
                            withStyle(style = SpanStyle(LightGrey)) { append("excluded") }
                            append(" letters:")
                        }
                        Text(
                            text = annotatedText,
                            fontSize = 24.sp,
                            color = Color.White,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(end = 24.dp).align(Alignment.CenterVertically)
                        )

                        TextField(
                            value = excludedLetters,
                            onValueChange = {
                                if (it.isAlphabetic()) {
                                    excludedLetters = it.uppercase().trim()
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center, color = Color.White,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = DarkBackground,
                                focusedIndicatorColor = LightGrey,
                                unfocusedIndicatorColor = DarkGrey,
                                backgroundColor = DarkBackground
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 88.dp, end = 24.dp)
                            .fillMaxWidth()
                    ) {
                        val annotatedText = buildAnnotatedString {
                            append("3. Enter ")
                            withStyle(style = SpanStyle(Green)) { append("guessed") }
                            append(" pattern till now:")
                        }
                        Text(
                            text = annotatedText,
                            fontSize = 24.sp,
                            color = Color.White,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(end = 24.dp).align(Alignment.CenterVertically)
                        )

                        PatternRow(value = guessedPattern) { guessedPattern = it }
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 100.dp, end = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                var s1 = mutableListOf<String>()
                                s1.addAll(mainWordList)
                                if (includedLetters.isNotBlank()) {
                                    val l = includedLetters.toCharArray()
                                    for (i in l) {
                                        s1 = s1.filter { it.contains(i, true) }.toMutableList()
                                    }
                                }
                                if (excludedLetters.isNotBlank()) {
                                    val e = excludedLetters.toCharArray()
                                    for (i in e) {
                                        s1 = s1.filterNot { it.contains(i, true) }.toMutableList()
                                    }
                                }
                                if (guessedPattern.isNotBlank()) {
                                    for (i in guessedPattern.indices) {
                                        if (guessedPattern[i] != '-') {
                                            s1 = s1.filter { it[i].equals(guessedPattern[i], true) }.toMutableList()
                                        }
                                    }
                                }
                                shortlist = s1
                            },
                            modifier = Modifier.padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Green)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Rounded.Search),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(start = 56.dp, end = 8.dp)
                            )
                            Text(
                                text = "SEARCH",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(end = 64.dp)
                            )
                        }

                        Button(
                            onClick = {
                                shortlist = mainWordList
                                includedLetters = ""
                                excludedLetters = ""
                                guessedPattern = ""
                            },
                            modifier = Modifier.padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Red)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Rounded.Refresh),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(start = 56.dp, end = 8.dp)
                            )
                            Text(
                                text = "RESET",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(end = 64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.fillMaxHeight(0.5f))
                }

                MatchedWordsColumn(modifier = Modifier.align(Alignment.CenterEnd), shortlist)
            }
        }
    }
}

@Composable
fun LetterCell(
    modifier: Modifier = Modifier,
    value: String,
) {
    Box(modifier = modifier) {
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PatternRow(
    modifier: Modifier = Modifier,
    length: Int = 5,
    value: String = "",
    onValueChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                if (it.isAlphabeticOrHyphen()) {
                    onValueChanged(it.uppercase())
                }
                if (it.length >= length) {
                    keyboard?.hide()
                }
            }
        },
        modifier = Modifier.size(0.dp).focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            imeAction = ImeAction.Search
        )
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        repeat(length) {
            val letter = value.getOrNull(it)?.toString() ?: ""
            LetterCell(
                modifier = modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(DarkBackground)
                    .border(width = 2.dp, color = if (letter.isBlank()) DarkGrey else Green)
                    .clickable {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                value = letter
            )
            Spacer(modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun MatchedWordsColumn(modifier: Modifier = Modifier, wordList: List<String> = listOf()) {
    Box(
        modifier = modifier
            .fillMaxWidth(1 / 3f)
            .fillMaxHeight()
    ) {
        Divider(color = DarkGrey, modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "${wordList.size} matching words",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(20.dp, 20.dp, 20.dp, 0.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                val scrollState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .align(Alignment.TopStart),
                    state = scrollState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(wordList.size) { idx ->
                        Text(
                            text = wordList[idx].uppercase(),
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(8.dp, 24.dp, 8.dp, 16.dp),
                    style = ScrollbarStyle(
                        minimalHeight = 24.dp,
                        thickness = 6.dp,
                        shape = RoundedCornerShape(4.dp),
                        unhoverColor = LightGrey,
                        hoverColor = DarkGrey,
                        hoverDurationMillis = 150
                    )
                )
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Wordle Guesser",
        onCloseRequest = ::exitApplication,
        resizable = false,
        state = rememberWindowState(width = 1440.dp, height = 1024.dp)
    ) {
        App()
    }
}
