/*
 * Copyright 2014 Mostafa Gazar
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

package com.meg7.sample;

import android.app.Activity;
import android.os.Bundle;

import com.meg7.widget.RippleView;

/**
 * Sample activity to show RippleView in action.
 *
 * @author Mostafa Gazar <eng.mostafa.gazar@gmail.com>
 */
public class RippleViewSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_ripple_view);

        // Attach ripple view to the following views.
        new RippleView(this, findViewById(R.id.customTglBtn));
        new RippleView(this, findViewById(R.id.button1));
        new RippleView(this, findViewById(R.id.button2));
        new RippleView(this, findViewById(R.id.button3));
    }

}
