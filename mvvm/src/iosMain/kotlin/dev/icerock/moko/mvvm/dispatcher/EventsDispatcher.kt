/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.mvvm.dispatcher

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.ref.WeakReference

actual class EventsDispatcher<ListenerType : Any>() {
    private var weakListener: WeakReference<ListenerType>? = null

    var listener: ListenerType?
        get() = weakListener?.get()
        set(value) {
            weakListener = if (value == null) {
                null
            } else {
                WeakReference(value)
            }
        }

    constructor(listener: ListenerType) : this() {
        this.listener = listener
    }

    actual fun dispatchEvent(block: ListenerType.() -> Unit) {
        val listener = weakListener?.get() ?: return
        val mainQueue = dispatch_get_main_queue()
        dispatch_async(mainQueue) {
            block(listener)
        }
    }
}
