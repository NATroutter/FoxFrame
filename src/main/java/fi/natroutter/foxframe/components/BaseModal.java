package fi.natroutter.foxframe.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.List;

@Getter
@AllArgsConstructor
public class BaseModal {

    private String id;

    private String modalName;

    private List<ItemComponent> items;

}
