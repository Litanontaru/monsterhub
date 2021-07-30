package org.dmg.monsterhub

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.model.processor.CreatureService
import org.springframework.stereotype.Service

@Route
@Service
class MainView(
        val service: CreatureService
) : VerticalLayout() {
    init {
        val area = TextArea("Big Area")
        val label = Label()
        val button = Button("Evaluate") {
            label.text = service.eval(area.value).toString()
        }
        add(button)
        add(area)
        add(label)
    }
}