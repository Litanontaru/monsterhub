package org.dmg.monsterhub

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

@Route
class MainView() : VerticalLayout() {
    init {
        val button = Button("Say hello") { e -> Notification.show("Hello") }
        add(button);
    }
}