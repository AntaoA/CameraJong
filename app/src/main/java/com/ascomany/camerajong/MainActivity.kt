package com.ascomany.camerajong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ascomany.camerajong.engine.ScoringEngine
import com.ascomany.camerajong.ui.manual.ManualScorerScreen
import com.ascomany.camerajong.ui.manual.ManualScorerViewModel
import com.ascomany.camerajong.ui.theme.CameraJongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialisation du moteur et du ViewModel
        val engine = ScoringEngine()
        val viewModel = ManualScorerViewModel(engine)

        setContent {
            // 2. Application du thème (vérifiez que le nom correspond à votre projet)
            CameraJongTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Appel de l'écran principal
                    ManualScorerScreen(viewModel)
                }
            }
        }
    }
}