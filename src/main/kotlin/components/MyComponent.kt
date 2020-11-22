package components

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute

/*
 * - PersistentStateComponent works by storing
        - public fields
        - annotated private fields
        - bean properties into an XML format.

     To remove a public field from the serialization you can annotate the field with @Transient.
 */
@State(
        name = "MyConfiguration",
        storages = [Storage(value = "myConfiguration.xml")]
)
class MyComponent : ApplicationComponent, PersistentStateComponent<MyComponent> {

    @Attribute private var localVersion: String = "0.0"

    private lateinit var version: String


    override fun initComponent() {
        super.initComponent()

        version = getPluginVersion()

        if (isANewVersion()) {
            updateLocalVersion()

            val noti = NotificationGroup(
                    displayId = "payroc-get-started",
                    displayType = NotificationDisplayType.BALLOON,
                    isLogByDefault = true
            )

            noti.createNotification(
                    title = "Plugin updated",
                    content = "Welcome to the new version",
                    type = NotificationType.INFORMATION,
                    listener = null
            ).notify(null)
        }
    }

    private fun getPluginVersion(): String {
        return PluginManager.getPlugin(
                PluginId.getId("payroc-get-started")
        )!!.version
    }

    private fun isANewVersion(): Boolean {
        val s1 = localVersion.split("-")[0].split(".")
        val s2 = version.split("-")[0].split(".")

        if (s1.size != s2.size) return false
        var i = 0

        do {
            if (s1[i] < s2[i]) return true
            i++
        } while (i < s1.size && i < s2.size)

        return false
    }

    private fun newVersionAvailable(): Boolean = true

    private fun updateLocalVersion() {
        localVersion = version
    }

    override fun getState(): MyComponent?  = this

    override fun loadState(state: MyComponent) = XmlSerializerUtil.copyBean(state, this)
}