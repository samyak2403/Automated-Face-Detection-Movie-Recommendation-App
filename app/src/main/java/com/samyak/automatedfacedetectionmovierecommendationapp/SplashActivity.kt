package com.samyak.automatedfacedetectionmovierecommendationapp

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.progressindicator.CircularProgressIndicator

class SplashActivity : AppCompatActivity() {

    private lateinit var logoImageView: ImageView
    private lateinit var appNameTextView: TextView
    private lateinit var taglineTextView: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var backgroundGradient: View

    companion object {
        private const val SPLASH_DELAY = 3000L // 3 seconds
        private const val ANIMATION_DURATION = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Make it fullscreen
        setupFullscreen()
        
        // Initialize views
        initializeViews()
        
        // Start animations
        startAnimations()
        
        // Navigate to main activity after delay
        navigateToMainActivity()
    }

    private fun setupFullscreen() {
        // Hide system UI for immersive experience
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        // Make status bar transparent
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }

    private fun initializeViews() {
        logoImageView = findViewById(R.id.logo_image_view)
        appNameTextView = findViewById(R.id.app_name_text_view)
        taglineTextView = findViewById(R.id.tagline_text_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        backgroundGradient = findViewById(R.id.background_gradient)

        // Set initial states for animations
        logoImageView.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
        }
        
        appNameTextView.apply {
            alpha = 0f
            translationY = 100f
        }
        
        taglineTextView.apply {
            alpha = 0f
            translationY = 50f
        }
        
        loadingIndicator.apply {
            alpha = 0f
            scaleX = 0f
            scaleY = 0f
        }
    }

    private fun startAnimations() {
        // Background fade in
        animateBackgroundFadeIn()
        
        // Logo animation with delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateLogo()
        }, 200)
        
        // App name animation with delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateAppName()
        }, 800)
        
        // Tagline animation with delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateTagline()
        }, 1200)
        
        // Loading indicator animation with delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateLoadingIndicator()
        }, 1600)
        
        // Start logo pulse animation
        Handler(Looper.getMainLooper()).postDelayed({
            startLogoPulseAnimation()
        }, 2000)
    }

    private fun animateBackgroundFadeIn() {
        val fadeIn = ObjectAnimator.ofFloat(backgroundGradient, "alpha", 0f, 1f)
        fadeIn.duration = 800
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.start()
    }

    private fun animateLogo() {
        val scaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0f, 1.2f, 1f)
        val alphaAnimator = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f)
        val rotationAnimator = ObjectAnimator.ofFloat(logoImageView, "rotation", 0f, 360f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator, rotationAnimator)
        animatorSet.duration = ANIMATION_DURATION
        animatorSet.interpolator = OvershootInterpolator(1.5f)
        animatorSet.start()
    }

    private fun animateAppName() {
        val alphaAnimator = ObjectAnimator.ofFloat(appNameTextView, "alpha", 0f, 1f)
        val translationAnimator = ObjectAnimator.ofFloat(appNameTextView, "translationY", 100f, 0f)
        val scaleXAnimator = ObjectAnimator.ofFloat(appNameTextView, "scaleX", 0.8f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(appNameTextView, "scaleY", 0.8f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, translationAnimator, scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 800
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun animateTagline() {
        val alphaAnimator = ObjectAnimator.ofFloat(taglineTextView, "alpha", 0f, 1f)
        val translationAnimator = ObjectAnimator.ofFloat(taglineTextView, "translationY", 50f, 0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, translationAnimator)
        animatorSet.duration = 600
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun animateLoadingIndicator() {
        val scaleXAnimator = ObjectAnimator.ofFloat(loadingIndicator, "scaleX", 0f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(loadingIndicator, "scaleY", 0f, 1f)
        val alphaAnimator = ObjectAnimator.ofFloat(loadingIndicator, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator)
        animatorSet.duration = 500
        animatorSet.interpolator = OvershootInterpolator(1.2f)
        animatorSet.start()
    }

    private fun startLogoPulseAnimation() {
        val scaleUpX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 1.1f)
        val scaleUpY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 1.1f)
        val scaleDownX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1.1f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1.1f, 1f)

        val pulseUp = AnimatorSet()
        pulseUp.playTogether(scaleUpX, scaleUpY)
        pulseUp.duration = 500
        pulseUp.interpolator = AccelerateDecelerateInterpolator()

        val pulseDown = AnimatorSet()
        pulseDown.playTogether(scaleDownX, scaleDownY)
        pulseDown.duration = 500
        pulseDown.interpolator = AccelerateDecelerateInterpolator()

        val pulseAnimator = AnimatorSet()
        pulseAnimator.playSequentially(pulseUp, pulseDown)
        pulseAnimator.start()
    }

    private fun navigateToMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            // Fade out animation before transitioning
            animateExit()
        }, SPLASH_DELAY)
    }

    private fun animateExit() {
        val fadeOutViews = listOf(logoImageView, appNameTextView, taglineTextView, loadingIndicator)
        val animators = mutableListOf<Animator>()

        fadeOutViews.forEach { view ->
            val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f)
            val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
            animators.addAll(listOf(alphaAnimator, scaleXAnimator, scaleYAnimator))
        }

        val exitAnimatorSet = AnimatorSet()
        exitAnimatorSet.playTogether(animators)
        exitAnimatorSet.duration = 500
        exitAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        
        exitAnimatorSet.start()

        // Navigate to main activity after exit animation
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            
            // Custom transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 500)
    }

    override fun onBackPressed() {
        // Disable back button during splash screen
        // Do nothing
    }
} 