package fi.natroutter.foxframe.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModalData {

    private String id;
    private String modalName;
    private List<ActionComponent> items;

}
