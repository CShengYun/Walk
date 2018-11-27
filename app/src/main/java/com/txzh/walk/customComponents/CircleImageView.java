package com.txzh.walk.customComponents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.loopj.android.image.SmartImageView;

@SuppressLint("AppCompatCustomView")
public class CircleImageView extends SmartImageView {
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public CircleImageView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    protected void onDraw(Canvas canvas){
        Drawable drawable = getDrawable();
        if(drawable == null){
            return;
        }
        
        if(getWidth() == 0 || getHeight() == 0){
            return;
        }
        
        if(!(drawable instanceof BitmapDrawable)){
            return;
        }

        Bitmap b = ((BitmapDrawable)drawable).getBitmap();
        
        if(null == b){
            return;
        }
        
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888,true);
        
        int w = getWidth();
        
        Bitmap roundBitmap = getCroppedBitmap(bitmap,w);
        canvas.drawBitmap(roundBitmap,0,0,null);
    }

    private static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight()!=radius){
            sbmp = Bitmap.createScaledBitmap(bmp,radius,radius,false);
        }else {
            sbmp=bmp;
        }
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0,sbmp.getWidth(),sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth()/2+0.7f,sbmp.getHeight()/2+0.7f,
                sbmp.getWidth()/2+0.1f,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp,rect,rect,paint);

        return output;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setColorFilter(0x33000000);
                //重写触摸事件的方法,当按钮被点击的时候

                mOnClickListener.onClick();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.setColorFilter(null);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 定义点击的接口
     */
    public interface OnClickListener {
        void onClick();
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener (OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

}
