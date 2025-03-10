package ventures.of.view.menu.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
public class MenuItemInterface {
    Function<Void, String> name;
    Function<Void, Void> actionClick;
    Function<Void, Void> changeFromInMenu;
public void onClick() {

}
public void onChangeFrom() {

}
}
