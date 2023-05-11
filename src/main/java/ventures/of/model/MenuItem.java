package ventures.of.model;

import lombok.Data;

import java.util.function.Function;

@Data
public class MenuItem {

    private Function<Void, String> name;
    private String value;
    private Function<Void, Void> actionClick;
    private Function<Void, Void> changeFromInMenu;

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
