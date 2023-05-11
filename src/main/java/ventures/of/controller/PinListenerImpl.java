package ventures.of.controller;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.Data;
import ventures.of.util.FunctionUtil;

import java.util.*;
import java.util.function.Function;

import static ventures.of.util.StringUtil.printVerbose;

@Data
public class PinListenerImpl implements GpioPinListenerDigital {

    private String name;
    private boolean verbose;
    private GpioPinDigitalInput pin;
    private List<Timer> timers = new ArrayList<>();
    private long lowTime = 9999924092789L;
    private long highTime = 0;

    private long timeToTrigger = 5000;

    private Function<Void, Void> releasedFunction;
    private Function<Void, Void> pressedFunction;
    private Function<Void, Void> triggerFunction;

    public PinListenerImpl(String name, boolean verbose, GpioPinDigitalInput pin) {
        this.name = name;
        this.verbose = verbose;
        this.pin = pin;
        pin.addListener(this);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (event.getState() == PinState.LOW) {
            lowTime = System.currentTimeMillis();
            if (triggerFunction != null) {
                Timer timer = new Timer();
                timers.add(timer);
                TimerTask task = new HoldDownTimer(pin, event, triggerFunction);
                timer.schedule(task, timeToTrigger);
            }
        } else if (event.getState() == PinState.HIGH) {
            timers.forEach(Timer::cancel);
            timers.clear();
            highTime = System.currentTimeMillis();
            printVerbose(name + " pressed for " + (highTime - lowTime), verbose);
        }

        if (event.getState() == PinState.LOW) {
            printVerbose(name + " pressed", verbose);
            FunctionUtil.applyIfExists(pressedFunction, null);
        } else if (event.getState() == PinState.HIGH && (highTime - lowTime < timeToTrigger || triggerFunction == null)) {
            printVerbose(name + " released", verbose);
            FunctionUtil.applyIfExists(releasedFunction, null);
        }
    }

}

class HoldDownTimer extends TimerTask {
    GpioPinDigitalInput pin;
    GpioPinDigitalStateChangeEvent event;
    Function<Void, Void> triggerFunction;

    HoldDownTimer(GpioPinDigitalInput pin, GpioPinDigitalStateChangeEvent event, Function<Void, Void> triggerFunction) {
        this.pin = pin;
        this.event = event;
        this.triggerFunction = triggerFunction;
    }

    @Override
    public void run() {
        if (pin.getState() == PinState.LOW && event.getState() == PinState.LOW)
            FunctionUtil.applyIfExists(triggerFunction, null);
    }
}