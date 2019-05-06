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
@file:Suppress("unused")

package com.afollestad.materialdialogs.bottomsheets

import androidx.annotation.Px
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlin.math.min

/** Expands the bottom sheet, so that it's at its maximum height. */
fun MaterialDialog.expandBottomSheet(): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  (dialogBehavior as BottomSheet).bottomSheetBehavior?.let {
    it.state = STATE_EXPANDED
  }
  return this
}

/** Collapses the bottom sheet, so that it's at its peek height. */
fun MaterialDialog.collapseBottomSheet(): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  (dialogBehavior as BottomSheet).bottomSheetBehavior?.let {
    it.state = STATE_COLLAPSED
  }
  return this
}

/** Sets the peek (collapsed) height for the bottom sheet, in terms of pixel size. */
fun MaterialDialog.setPeekHeight(
  @Px height: Int,
  animationDuration: Long = 1000
): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  (dialogBehavior as BottomSheet).let {
    it.minimumPeekHeight = height
    it.minimumPeekHeightRatio = null
    val measuredHeight = it.bottomSheetView?.measuredHeight ?: height
    it.bottomSheetBehavior?.animatePeekHeight(
        dest = min(measuredHeight, height),
        duration = animationDuration
    )
  }
  return this
}

/**
 * Sets the peek (collapsed) height for the bottom sheet, in terms of a fraction of
 * the total screen height.
 * */
fun MaterialDialog.setPeekHeight(
  heightRatio: Float,
  animationDuration: Long = 1000
): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  require(heightRatio in 0f..1f) {
    "Height ratio must be between 0.0 and 1.0."
  }
  (dialogBehavior as BottomSheet).let {
    it.minimumPeekHeight = null
    it.minimumPeekHeightRatio = heightRatio
    val (_, windowHeight) = window!!.windowManager.getWidthAndHeight()
    val ratioHeight = (windowHeight * heightRatio).toInt()
    val measuredHeight = it.bottomSheetView?.measuredHeight ?: ratioHeight
    it.bottomSheetBehavior?.animatePeekHeight(
        dest = min(measuredHeight, ratioHeight),
        duration = animationDuration
    )
  }
  return this
}
