/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package net.sevendays.android.code_check

import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import net.sevendays.android.code_check.R
import java.util.*

class TopActivity : AppCompatActivity(R.layout.activity_top) {
    companion object {
        lateinit var lastSearchDate: Date
    }
}
