package com.github.capstone.Scene.Components;

import com.github.capstone.Twotris;
import com.github.capstone.Util.Keybinds;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Rectangle;

public class ButtonKeybind extends Button
{
    private int keyValue;
    private String title;

    public ButtonKeybind(int x, int y, String title, int defaultVal)
    {
        super(x, y, title + ": " + Keyboard.getKeyName(defaultVal).substring(0, 1) + Keyboard.getKeyName(defaultVal).substring(1).toLowerCase());
        this.title = title;
        this.keyValue = defaultVal;
    }

    @Override
    public void update()
    {
        Rectangle mouse = new Rectangle(Mouse.getX(), Display.getHeight() - Mouse.getY(), 1, 1);
        this.hovering = this.getHitBox().intersects(mouse);
        if (this.isClicked())
        {
            this.buttonText = title + ": _";
            this.keyValue = -1;
        }
        if (this.keyValue == -1)
        {
            Mouse.setGrabbed(true);
            while (Keyboard.next())
            {
                if (Keyboard.next())
                {
                    continue;
                }
                if (Keyboard.getEventKeyState())
                {
                    this.keyValue = Keyboard.getEventKey();
                }
            }
            if (this.keyValue > -1)
            {
                this.buttonText = title + ": " + intKeyToPrettyStr(this.keyValue);
                updateKeybind(this.title, this.keyValue);
            }
        }
        else
        {
            if (Mouse.isGrabbed())
            {
                Mouse.setGrabbed(false);
            }
        }
    }

    private String intKeyToPrettyStr(int keyVal)
    {
        String ret = Keyboard.getKeyName(keyVal);
        ret = ret.substring(0, 1) + ret.substring(1).toLowerCase();
        return ret;
    }

    private void updateKeybind(String keybind, int newVal)
    {
        Keybinds kb = Twotris.getInstance().keybinds;
        switch (keybind)
        {
            case "Left":
                kb.moveLeft = newVal;
                break;
            case "Right":
                kb.moveRight = newVal;
                break;
            case "Rotate":
                kb.rotate = newVal;
                break;
            case "Accel":
                kb.accelerate = newVal;
                break;
            case "Place":
                kb.place = newVal;
                break;
            case "Menu/Back":
                kb.menuBack = newVal;
                break;
        }
        kb.updateConfig();
    }
}
