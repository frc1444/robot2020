package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import me.retrodaredevil.controller.gdx.ControllerProvider

class BestNameControllerProvider(
        private val bestNames: List<String>
) : ControllerProvider {
    override fun getName(): String {
        return controller?.name ?: ""
    }

    override fun getController(): Controller? {
        val map = mutableMapOf<String, Controller>()
        for(controller in Controllers.getControllers()){
            val controllerName = controller.name.toLowerCase()
            for(name in bestNames){
                if(name.toLowerCase() in controllerName){
                    map[name] = controller
                }
            }
        }
        for(name in bestNames){
            return map[name] ?: continue
        }
        return null
    }

    override fun isConnected(): Boolean {
        for(controller in Controllers.getControllers()){
            val controllerName = controller.name.toLowerCase()
            for(name in bestNames){
                if(name.toLowerCase() in controllerName){
                    return true
                }
            }
        }
        return false
    }

}
