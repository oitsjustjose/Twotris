package com.github.capstone.Scene;

import com.github.capstone.Manager.AudioManager;
import com.github.capstone.Util.Helper;
import org.lwjgl.opengl.Display;

public abstract class Scene
{

    // return false if the game should be quit
    public abstract boolean drawFrame(float delta);

    /**
     * @param none
     * @return null
     * @throws none
     * @nextScene The null indicates that Twotris should load the menu.
     */
    public Scene nextScene()
    {
        return null;
    }

    /**
     * @param none
     * @return none
     * @throws none
     * @go This returns false when the game should be exited.
     */
    public boolean go()
    {
        long lastloop = Helper.getTime();

        boolean keepGoing = true;
        do
        {
            Display.sync(60);
            long now = Helper.getTime();
            long delta = now - lastloop;
            lastloop = now;

            keepGoing = drawFrame(delta);

            Display.update();
            AudioManager.getInstance().update();

            if (Display.isCloseRequested())
            {
                return false;
            }
        } while (keepGoing);

        return true;
    }

    public void resizeContents()
    {
    }
}
