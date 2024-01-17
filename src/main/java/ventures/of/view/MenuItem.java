package ventures.of.view;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
public class MenuItem {
    Function<Void, String> name;
    String value;
    Function<Void, Void> actionClick;
    Function<Void, Void> changeFromInMenu;

    public MenuItem(Function<Void, String> name, Function<Void, Void> actionClick) {
        this.name = name;
        this.actionClick = actionClick;
        this.changeFromInMenu = null;
    }

    public MenuItem(String name, Function<Void, Void> actionClick) {
        this.name = ((e) -> name);
        this.actionClick = actionClick;
        this.changeFromInMenu = null;
    }

    public MenuItem(String name, Function<Void, Void> actionClick, Function<Void, Void> changeFromInMenu) {
        this(name, actionClick);
        this.changeFromInMenu = changeFromInMenu;
    }
}
