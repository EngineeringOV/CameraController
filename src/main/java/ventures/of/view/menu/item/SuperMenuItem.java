package ventures.of.view.menu.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
public class SuperMenuItem extends MenuItemInterface {
    Function<Void, String> name;
    String value;
    Function<Void, Void> actionClick;
    Function<Void, Void> changeFromInMenu;

    public SuperMenuItem(Function<Void, String> name, Function<Void, Void> actionClick) {
        this.name = name;
        this.actionClick = actionClick;
        this.changeFromInMenu = null;
    }

    public SuperMenuItem(String name, Function<Void, Void> actionClick) {
        this.name = ((e) -> name);
        this.actionClick = actionClick;
        this.changeFromInMenu = null;
    }

    public SuperMenuItem(String name, Function<Void, Void> actionClick, Function<Void, Void> changeFromInMenu) {
        this(name, actionClick);
        this.changeFromInMenu = changeFromInMenu;
    }

}
