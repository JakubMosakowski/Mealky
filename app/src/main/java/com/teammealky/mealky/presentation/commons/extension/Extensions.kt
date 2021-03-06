package com.teammealky.mealky.presentation.commons.extension

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teammealky.mealky.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.util.*
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager

fun Date.isOlderThan(minutes: Int, nowFn: () -> Date = { Date() }): Boolean {
    return nowFn().time - 1000 * 60 * minutes > time
}

fun BottomNavigationView.clearSelection() {
    for (i in 0 until menu.size()) {
        menu.getItem(i).apply {
            isCheckable = false
            isChecked = false
            isCheckable = true
        }
    }
}

fun BottomNavigationView.markAsSelected(position: Int) {
    clearSelection()
    menu.getItem(position).isChecked = true
}

fun ViewGroup.inflate(
        layoutId: Int, attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ImageView.loadImage(url: String, transformation: Transformation? = null,
                        callback: Callback? = null,
                        placeholder: Drawable? = null,
                        fit: Boolean = false) {
    val usableUrl = if (url.isEmpty()) "errorUrl" else url
    tag = usableUrl

    val picasso = Picasso
            .get()
            .load(usableUrl)
            .config(Bitmap.Config.RGB_565)
            .error(R.drawable.broken_image)

    if (null != placeholder) picasso.placeholder(placeholder)
    else picasso.placeholder(R.color.colorAccent)
    if (fit) picasso.fit()
    if (transformation != null) picasso.transform(transformation)

    picasso.into(this, callback)
}

fun View.isVisible(isVisible: Boolean) {
    when (isVisible) {
        true -> this.visibility = View.VISIBLE
        false -> this.visibility = View.GONE
    }
}

fun View.isInvisible(isInvisible: Boolean) {
    when (isInvisible) {
        true -> this.visibility = View.INVISIBLE
        false -> this.visibility = View.VISIBLE
    }
}

fun genRandomIntExcept(start: Int, end: Int, excluded: List<Int>): Int {
    val rand = Random()
    var random = start + rand.nextInt(end - start - excluded.size)
    for (ex in excluded) {
        if (random < ex) {
            break
        }
        random++
    }
    return random
}

fun dp2px(dp: Int): Float = dp * Resources.getSystem().displayMetrics.density

fun getDisplaySize(windowManager: WindowManager): Point {
    return try {
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
    } catch (e: Exception) {
        e.printStackTrace()
        Point(0, 0)
    }
}

fun getResizedImageHeight(aspectRatio: Float): Int {
    return Math.round(getScreenWidth() * aspectRatio)
}

fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels
