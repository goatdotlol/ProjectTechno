package com.techno.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Mouse;

import java.util.Random;

public class TechnoClicker {
    private static final TechnoClicker INSTANCE = new TechnoClicker();
    private final Random random = new Random();
    
    private long lastClickTime = 0;
    private long nextDelay = 0;
    private boolean isClicking = false;

    // Config: 10 CPS Average
    private static final double MEAN_DELAY = 100.0; // ms
    private static final double STD_DEV = 15.0; // variance

    public static TechnoClicker getInstance() {
        return INSTANCE;
    }

    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null) return;

        // Check if Left Mouse Button is held down
        if (Mouse.isButtonDown(0)) {
            long currentTime = System.nanoTime() / 1000000; // convert to ms

            if (currentTime - lastClickTime >= nextDelay) {
                // Perform the click
                performClick(mc);
                
                // Calculate next delay using Gaussian distribution
                double gaussian = random.nextGaussian();
                nextDelay = (long) (MEAN_DELAY + (gaussian * STD_DEV));
                
                // Clamp delay to reasonable limits (e.g., 50ms to 150ms) to avoid insane speeds
                if (nextDelay < 50) nextDelay = 50;
                if (nextDelay > 150) nextDelay = 150;
                
                lastClickTime = currentTime;
            }
        } else {
            // Reset state slightly but maintain randomness
            lastClickTime = (System.nanoTime() / 1000000) - 200; 
        }
    }

    private void performClick(Minecraft mc) {
        // Option 1: KeyBinding state (Safest for Ghost)
        // We flash the keybind state to simulate a press
        // However, standard MC left click is processed via clickMouse() called from runTick when button is down.
        // If we want to ADD clicks while holding, we can manually invoke the left click logic.
        
        // Since we are creating an Autoclicker that triggers WHILE holding:
        // Vanilla naturally keeps clicking, but at a very slow/weird rate if you just hold (it hits cooldown).
        // 1.8.9 PvP requires clicking fast.
        
        // Approach: Simply call clickMouse() via accessing the method (need reflection or Mixin Shadow)
        // OR
        // Use KeyBinding.onTick(keyCode) or setKeyBindState.
        
        // Let's rely on standard "Ghost" input simulation:
        // We set the key state to false then true?
        // Actually, for 1.8.9 reset the attack timer?
        
        // Simulating raw left click:
        mc.clickMouse();
        
        // NOTE: clickMouse() is named "func_147116_af" in searge. 
        // With Fabric Loom and Yarn, it's mapped to clickMouse.
    }
}
