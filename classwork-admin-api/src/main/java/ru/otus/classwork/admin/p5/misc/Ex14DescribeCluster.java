package ru.otus.classwork.admin.p5.misc;

import ru.otus.classwork.admin.Utils;

public class Ex14DescribeCluster {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    var res = client.describeCluster();
                    Utils.log.info("Controller: {}", res.controller().get());
                    Utils.log.info("Nodes:\n{}", res.nodes().get());
                });
    }
}
