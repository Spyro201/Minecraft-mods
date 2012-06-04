package net.minecraft.src;

import java.util.List;
import java.util.Collections;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;

public class GuiOldDaysSettings extends GuiScreen{
    private GuiScreen parent;
    private int id;
    private boolean dragging = false;
    private int currenty = 0;
    private int scrolled = 0;
    private static boolean scrolling = false;

    public GuiOldDaysSettings(GuiScreen guiscreen, int i){
        parent = guiscreen;
        id = i;
    }

    public void updateScreen()
    {
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        controlList.add(new GuiButton(0, width / 2 - 75, height - 28, 150, 20, stringtranslate.translateKey("menu.returnToGame")));
        for (int i = 1; i <= mod_OldDays.proplength[id]; i++){
            int x = width / 2 - 155;
            if (i % 2 == 0){
                x+=160;
            }
            int margin = 30;
            int top = 10;
            if (!scrolling){
                if (mod_OldDays.proplength[id] > 14){
                    top += (margin / 1.8);
                    margin -= 6;
                }else if (mod_OldDays.proplength[id] > 12){
                    margin -= 3;
                    top += (margin / 2);
                }else if (mod_OldDays.proplength[id] > 10){
                    top += (margin / 3);
                }
            }
            int y = height / 6 - top + (((i-1)/2) * margin);
            GuiButton button = new GuiButton(i+1, x, y, 150, 20, getState(i, id, i));
            button.enabled = !mod_OldDays.disabled[id][i] && !(mod_OldDays.propsmp[id][i]>=0 && ModLoader.getMinecraftInstance().theWorld.isRemote);
            controlList.add(button);
        }
        if (scrolling){
            updatePos(height/6-10);
        }
    }
    
    private String getState(int i2, int id, int i){
        String res = mod_OldDays.propname[id][i2];
        if (mod_OldDays.proptype[id][i2]==0){
            res = res+": ";
            int state = mod_OldDays.propvalue[id][i];
            StringTranslate stringtranslate = StringTranslate.getInstance();
            return state>0 ? res+stringtranslate.translateKey("options.on") : res+stringtranslate.translateKey("options.off");
        }
        if (mod_OldDays.proptype[id][i2]==1){
            res = res+": ";
            int state = mod_OldDays.propvalue[id][i];
            if (mod_OldDays.propnames[id][i2][state]!=null){
                return res+mod_OldDays.propnames[id][i2][state];
            }else{
                return res+state;
            }
        }
        if (mod_OldDays.proptype[id][i2]==2){
            return res+": "+mod_OldDays.propvaluestr[id][i];
        }
        return "NULL";
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if (!guibutton.enabled){
            return;
        }
        if (guibutton.id == 0){
            mc.displayGuiScreen(parent);
        }
        if (guibutton.id == 1){
            mc.displayGuiScreen(parent);
        }
        if (guibutton.id > 1){
            if (mod_OldDays.proptype[id][guibutton.id-1]==0){
                boolean b = mod_OldDays.propvalue[id][guibutton.id-1]==0;
                mod_OldDays.propvalue[id][guibutton.id-1]=b ? 1 : 0;
            }else if (mod_OldDays.proptype[id][guibutton.id-1]==1){
                boolean b = mod_OldDays.propvalue[id][guibutton.id-1]<mod_OldDays.propmax[id][guibutton.id-1];
                mod_OldDays.propvalue[id][guibutton.id-1]=b ? mod_OldDays.propvalue[id][guibutton.id-1]+1 : 1;
            }else if (mod_OldDays.proptype[id][guibutton.id-1]==2){
                String newstr = "test"+(new java.util.Random()).nextInt(9999);
                mod_OldDays.propvaluestr[id][guibutton.id-1] = newstr;
            }
            mod_OldDays.saveModuleProperties(id);
            if (mod_OldDays.proptype[id][guibutton.id-1]==0 || mod_OldDays.proptype[id][guibutton.id-1]==1){
                mod_OldDays.sendCallback(id, guibutton.id-1, mod_OldDays.propvalue[id][guibutton.id-1]);
            }else if (mod_OldDays.proptype[id][guibutton.id-1]==2){
                mod_OldDays.sendCallbackStr(id, guibutton.id-1, mod_OldDays.propvaluestr[id][guibutton.id-1]);
            }
            guibutton.displayString = getState(guibutton.id-1, id, guibutton.id-1);
        }
    }

    private void updatePos(int scroll){
        List list = controlList;
        int margin = 30;
        for (int i = 1; i < list.size(); i++){
            GuiButton button = ((GuiButton)list.get(i));
            int y = (((i-1)/2) * margin) + scroll;
            button.yPosition = y;
            button.drawButton = !(button.yPosition > height - 55 || button.yPosition < height / 6 - 10);
        }
        scrolled = scroll;
    }

    private boolean needScrolling(){
        List list = controlList;
        for (int i = list.size()-1; i >= 0; i--){
            if (!((GuiButton)list.get(i)).drawButton){
                return true;
            }
        }
        return false;
    }

    protected void mouseClicked(int par1, int par2, int par3){
        if (par2 < height - 55 && par2 > height / 6 - 10 && needScrolling() && scrolling){
            dragging = true;
            currenty = par2 - scrolled;
        }
        super.mouseClicked(par1, par2, par3);
    }

    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        if (!scrolling){
            if (par3==0){
                dragging = false;
            }
            if (dragging && par3==-1){
                updatePos(Math.max(Math.min(height/6-10, par2 - currenty), ((14-((mod_OldDays.proplength[id]-1)/2))*30)-235));
            }
        }
        super.mouseMovedOrUp(par1, par2, par3);
    }

    private void drawTooltip(String str, int x, int y, boolean smp){
        String str2 = str;
        if (smp){
            str2 = "";
        }
//         drawRect(x, y, x + 5 + fontRenderer.getStringWidth(str2), y + 13, 0x80000000);
//         drawString(fontRenderer, str2, x + 3, y + 3, smp ? 0xff0000 : 0xffffff);
        int top = 23;
        if (scrolling){
            top = 20;
        }else{
            if (mod_OldDays.proplength[id] > 14){
                top += 16;
            }else if (mod_OldDays.proplength[id] > 12){
                top += 13;
            }else if (mod_OldDays.proplength[id] > 10){
                top += 10;
            }
        }
       drawCenteredString(fontRenderer, str2, width / 2, height / 6 - top, smp ? 0xff0000 : 0xffffff);
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        super.drawScreen(i,j,f);
        for (int k = 1; k < controlList.size(); k++){
            GuiButton button = ((GuiButton)controlList.get(k));
            if (i > button.xPosition && i < button.xPosition+150 && j > button.yPosition && j < button.yPosition+20){
                String str = mod_OldDays.propdesc[id][k];
                boolean smp = mod_OldDays.propsmp[id][k]>=0 && ModLoader.getMinecraftInstance().theWorld.isRemote;
                if (str == null && !smp){
                    return;
                }
                drawTooltip(str, i + 4, j - 13, smp);
            }
        }
    }
}