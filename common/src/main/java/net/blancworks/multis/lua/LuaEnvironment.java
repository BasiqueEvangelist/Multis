package net.blancworks.multis.lua;

import com.google.common.base.Charsets;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.terasology.jnlua.LuaState53;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class manages almost everything related to the lua environment of Multis.
 */
public class LuaEnvironment {

    private static LuaState53 luaState;

    public static void init() {
        luaState = new LuaState53();
        loadEnvironment();
    }

    public static void client_init() {

    }

    public static void server_init() {

    }


    private static void loadEnvironment() {
        try (InputStream is = LuaEnvironment.class.getResourceAsStream("/lua_env.lua")) {
            String s = IOUtils.toString(is, Charsets.UTF_8);

            luaState.openLibs();
            luaState.load(s, "lua_env.lua");
            luaState.call(0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMultisScript(String src, Identifier id) {
        luaState.getGlobal("loadMultisObject");
        luaState.pushString(src);
        luaState.pushString(id.toString());
        luaState.call(2, 0);
    }

    public static <T> T loadMultisObject(String src, Identifier id, Class c) {
        luaState.getGlobal("loadMultisObject");
        luaState.pushString(src);
        luaState.pushString(id.toString());
        luaState.call(2, 1);

        T gotten = (T) luaState.getProxy(-1, c);

        luaState.pop(1);

        return gotten;
    }

    public static void clear(){
        luaState.close();

        luaState = new LuaState53();
        loadEnvironment();
    }
}
