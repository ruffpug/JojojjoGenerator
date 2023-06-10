package net.ruffpug.jojojjogenerator

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.compose.web.attributes.height
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Canvas
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//  画像ファイル名
private const val imageFileName: String = "marisa.png"

//  画像横幅
private const val imageWidth: Int = 1280

//  画像高さ
private const val imageHeight: Int = 720

//  画像縦横比
private const val imageRatio: Float = imageWidth.toFloat() / imageHeight

//  発言メッセージの描画原点X座標
private const val messageOriginX: Double = 100.0

//  発言メッセージの描画原点Y座標
private const val messageOriginY: Double = 180.0

/**
 * Canvas描画部分
 */
@Composable
internal fun MarisaCanvas(canvasId: String, maxWidth: CSSNumeric, message: String) {
    Canvas(
        attrs = {
            id(canvasId)
            width(imageWidth)
            height(imageHeight)
            style {
                width(100.percent)
                height(auto)
                maxWidth(maxWidth)
                margin(16.px)
                padding(0.px)
                property("aspect-ratio", imageRatio)
            }
        },
    ) {
        var currentMessage: String? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            val image = loadImageAsync(src = imageFileName)
            val messageFlow = snapshotFlow { currentMessage }.filterNotNull()

            //  発言メッセージが変化したタイミングで再描画を走らせる。
            messageFlow.collect { msg ->
                val canvas = document.getElementById(canvasId) as HTMLCanvasElement
                draw(canvas = canvas, img = image, message = msg)
            }
        }

        SideEffect {
            if (currentMessage != message) currentMessage = message
        }
    }
}

//  画像を読み込む。
private suspend fun loadImageAsync(src: String): Image = suspendCoroutine { continuation ->
    val img = Image()
    img.onload = { continuation.resume(img) }
    img.onerror = { _, _, _, _, _ -> continuation.resumeWithException(Exception("画像読み込みエラー")) }
    img.src = src
}

//  Canvasの描画処理を行う。
private fun draw(canvas: HTMLCanvasElement, img: Image, message: String) {
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.clearRect(x = 0.0, y = 0.0, w = canvas.width.toDouble(), h = canvas.height.toDouble())

    //  画像を描画する。
    context.drawImage(img, dx = 0.0, dy = 0.0)

    //  発言メッセージを描画する。
    context.fillStyle = "#000000"
    context.font = "48px sans-serif"

    val lineHeight = 96
    for ((index, line) in message.lineSequence().withIndex()) {
        val offsetY = lineHeight * index
        context.fillText(line, x = messageOriginX, y = messageOriginY + offsetY)
    }
}
