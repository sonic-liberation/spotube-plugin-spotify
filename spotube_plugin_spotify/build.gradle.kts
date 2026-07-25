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

import com.codingfeline.buildkonfig.compiler.FieldSpec
import dev.krtirtho.PluginAbility
import dev.krtirtho.PluginCapability
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.zipline.gradle.plugin)
    alias(libs.plugins.spotubeGradle)
    id("com.codingfeline.buildkonfig") version "0.18.0"
}

spotubePlugin {
    name = "Spotify"
    version = "0.1.0"
    apiVersion = "0.0.1"
    description = "Spotify plugin for Spotube metadata fetching"
    author = "Sonic Liberation"
    capabilities = listOf(
        PluginCapability.NETWORK_REQUESTS,
        PluginCapability.PERSISTENT_STORAGE,
        PluginCapability.WEBVIEW
    )
    abilities = listOf(PluginAbility.METADATA)
    license = "AGPL-3.0-or-later"
    contact = "sonic.liberation.69@gmail.com"
    repository = "https://github.com/sonic-liberation/spotube-plugin-spotify"
    bugs = "https://github.com/sonic-liberation/spotube-plugin-spotify/issues"
}

buildkonfig {
    val localProperties = Properties().apply {
        load(project.file("local.properties").inputStream())
    }
    val accessToken: String = localProperties["accessToken"] as String?
        ?: throw Exception("accessToken not found in local.properties")
    val spDcCookie: String = localProperties["spDcCookie"] as String?
        ?: throw Exception("spDcCookie not found in local.properties")
    val spTCookie: String = localProperties["spTCookie"] as String?
        ?: throw Exception("spTCookie not found in local.properties")

    packageName = "io.github.sonic_liberation.spotube_plugin_spotify"  // Your package
    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "ACCESS_TOKEN", accessToken)
        buildConfigField(FieldSpec.Type.STRING, "SP_DC_COOKIE", spDcCookie)
        buildConfigField(FieldSpec.Type.STRING, "SP_T_COOKIE", spTCookie)
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":spotify_gql_client"))
            api(libs.zipline.core)
            implementation(libs.spotube.plugin.interfaces)
            api(libs.semver)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.base32)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
        }

        jsMain.dependencies {
        }

    }
}

zipline {
    mainFunction.set("io.github.sonic_liberation.spotube_plugin_spotify.main")
}

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().yarnLockAutoReplace = true
}
