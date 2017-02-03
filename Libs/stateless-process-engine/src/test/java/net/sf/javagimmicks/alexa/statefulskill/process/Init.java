package net.sf.javagimmicks.alexa.statefulskill.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Init implements JavaDelegate, ProcessConstants
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        execution.setVariable(VAR_FINISHED, false);
    }
}