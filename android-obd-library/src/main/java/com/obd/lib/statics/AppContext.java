/*
 * Pnuema Productions LLC ("COMPANY") CONFIDENTIAL
 * Unpublished Copyright (c) 2009-2015 Pnuema Productions, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of COMPANY. The intellectual and technical concepts contained herein are proprietary to COMPANY and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from COMPANY.  Access to the source code contained herein is hereby forbidden to anyone except current COMPANY employees, managers or contractors who have executed  Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  information that is confidential and/or proprietary, and is a trade secret, of  COMPANY.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF COMPANY IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package com.obd.lib.statics;

import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class handles getting the applications context.
 * <p/>
 * Created by Brad Barnhill on 4/22/2014.
 */
public class AppContext extends Application {
    private static Context mContext;

    public AppContext() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static String getResourceString(final int stringRes) {
        return mContext.getString(stringRes);
    }

    public static String[] getResourceStringArray(final int stringArrayRes) {
        return mContext.getResources().getStringArray(stringArrayRes);
    }

    public static InputStream getResourceFileInputStream(final String fileName) {
        try {
            return mContext.getAssets().open(fileName);
        } catch (final IOException e) {
            return null;
        }
    }
}