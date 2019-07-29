package com.creepersan.rectprogressbar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import androidx.annotation.ColorInt


class RectProgressBar : View {

    constructor(context:Context) : this(context, null)
    constructor(context:Context, attr:AttributeSet?) : this(context, attr, 0)
    constructor(context:Context, attr:AttributeSet?, defaultStyle:Int) : super(context, attr, defaultStyle){
        attr?.apply {
            val typedValue = context.obtainStyledAttributes(attr, R.styleable.RectProgressBar)
            mProgressColor = typedValue.getColor(R.styleable.RectProgressBar_progressColor, mProgressColor)
            mDefaultColor = typedValue.getColor(R.styleable.RectProgressBar_defaultColor, mDefaultColor)
            mDividerColor = typedValue.getColor(R.styleable.RectProgressBar_dividerColor, mDividerColor)
            mTextColor = typedValue.getColor(R.styleable.RectProgressBar_textColor, mTextColor)
            mDividerWidth = typedValue.getDimensionPixelSize(R.styleable.RectProgressBar_dividerWidth, mDividerWidth.toInt()).toFloat()
            mTextSize = typedValue.getDimensionPixelSize(R.styleable.RectProgressBar_textSize, mTextSize.toInt()).toFloat()
            mMax = typedValue.getInteger(R.styleable.RectProgressBar_max, mMax)
            mMin = typedValue.getInteger(R.styleable.RectProgressBar_min, mMin)
            mProgress = typedValue.getInteger(R.styleable.RectProgressBar_progress, mProgress)
            mIsCompressionText = typedValue.getBoolean(R.styleable.RectProgressBar_compressionText, mIsCompressionText)
            mOmitText = typedValue.getString(R.styleable.RectProgressBar_omit) ?: mOmitText
            typedValue.recycle()
        }
    }

