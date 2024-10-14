package com.kos.boxdrawe.corutine

import kotlinx.coroutines.Dispatchers

object DispatcherList {
    val IO = Dispatchers.IO
    val Default = Dispatchers.Default
    val Unconfined = Dispatchers.Unconfined
    val Main = Dispatchers.Main
    val Creator = DefaultFigureScheduler
}

