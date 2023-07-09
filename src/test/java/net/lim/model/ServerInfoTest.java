package net.lim.model;

import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerInfoTest extends BaseFXUnitTestClass {

    private final String testName = "someName";
    private final String testIp = "someIp.com";
    private final int testPort = 1000;
    private final String testDescription = "some description";

    @Test
    void testToStringWithoutDescription() {

        ServerInfo testServerInfo = new ServerInfo(testName, testIp, testPort);

        String serverString = testServerInfo.toString();

        Assertions.assertTrue(serverString.contains(testName));
        Assertions.assertFalse(serverString.contains(testIp));
        Assertions.assertFalse(serverString.contains(String.valueOf(testPort)));
    }

    @Test
    void testToStringWithDescription() {
        ServerInfo testServerInfo = new ServerInfo(testName, testDescription, testIp, testPort);

        String serverString = testServerInfo.toString();

        Assertions.assertTrue(serverString.contains(testName));
        Assertions.assertTrue(serverString.contains(testDescription));
        Assertions.assertFalse(serverString.contains(testIp));
        Assertions.assertFalse(serverString.contains(String.valueOf(testPort)));
    }

    @Test
    void testToStringWithDescriptionEmptyName() {
        ServerInfo testServerInfo = new ServerInfo("", testDescription, testIp, testPort);

        String serverString = testServerInfo.toString();

        Assertions.assertTrue(serverString.contains("Unnamed server: "));
        Assertions.assertTrue(serverString.contains(testDescription));
        Assertions.assertTrue(serverString.contains(testIp));
        Assertions.assertFalse(serverString.contains(String.valueOf(testPort)));
    }

}