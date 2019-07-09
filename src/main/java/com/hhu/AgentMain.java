package com.hhu;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    public static void premain(String agentArgument, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new MyClassFileTransformer(), true);
    }
}
