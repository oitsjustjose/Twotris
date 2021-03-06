package com.github.capstone.Scene.Menus;

import com.github.capstone.Scene.Components.Button;
import com.github.capstone.Scene.Game;
import com.github.capstone.Scene.Scene;
import com.github.capstone.Twotris;
import com.github.capstone.Util.Helper;
import com.github.capstone.Util.Textures;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;

public class Options extends Scene
{
    private Button back;
    private Button keybinds;
    private LinkedHashMap<Button, String> buttons;
    public Scene last;
    private Scene next;
    private boolean fontChanged;
    private boolean colorChanged;
    private static Options instance;

    private int curs; private long lastKeypress;

    /**
     * @param lastscene The scene supplied.
     * @return none
     * @throws none
     * @Options This constructor method adds button to a linkedhashmap, added the buttons to the options menu, adds a back button, adjusts the buttons, and then creates the next as the last scene.
     */
    public Options(Scene lastScene)
    {
        buttons = new LinkedHashMap<>();
        Twotris.getInstance().config.addButtonsToOptionsGUI(this);
        if(!Twotris.getInstance().config.kiosk_mode)
            keybinds = new Button(256, 64, "Keybinds");
        back = new Button(256, 64, "Back");
        this.adjustButtons();
        this.last = lastScene;
        this.fontChanged = false;
        this.colorChanged = false;
    }

    public static Options getInstance(Scene lastScene)
    {
        if (instance == null)
        {
            instance = new Options(lastScene);
        }
        instance.last = lastScene;
        return instance;
    }

    /**
     * @param option The String given.
     * @return none
     * @throws none
     * @addButton This method receives buttons and puts them into the options menu.
     */
    public void addButton(String option)
    {
        this.buttons.put(new Button(256, 64, option), option);
    }

