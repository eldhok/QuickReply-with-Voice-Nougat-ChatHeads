
/*
 * Copyright (C) 2018 Screenfly Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.notifications.screen.theme;

import android.view.View;
import android.view.ViewStub;

import com.notifications.screen.R;

/**
 * Android L theme
 */
public class L5Black extends L5Dark {

    public L5Black(ViewStub stub) {
        super(stub);
    }

    @Override
    public void init(View layout) {
        layout.findViewById(R.id.linearLayout).setBackgroundResource(R.drawable.card_black);
        super.init(layout);
    }


}
