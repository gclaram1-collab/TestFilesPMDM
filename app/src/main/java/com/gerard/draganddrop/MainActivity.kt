package com.gerard.draganddrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gerard.draganddrop.ui.theme.DragAndDropTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragAndDropTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DragImageScreen2(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DragImageScreen(modifier: Modifier = Modifier) {

    // 1. Estat de posició
    // aqui Offset es una estructura de dades ja pensada per guardar coordinades.
    var posicioImatge by remember { mutableStateOf( Offset(0f, 0f))}
    // una altra opció seria: (pero s'acostuma a fer com a dalt).
    // var offsetX by rememberSaveable { mutableStateOf(0f) }
    // var offsetY by rememberSaveable { mutableStateOf(0f) }
    // patata

    // 2. Contenidor principal
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .border(4.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // 3. Imatge draggable
        Image(
            painter = painterResource(R.drawable.spiderman),
            contentDescription = null,
            modifier = Modifier
                // Offset reactiu
                .offset {
                    IntOffset(posicioImatge.x.toInt(), posicioImatge.y.toInt())
                }
                // Detector de gestos
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        // compte, això es una suma
                        posicioImatge += Offset(dragAmount.x, dragAmount.y)
                        // si haguessim definit x i y com variables independents
                        //offsetX += dragAmount.x
                        //offsetY += dragAmount.y
                    }
                }
        )
    }
}

@Preview
@Composable
fun previewDragImageScreen() {
    DragImageScreen();
}


@Composable
fun DragImageScreen2(modifier: Modifier = Modifier) {

    // ----- ESTATS -----
    // escala → mida de la imatge (1f = mida normal)
    var escala by remember { mutableStateOf(1f) }

    // rotacio → graus de gir de la imatge
    var rotacio by remember { mutableStateOf(0f) }

    // posicio → desplaçament X i Y de la imatge
    var posicio by remember { mutableStateOf(Offset.Zero) }

    // animem l’escala perquè el zoom sigui suau
    val escalaAnimada by animateFloatAsState(escala)

    // ----- CONTENIDOR PRINCIPAL -----
    Box(
        modifier = modifier
            .fillMaxSize()              // ocupa tota la pantalla
            .background(Color.LightGray) // color de fons
            .border(4.dp, Color.Black),  // vora negra
        contentAlignment = Alignment.Center // contingut centrat inicialment
    ) {

        // ----- IMATGE INTERACTIVA -----
        Image(
            painter = painterResource(R.drawable.spiderman),
            contentDescription = null,
            modifier = Modifier

                // graphicsLayer permet:
                // - escalar
                // - rotar
                // - traslladar (moure)
                .graphicsLayer(
                    scaleX = escalaAnimada,   // zoom horitzontal
                    scaleY = escalaAnimada,   // zoom vertical
                    rotationZ = rotacio,      // rotació en graus
                    translationX = posicio.x, // moviment X
                    translationY = posicio.y,  // moviment Y
                    // transformació des del centre
                    transformOrigin = TransformOrigin.Center
                )

                // ----- GESTOS MULTITÀCTILS -----
                // detectTransformGestures gestiona:
                // - pan (arrossegar)
                // - zoom (pinça)
                // - rotation (gir amb 2 dits)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotation ->

                        // LIMITEM EL ZOOM entre 0.5 i 3
                        escala = (escala * zoom).coerceIn(0.5f, 3f)

                        // sumem la rotació detectada
                        rotacio += rotation

                        // desplaçament - versió standard
                        // sumem el desplaçament detectat
                        // posicio += pan

                        // desplaçament - versió corregint el angle.
                        // convertim graus a radians
                        val radians = Math.toRadians(rotacio.toDouble())
                        // corregim el pan perquè segueixi la pantalla
                        val panCorregit = Offset(
                            x = pan.x * cos(radians).toFloat() - pan.y * sin(radians).toFloat(),
                            y = pan.x * sin(radians).toFloat() + pan.y * cos(radians).toFloat()
                        )

                        posicio += panCorregit

                    }
                }

                // ----- DOBLE TAP RESET -----
                // detectTapGestures escolta tocs ràpids
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            // tornem als valors inicials
                            escala = 1f
                            rotacio = 0f
                            posicio = Offset.Zero
                        }
                    )
                }
        )
    }
}

fun returnName(name:String): String {
    return name
}
