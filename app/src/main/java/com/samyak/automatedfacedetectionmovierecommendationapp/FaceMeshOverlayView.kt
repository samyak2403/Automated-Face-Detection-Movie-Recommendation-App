package com.samyak.automatedfacedetectionmovierecommendationapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.*

class FaceMeshOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint objects for different elements
    private val faceBoxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val landmarkPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val confidenceBgPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val cornerPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    // Face detection data
    private var faces: List<Face> = emptyList()
    private var previewWidth = 0
    private var previewHeight = 0
    private var isFrontFacing = true
    private var detectionMode = "CLOSE_RANGE"
    
    // Animation properties
    private var animationProgress = 0f
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { 
            animationProgress = it.animatedValue as Float
            invalidate()
        }
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }

    // Colors for different states
    private val excellentColor = Color.parseColor("#4CAF50") // Green
    private val goodColor = Color.parseColor("#FF9800")      // Orange
    private val poorColor = Color.parseColor("#F44336")      // Red
    private val trackingColor = Color.parseColor("#2196F3")  // Blue

    fun setFaces(faces: List<Face>, previewWidth: Int, previewHeight: Int) {
        this.faces = faces
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        
        // Start animation if faces detected, stop if no faces
        if (faces.isNotEmpty() && !animator.isRunning) {
            animator.start()
        } else if (faces.isEmpty() && animator.isRunning) {
            animator.cancel()
        }
        
        invalidate()
    }

    fun setCameraFacing(frontFacing: Boolean) {
        isFrontFacing = frontFacing
    }

    fun setDetectionMode(mode: String) {
        detectionMode = mode
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (faces.isEmpty()) return

        val scaleX = width.toFloat() / previewWidth
        val scaleY = height.toFloat() / previewHeight
        
        // Calculate offset to center the preview while maintaining aspect ratio
        val offsetX = (width - previewWidth * scaleX) / 2f
        val offsetY = (height - previewHeight * scaleY) / 2f

        faces.forEachIndexed { index, face ->
            drawFaceDetection(canvas, face, scaleX, scaleY, offsetX, offsetY, index)
        }
    }

    private fun drawFaceDetection(
        canvas: Canvas,
        face: Face,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        faceIndex: Int
    ) {
        val bounds = face.boundingBox
        
        // Calculate face quality based on size and position
        val faceQuality = calculateFaceQuality(face, previewWidth, previewHeight)
        val color = getColorForQuality(faceQuality)
        
        // Apply transformations for front camera mirroring
        val left = if (isFrontFacing) {
            width - (bounds.right * scaleX + offsetX)
        } else {
            bounds.left * scaleX + offsetX
        }
        val right = if (isFrontFacing) {
            width - (bounds.left * scaleX + offsetX)
        } else {
            bounds.right * scaleX + offsetX
        }
        val top = bounds.top * scaleY + offsetY
        val bottom = bounds.bottom * scaleY + offsetY
        
        val rectF = RectF(left, top, right, bottom)
        
        // Draw face box with corner brackets
        drawCornerBrackets(canvas, rectF, color)
        
        // Draw face landmarks if available
        drawFaceLandmarks(canvas, face, scaleX, scaleY, offsetX, offsetY, color)
        
        // Draw confidence and info overlay
        drawFaceInfo(canvas, face, rectF, faceQuality, faceIndex)
        
        // Draw center crosshair for primary face
        if (faceIndex == 0) {
            drawCenterCrosshair(canvas, rectF, color)
        }
    }

    private fun drawCornerBrackets(canvas: Canvas, rect: RectF, color: Int) {
        cornerPaint.color = color
        
        // Add pulsing effect
        val alpha = (128 + (127 * sin(animationProgress * 2 * PI)).toInt()).coerceIn(128, 255)
        cornerPaint.alpha = alpha
        
        val cornerLength = minOf(rect.width(), rect.height()) * 0.15f
        val strokeWidth = cornerPaint.strokeWidth
        
        // Top-left corner
        canvas.drawLine(rect.left, rect.top + cornerLength, rect.left, rect.top, cornerPaint)
        canvas.drawLine(rect.left, rect.top, rect.left + cornerLength, rect.top, cornerPaint)
        
        // Top-right corner
        canvas.drawLine(rect.right - cornerLength, rect.top, rect.right, rect.top, cornerPaint)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLength, cornerPaint)
        
        // Bottom-left corner
        canvas.drawLine(rect.left, rect.bottom - cornerLength, rect.left, rect.bottom, cornerPaint)
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLength, rect.bottom, cornerPaint)
        
        // Bottom-right corner
        canvas.drawLine(rect.right - cornerLength, rect.bottom, rect.right, rect.bottom, cornerPaint)
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerLength, cornerPaint)
    }

    private fun drawFaceLandmarks(
        canvas: Canvas,
        face: Face,
        scaleX: Float,
        scaleY: Float,
        offsetX: Float,
        offsetY: Float,
        color: Int
    ) {
        landmarkPaint.color = color
        landmarkPaint.alpha = 180
        
        val landmarks = listOf(
            FaceLandmark.LEFT_EYE,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.NOSE_BASE,
            FaceLandmark.MOUTH_LEFT,
            FaceLandmark.MOUTH_RIGHT,
            FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_EAR
        )
        
        landmarks.forEach { landmarkType ->
            face.getLandmark(landmarkType)?.let { landmark ->
                val x = if (isFrontFacing) {
                    width - (landmark.position.x * scaleX + offsetX)
                } else {
                    landmark.position.x * scaleX + offsetX
                }
                val y = landmark.position.y * scaleY + offsetY
                
                canvas.drawCircle(x, y, 8f, landmarkPaint)
            }
        }
    }

    private fun drawFaceInfo(canvas: Canvas, face: Face, rect: RectF, quality: FaceQuality, index: Int) {
        val padding = 16f
        val lineHeight = 32f
        var currentY = rect.top - padding
        
        // Face info background
        val infoHeight = lineHeight * 4 + padding * 2
        val infoRect = RectF(rect.left, currentY - infoHeight, rect.right, currentY)
        
        backgroundPaint.color = Color.BLACK
        backgroundPaint.alpha = 180
        canvas.drawRoundRect(infoRect, 12f, 12f, backgroundPaint)
        
        currentY -= padding
        
        // Face ID
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        face.trackingId?.let { id ->
            canvas.drawText("Face #$id", rect.left + padding, currentY, textPaint)
        } ?: run {
            canvas.drawText("Face #${index + 1}", rect.left + padding, currentY, textPaint)
        }
        currentY -= lineHeight
        
        // Quality indicator
        val qualityColor = getColorForQuality(quality)
        textPaint.color = qualityColor
        textPaint.textSize = 24f
        canvas.drawText("${quality.name}: ${quality.score}%", rect.left + padding, currentY, textPaint)
        currentY -= lineHeight
        
        // Detection mode
        textPaint.color = Color.CYAN
        textPaint.textSize = 20f
        canvas.drawText("Mode: $detectionMode", rect.left + padding, currentY, textPaint)
        currentY -= lineHeight
        
        // Face size info
        textPaint.color = Color.YELLOW
        val faceSize = "${(rect.width()).toInt()}x${(rect.height()).toInt()}"
        canvas.drawText("Size: $faceSize", rect.left + padding, currentY, textPaint)
        
        // Emotion indicators (if available)
        drawEmotionIndicators(canvas, face, rect)
    }

    private fun drawEmotionIndicators(canvas: Canvas, face: Face, rect: RectF) {
        val centerX = rect.centerX()
        val bottomY = rect.bottom + 60f
        val iconSize = 40f
        val spacing = 50f
        
        var currentX = centerX - spacing
        
        // Smile indicator
        face.smilingProbability?.let { probability ->
            val emoji = if (probability > 0.5f) "😊" else "😐"
            textPaint.textSize = iconSize
            textPaint.color = if (probability > 0.5f) excellentColor else Color.GRAY
            canvas.drawText(emoji, currentX, bottomY, textPaint)
            
            // Probability text
            textPaint.textSize = 16f
            textPaint.color = Color.WHITE
            canvas.drawText("${(probability * 100).toInt()}%", currentX, bottomY + 30f, textPaint)
        }
        
        currentX += spacing
        
        // Eye open indicator
        val leftEyeProbability = face.leftEyeOpenProbability ?: 0f
        val rightEyeProbability = face.rightEyeOpenProbability ?: 0f
        val avgEyeOpen = (leftEyeProbability + rightEyeProbability) / 2f
        
        val eyeEmoji = if (avgEyeOpen > 0.5f) "👁️" else "😴"
        textPaint.textSize = iconSize
        textPaint.color = if (avgEyeOpen > 0.5f) excellentColor else poorColor
        canvas.drawText(eyeEmoji, currentX, bottomY, textPaint)
        
        textPaint.textSize = 16f
        textPaint.color = Color.WHITE
        canvas.drawText("${(avgEyeOpen * 100).toInt()}%", currentX, bottomY + 30f, textPaint)
    }

    private fun drawCenterCrosshair(canvas: Canvas, rect: RectF, color: Int) {
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val crosshairSize = 30f
        
        cornerPaint.color = color
        cornerPaint.strokeWidth = 4f
        cornerPaint.alpha = (180 + (75 * sin(animationProgress * 4 * PI)).toInt()).coerceIn(180, 255)
        
        // Draw crosshair
        canvas.drawLine(centerX - crosshairSize, centerY, centerX + crosshairSize, centerY, cornerPaint)
        canvas.drawLine(centerX, centerY - crosshairSize, centerX, centerY + crosshairSize, cornerPaint)
        
        // Draw center circle
        canvas.drawCircle(centerX, centerY, 8f, cornerPaint)
    }

    private fun calculateFaceQuality(face: Face, imageWidth: Int, imageHeight: Int): FaceQuality {
        val bounds = face.boundingBox
        val faceWidth = bounds.width()
        val faceHeight = bounds.height()
        val faceArea = faceWidth * faceHeight
        val imageArea = imageWidth * imageHeight
        val faceRatio = faceArea.toFloat() / imageArea.toFloat()
        
        // Check if face is well-positioned (not too close to edges)
        val margin = 0.1f
        val isWellPositioned = bounds.left > imageWidth * margin &&
                bounds.top > imageHeight * margin &&
                bounds.right < imageWidth * (1 - margin) &&
                bounds.bottom < imageHeight * (1 - margin)
        
        // Check face size appropriateness
        val isGoodSize = when (detectionMode) {
            "CLOSE_RANGE" -> faceRatio > 0.02f && faceRatio < 0.5f
            "FAR_RANGE" -> faceWidth >= 100 && faceHeight >= 100
            else -> faceRatio > 0.01f
        }
        
        // Calculate overall score
        var score = 50 // Base score
        
        if (isGoodSize) score += 30
        if (isWellPositioned) score += 20
        
        // Bonus for having landmarks
        if (face.allLandmarks.isNotEmpty()) score += 10
        
        // Bonus for clear emotions
        face.smilingProbability?.let { if (it > 0.7f || it < 0.3f) score += 10 }
        
        score = score.coerceIn(0, 100)
        
        return when {
            score >= 80 -> FaceQuality.EXCELLENT
            score >= 60 -> FaceQuality.GOOD
            score >= 40 -> FaceQuality.FAIR
            else -> FaceQuality.POOR
        }.apply { this.score = score }
    }

    private fun getColorForQuality(quality: FaceQuality): Int {
        return when (quality) {
            FaceQuality.EXCELLENT -> excellentColor
            FaceQuality.GOOD -> goodColor
            FaceQuality.FAIR -> Color.YELLOW
            FaceQuality.POOR -> poorColor
        }
    }

    enum class FaceQuality(var score: Int = 0) {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animator.isRunning) {
            animator.cancel()
        }
    }
}
