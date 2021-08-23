package com.udacity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

const val standardAnimTime = 1500L

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var loadingColor = 0
    private var buttonColor = 0
    private var loadingIconColor = 0
    private var textSize = 55f
    private var text = context.getString(R.string.download)
    private var textColor = 0
    private var buttonRect = Rect(0,0,0,0)
    private val textBounds = Rect()
    private var loadingRadius = 0f
    private var loadingAngle = 0f
    private var circleRect = RectF(0f, 0f, 0f, 0f)
    private var buttonAnimRight = 0
    private var buttonAnimRect = Rect(0,0,0,0)
    val set = AnimatorSet()


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }




    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = width
        heightSize = height
        buttonRect = Rect(0,0, widthSize, heightSize)
        buttonAnimRect = Rect(0,0, widthSize, heightSize)
        loadingRadius = height/4f
        circleRect.top = heightSize/2f - loadingRadius
        circleRect.bottom = heightSize/2f + loadingRadius
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, ContextCompat.getColor(context, R.color.colorPrimaryDark))
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, ContextCompat.getColor(context, R.color.colorPrimary))
            textSize = getFloat(R.styleable.LoadingButton_textSize, 55f)
            text = getText(R.styleable.LoadingButton_text).toString()
            textColor = getColor(R.styleable.LoadingButton_loadingColor, ContextCompat.getColor(context, android.R.color.white))
            loadingIconColor = getColor(R.styleable.LoadingButton_loadingIconColor, ContextCompat.getColor(context, R.color.colorAccent))
        }
        paint.textSize = textSize

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { cv->
            //paint button background
            paint.color = buttonColor
            cv.drawRect(buttonRect, paint)

            //paint loading background
            paint.color = loadingColor
            buttonAnimRect.right = buttonAnimRight
            cv.drawRect(buttonAnimRect, paint)

            //paint loading text
            paint.color = textColor
            paint.getTextBounds(text, 0, text.length, textBounds)
            cv.drawText(text, widthSize/2f, heightSize/2 - textBounds.exactCenterY(), paint)

            //paint loading circle
            paint.color = loadingIconColor
            //cv.drawCircle(widthSize/2f + textBounds.right/2f + 4 + loadingRadius, heightSize/2f, loadingRadius, paint)
            circleRect.left = widthSize/2f + textBounds.right/2f + 4
            circleRect.right = circleRect.left + (2*loadingRadius)
            cv.drawArc(circleRect, 0f, loadingAngle, true, paint )

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private var wasCancel = false
    fun startDownload(animTime: Long = standardAnimTime) {
        wasCancel = false
        val circleAnimation = getCircleAnimation()
        val buttonAnimation = getButtonAnimation()
        set.apply {
            playTogether(circleAnimation, buttonAnimation)
            duration = if(animTime <= 0) standardAnimTime else animTime
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {
                    text = context.getString(R.string.we_are_loading)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if(!wasCancel) {
                        resetAnimationValues()
                    }
                }
                override fun onAnimationCancel(animation: Animator?) {
                    wasCancel = true
                }
                override fun onAnimationRepeat(animation: Animator?) {}
            })
        }
        set.start()
    }

    fun cancelAnimation(){
        wasCancel = true
        set.cancel()
    }

    private fun resetAnimationValues(){
        text = context.getString(R.string.download)
        loadingAngle = 0f
        buttonAnimRight = 0

        invalidate()
    }


    fun getCircleAnimation(): ValueAnimator? {
        return ValueAnimator.ofFloat(loadingAngle, 360f).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                loadingAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
    }

    fun getButtonAnimation(): ValueAnimator? {
        return ValueAnimator.ofInt(buttonAnimRight, widthSize).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                buttonAnimRight = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
    }


}