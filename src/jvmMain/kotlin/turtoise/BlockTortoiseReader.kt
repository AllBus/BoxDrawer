package turtoise

import turtoise.memory.TortoiseMemory
import turtoise.parser.TortoiseParserStackItem
import kotlin.math.max

object BlockTortoiseReader {
    const val HOLE_UP = 0.05
    const val HOLE_DROP = 0.5

    fun readDrawerSettings(line:TortoiseParserStackItem?, memory: TortoiseMemory, ds:DrawerSettings):DrawerSettings{
        return line?.let{ block ->
            val bw = valueAt(block, 0, memory, ds.boardWeight)
            DrawerSettings(
                boardWeight = bw,
                holeDrop = valueAt(block, 1, memory, ds.holeDrop),
                holeWeight = valueAt(block,  2, memory, bw+ HOLE_UP),
                zigDrop = valueAt(block, "z", memory, ds.zigDrop),
                appoximationSize = max(1, valueAt(block, "a", memory, ds.appoximationSize.toDouble()).toInt()),
                holeDropHeight = valueAt(block, "h.1", memory, ds.holeDropHeight),
                holeOffset = valueAt(block, "h.2", memory, ds.holeOffset),
            )
        }?: ds
    }

    fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: Int,
        memory:TortoiseMemory,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }

    fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: String,
        memory:TortoiseMemory,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }
}