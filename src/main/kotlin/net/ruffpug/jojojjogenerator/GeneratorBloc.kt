package net.ruffpug.jojojjogenerator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 生成器のBLoC
 */
internal class GeneratorBloc {

    //  発言メッセージ
    private val _message = MutableStateFlow(value = "")

    /**
     * 発言メッセージ
     */
    val message: StateFlow<String> = this._message.asStateFlow()

    /**
     * 発言メッセージが変更されたとき。
     */
    fun onMessageChanged(msg: String) {
        this._message.value = msg
    }

    /**
     * 終了処理を行う。
     */
    fun dispose() = Unit
}
