package dev.lotnest.sequoia.function;

import com.wynntils.core.components.Managers;
import com.wynntils.core.consumers.functions.FunctionManager;
import dev.lotnest.sequoia.function.functions.MantleOfTheBovemistsCountFunction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class WynntilsFunctionInjector {
    private WynntilsFunctionInjector() {}

    public static void injectFunctions()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method registerFunctionMethod = FunctionManager.class.getDeclaredMethod(
                "registerFunction", com.wynntils.core.consumers.functions.Function.class);

        registerFunctionMethod.setAccessible(true);
        registerFunctionMethod.invoke(Managers.Function, new MantleOfTheBovemistsCountFunction());
    }
}
