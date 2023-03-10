package com.example.composestatepractice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composestatepractice.ui.theme.ComposeStatePracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStatePracticeTheme {
                Greeting("Android")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(10.dp)) {
        items(30) {
            ExpandingText(text = "這款產品真是太棒了！我剛剛買了一個，使用起來非常方便，質感也非常好。它的功能也很強大，能滿足我的所有需求。另外，售後服務也非常周到，在我遇到問題時給了我很大的幫助。我強烈推薦這款產品給大家！",
            showMoreText = "展開全部", showMoreColor = Color.Blue, showMoreTextSize = 13, maxLines = 2
                )

        }
    }
}

@Composable
fun ExpandableText(
    text: String,
    fontSize: Int,
    seeMoreFontSize: Int,
    seeMoreColor: Color,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 1,
) {
    var cutText by remember(text) { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val seeMoreSizeState = remember { mutableStateOf<IntSize?>(null) }
    val seeMoreOffsetState = remember { mutableStateOf<Offset?>(null) }

    val textLayoutResult = textLayoutResultState.value
    val seeMoreSize = seeMoreSizeState.value
    val seeMoreOffset = seeMoreOffsetState.value

//    LaunchedEffect(text, expanded, textLayoutResult, seeMoreSize) {
//        val lastLineIndex = minimizedMaxLines - 1
//        if (!expanded && textLayoutResult != null && seeMoreSize != null
//            && lastLineIndex + 1 == textLayoutResult.lineCount
//            && textLayoutResult.isLineEllipsized(lastLineIndex)
//        ) {
//            // 顯示更多區塊的繪製座標 : (x = 全部文字長度-顯示更多文字的長度, y = 全部文字的底-顯示更多文字的高度)
//            var lastCharIndex = textLayoutResult.getLineEnd(lastLineIndex, visibleEnd = true) + 1
//            var charRect: Rect
//            do {
//                lastCharIndex -= 1
//                charRect = textLayoutResult.getCursorRect(lastCharIndex)
//            } while (
//                charRect.left > textLayoutResult.size.width - seeMoreSize.width
//            )
//            seeMoreOffsetState.value = Offset(charRect.left, charRect.bottom - seeMoreSize.height)
//            cutText = text.substring(startIndex = 0, endIndex = lastCharIndex)
//        }
//    }

    Box(modifier) {
        Text(
            text = cutText ?: text,
            fontSize = fontSize.sp,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
//            onTextLayout = { textLayoutResultState.value = it },
            modifier = modifier.animateContentSize(animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
            )
        )
//        if (!expanded) {
//            val density = LocalDensity.current
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(style = SpanStyle(color = Color.Black)) {
//                        append("... ")
//                    }
//                    withStyle(style = SpanStyle(color = seeMoreColor)) {
//                        append("展開全部")
//                    }
//                },
//                onTextLayout = { seeMoreSizeState.value = it.size },
//                modifier = Modifier
//                    .then(
//                        if (seeMoreOffset != null)
//                            Modifier.offset(
//                                x = with(density) { seeMoreOffset.x.toDp() },
//                                y = with(density) { seeMoreOffset.y.toDp() },
//                            )
//                        else
//                            Modifier
//                    )
//                    .clickable {
//                        expanded = true
//                        cutText = null
//                    }
//                    .alpha(if (seeMoreOffset != null) 1f else 0f)
//                    .animateContentSize(),
//                fontSize = seeMoreFontSize.sp,
//                color = seeMoreColor,
//            )
//        }
    }
}

@Composable
fun ExpandingText(modifier: Modifier = Modifier,
                  text: String,
                  showMoreText: String,
                  showMoreTextSize: Int,
                  showMoreColor: Color,
                  maxLines: Int,
) {
    val ellipsis = Char(0x2026) // 16-bit Unicode格式的省略號
    val space = Char(0x0020) // 16-bit Unicode格式的空白格
    val showMoreString = "${space}${showMoreText}"

    var isExpanded by remember { mutableStateOf(false) } // 是否已展開
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var finalText by remember { mutableStateOf(text) } // 欲顯示的文字

    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = text
            }
            textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(maxLines - 1, true)
                finalText = text.substring(startIndex = 0, endIndex = lastCharIndex - showMoreString.length).plus(ellipsis)
            }
        }
    }

    Box(modifier = modifier) {
        Text(
            text = finalText,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
            onTextLayout = { textLayoutResultState.value = it },
            modifier = Modifier
                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)))

        if (isExpanded.not()) {
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(space)
                }
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append(showMoreText)
                }
            }, modifier = Modifier.clickable { isExpanded = !isExpanded }.align(Alignment.BottomEnd))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeStatePracticeTheme {
        Greeting("Android")
    }
}