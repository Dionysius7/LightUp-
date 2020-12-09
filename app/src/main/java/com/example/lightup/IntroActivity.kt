package com.example.lightup

import android.Manifest
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntro2Fragment
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPagerBuilder

private lateinit var manager:PreferencesManager

class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = PreferencesManager(this)

        if(manager.isFirstRun()){
            showIntroSlides()
        } else{
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun showIntroSlides(){
        manager.setFirstRun()
        val slideOne = SliderPagerBuilder()
            .title("Step 1")
            .description("Search for bluetooth device")
            .imageDrawable(R.drawable.intro1)
            .bgColor(Color.parseColor("#4d74d9"))
            .build()
        val slideTwo = SliderPagerBuilder()
            .title("Step 2")
            .description("Connect with smartphone")
            .imageDrawable(R.drawable.intro2)
                .bgColor(Color.parseColor("#4d74d9"))
            .build()
        val slideThree = SliderPagerBuilder()
            .title("Step 3")
            .description("Customize your lights!")
            .imageDrawable(R.drawable.intro3)
                .bgColor(Color.parseColor("#4d74d9"))
            .build()

        addSlide(AppIntroFragment.newInstance(slideOne))
        addSlide(AppIntroFragment.newInstance(slideTwo))
        addSlide(AppIntroFragment.newInstance(slideThree))
        setFlowAnimation()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this,MainActivity::class.java))
    }
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this,MainActivity::class.java))
    }
}