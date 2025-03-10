package ventures.of.view.menu.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
public class SuperMenuItem extends MenuItemInterface {
    MenuItemSetting[] settings;
    //todo index to track current setting

    public SuperMenuItem(String name, DirectMenuItem... settings) {
        this.name = ((e) -> name);
        this.settings = settings;
    }

}
