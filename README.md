# WatermarkImageDrawSample

<img src="https://github.com/Luckyrana001/WatermarkImageDrawSample/blob/master/watermark1.png" alt="Drawing" style="width: 200px;"/>




code used to add watermark :-

<pre  style="font-family:arial;font-size:12px;border:1px dashed #CCCCCC;width:99%;height:auto;overflow:auto;background:#f0f0f0;;background-image:URL(http://2.bp.blogspot.com/_z5ltvMQPaa8/SjJXr_U2YBI/AAAAAAAAAAM/46OqEP32CJ8/s320/codebg.gif);padding:0px;color:#000000;text-align:left;line-height:20px;"><code style="color:#000000;word-wrap:normal;">  /**  
    * Embeds an image watermark over a source image to produce  
    * a watermarked one.  
    * @param watermarkImageFile The image file used as the watermark.  
    * @param sourceImageFile The source image file.  
    * @param destImageFile The output image file.  
    */  
   /**  
    * Adds a watermark on the given image.  
    */  
   public static Bitmap addWatermark(Resources res, Bitmap source) {  
     int w, h;  
     Canvas c;  
     Paint paint;  
     Bitmap bmp, watermark;  
     Matrix matrix;  
     float scale;  
     RectF r;  
     w = source.getWidth();  
     h = source.getHeight();  
     // Create the new bitmap  
     bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  
     paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);  
     // Copy the original bitmap into the new one  
     c = new Canvas(bmp);  
     c.drawBitmap(source, 0, 0, paint);  
     // Load the watermark  
     watermark = BitmapFactory.decodeResource(res, R.drawable.dell_logo);  
     // Scale the watermark to be approximately 40% of the source image height  
     scale = (float) (((float) h * 0.40) / (float) watermark.getHeight());  
     // Create the matrix  
     matrix = new Matrix();  
     matrix.postScale(scale, scale);  
     // Determine the post-scaled size of the watermark  
     r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());  
     matrix.mapRect(r);  
     // Move the watermark to the bottom right corner  
     matrix.postTranslate(w - r.width(), h - r.height());  
     // Draw the watermark  
     c.drawBitmap(watermark, matrix, paint);  
     // Free up the bitmap memory  
     watermark.recycle();  
     return bmp;  
   }  
</code></pre>
