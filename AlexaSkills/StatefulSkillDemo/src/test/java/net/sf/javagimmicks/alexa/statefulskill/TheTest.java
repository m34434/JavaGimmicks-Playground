package net.sf.javagimmicks.alexa.statefulskill;

import static net.sf.javagimmicks.alexa.process.ProcessEngineFactory.getProcessEngine;

import java.io.IOException;

import net.sf.javagimmicks.alexa.process.model.ProcessSnapshot;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TheTest
{
    private static final String VAR_FINISHED = "finished";

    private ObjectMapper m = new ObjectMapper();
    private ProcessEngine pe = getProcessEngine();
    private RuntimeService runtimeService = pe.getRuntimeService();
    private TaskService taskService = pe.getTaskService();
    private RepositoryService repositoryService = pe.getRepositoryService();

    @Before
    public void setup()
    {
        repositoryService.createDeployment().addInputStream("SkillDemo.bpmn", getClass().getResourceAsStream("SkillDemo.bpmn")).deploy();
    }
    
    @After
    public void shutdown()
    {
        for(Deployment d : repositoryService.createDeploymentQuery().list())
        {
            repositoryService.deleteDeployment(d.getId());
        }
    }
    
    @Test
    public void testProcessEngineNormal() throws JsonGenerationException, JsonMappingException, IOException
    {
        repositoryService.createDeployment().addInputStream("SkillDemo.bpmn", getClass().getResourceAsStream("SkillDemo.bpmn")).deploy();
        
        final ProcessInstance pi = runtimeService.startProcessInstanceByKey("prcDemoSkill", Variables.putValue(VAR_FINISHED, false));
        
        System.out.println(pi.getProcessInstanceId());
        System.out.println(m.writerWithDefaultPrettyPrinter().writeValueAsString(ProcessSnapshot.fromProcessInstance(runtimeService, pi)));
        
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.complete(taskId);

        System.out.println(pi.getProcessInstanceId());
        System.out.println(m.writerWithDefaultPrettyPrinter().writeValueAsString(ProcessSnapshot.fromProcessInstance(runtimeService, pi)));

        taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.setVariable(taskId, VAR_FINISHED, true);
        taskService.complete(taskId);
        
        Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    public void testProcessEngineWithStopNormal() throws JsonGenerationException, JsonMappingException, IOException
    {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("prcDemoSkill", Variables.putValue(VAR_FINISHED, false));
        
        ProcessSnapshot theState = ProcessSnapshot.fromProcessInstance(runtimeService, pi);
        final String theStateString = m.writerWithDefaultPrettyPrinter().writeValueAsString(theState);
        System.out.println(pi.getProcessInstanceId());
        System.out.println(theStateString);

        runtimeService.deleteProcessInstance(pi.getProcessInstanceId(), "Stateful services stops", true);
        Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
        
        // RESTORE !!!!
        theState = m.readValue(theStateString, ProcessSnapshot.class);
        pi = theState.startProcessInstance(runtimeService);
        String taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.complete(taskId);

        System.out.println(pi.getProcessInstanceId());
        System.out.println(m.writerWithDefaultPrettyPrinter().writeValueAsString(ProcessSnapshot.fromProcessInstance(runtimeService, pi)));

        taskId = taskService.createTaskQuery().singleResult().getId();
        taskService.setVariable(taskId, VAR_FINISHED, true);
        taskService.complete(taskId);
        
        Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}
