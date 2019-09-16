package com.hhu.demo3;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    // 破解CenSum
    public static void premain(String agentArgument, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new MyClassFileTransformer(), true);
    }
}
