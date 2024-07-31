package turtoise

import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParserStackItem
import kotlin.math.max

object BlockTortoiseReader {
    const val HOLE_UP = 0.05
    const val HOLE_DROP = 0.5

    fun readDrawerSettings(
        line: TortoiseParserStackItem?,
        memory: TortoiseMemory,
        ds: DrawerSettings
    ): DrawerSettings {
        return line?.let { block ->
            val bw = valueAt(block, 0, memory, ds.boardWeight)
            DrawerSettings(
                boardWeight = bw,
                holeDrop = valueAt(block, 1, memory, ds.holeDrop),
                holeWeight = valueAt(block, 2, memory, bw + HOLE_UP),
                zigDrop = valueAt(block, "z", memory, ds.zigDrop),
                appoximationSize = max(
                    1,
                    valueAt(block, "a", memory, ds.appoximationSize.toDouble()).toInt()
                ),
                holeDropHeight = valueAt(block, "h.1", memory, ds.holeDropHeight),
                holeOffset = valueAt(block, "h.2", memory, ds.holeOffset),
            )
        } ?: ds
    }

    fun readZigInfo(
        block: TortoiseParserStackItem?,
        memory: TortoiseMemory,
        ds: DrawerSettings
    ): ZigzagInfo {
        return ZigzagInfo(
            width = valueAt(block, 1, memory, 15.0),
            delta = valueAt(block, 0, memory, 35.0),
            height = valueAt(
                block,
                2,
                memory,
                ds.boardWeight,
            ),
            fromCorner = true,
        )
    }

    fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: Int,
        memory: TortoiseMemory,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }

    fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: String,
        memory: TortoiseMemory,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }

    fun putZigZagInfo(zihe: ZigzagInfo, memory: TortoiseMemory){
        memory.assign(MemoryKey("zigzagWidth"), zihe.width)
        memory.assign(MemoryKey("zigzagHeight"), zihe.height)
        memory.assign(MemoryKey("zigzagDelta"), zihe.delta)
        memory.assign(MemoryKey("zigzagDrop"), zihe.drop)
    }

    fun putReverse(reverse:Boolean, memory: TortoiseMemory){
        memory.assign(MemoryKey("reverse"), if (reverse) -1.0 else 1.0)
    }
}