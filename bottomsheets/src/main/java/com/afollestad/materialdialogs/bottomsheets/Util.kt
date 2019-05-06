/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialdialogs.bottomsheets

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN

internal fun BottomSheetBehavior<*>.onHide(block: () -> Unit) {
  setBottomSheetCallback(object : BottomSheetCallback() {
    override fun onSlide(
      view: View,
      dY: Float
    ) = Unit

    override fun onStateChanged(
      view: View,
      state: Int
    ) {
      if (state == STATE_HIDDEN) {
        block()
      }
    }
  })
}

internal fun BottomSheetBehavior<*>.animatePeekHeight(
  dest: Int,
  duration: Long
) {
  if (dest == peekHeight) {
    return
  } else if (duration <= 0) {
    peekHeight = dest
    return
  }
  animateValues(peekHeight, dest, duration, onUpdate = {
    peekHeight = it
  })
}

private fun animateValues(
  from: Int,
  to: Int,
  duration: Long,
  onUpdate: (currentValue: Int) -> Unit
): Animator {
  return ValueAnimator.ofInt(from, to)
      .apply {
        this.interpolator = DecelerateInterpolator()
        this.duration = duration
        addUpdateListener {
          onUpdate(it.animatedValue as Int)
        }
      }
}
