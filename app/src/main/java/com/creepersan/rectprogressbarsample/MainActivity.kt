package com.creepersan.rectprogressbarsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.creepersan.rectprogressbar.RectProgressBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ids = arrayOf(
            progressbar_01,
            progressbar_02,
            progressbar_03,
            progressbar_04,
            progressbar_05,
            progressbar_06
        )

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                ids.forEach {
                    it.setProgress(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        progressbar_01.setTextDecorator(object : RectProgressBar.TextDecorator{
            override fun onDrawText(progress: Int): String {
                return ""
            }
        })

        progressbar_03.setTextDecorator(object : RectProgressBar.TextDecorator{
            override fun onDrawText(progress: Int): String {
                return when{
                    progress >= 99 -> {
                        "Done"
                    }
                    progress > 66 -> {
                        "Almost get it"
                    }
                    progress > 33 -> {
                        "Working"
                    }
                    else -> {
                        "Preparing"
                    }
                }
                return "IncompressibleValue->$progress"
            }
        })

        progressbar_04.setTextDecorator(object : RectProgressBar.TextDecorator{
            override fun onDrawText(progress: Int): String {
                return "IncompressibleValue->$progress"
            }
        })

        progressbar_05.setTextDecorator(object : RectProgressBar.TextDecorator{
            override fun onDrawText(progress: Int): String {
                return "Value->$progress"
            }
        })

        progressbar_06.setTextDecorator(object : RectProgressBar.TextDecorator{
            override fun onDrawText(progress: Int): String {
                return "CompressibleValue->$progress"
            }
        })
    }
}
