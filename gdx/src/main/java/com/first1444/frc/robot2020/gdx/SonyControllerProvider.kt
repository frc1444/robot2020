package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import me.retrodaredevil.controller.gdx.ControllerProvider

class SonyControllerProvider : ControllerProvider {
    override fun getName(): String {
        return controller?.name ?: ""
    }

    override fun getController(): Controller? {
        for(controller in Controllers.getControllers()){
            if("sony" in controller.name.toLowerCase()){
                return controller
            }
        }
        return null
    }

    override fun isConnected(): Boolean {
        return controller != null
    }

}
