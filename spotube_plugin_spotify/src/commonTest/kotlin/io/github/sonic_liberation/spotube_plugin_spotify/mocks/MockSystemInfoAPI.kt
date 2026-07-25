/*
 * Copyright (C) 2026 Sonic Liberation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.sonic_liberation.spotube_plugin_spotify.mocks

import dev.krtirtho.plugin_interfaces.host_apis.SystemInformationAPI

class MockSystemInfoAPI: SystemInformationAPI {
    override fun getTimeZone(): String {
        return "Asia/Kolkata"
    }

    override fun getLocale(): String {
        return "en-US"
    }

    override fun getOperatingSystem(): String {
        return "Windows"
    }

    override fun getAppVersion(): String {
        return "1.0.0"
    }
}