package com.github.kfcfans.powerjob;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.github.kfcfans.powerjob.common.ExecuteType;
import com.github.kfcfans.powerjob.common.ProcessorType;
import com.github.kfcfans.powerjob.common.RemoteConstant;
import com.github.kfcfans.powerjob.common.utils.NetUtils;
import com.github.kfcfans.powerjob.worker.OhMyWorker;
import com.github.kfcfans.powerjob.worker.common.OhMyConfig;
import com.github.kfcfans.powerjob.worker.common.utils.AkkaUtils;
import com.github.kfcfans.powerjob.worker.pojo.model.InstanceInfo;
import com.github.kfcfans.powerjob.worker.pojo.request.TaskTrackerStartTaskReq;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * 启动公共服务
 *
 * @author tjq
 * @since 2020/6/17
 */
public class CommonTest {

    protected static ActorSelection remoteProcessorTracker;
    protected static ActorSelection remoteTaskTracker;

    @BeforeAll
    public static void startWorker() throws Exception {
        OhMyConfig ohMyConfig = new OhMyConfig();
        ohMyConfig.setAppName("oms-test");
        ohMyConfig.setEnableTestMode(true);

        OhMyWorker worker = new OhMyWorker();
        worker.setConfig(ohMyConfig);
        worker.init();

        ActorSystem testAS = ActorSystem.create("oms-test", ConfigFactory.load("oms-akka-test.conf"));
        String address = NetUtils.getLocalHost() + ":27777";

        remoteProcessorTracker = testAS.actorSelection(AkkaUtils.getAkkaWorkerPath(address, RemoteConstant.PROCESSOR_TRACKER_ACTOR_NAME));
        remoteTaskTracker = testAS.actorSelection(AkkaUtils.getAkkaWorkerPath(address, RemoteConstant.Task_TRACKER_ACTOR_NAME));
    }

    @AfterAll
    public static void stop() throws Exception {
        Thread.sleep(120000);
    }

    public static TaskTrackerStartTaskReq genTaskTrackerStartTaskReq(String processor) {

        InstanceInfo instanceInfo = new InstanceInfo();

        instanceInfo.setJobId(1L);
        instanceInfo.setInstanceId(10086L);

        instanceInfo.setExecuteType(ExecuteType.STANDALONE.name());
        instanceInfo.setProcessorType(ProcessorType.EMBEDDED_JAVA.name());
        instanceInfo.setProcessorInfo(processor);

        instanceInfo.setInstanceTimeoutMS(500000);

        instanceInfo.setThreadConcurrency(5);
        instanceInfo.setTaskRetryNum(3);

        TaskTrackerStartTaskReq req = new TaskTrackerStartTaskReq();

        req.setTaskTrackerAddress(NetUtils.getLocalHost() + ":27777");
        req.setInstanceInfo(instanceInfo);

        req.setTaskId("0");
        req.setTaskName("ROOT_TASK");
        req.setTaskCurrentRetryNums(0);

        return req;
    }
}
