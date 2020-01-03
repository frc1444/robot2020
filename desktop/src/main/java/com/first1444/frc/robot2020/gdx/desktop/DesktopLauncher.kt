@file:JvmName("DesktopLauncher")
package com.first1444.frc.robot2020.gdx.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.first1444.frc.robot2020.gdx.createScreen

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    Lwjgl3Application(createScreen(), config)
}
