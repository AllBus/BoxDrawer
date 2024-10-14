package com.kos.boxdrawe.corutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object DefaultFigureScheduler : ExecutorCoroutineDispatcher(), Executor {

    private val default = LimitedFigureScheduler

    override val executor: Executor
        get() = this

    override fun execute(command: java.lang.Runnable) = dispatch(EmptyCoroutineContext, command)

    @ExperimentalCoroutinesApi
    override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
        // See documentation to Dispatchers.IO for the rationale
        return LimitedFigureScheduler.limitedParallelism(parallelism)
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        default.dispatch(context, block)
    }

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) {
        default.dispatchYield(context, block)
    }

    override fun close() {
        error("Cannot be invoked on Dispatchers.Figure")
    }

    override fun toString(): String = "Dispatchers.Figure"
}

private object LimitedFigureScheduler : CoroutineDispatcher() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun dispatch(context:CoroutineContext, block: Runnable) {
        executor.execute(block)
    }

}