package com.kos.boxdrawer.detal.grid

import vectors.Vec2

object GridRazvertka {

    fun findSharedEdge(loop: Loop, development: List<Vec2>): Pair<Vec2, Vec2>? {
        val loopVec2 = loop.toVec2List()
        for (i in loopVec2.indices) {
            val p1 = loopVec2[i]
            val p2 = loopVec2[(i + 1) % loopVec2.size]
            if (p1 in development && p2 in development) {
                return Pair(p1, p2)
            }
        }
        return null
    }


    fun developLoops(loops: Set<Loop>): List<Vec2> {
        val development = mutableListOf<Vec2>()
        val usedLoops = mutableSetOf<Loop>()

        fun addLoopToDevelopment(loop: Loop) {
            if (loop !in usedLoops) {
                if (development.isEmpty()) {
                    development.addAll(loop.toVec2List())
                } else {
                    val sharedEdge = findSharedEdge(loop, development)
                    if (sharedEdge != null) {
                        val (p1, p2) = sharedEdge
                        val loopVec2 = loop.toVec2List()
                        val loopStartIndex = loopVec2.indexOf(p1)
                        val devStartIndex = development.indexOf(p1)

                        val alignedLoop = if (development[devStartIndex + 1] == p2) {
                            loopVec2.subList(loopStartIndex, loopVec2.size) + loopVec2.subList(0, loopStartIndex)
                        } else {
                            loopVec2.subList(loopStartIndex, loopVec2.size).asReversed() + loopVec2.subList(0, loopStartIndex).asReversed()
                        }

                        for (point in alignedLoop) {
                            if (point !in development) {
                                development.add(point)
                            }
                        }
                    } else {
                        // Handle cases where there's no shared edge (e.g., disconnected components)
                        // ... You might want to add the loop separately or try different starting points
                    }
                }
                usedLoops.add(loop)
            }
        }

        val startingLoop = loops.first()
        addLoopToDevelopment(startingLoop)

        while (usedLoops.size < loops.size) {
            val nextLoop = loops.find { it !in usedLoops && findSharedEdge(it, development) != null }
            if (nextLoop != null) {
                addLoopToDevelopment(nextLoop)
            } else {
                // Handle cases where there are disconnected components
                // ... You might want to add the loop separately or try different starting points
            }
        }

        return development
    }

}