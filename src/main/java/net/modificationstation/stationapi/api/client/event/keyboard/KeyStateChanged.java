package net.modificationstation.stationapi.api.client.event.keyboard;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.options.KeyBinding;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegister;
import net.modificationstation.stationapi.api.common.event.Event;
import org.lwjgl.input.Keyboard;

/**
 * Used to handle keypresses.
 * Implement this in the class you plan to use to handle your keypresses.
 * All events need to be registered in your mod's preInit method using KeyPressed.EVENT.register(yourInstantiatedClass).
 * You want to check for your key with {@link Keyboard#getEventKey()} against {@link KeyBinding#key myKeybind.key} before executing any related code.
 *
 * @author mine_diver
 * @see KeyBindingRegister
 */
@RequiredArgsConstructor
public class KeyStateChanged extends Event {

    public final Environment environment;

    public enum Environment {
        IN_GUI,
        IN_GAME
    }
}