    // 尺寸相关
    private var mDefaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360f, resources.displayMetrics)
    private var mDefaultHeight= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics)
    private var mDividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    private var mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
    // 颜色相关
    private var mProgressColor = Color.parseColor("#649BFE")
    private var mDefaultColor = Color.parseColor("#CFCFCF")
    private var mDividerColor = Color.parseColor("#F0F0F0")
    private var mTextColor = Color.parseColor("#FFFFFF")
    // 状态标志相关
    private var mIsCompressionText = false
    // 数值相关
    private var mMax = 100
    private var mMin = 0
    private var mProgress = 30
    private var mOmitText = "..."
    // 其他
    private var mTextDecorator : TextDecorator? = null
    // 画笔
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    /**
     * 测量大小
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measureSpec(widthMeasureSpec, mDefaultWidth),
            measureSpec(heightMeasureSpec, mDefaultHeight)
        )
    }

    /**
     * 测量控件在轴上的尺寸
     * @param sizeMeasureSpec 轴
     * @param defaultSize 默认大小
     * @return 最终确定的大小
     */
    private fun measureSpec(sizeMeasureSpec:Int, defaultSize:Float) : Int{
        val mode = MeasureSpec.getMode(sizeMeasureSpec)
        val size = MeasureSpec.getSize(sizeMeasureSpec)
        return when (mode) {
            MeasureSpec.AT_MOST -> {
                defaultSize.toInt()
            }
            MeasureSpec.EXACTLY -> {
                size
            }
            MeasureSpec.UNSPECIFIED -> {
                defaultSize.toInt()
            }
            else -> {
                defaultSize.toInt()
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas ?: return
        // 绘制底色
        mPaint.color = mDefaultColor
        canvas.drawRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            width - paddingRight.toFloat(),
            height - paddingBottom.toFloat(),
            mPaint
        )
        // 绘制进度
        val progress = (mProgress - mMin).toFloat() / (mMax - mMin).toFloat()
        val progressXAxisLocation = (width - paddingRight.toFloat()) - (1-progress) * (width - paddingLeft - paddingRight) // 从右到左的距离！
        mPaint.color = mProgressColor
        canvas.drawRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            progressXAxisLocation,
            height - paddingBottom.toFloat(),
            mPaint
        )
        // 绘制分割线
        mPaint.color = mDividerColor
        when{
            progressXAxisLocation > width-paddingRight-mDividerWidth/2 -> {
                canvas.drawRect(
                    width - mDividerWidth - paddingRight.toFloat(),
                    paddingTop.toFloat(),
                    width - paddingRight.toFloat(),
                    height - paddingBottom.toFloat(),
                    mPaint
                )
            }
            progressXAxisLocation < paddingLeft + mDividerWidth/2 -> {
                canvas.drawRect(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    paddingLeft.toFloat() + mDividerWidth,
                    height - paddingBottom.toFloat(),
                    mPaint
                )
            }
            else -> {
                canvas.drawRect(
                    progressXAxisLocation - mDividerWidth / 2,
                    paddingTop.toFloat(),
                    progressXAxisLocation + mDividerWidth / 2,
                    height - paddingBottom.toFloat(),
                    mPaint
                )
            }
        }
        // 绘制文本
        mPaint.textSize = mTextSize
        mPaint.color = mTextColor
        val fontMetrics = mPaint.fontMetrics
        if (fontMetrics.bottom - fontMetrics.top < height-paddingTop-paddingBottom){ // 是否高度足够绘制文字，如果不够，则不绘制
            val drawString = getCompressionText(
                mTextDecorator?.onDrawText(mProgress) ?: mProgress.toString(),
                (width-paddingLeft-paddingRight)*progress - mDividerWidth/2,
                mPaint
            )
            canvas.drawText(
                drawString,
                paddingLeft + ((width-paddingLeft-paddingRight)*progress-mDividerWidth/2)/2,
                paddingTop+(height-paddingTop-paddingBottom)/2-fontMetrics.ascent/2-fontMetrics.bottom/2,
                mPaint
            )
        }
    }

    /**
     * 获取压缩后的文本
     * @param origin 原来的文本
     * @param widthMax 最大宽度
     * @param paint 绘制的画笔（用于测量文本宽度）
     * @return 压缩后的文本
     */
    private fun getCompressionText(origin:String, widthMax:Float, paint:Paint):String{
        val originWidth = paint.measureText(origin)
        if (originWidth < widthMax){ // 是否文字长度超出空间？ 没有超出
            return origin
        }else if (!mIsCompressionText){ // 是否文字长度超出空间？ 超出 但是不压缩文字内容
            return ""
        }
        // 是否文字长度超出空间？ 超出 可以压缩文字内容
        var tmpText : String
        var tmpPrevText = ""
        for (i in 1 .. origin.length){
            tmpText = "${origin.substring(0, i/2)}$mOmitText${origin.substring(origin.length-i/2, origin.length)}"
            val tmpWidth = mPaint.measureText(tmpText)
            if (tmpWidth > widthMax){
                return tmpPrevText
            }else{
                tmpPrevText = tmpText
            }
        }
        return origin
    }

    /************ 以下的方法提供外部调用 ************/

    // 文本装饰器，对展示在进度条上的文本进行加工
    interface TextDecorator{
        /**
         * 对展示在进度条上的文本进行加工
         * @param progress 当前进度值
         * @return 加工后要显示的文本
         */
        fun onDrawText(progress:Int):String
    }

    /**
     * 设置进度
     * @param progress 进度
     */
    fun setProgress(progress:Int){
        mProgress = when{
            progress > mMax -> { mMax }
            progress < mMin -> { mMin }
            else -> progress
        }
        mProgress = progress
        invalidate()
    }

    /**
     * 设置文本装饰器
     * @param textDecorator 文本装饰器
     */
    fun setTextDecorator(textDecorator:TextDecorator){
        mTextDecorator = textDecorator
        invalidate()
    }

    /**
     * 设置最大值
     * @param max 最大值，不能小于等于最小值
     */
    fun setMax(max:Int){
        if (max <= mMin){
            throw IllegalArgumentException("最大值【${mMax}】不能小于等于最小值【$mMin】")
        }
        mMax = max
        invalidate()
    }

    /**
     * 设置最小值
     * @param min 最小值，不能大于等于最大值
     */
    fun setMin(min:Int){
        if (min >= mMax){
            throw IllegalArgumentException("最小值【${mMin}】不能大于等于最大值【$mMax】")
        }
        mMin = min
        invalidate()
    }

    /**
     * 设置进度颜色
     * @param color 进度颜色
     */
    fun setProgressColor(@ColorInt color:Int){
        mProgressColor = color
        invalidate()
    }

    /**
     * 设置底色
     * @param color 底色
     */
    fun setDefaultColor(@ColorInt color:Int){
        mDefaultColor = color
        invalidate()
    }

    /**
     * 设置分割线颜色
     * @param color 分割线颜色
     */
    fun setDividerColor(@ColorInt color:Int){
        mDividerColor = color
        invalidate()
    }

    /**
     * 设置文字颜色
     * @param color 文字颜色
     */
    fun setTextColor(@ColorInt color:Int){
        mTextColor = color
        invalidate()
    }

    /**
     * 设置文字尺寸
     * @param textSize 文字尺寸
     */
    fun setTextSize(textSize:Float){
        mTextSize = textSize
        invalidate()
    }

    /**
     * 设置分割线宽度
     * @param width 分割线宽度
     */
    fun setDividerWidth(width:Float){
        mDividerWidth = width
        invalidate()
    }

    /**
     * 设置省略符号
     * @param omit 省略字符
     */
    fun setOmitText(omit:String){
        mOmitText = omit
        invalidate()
    }

}