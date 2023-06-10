package net.ruffpug.jojojjogenerator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement

//  描画CanvasのID
private const val canvasId: String = "marisa-canvas"

//  出力画像のID
private const val outputImgId: String = "output-img"

//  コンポネントの最大横幅
private val componentMaxWidth: CSSNumeric = 640.px

//  出力画像の最大横幅
private val outputImgMaxWidth: CSSNumeric = componentMaxWidth / 2

/**
 * 生成器ページ
 */
@Composable
internal fun GeneratorPage(bloc: GeneratorBloc) {
    val message: String by bloc.message.collectAsState()

    //  ヘッダ部分
    PageHeader()

    //  Canvas部分
    MarisaCanvas(
        canvasId = canvasId,
        maxWidth = componentMaxWidth,
        message = message,
    )

    //  入力フィールド
    MessageField(
        msg = message,
        maxWidth = componentMaxWidth,
        onMessageChanged = remember(bloc) { { bloc.onMessageChanged(it) } },
    )

    //  出力ボタン
    OutputButton(
        onButtonClicked = {
            //  クリック時にCanvasの描画イメージをimg要素に出力する。
            val canvas = document.getElementById(canvasId) as HTMLCanvasElement
            val img = document.getElementById(outputImgId) as HTMLImageElement
            img.src = canvas.toDataURL(type = "image/png")
        },
    )

    //  出力画像
    OutputImg(id = outputImgId, maxWidth = outputImgMaxWidth)
}

//  ヘッダ部分
@Composable
private fun PageHeader() {
    Nav(
        attrs = {
            classes("navbar", "navbar-dark", "bg-dark")
            style {
                width(100.vw)
                height(auto)
                color(rgb(255, 255, 255))
            }
        },
    ) {
        Div(attrs = { classes("container-fluid") }) {
            Span(attrs = { classes("navbar-brand") }) {
                Text("じょじょっじょジェネレータ")
            }
        }
    }
}

//  発言メッセージの入力フィールド
@Composable
private fun MessageField(msg: String, maxWidth: CSSNumeric, onMessageChanged: (String) -> Unit) {
    TextArea(
        value = msg,
        attrs = {
            classes("form-control")
            placeholder("じょじょっじょはなんて言うてる?")
            style {
                width(100.percent)
                maxWidth(maxWidth)
                margin(16.px)
            }
            onInput { event -> onMessageChanged.invoke(event.value) }
        },
    )
}

//  出力ボタン
@Composable
private fun OutputButton(onButtonClicked: () -> Unit) {
    Button(
        attrs = {
            classes("btn", "btn-primary")
            style { margin(8.px) }
            onClick { onButtonClicked.invoke() }
        },
    ) {
        Text("↓ 画像出力")
    }
}

//  出力画像
@Composable
private fun OutputImg(id: String, maxWidth: CSSNumeric) {
    Img(
        src = "",
        attrs = {
            id(id)
            style {
                margin(16.px)
                maxWidth(maxWidth)
                display(DisplayStyle.Block)
            }
        },
    )
}
