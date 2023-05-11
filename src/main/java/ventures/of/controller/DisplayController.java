package ventures.of.controller;

import com.pi4j.io.gpio.*;
import lombok.Data;
import ventures.of.MainProgram;
import ventures.of.util.MathUtil;
import ventures.of.util.ProcessUtil;

import static ventures.of.util.ProcessUtil.runCommand;
import static ventures.of.util.StringUtil.printVerbose;

@Data
public class DisplayController {
    private int backlight = 80;
    private int backlightChange = 80;

    private GpioController gpio;
    private GpioPinDigitalInput click, button1, button2, button3, up, down, left, right;

    //Causes screen to flicker not sure why
    //final GpioPinPwmOutput backlightPwm = gpio.provisionSoftPwmOutputPin(RaspiBcmPin.GPIO_05);

    public DisplayController() {
        gpio = GpioFactory.getInstance();
        click = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_23, PinPullResistance.PULL_UP);
        button1 = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_29, PinPullResistance.PULL_UP);
        button2 = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_28, PinPullResistance.PULL_UP);
        button3 = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_27, PinPullResistance.PULL_UP);
        up = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_22, PinPullResistance.PULL_UP);
        down = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_24, PinPullResistance.PULL_UP);
        left = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_21, PinPullResistance.PULL_UP);
        right = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_25, PinPullResistance.PULL_UP);

        runCommand("sudo pigpiod", false);
        //backlightPwm.setPwmRange(255);
        //updateBacklightCliAction(80);

        boolean buttonVerbose = false;


        // Click
        PinListenerImpl clickListener = new PinListenerImpl("click", buttonVerbose, click);
        clickListener.setReleasedFunction(e -> MainProgram.cameraMenu.menuTriggerCurrentAction());

        //BUTTON1
        PinListenerImpl b1Listener = new PinListenerImpl("button1", buttonVerbose, button1);
        b1Listener.setReleasedFunction(e -> MainProgram.cameraController.triggerVideo());

        //BUTTON2
        PinListenerImpl b2Listener = new PinListenerImpl("button2", buttonVerbose, button2);
        b2Listener.setReleasedFunction(e -> MainProgram.cameraController.triggerTimelapse());

        //BUTTON3
        PinListenerImpl b3Listener = new PinListenerImpl("button3", buttonVerbose, button3);
        b3Listener.setReleasedFunction(e -> MainProgram.cameraController.triggerTakeStill());

        //UP
        PinListenerImpl upListener = new PinListenerImpl("up", buttonVerbose, up);
        upListener.setReleasedFunction(e -> updateBacklightCliAction(backlightChange));

        // DOWN
        PinListenerImpl downListener = new PinListenerImpl("down", buttonVerbose, down);
        downListener.setReleasedFunction(e -> updateBacklightCliAction(-backlightChange));

        //LEFT
        PinListenerImpl leftListener = new PinListenerImpl("left", buttonVerbose, left);
        leftListener.setReleasedFunction(e -> MainProgram.cameraMenu.menuGoLeftAction());

        // RIGHT
        PinListenerImpl rightListener = new PinListenerImpl("right", buttonVerbose, right);
        rightListener.setReleasedFunction(e -> MainProgram.cameraMenu.menuGoRightAction());

        updateBacklightCliAction(backlightChange);
    }

    public Void updateBacklightCliAction(int change) {
        int newBacklight = MathUtil.minMax(backlight + change, 0, 255);
        printVerbose(newBacklight + "", false);
        backlight = newBacklight;
        ProcessUtil.setBacklight(newBacklight);
        return null;
    }
    /*public Void updateBacklightJaveAction(int change) {
        int newBacklight = MathUtil.minMax(backlightPwm.getPwm() + change, 0, 255);
        printVerbose(newBacklight + "", true);
        backlightPwm.setPwm(newBacklight);
        return null;
    }

     */
}
