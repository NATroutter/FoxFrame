package fi.natroutter.foxframe.interfaces;

import lombok.Getter;

public enum Node {



    ;
    @Getter
    private String node;
    Node(String node) {
        this.node = node;
    }

}
