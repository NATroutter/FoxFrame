package fi.natroutter.foxframe.interfaces;

import lombok.Getter;

public enum PermissionNode {



    ;
    @Getter
    private String node;
    PermissionNode(String node) {
        this.node = node;
    }

}
