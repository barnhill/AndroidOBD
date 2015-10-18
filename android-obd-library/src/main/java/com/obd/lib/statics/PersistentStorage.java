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

import com.obd.lib.models.PID;

import java.util.HashMap;

/**
 * Storage for the persistent pids so they dont have to be retrieved more than once.
 */
public class PersistentStorage {
    private static PersistentStorage smInstance;

    private static final HashMap<String, PID> persistentPidStorage = new HashMap<>();

    public static PersistentStorage getInstance() {
        if (smInstance == null) {
            smInstance = new PersistentStorage();
        }

        return smInstance;
    }

    public static void addElement(final PID element) {
        if (element != null) {
            persistentPidStorage.put(formKey(element), element);
        }
    }

    public static void removeElement(final PID element) {
        if (element != null) {
            persistentPidStorage.remove(formKey(element));
        }
    }

    public static PID getElement(final PID element) {
        if (element != null) {
            return persistentPidStorage.get(formKey(element));
        }

        return null;
    }

    public static boolean containsPid(final PID element) {
        return element.isPersistent && persistentPidStorage.containsKey(formKey(element));
    }

    public static void clearAll() {
        persistentPidStorage.clear();
    }

    private static String formKey(final PID pid) {
        return pid.Mode + pid.PID;
    }
}
