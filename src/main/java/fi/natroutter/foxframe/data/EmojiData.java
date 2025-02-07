package fi.natroutter.foxframe.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmojiData {
    private String name;
    private long id;
    private boolean animated;

    public Emoji asEmoji(){
        return Emoji.fromCustom(name,id,animated);
    }

    public String asFormat() {
        if (id > 0) {
            return asEmoji().getFormatted();
        }
        return ":"+name+":";
    }
}