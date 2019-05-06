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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.afollestad.materialdialogs.utils.MDUtil.waitForLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlin.math.max
import kotlin.math.min

/** @author Aidan Follestad (@afollestad) */
class BottomSheet : DialogBehavior {
  internal var bottomSheetBehavior: BottomSheetBehavior<*>? = null
  internal var bottomSheetView: ViewGroup? = null

  internal var minimumPeekHeight: Int? = null
  internal var minimumPeekHeightRatio: Float? = null

  private var rootView: CoordinatorLayout? = null
  private var dialog: MaterialDialog? = null

  @SuppressLint("InflateParams")
  override fun createView(
    context: Context,
    window: Window,
    layoutInflater: LayoutInflater,
    dialog: MaterialDialog
  ): ViewGroup {
    this.dialog = dialog
    rootView = layoutInflater.inflate(
        R.layout.md_dialog_base_bottomsheet,
        null,
        false
    ) as CoordinatorLayout

    bottomSheetView = rootView!!.findViewById(R.id.md_root_bottom_sheet)
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        .apply {
          isHideable = true
          onHide { dialog.dismiss() }
        }

    val (_, windowHeight) = window.windowManager.getWidthAndHeight()
    val actualMinPeek = if (minimumPeekHeightRatio != null) {
      (windowHeight * minimumPeekHeightRatio!!).toInt()
    } else {
      minimumPeekHeight
    } ?: (windowHeight * DEFAULT_PEEK_HEIGHT_RATIO).toInt()

    bottomSheetBehavior?.peekHeight = actualMinPeek
    bottomSheetView?.waitForLayout {
      if (this.measuredHeight >= actualMinPeek) {
        bottomSheetBehavior?.animatePeekHeight(
            dest = min(actualMinPeek, windowHeight),
            duration = LAYOUT_PEEK_CHANGE_DURATION_MS
        )
      } else {
        bottomSheetBehavior?.animatePeekHeight(
            dest = max(this.measuredHeight, actualMinPeek),
            duration = LAYOUT_PEEK_CHANGE_DURATION_MS
        )
      }
    }

    return rootView!!
  }

  override fun getDialogLayout(root: ViewGroup): DialogLayout {
    return root.findViewById(R.id.md_root) as DialogLayout
  }

  override fun setWindowConstraints(
    context: Context,
    window: Window,
    view: DialogLayout,
    maxWidth: Int?
  ) {
    if (maxWidth == 0) {
      // Postpone
      return
    }
    window.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    val lp = LayoutParams()
        .apply {
          copyFrom(window.attributes)
          width = LayoutParams.MATCH_PARENT
          height = LayoutParams.MATCH_PARENT
        }
    window.attributes = lp
  }

  override fun setBackgroundColor(
    context: Context,
    window: Window,
    view: DialogLayout,
    color: Int,
    cornerRounding: Float
  ) {
    window.setBackgroundDrawable(null)
    bottomSheetView?.background = GradientDrawable().apply {
      cornerRadii = floatArrayOf(
          cornerRounding, cornerRounding, // top left
          cornerRounding, cornerRounding, // top right
          0f, 0f, // bottom left
          0f, 0f // bottom right
      )
      setColor(color)
    }
  }

  override fun onShow() {
    rootView?.setOnClickListener {
      if (dialog?.cancelOnTouchOutside == true) {
        // Clicking outside the bottom sheet dismisses the dialog
        dialog!!.dismiss()
      }
    }
  }

  override fun onDismiss(): Boolean {
    if (bottomSheetBehavior != null && bottomSheetBehavior!!.state != STATE_HIDDEN) {
      bottomSheetBehavior!!.state = STATE_HIDDEN
      bottomSheetBehavior = null
      bottomSheetView = null
      rootView = null
      dialog = null
      return true
    }
    return false
  }

  private companion object {
    private const val DEFAULT_PEEK_HEIGHT_RATIO = 0.6f
    private const val LAYOUT_PEEK_CHANGE_DURATION_MS = 1000L
  }
}
