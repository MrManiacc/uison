package me.jraynor.uison.parser;

import lombok.Getter;

public enum UIType {
    UIBLOCK("block"), UIFLEX("flex"),
    UICONSTRAINT("const"), UITEXT("label"),
    UICOMPONENT("comp"), UIVBOX("vbox"),
    UIHBOX("hbox"), UIUNKOWN("unk"), UISEPERATOR("sep"),
    UITEXTBOX("tbox"), UIBAR("bar"), UISLIDER("slide");
    @Getter
    private String identifier;

    UIType(String value) {
        this.identifier = value;
    }
}