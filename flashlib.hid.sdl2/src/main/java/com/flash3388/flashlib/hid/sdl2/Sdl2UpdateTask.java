package com.flash3388.flashlib.hid.sdl2;

import sdl2.SDL;
import sdl2.SDLEvents;
import sdl2.SDLJoystick;
import sdl2.events.Event;
import sdl2.events.JAxisMotionEvent;
import sdl2.events.JButtonChangeEvent;
import sdl2.events.JDeviceConnectionEvent;
import sdl2.events.JHatMotionEvent;

public class Sdl2UpdateTask implements Runnable {

    private static final int UPDATE_PERIOD_MAX = 5000;

    private final Sdl2HidData mHidData;

    public Sdl2UpdateTask(Sdl2HidData hidData) {
        mHidData = hidData;
    }

    @Override
    public void run() {
        SDL.init(SDL.INIT_JOYSTICK);
        try {
            while (!Thread.interrupted()) {
                pollUpdate();
            }
        } finally {
            SDL.quit();
        }
    }

    private void pollUpdate() {
        Event event = SDLEvents.waitEventTimeout(UPDATE_PERIOD_MAX);
        if (event == null) {
            return;
        }

        switch (event.getType()) {
            case JOY_AXIS_MOTION:
                JAxisMotionEvent axisMotionEvent = (JAxisMotionEvent) event;
                mHidData.setAxis(axisMotionEvent.getJoystickId(),
                        axisMotionEvent.getAxis(),
                        axisMotionEvent.getValue());
                break;
            case JOY_BUTTON_UP:
            case JOY_BUTTON_DOWN:
                JButtonChangeEvent buttonEvent = (JButtonChangeEvent) event;
                mHidData.setButton(buttonEvent.getJoystickId(),
                        buttonEvent.getButton(),
                        buttonEvent.getState());
                break;
            case JOY_HAT_MOTION:
                JHatMotionEvent hatMotionEvent = (JHatMotionEvent) event;
                mHidData.setHat(hatMotionEvent.getJoystickId(),
                        hatMotionEvent.getHat(),
                        hatMotionEvent.getValue());
                break;
            case JOY_DEVICE_ADDED:
            case JOY_DEVICE_REMOVED:
                JDeviceConnectionEvent connectionEvent = (JDeviceConnectionEvent) event;
                if (connectionEvent.isConnected()) {
                    long ptr = SDLJoystick.open(connectionEvent.getDeviceIndex());
                    int id = SDLJoystick.getInstanceId(ptr);

                    Sdl2HidMeta hidMeta = new Sdl2HidMeta(
                            SDLJoystick.getType(ptr),
                            SDLJoystick.getNumAxes(ptr),
                            SDLJoystick.getNumButtons(ptr),
                            SDLJoystick.getNumHats(ptr)
                    );
                    mHidData.setMeta(id, hidMeta);
                } else {
                    // TODO: remove meta?
                }
                break;
        }
    }
}
