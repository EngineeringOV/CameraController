package ventures.of.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ventures.of.model.ValueWithIndex;

@Data
@EqualsAndHashCode(callSuper=true)
public class MenuItemSetting extends MenuItem{

    public MenuItemSetting(String name, ValueWithIndex value) {
        super(e -> CameraMenu.buildSettingText(name, value), e -> CameraMenu.updateSettingAction(value));
    }

    public MenuItemSetting(String name, ValueWithIndex value, String staticSuffix) {
        super(e -> CameraMenu.buildSettingTextStaticSuffix(name, value, staticSuffix), e -> CameraMenu.updateSettingAction(value));
    }

}
