package com.lenibonje.scoreindicator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scoreIndicator = findViewById<ScoreIndicator>(R.id.score_indicator)
        scoreIndicator.animateRotation(84F, 2000L)
    }
}