    /**
     * @param delta
     * @return isGameOver true/false.
     * @throws none
     * @drawFrame This method Is able to resize the screen, and is responsible for updating the entities by delta, adjusting fullscreen, the score displayed, and controls when the game is over.
     */
    public boolean drawFrame(float delta)
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(.09F, 0.09F, 0.09F, 0F);
        // Screen resize handler
        if (Display.wasResized())
        {
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        updateButtons();
        drawButtons();

        if (Keyboard.isKeyDown(Twotris.getInstance().keybinds.screenshot))
        {
            Twotris.getInstance().screenshotManager.takeScreenshot();
        }

        //If left or right is pushed, move cursor
        if (Keyboard.isKeyDown(Twotris.getInstance().keybinds.moveRight) && Helper.getTime() - lastKeypress > 150)
        {
            int num_menu_buttons = 2;
            if(Twotris.getInstance().config.kiosk_mode)
                num_menu_buttons = 1;
            curs++;
            if(curs >= buttons.keySet().size()+num_menu_buttons) {
              curs = 0;
            }
            if(curs < buttons.keySet().size()) {
              moveMouseOver(curs);
            } else if(curs == buttons.keySet().size()) {
              moveMouseOver(back);
            } else if(curs == buttons.keySet().size()+1) {
                if(!Twotris.getInstance().config.kiosk_mode)
                    moveMouseOver(keybinds);
            }
            lastKeypress = Helper.getTime();
        }
        if (Keyboard.isKeyDown(Twotris.getInstance().keybinds.moveLeft) && Helper.getTime() - lastKeypress > 150)
        {
            int num_menu_buttons = 2;
            if(Twotris.getInstance().config.kiosk_mode)
                num_menu_buttons = 1;
            curs--;
            if(curs < 0) {
              curs = buttons.keySet().size()+num_menu_buttons-1;
            }
            if(curs < buttons.keySet().size()) {
              moveMouseOver(curs);
            } else if(curs == buttons.keySet().size()) {
              moveMouseOver(back);
            } else if(curs == buttons.keySet().size()+1) {
                if(!Twotris.getInstance().config.kiosk_mode)
                    moveMouseOver(keybinds);
            }
            lastKeypress = Helper.getTime();
        }

        if (Mouse.isButtonDown(0) || Keyboard.isKeyDown(Twotris.getInstance().keybinds.rotate) || Keyboard.isKeyDown(Twotris.getInstance().keybinds.rotateBack))
        {
            if (back.isClicked())
            {
                if (this.fontChanged)
                {
                    Textures.fontName = Twotris.getInstance().config.font.toLowerCase();
                    Textures.reloadFont();
                    this.fontChanged = false;
                }
                if (this.colorChanged)
                {
                    if (last instanceof MainMenu)
                    {
                        ((MainMenu) last).recolor();
                    }
                    else if (last instanceof Game)
                    {
                        ((Game) last).recolor();
                    }

                    this.colorChanged = false;
                }
                this.next = this.last;
                return false;
            }
            else if (!Twotris.getInstance().config.kiosk_mode && keybinds.isClicked())
            {
                this.next = new OptionsKeybinds(this);
                return false;
            }
            else
            {
                for (Button b : buttons.keySet())
                {
                    if (b.isClicked())
                    {
                        String prop = buttons.get(b).substring(0, buttons.get(b).indexOf(":"));
                        Twotris.getInstance().config.toggleOption(prop, b);
                        if (b.getText().contains("Fullscreen"))
                        {
                            this.resizeContents();
                        }
                        else if (b.getText().contains("Font"))
                        {
                            this.fontChanged = true;
                        }
                        else if (b.getText().contains("Color"))
                        {
                            this.colorChanged = true;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * @param none
     * @return none
     * @throws none
     * @updateButtons This method updates the back button, and the other buttons in the keyset, then adjusts them.
     */
    public void updateButtons()
    {
        back.update();
        if(!Twotris.getInstance().config.kiosk_mode)
            keybinds.update();
        for (Button b : buttons.keySet())
        {
            b.update();
        }
        adjustButtons();
    }

    /**
     * @param none
     * @return none
     * @throws none
     * @drawButtons This method draws the back button, then all buttons in keyset.
     */
    public void drawButtons()
    {
        back.draw();
        if(!Twotris.getInstance().config.kiosk_mode)
            keybinds.draw();

        for (Button b : buttons.keySet())
        {
            b.draw();
        }
    }

    /**
     * @param none
     * @return none
     * @throws none
     * @adjustButtons This method adjusts the SIZE of the buttons within the keySet.
     */
    public void adjustButtons()
    {
        int lastY = 32;
        boolean isLeft = true;
        for (Button b : buttons.keySet())
        {
            int offset = isLeft ? Display.getWidth() / 4 : 3 * (Display.getWidth() / 4);
            int x = offset - (b.getHitBox().getWidth() / 2);
            b.getHitBox().setLocation(x, lastY);
            isLeft = !isLeft;
            if (isLeft)
            {
                lastY += b.getHitBox().getHeight() + 16;
            }
        }
        back.getHitBox().setLocation(16, Display.getHeight() - back.getHitBox().getHeight() - 16);
        
        if(!Twotris.getInstance().config.kiosk_mode)
            keybinds.getHitBox().setLocation(Display.getWidth() - keybinds.getHitBox().getWidth() - 16, Display.getHeight() - back.getHitBox().getHeight() - 16);
    }

    public void moveMouseOver(int curs) {
      //Move mouse
      int pos=0;
      for (Button b : buttons.keySet())
      {
        if(pos==curs) {
          Mouse.setCursorPosition((b.getHitBox().getX() + (b.getHitBox().getWidth() / 2)), Display.getHeight() - (b.getHitBox().getY() + (b.getHitBox().getHeight() / 2)));
          break;
        }
        pos++;
      }
    }

    public void moveMouseOver(Button b) {
      //Move mouse
      Mouse.setCursorPosition((b.getHitBox().getX() + (b.getHitBox().getWidth() / 2)), Display.getHeight() - (b.getHitBox().getY() + (b.getHitBox().getHeight() / 2)));
    }

    /**
     * @param none
     * @return next
     * @throws none
     * @nextScene This method sets the next scene, setting the next selection.
     */
    public Scene nextScene()
    {
        return next;
    }


    @Override
    public void resizeContents()
    {
        this.last.resizeContents();
        if (this.next != null)
        {
            this.next.resizeContents();
        }
    }
}
