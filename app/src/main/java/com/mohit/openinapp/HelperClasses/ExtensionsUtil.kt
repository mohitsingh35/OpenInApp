package com.mohit.openinapp.HelperClasses

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mohit.openinapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
File : ExtensionsUtil.kt -> com.mohit.openinapp
Description : This file contains extension functions for different datatypes for easing out the development process

Author : Mohit Singh
Link : https://github.com/mohitsingh35

Creation : 3:16 pm on 26/07/24

Todo >
Tasks CLEAN CODE :
Tasks BUG FIXES :
Tasks FEATURE MUST HAVE :
Tasks FUTURE ADDITION :
*/


object ExtensionsUtil {

    fun parseData(dataString: String): Map<String, Double>? {
        return try {
            val map = mutableMapOf<String, Double>()
            val entries = dataString.removeSurrounding("{", "}").split(", ")

            for (entry in entries) {
                val (key, value) = entry.split("=")
                map[key.trim()] = value.trim().toDouble()
            }

            map
        } catch (e: Exception) {
            null
        }
    }

    fun showProgressDialog(context: Context, message: String): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_dialog, null)

        val lottieAnimationView: LottieAnimationView = view.findViewById(R.id.animationView)
        val textView: TextView = view.findViewById(R.id.textView)

        lottieAnimationView.setAnimation(R.raw.loading)
        lottieAnimationView.playAnimation()
        textView.text = message

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.show()
        return dialog
    }

    fun showButtonDialog(context: Context, message: String, onButtonClick: () -> Unit): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.button_dialog, null)
        val textView: TextView = view.findViewById(R.id.textView)
        val button: TextView = view.findViewById(R.id.button)
        textView.text = message
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()
        button.setOnClickListener {
            onButtonClick()
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

    fun Context.copyToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ClipData.newPlainText("text", text)
        } else {
            ClipData.newPlainText(null, text)
        }
        clipboardManager.setPrimaryClip(clipData)
    }

    fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = SimpleDateFormat("dd MMM yyyy")

        val date = inputFormat.parse(dateString) ?: return ""
        return outputFormat.format(date)
    }

    // Visibililty Extensions

    fun View.gone() = run { visibility = View.GONE }
    fun View.visible() = run { visibility = View.VISIBLE }
    fun View.invisible() = run { visibility = View.INVISIBLE }

    infix fun View.visibleIf(condition: Boolean) =
        run { visibility = if (condition) View.VISIBLE else View.GONE }

    infix fun View.goneIf(condition: Boolean) =
        run { visibility = if (condition) View.GONE else View.VISIBLE }

    infix fun View.invisibleIf(condition: Boolean) =
        run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }


    fun View.progressGone(context: Context, duration: Long = 1500L) = run {
        animFadeOut(context, duration)
        visibility = View.GONE

    }

    fun View.progressVisible(context: Context, duration: Long = 1500L) = run {
        visibility = View.VISIBLE
        animFadein(context, duration)
    }


    fun View.progressGoneSlide(context: Context, duration: Long = 1500L) = run {
        animSlideUp(context, duration)
        visibility = View.GONE

    }

    fun View.progressVisibleSlide(context: Context, duration: Long = 1500L) = run {
        visibility = View.VISIBLE
    }


    fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }


    // Toasts

    fun Fragment.toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun Fragment.toast(@StringRes message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun Activity.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun Activity.toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    //Snackbar

    fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).setAnimationMode(ANIMATION_MODE_SLIDE).show()
    }

    fun View.snackbar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }


    fun Activity.hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun Fragment.hideKeyboard() {
        activity?.apply {
            val imm: InputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = currentFocus ?: View(this)
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun Fragment.showKeyboard(editBox: EditText) {
        activity?.apply {
            val imm: InputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInputFromInputMethod(editBox.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }

    fun EditText.showKeyboardB() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }


    // Convert px to dp
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    //Convert dp to px
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()


    val String.isDigitOnly: Boolean
        get() = matches(Regex("^\\d*\$"))

    val String.isAlphabeticOnly: Boolean
        get() = matches(Regex("^[a-zA-Z]*\$"))

    val String.isAlphanumericOnly: Boolean
        get() = matches(Regex("^[a-zA-Z\\d]*\$"))


    //Null check
    val Any?.isNull get() = this == null

    fun Any?.ifNull(block: () -> Unit) = run {
        if (this == null) {
            block()
        }
    }

    /**
     * Set Drawable to the left of EditText
     * @param icon - Drawable to set
     */
    fun EditText.setDrawable(icon: Drawable) {
        this.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
    }


    /**
     * Function to run a delayed function
     * @param millis - Time to delay
     * @param function - Function to execute
     */
    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    /**
     * Show multiple views
     */
    fun showViews(vararg views: View) {
        views.forEach { view -> view.visible() }
    }


    /**
     * Hide multiple views
     */
    fun hideViews(vararg views: View) {
        views.forEach { view -> view.gone() }
    }


    //Date formatting
    fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.parse(this)
    }

    fun Date.toStringFormat(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.format(this)
    }


    //Permission
    fun Context.isPermissionGranted(permission: String) = run {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun ImageView.load(url: Any, placeholder: Drawable) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().placeholder(placeholder))
            .load(url)
            .thumbnail(0.05f)
            .error(placeholder)
            .into(this)
    }




    fun isValidContext(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }




    //Animation
    fun View.animSlideUp(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_up)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }




    fun View.animSlideUpVisible(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_up)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
        this.visible()
    }


    fun View.slideDownAndGone(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        return animate()
            .translationYBy(height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                visibility = View.GONE
                onEndAction?.invoke()
            }
    }

    fun View.slideUpAndVisible(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        visibility = View.VISIBLE
        return animate()
            .translationYBy(-height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                onEndAction?.invoke()
            }
    }

    fun View.animSlideLeft(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.animSlideRight(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.animFadein(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }


    fun View.rotate180(context: Context, animDuration: Long = 500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotate180)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.set180(context: Context, animDuration: Long = 200L) {
        clearAnimation()
        val currentRotation = tag as? Float ?: 0f
        val targetRotation = if (currentRotation == 0f) 180f else 0f
        val rotationProperty =
            PropertyValuesHolder.ofFloat(View.ROTATION, currentRotation, targetRotation)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, rotationProperty)
            .apply {
                duration = animDuration
            }
        tag = targetRotation

        animator.start()
    }


    fun View.rotateInfinity(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotateinfi)
        this.startAnimation(animation)
    }


    fun View.popInfinity(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.popinfi)
        this.startAnimation(animation)
    }


    fun View.blink(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.blink)
        this.startAnimation(animation)
    }

    fun View.blinkinfi(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.blinkinf)
        this.startAnimation(animation)
    }

    fun View.rotateInfinityReverse(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotateinfirev)
        this.startAnimation(animation)
    }

    fun View.animFadeOut(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation =
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_out)
                .apply {
                    duration = animDuration
                }
        this.startAnimation(animation)

    }

    fun View.bounce(context: Context, animDuration: Long = 500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }



    fun TextInputEditText.appendTextAtCursor(textToAppend: String) {
        val start = selectionStart
        val end = selectionEnd

        val editable = text

        editable?.replace(start, end, textToAppend)
        setSelection(start + textToAppend.length)
    }


    fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        if (this.layoutParams is ViewGroup.MarginLayoutParams) {
            val params = this.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(left, top, right, bottom);
        }
    }

    fun TextInputEditText.appendTextAtCursorMiddleCursor(textToAppend: String, type: Int) {
        val position = this.selectionStart
        val text = this.text
        val newText = StringBuilder(text).insert(position, textToAppend).toString()
        this.setText(newText)
        if (type == 2) this.setSelection(position + 1)
        else this.setSelection(position + 2)
    }


    fun View.setOnClickThrottleBounceListener(throttleTime: Long = 600L, onClick: () -> Unit) {

        this.setOnClickListener(object : View.OnClickListener {

            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                context?.let {
                    v.bounce(context)
                    if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
                    else onClick()
                    lastClickTime = SystemClock.elapsedRealtime()
                }

            }
        })
    }


    fun deleteDownloadedFile(downloadID : Long, context: Context) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(downloadID)
    }

    fun getVersionName(context: Context): String? {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun String.isGreaterThanVersion(otherVersion: String): Boolean {
        val thisParts = this.split(".").map { it.toInt() }
        val otherParts = otherVersion.split(".").map { it.toInt() }

        for (i in 0 until maxOf(thisParts.size, otherParts.size)) {
            val thisPart = thisParts.getOrNull(i) ?: 0
            val otherPart = otherParts.getOrNull(i) ?: 0

            if (thisPart != otherPart) {
                return thisPart > otherPart
            }
        }

        return false
    }


    fun View.setOnClickSingleTimeBounceListener(onClick: () -> Unit) {

        this.setOnClickListener(object : View.OnClickListener {
            private var clicked: Boolean = false
            override fun onClick(v: View) {
                //context.performHapticFeedback()
                v.bounce(context)
                if (clicked) return
                else onClick()
                clicked = true
            }
        })
    }

    inline fun View.setOnClickFadeInListener(crossinline onClick: () -> Unit) {
        setOnClickListener {
            // context.performHapticFeedback()
            it.animFadein(context, 100)
            onClick()
        }
    }


    fun View.setSingleClickListener(throttleTime: Long = 600L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
                else action()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }


    fun View.setBackgroundColorRes(colorResId: Int) {
        val color = context.resources.getColor(colorResId)
        setBackgroundColor(color)
    }

    fun View.setBackgroundColor(color: Int) {
        background = ColorDrawable(color)
    }

    fun View.setBackgroundDrawable(drawable: Drawable) {
        background = drawable
    }
}

