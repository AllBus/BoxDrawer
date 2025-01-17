package com.kos.boxdrawer.presentation.ai

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.fireworks1
import com.kos.boxdrawer.generated.resources.party_popper
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class FireworkParticle(
    var x: Float,
    var y: Float,
    var x2: Float,
    var y2: Float,
    var color: Color,
    var strokeWidth: Float,
    var radius: Float,
    var alpha: Float,
    var dx: Float, // Velocity in x direction
    var dy: Float, // Velocity in y direction
    var fontSize: TextUnit, // Added fontSize
    var angle: Float = 0f, // Added angle

)

data class Star(
    var x: Float,
    var y: Float,
    var color: Color,
    var radius: Float,
    var alpha: Float
)

fun generateStars(particles: List<FireworkParticle>): List<Star> {
    return particles.filter { it.alpha < 0.1f }.map { particle ->
        Star(
            x = particle.x2,
            y = particle.y2,
            color = particle.color,
            radius = Random.nextFloat() * 5f + 2f, // Adjust star size
            alpha = 1f
        )
    }
}

//fun generateFireworkParticlesCircle(): List<FireworkParticle> {
//    val centerX = 300f // Adjust center position
//    val centerY = 300f
//    val numParticles = 50
//    val colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Cyan, Color.Magenta)
//
//    return (1..numParticles).map {
//        val angle = (it * 360f / numParticles) * (Math.PI / 180).toFloat()
//        val speed = Random.nextFloat() * 5f + 2f // Adjust speed
//        FireworkParticle(
//            x = centerX,
//            y = centerY,
//            color = colors.random(),
//            radius = Random.nextFloat() * 5f + 2f, // Adjust size
//            alpha = 1f,
//            dx = speed * cos(angle),
//            dy = speed * sin(angle),
//            x2 = centerX,
//            y2 = centerY,
//            strokeWidth = 1f,
//            fontSize = 12.sp
//        )
//    }
//}

fun generateFireworkParticles(): List<FireworkParticle> {
    val centerX = 300f // Adjust center position
    val centerY = 300f
    val numParticles = 50
    val colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Cyan, Color.Magenta)

    return (1..numParticles).map {
        val angle = (it * 360f / numParticles) * (Math.PI / 180).toFloat()
        val speed = Random.nextFloat() * 5f + 2f // Adjust speed
        FireworkParticle(
            x = centerX,
            y = centerY,
            x2 = centerX, // Initial end point is the same as start point
            y2 = centerY,
            color = colors.random(),
            strokeWidth = Random.nextFloat() * 3f + 1f, // Adjust stroke width
            alpha = 1f,
            dx = speed * cos(angle),
            dy = speed * sin(angle),
            fontSize = (Random.nextInt(20) + 20).sp,
            angle = Random.nextFloat()*2* Math.PI.toFloat(), // Initial angle
            radius = Random.nextFloat() * 0.5f
        )
    }
}


@Composable
fun FireworkAnimation(modifier: Modifier = Modifier) {
    var particles by remember { mutableStateOf(emptyList<FireworkParticle>()) }
    var stars by remember { mutableStateOf(emptyList<Star>()) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Generate new particles for the firework explosion
            particles = generateFireworkParticles()

            // Update particle positions and fade them out
            while (particles.isNotEmpty() && particles.any { it.alpha > 0f }) {
                particles = particles.map { particle ->

                    // Apply spiral motion
                    val angle = particle.angle + 0.1f // Adjust spiral speed
                    val radius = particle.radius + 0.5f // Adjust radius increase speed

                    particle.copy(
                        x = (particle.x + particle.x2)/2,
                        y = (particle.y +  particle.y2)/2,
                        x2 = particle.x2 + radius * cos(angle)+particle.dx,
                        y2 = particle.y2 + radius * sin(angle)+particle.dy,
                        angle = angle,
                        radius = radius,
                        alpha = particle.alpha - 0.01f // Adjust fade speed
                    )
                }.filter { it.alpha >0f }

                delay(16) // Update every 16ms (approximately 60fps)
            }

            delay(1000) // Delay before the next firework
        }
    }


    val tm = rememberTextMeasurer()
    val image = painterResource(Res.drawable.fireworks1)
    val image1 = imageResource(Res.drawable.party_popper)
    Box(modifier = modifier) {
        var currentRotation by remember { mutableFloatStateOf(-45f) }
        var currentScale by remember { mutableFloatStateOf(1f) }

        val rotation = remember { Animatable(currentRotation) }
        val scale = remember { Animatable(currentScale) }
        val alphaValue = remember { Animatable(1f) }

        LaunchedEffect(true) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        }
        LaunchedEffect(true) {
            alphaValue.animateTo(
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(1500)
                )
            ) {
                currentRotation = value
            }
        }

        LaunchedEffect(true) {
            scale.animateTo(
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            ){
                currentScale = value
            }
        }
       // particles.forEach { particle ->
        Image(
                painter = image,
                contentDescription = null,
                alpha = alphaValue.value,
                modifier = Modifier.size(200.dp).offset(100.dp, 100.dp).rotate(rotation.value).scale(scale.value)
        )

//        Canvas(modifier = Modifier.fillMaxSize()) {
//            particles.forEach { particle ->
//
//                translate(
//                    particle.x2 - particle.fontSize.value / 2, particle.y2 - particle.fontSize.value / 2
//                ) {
//                    val angleScale = 1f + (sin(particle.angle) * 0.2f) //
//                    val pulsationScale = 1f + (sin(particle.alpha * 3f) * 2.4f) // Adjust pulsation speed and amplitude
//                    val scale = angleScale * pulsationScale
//                    drawImage(
//                        image = image,
//                        dstOffset = IntOffset(
//                            x = (particle.x2 - (image.width * scale) / 2).toInt(),
//                            y = (particle.y2 - (image.height * scale) / 2).toInt()
//                        ), dstSize = IntSize(
//                            width = (image.width * scale).toInt(),
//                            height = (image.height * scale).toInt()
//                        ),
//                        //alpha = particle.alpha
//                    )
//
//                }
//            }
//
//            stars.forEach { star ->
//                drawCircle(
//                    color = star.color,
//                    radius = star.radius,
//                    center = Offset(star.x, star.y),
//                    alpha = star.alpha
//                )
//            }
//        }


    }
}

