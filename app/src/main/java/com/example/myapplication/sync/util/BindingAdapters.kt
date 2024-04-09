/*
 * Copyright (C) 2019 Google Inc.
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

package com.example.myapplication.sync.util

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.myapplication.R
import com.example.myapplication.sync.domain.TripPointStatus

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "playlist")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, playlist: Any?) {
    view.visibility = if (playlist != null) View.GONE else View.VISIBLE

    if(isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to set card color based on trip point status.
 */
@BindingAdapter("backgroundByStatus")
fun setBackgroundByStatus(view: View, status: TripPointStatus) {
    val context = view.context
    val backgroundDrawable = when (status) {
        TripPointStatus.PASSED -> ContextCompat.getDrawable(context, R.drawable.background_passed)
        TripPointStatus.CURRENT -> ContextCompat.getDrawable(context, R.drawable.background_current)
        TripPointStatus.FUTURE -> ContextCompat.getDrawable(context, R.drawable.background_future)
        TripPointStatus.UNAWARE -> ContextCompat.getDrawable(context, R.drawable.background_future)
        else -> ContextCompat.getDrawable(context, R.drawable.background_unknown)
    }
    view.background = backgroundDrawable
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Toast.makeText(imageView.context, "SUPPOSED TO SHOW IMAGE", Toast.LENGTH_SHORT).show()
}