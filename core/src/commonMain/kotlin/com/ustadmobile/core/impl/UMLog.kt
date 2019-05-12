/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */
package com.ustadmobile.core.impl

/**
 * Logging interface to be implemented by the System Implementation
 * @author mike
 */
expect class UMLog {


    companion object {


        /**
         * Log the given message
         *
         * @param level
         * @param code An Error code to assist debugging
         * @param message  (May be null)
         */
        fun l(level: Int, code: Int, message: String?)

        /**
         * Log the message given and the exception
         *
         * @param level
         * @param code An Error code to assist debugging
         * @param message
         * @param exception
         */
        fun l(level: Int, code: Int, message: String, exception: Exception)


        const val CRITICAL = 0

        const val ERROR = 1

        const val WARN = 2

        const val INFO = 3

        const val VERBOSE = 4

        const val DEBUG = 5
    }
}
