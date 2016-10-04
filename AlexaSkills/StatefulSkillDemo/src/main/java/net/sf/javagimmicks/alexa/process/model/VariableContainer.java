package net.sf.javagimmicks.alexa.process.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VariableContainer
{
    private final Map<String, Object> variables = new HashMap<>();

    public Map<String, Object> getVariables()
    {
        return Collections.unmodifiableMap(variables);
    }
}